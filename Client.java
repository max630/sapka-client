import java.io.IOException;

import java.net.Socket;
import java.net.InetAddress;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import java.lang.ref.WeakReference;

public class Client {
	Socket client_socket;
	ConcurrentHashMap<Integer, WeakReference<BlockingQueue<String>>> listeners;
	AtomicInteger counter;

	Client(String hostname, int port)
	{
		try {
			this.client_socket = new Socket(InetAddress.getLocalHost(), 20015);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		this.listeners = new ConcurrentHashMap<Integer, WeakReference<BlockingQueue<String>>>();
		this.counter = new AtomicInteger(0);

		final Client reader = this;
		new Thread(new Runnable() {
			public void run() {
				try {
					while(true) {
						String data = reader.read();
						for (Integer q_idx: reader.listeners.keySet()) {
							BlockingQueue<String> q = reader.listeners.get(q_idx).get();
							if (q != null) {
								q.add(data);
							} else {
								reader.listeners.remove(q_idx);
							}
						}
					}
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}).start();
	}

	public BlockingQueue<String> addListener() {
		BlockingQueue<String> res = new LinkedBlockingQueue<String>();
		this.listeners.put(new Integer(this.counter.addAndGet(1)), new WeakReference<BlockingQueue<String>>(res));
		return res;
	}

	private String read() {
		byte buf[] = new byte[1024];
		int read_cnt;
		try {
			read_cnt = this.client_socket.getInputStream().read(buf);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		if (read_cnt <= 0) {
			throw new RuntimeException("EOF");
		}
		return new String(buf, 0, read_cnt);
	}

	public void write(String data) {
		try {
			this.client_socket.getOutputStream().write(data.getBytes());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
