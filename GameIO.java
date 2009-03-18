
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

import java.util.concurrent.BlockingQueue;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class GameIO {
	Client client;

	public GameIO(Client client) {
		this.client = client;

		this.client.write("launch;");
	}

	public void execute() {
		final Client.Events src = this.client.addListener();
		Thread listener = new Thread(new Runnable() {
			public void run() {
				while (true) {
					if (Thread.interrupted()) {
						return;
					}
					try {
						parseOutput(src.take());
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}
			}
		});

		listener.start();

		try {
			byte buf[] = new byte[1024];
			int read_cnt;
			while ((read_cnt = System.in.read(buf)) > 0) {
				if (!listener.isAlive()) {
					return;
				}
				StringTokenizer commands
				 = new StringTokenizer(new String(buf, 0, read_cnt), ";", true);
				
				while (commands.countTokens() > 1) {
					String command = commands.nextToken();
					command += commands.nextToken();
					System.out.println("Cmd: " + command);

					if (command.equals("p;")) {
						try {
							this.print();
						} catch (Exception e) {
							e.printStackTrace(System.out);
						}
					} else {
						client.write(command);
					}
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			listener.interrupt();
		}
	}

	// now state.
	
	int pid;
	int round;

	int cell_size;
	int map_height;
	int map_width;
	List<String> map;

	int time;

	boolean dead;
	Point me;

	Point bomb;

	// for printing
	boolean map_changed;
	int last_visual_x;
	int last_visual_y;

	static Pattern head;
	static Pattern map_symbols;
	static Pattern sapka_info_pat;
	static Pattern changes_part;

	
	private synchronized void parseOutput(String output) {
		Matcher matcher = head.matcher(output);

		if (!matcher.find()) {
			System.out.println("Output: " + output);
			return;
		}

		if (matcher.group(2) != null) {
			this.pid = Integer.parseInt(matcher.group(3));
		} else if (matcher.group(4) != null) {
			this.round = Integer.parseInt(matcher.group(5));
			this.cell_size =  Integer.parseInt(matcher.group(6));
			this.map = new ArrayList<String>();
			Matcher map_matcher = map_symbols.matcher(matcher.group(7));
			this.map_height = 0;
			this.map_width = 0;
			while (map_matcher.find()) {
				this.map_height++;
				String line = map_matcher.group(1);
				if (this.map_width == 0) {
					this.map_width = line.length();
				}
				this.map.add(line);
			}
			this.map_height = this.map.size();
			Collections.reverse(this.map);
		} else if (matcher.group(8) != null) {
			this.time = Integer.parseInt(matcher.group(9));
			Matcher sapka_matcher = sapka_info_pat.matcher(matcher.group(10));
			while (sapka_matcher.find()) {
				if (Integer.parseInt(sapka_matcher.group(1)) != this.pid) {
					continue;
				}

				if (sapka_matcher.group(2).equals("dead")) {
					this.dead = true;
					break;
				}
				int x = Integer.parseInt(sapka_matcher.group(3));
				int y = this.map_height * this.cell_size - Integer.parseInt(sapka_matcher.group(4)) - 1;
				this.me = new Point(x, y);
			}

			if (this.me != null
				&& (me.x / cell_size != last_visual_x
					|| me.y / cell_size != last_visual_y))
			{
				print();
			}
		}
	}

	private synchronized void print() {
		try {
			int visual_x = -1;
			int visual_y = -1;
			if (this.me != null) {
				visual_x = this.me.x / this.cell_size;
				visual_y = this.me.y / this.cell_size;
			}
			for (int i = this.map.size() - 1; i >= 0; --i) {
				String line = this.map.get(i);
				if (visual_y == i) { // if visual_y == -1 this never happen
					line = line.substring(0, visual_x) + "@" + line.substring(visual_x + 1);
				}
				System.out.println(line);
			}
			if (this.me != null) {
				System.out.println("Me: " + this.me.x + ", " + this.me.y);
			}
			this.last_visual_x = visual_x;
			this.last_visual_y = visual_y;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	static {
		head =
		 Pattern.compile(
		    "^(" // 1
			+ "(PID([0-9]*)&[^;]*)" // 2,3
			+ "|(START([0-9]*)&([0-9]+)\r\n([^;]+))" // 4,5,6,7
			+ "|(T([0-9]*)&([^;&]*)&([^;&]*)(&[^;])?)" // 8,9,10,11,12
			+ ");");
		/*
			+ "(REND (-?[0-9]*))" // 10,11
			+ "(GEND (-?[0-9]*))" // 12,13
		*/
		
		map_symbols = Pattern.compile("^([\\.Xw]+)$", Pattern.MULTILINE);

		sapka_info_pat =
		 Pattern.compile("P([0-9]+) "
		  + "(dead|([0-9]+) ([0-9]+) ([0-9]+) ([0-9]+) ([0-9]+)( i)?)(,|$)");
	}
}
