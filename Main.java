
import java.io.OutputStream;
import java.io.InputStream;
import java.io.IOException;

import java.net.Socket;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class Main {

	Socket client_socket;

	public static void main(String[] args)
		throws IOException
		, UnknownHostException
	{
		final Main worker = new Main();

		worker.connect();

		final OutputStream in = worker.client_socket.getOutputStream();
		new Thread(new Runnable() {
			public void run() {
				try {
					worker.handleInput(in);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}).start();

		final InputStream out = worker.client_socket.getInputStream();
		new Thread(new Runnable() {
			public void run() {
				try {
					worker.handleOutput(out);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}).start();
	}

	private void connect()
		throws IOException
		, UnknownHostException
	{
		client_socket = new Socket(InetAddress.getLocalHost(), 20015);
	}

	private void handleInput(OutputStream in)
		throws IOException
	{
		byte buf[] = new byte[1024];
		int read_cnt;
		while ((read_cnt = System.in.read(buf)) > 0) {
			String command = new String(buf, 0, read_cnt);
			in.write(command.getBytes());
		}
	}

	private void handleOutput(InputStream out)
		throws IOException
	{
		byte buf[] = new byte[1024];
		int read_cnt;
		while ((read_cnt = out.read(buf)) > 0) {
			String output = new String(buf, 0, read_cnt);
			System.out.println("Output: " + output);
		}
	}
}
