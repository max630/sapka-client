import java.io.IOException;

import java.net.Socket;
import java.net.InetAddress;

public class Client {
	Socket client_socket;

	Client(String hostname, int port)
	{
		try {
			this.client_socket = new Socket(InetAddress.getLocalHost(), 20015);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public String read() {
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
