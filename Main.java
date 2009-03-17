import java.io.IOException;
import java.util.StringTokenizer;
import java.util.concurrent.BlockingQueue;

public class Main {

	public static void main(String[] args)
		throws IOException
	{
		final Client client = new Client(args[0], Integer.parseInt(args[1]));

		Init.doInit(client);

		GameIO game_io = new GameIO(client);
		game_io.execute();
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
}
