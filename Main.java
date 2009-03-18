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
}
