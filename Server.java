
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketOption;
import java.util.ArrayList;
import java.util.Iterator;

import javax.imageio.stream.MemoryCacheImageInputStream;

public class Server {
	public Server(int ok, int allo) {
		// TODO Auto-generated constructor stub
		port = ok;
		repetitions = allo;
	}

	int port = 0;
	Integer repetitions = 0;
	ArrayList<Thread> threads = new ArrayList<Thread>();

	long interval = 1000000000;
	static Integer numberOfRequests = 0;
	static long start;
	static long now;

	public class ClientHandler implements Runnable {
		BufferedReader reader;
		PrintWriter writer1;
		Socket s;
		int myRequestsComp = 0;

		public ClientHandler(Socket clSock) {

			try {
				s = clSock;
				InputStreamReader isReader = new InputStreamReader(s.getInputStream());
				reader = new BufferedReader(isReader);
				numberOfRequests++;
				myRequestsComp++;
				synchronized (repetitions) {
					repetitions--;
					if (repetitions == 0)
						System.out.println(numberOfRequests);

				}
				writer1 = new PrintWriter(s.getOutputStream());
			} catch (Exception ex) {
				ex.printStackTrace();
			}

		}

		public String retNum(String ok) {
			String kati = "";
			for (int i = 0; i < ok.length(); i++) {
				if (ok.charAt(i) != ' ')
					kati = kati + ok.charAt(i);
				else {
					kati = "";
				}
			}
			return kati;
		}

		public void run() {
			String message;
			try {
				while ((message = reader.readLine()) != null) {
					// System.out.println(message);
					writer1.println("Welcome " + retNum(message) + payload());
					// System.out.println(message);
					myRequestsComp++;
					numberOfRequests++;
					synchronized (repetitions) {
						repetitions--;
						if (repetitions == 0) {
							System.out.println(numberOfRequests);
							while (!threads.isEmpty()) {
								threads.get(0).interrupt();
								threads.remove(0);
							}
						}

					}
					// System.out.println(myRequestsComp);
					/*
					 * synchronized (numberOfRequests) { numberOfRequests++;
					 * 
					 * }
					 */

					if (interval < System.nanoTime() - start) {
						System.out.println(numberOfRequests);
						start = Long.MAX_VALUE;
					}

					writer1.flush();
					// tellEveryone(message);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	public void go() {
		try {
			ServerSocket server = new ServerSocket(port);

			while (true) {
				Socket clSock = server.accept();

				synchronized (numberOfRequests) {

					if (numberOfRequests == 0) {
						start = System.nanoTime();
						// System.out.println(System.nanoTime());
					}
				}
				// Thread t=new Thread(new ClientHandler(clSock));
				threads.add(0, new Thread(new ClientHandler(clSock)));
				// t.start();
				threads.get(0).start();
				// System.out.println("got cockNection");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public String payload() {
		int msgSize = (int) (1700000 * Math.random() + 300000);
		StringBuilder sb = new StringBuilder(msgSize);
		for (int i = 0; i < msgSize; i++) {
			sb.append('a');
		}
		String toBeSent = sb.toString();
		return toBeSent;
	}

	public static void main(String[] args) {
		for (int i = 0; i < 100; i++)
			new Server(Integer.parseInt(args[0]), Integer.parseInt(args[1])).go();

	}

}
