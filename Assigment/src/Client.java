
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Enumeration;
/**
 * The client side of the program.Simulates the concurrent 
 * users requesting info from the server.
 * @author gkyrli01,mhadji05
 *
 */
public class Client {

	static Integer num = 0;
	/**
	 * The user class that extends Thread.
	 * @author gkyrli01,mhadji05
	 *
	 */
	public class User extends Thread {
		/**
		 * How many requests the user makes.
		 */
		int count = 300;
		/**
		 * Every latency taken for all the requests.
		 */
		ArrayList<Long> latencys = new ArrayList<Long>();
		/**
		 * The Constructor
		 */
		public User() {
			synchronized (num) {
				id = num++;
			}
			go();
		}

		/***
		 * The implementation of the run method for Runnable interface.
		 * Waits for the server respond.When the server responses it calculates the
		 * latency of the request(end variable and start variable),reduces the requests 
		 * to be made and sends the next
		 * request setting the start time at the current time .If the counter==0
		 * then it closes the streams and the socket and finds the average latency.
		 */
		@Override
		public void run() {
			long start = System.nanoTime();
			long end = System.nanoTime();
			long latency = 0;
			String msg;

			try {
				while ((msg = reader.readLine()) != null) {
					end = System.nanoTime();
					count--;
					if (count == 0) {
						reader.close();
						writer.close();
						this.s.close();
						latency = end - start;
						latencys.add(latency);
						averageLatency();
						break;

					} else {

						latency = end - start;
						latencys.add(latency);
						writer.println("HELLO " + InetAddress.getLocalHost().getHostAddress() + " " + port + " " + id);
						start = System.nanoTime();
						writer.flush();
					}
				}
			} catch (Exception ex) {
				//ex.printStackTrace();
			}
			if(count!=0)
				averageLatency();

		}
		/**
		 * Calculates the average latency of the user.
		 */
		public void averageLatency() {
			Long all = (long) 0;
			for (Long long1 : latencys) {
				all += long1 / 1000000;
			}
			System.out.println(all / latencys.size()+ " ms");
		}

		BufferedReader reader;
		PrintWriter writer;
		Socket s;
		int id = 0;
		
		public void go() {
			setUpItterenet();
		}
		/**
		 * Sends the first request to the server.
		 */
		private void setUpItterenet() {

			try {
				s = new Socket(ip, port);
				InputStreamReader stream = new InputStreamReader(s.getInputStream());
				reader = new BufferedReader(stream);
				writer = new PrintWriter(s.getOutputStream());
				writer.println("HELLO " + InetAddress.getLocalHost().getHostAddress() + " " + port + " " + id);
				writer.flush();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

	}

	static String ip;
	static int port;

	public User retUser() {
		return new User();
	}

	public static void main(String[] args) {
		ip = args[0];
		port = Integer.parseInt(args[1]);
		Client father = new Client();
		for (int i = 0; i < 10; i++) {
			User mUser = father.retUser();
			mUser.start();
		}

	}

}
