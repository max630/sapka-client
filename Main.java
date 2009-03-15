import java.io.IOException;
import java.util.StringTokenizer;
import java.util.concurrent.BlockingQueue;

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

		final BlockingQueue<String> source = client.addListener();
		new Thread(new Runnable() {
			public void run() {
				try {
					handleOutput(source);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}).start();

		Init.doInit(client);
	}

	private static void handleInput(Client client)
		throws IOException
	{
		DNA dna = null;
		byte buf[] = new byte[1024];
		int read_cnt;
		while ((read_cnt = System.in.read(buf)) > 0) {
			StringTokenizer commands
			 = new StringTokenizer(new String(buf, 0, read_cnt), ";", true);
			
			while (commands.countTokens() > 1) {
				String command = commands.nextToken();
				command += commands.nextToken();

				if (dna != null && dna.handleCommand(command)) {
					if (dna.closed()) {
						dna = null;
					}
				} else if (command.equals("dna;")) {
					dna = new DNA(client);
				} else {
					client.write(command);
				}
			}
		}
	}

	private static void handleOutput(BlockingQueue<String> source)
		throws InterruptedException
	{
		while (true) {
			System.out.println("Output: " + source.take());
		}
	}
}
