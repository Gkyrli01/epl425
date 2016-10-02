

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketOption;
import java.util.ArrayList;
import java.util.Iterator;

import javax.imageio.stream.MemoryCacheImageInputStream;

/**
 * 
 * The server part of the programe.Waits for user request reads the
 * message,finds the number of the user,creates a random payload and sends it
 * with a welcome +id message back to the user. In the server side we find the
 * number of requests served each second.
 * 
 * @author gkyrli01,mhadji05
 *
 */
public class Server {
	ServerSocket server;
	public Server(int ok, int allo) {
		port = ok;
		repetitions = allo;
	}

	private int port = 0;
	private Integer repetitions = 0;
	private ArrayList<Thread> threads = new ArrayList<Thread>();
	private long interval = 1000000000;
	private static Integer numberOfRequests = 0;
	private static long start;

	public class UserManage implements Runnable {
		BufferedReader reader;
		PrintWriter writer1;
		Socket s;
		/**
		 * Here is the constructor of a user manage instance where it accepts the
		 * user and opens the input and output streams to get and send messages to
		 * the user.
		 * @param clSock
		 */
		public UserManage(Socket clSock) {

			try {
				s = clSock;
				s.setTcpNoDelay(true);
				InputStreamReader isReader = new InputStreamReader(s.getInputStream());
				reader = new BufferedReader(isReader);
				
				synchronized (repetitions) {
					numberOfRequests++;
					repetitions--;
					if (repetitions == 0)
						System.out.println(numberOfRequests);

				}
				writer1 = new PrintWriter(s.getOutputStream());
			} catch (Exception ex) {
				ex.printStackTrace();
			}

		}
		/**
		 * Gets the hello message from the user and return the id of the
		 * user.
		 * @param ok
		 * @return the number of the user sending the request.
		 */
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

		/**
		 * Runnables implementation of run method for UserManage class.
		 * Reads the message that the user sends with it's request
		 * and send back a message with a payload.Every loop it counts
		 * number of requests served globaly and if it reaches maximum repetitions
		 * it stops.
		 */
		public void run() {
			String message;
			boolean flag=false;
			try {
				while ((message = reader.readLine()) != null && repetitions>0) {
					writer1.println("Welcome " + retNum(message));
					
					synchronized (Server.this) {
						numberOfRequests++;
						repetitions--;
						if (repetitions <= 0) {
							
							if(!flag)
							System.out.println(numberOfRequests +"Requests served.Maximum"
									+ " requests reached,server will close");		
							flag=true;
							server.close();
							writer1.close();
							reader.close();
							break;
							
						}

					}
					if (interval < System.nanoTime() - start) {
						System.out.println(numberOfRequests);
						start = System.nanoTime();
					}
					writer1.flush();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}finally {
				
			}
		}
	}

	/**
	 * The method that starts the server.Creates a server socket at the port
	 * given from the arguments and waits for user requests.
	 */
	public void go() {
		try {
			 server = new ServerSocket(port);

			while (true) {
				Socket clSock = server.accept();

				synchronized (numberOfRequests) {

					if (numberOfRequests == 0) {
						start = System.nanoTime();
					}
				}
				threads.add(0, new Thread(new UserManage(clSock)));
				threads.get(0).start();
			}
		} catch (Exception ex) {
			//ex.printStackTrace();
		}
	}

	/**
	 * Genarates a random payload between 300kb-2Mb
	 * 
	 * @return String
	 */
	public String payload() {
		int msgSize = (int) (1700000 * Math.random() + 300000);
		StringBuilder sb = new StringBuilder(msgSize);
		for (int i = 0; i < msgSize; i++) {
			sb.append('k');
		}
		String toBeSent = sb.toString();
		return toBeSent;
	}

	public static void main(String[] args) {
		Server rela = new Server(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
		rela.go();
	}

}
