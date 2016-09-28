
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.print.attribute.standard.RequestingUserName;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

import org.omg.CORBA.FloatHolder;

public class Client {
	
	static int num=0;
	
	public class User extends Thread {
		int count=1220;
		public User() {
			// TODO Auto-generated constructor stub
			id=num++;
			go();
		}
		@Override
		public void run() {
	     		long start = System.nanoTime();  
			    long end = System.nanoTime();	
			    long latency=0;
				String msg;

				try {
					while ((msg = reader.readLine()) != null) {
						//System.out.println("read" + msg);
						end = System.nanoTime();
						count--;
						if (count==0){
							reader.close();
							writer.close();
							this.s.close();
							latency=end-start;
							//System.out.println(latency);
							break;
							
						}
						else {
							
						latency=end-start;
						//System.out.println(latency);
						writer.println("HELLO " + InetAddress.getLocalHost().getHostAddress()+" "+ port+" " + id);
						start = System.nanoTime(); 
						writer.flush();
						}
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			
		}


		BufferedReader reader;
		PrintWriter writer;
		Socket s;
		int id = 0;
		
		public void go() {
			setUpItterenet();	
		}

		private void setUpItterenet() {

			try {
				s = new Socket("127.0.0.1",5000);
				Enumeration<NetworkInterface> ola = NetworkInterface
						.getNetworkInterfaces();
				InputStreamReader stream = new InputStreamReader(s.getInputStream());
				reader = new BufferedReader(stream);
				writer = new PrintWriter(s.getOutputStream());
				writer.println("HELLO " + InetAddress.getLocalHost().getHostAddress()+" "+ port+" " + id);
				writer.flush();
				//System.out.println("itterne estaplished");
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
	//	ip = args[0];
	//	port = Integer.parseInt(args[1]);
		Client father=new Client();
		for (int i = 0; i < 35; i++) {
			User mUser=father.retUser();
			mUser.start();
		}
		
	}

	
	
	
	

}
