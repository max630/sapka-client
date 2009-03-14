import java.io.IOException;

public class Main {

	public static void main(String[] args)
		throws IOException
	{
		final Client client = new Client("localhost", 20015);

		new Thread(new Runnable() {
			public void run() {
				try {
					handleInput(client);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}).start();

		new Thread(new Runnable() {
			public void run() {
				try {
					handleOutput(client);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}).start();
	}

	private static void handleInput(Client client)
		throws IOException
	{
		byte buf[] = new byte[1024];
		int read_cnt;
		while ((read_cnt = System.in.read(buf)) > 0) {
			String command = new String(buf, 0, read_cnt);
			client.write(command);
		}
	}

	private static void handleOutput(Client client)
		throws IOException
	{
		while (true) {
			System.out.println("Output: " + client.read());
		}
	}
}
