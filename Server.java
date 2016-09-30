
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;

import javax.imageio.stream.MemoryCacheImageInputStream;



public class Server {
	static PayLoad ok;
	ArrayList OutStreams;
	static ArrayList<PayLoad> randomPayloads=new ArrayList<PayLoad>();
	long interval=2*500000000;
	static int numberOfRequests=0;
	static  long  start;  
    static long now;
	public  Server.PayLoad name() {
		return new PayLoad();
	}
	class PayLoad{
 		final int MIN=300000;
		final int MAX=2000000;
		String toBeSent;
		public PayLoad() {
			//int msgSize=(int)(1700000*Math.random());
			int msgSize=(int)(1150000);
				  StringBuilder sb = new StringBuilder(msgSize);
				  for (int i=0; i<msgSize; i++) {
				    sb.append('a');
				  }
				 toBeSent=sb.toString();
				
			// TODO Auto-generated constructor stub
		}
	}
	
	public class ClientHandler implements Runnable{
		BufferedReader reader;
		PrintWriter writer1;
		Socket s;
		
		public ClientHandler(Socket clSock){
			
			try{
				s=clSock;
				InputStreamReader isReader=new InputStreamReader(s.getInputStream());
				reader=new BufferedReader(isReader);
				numberOfRequests++;
				
				writer1=new PrintWriter(s.getOutputStream());
			}catch(Exception ex){
				ex.printStackTrace();
			}
			
		}
		public String retNum(String ok){
			String kati="";
			for(int i=0;i<ok.length();i++){
				if(ok.charAt(i)!=' ')
					kati=kati +ok.charAt(i);
				else {
					kati="";
				}
			}
			return kati;
		}
		
		public   void run() {
			String message;
			try{
				while((message=reader.readLine())!=null){
					//System.out.println(message);
					writer1.println("Welcome " +retNum(message)+ok);
					//System.out.println(message);
					numberOfRequests++;
			
					if(interval<System.nanoTime()-start){
						System.out.println(numberOfRequests);
						start=Long.MAX_VALUE;
					}
					
					writer1.flush();
					//tellEveryone(message);
				}
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
	}
	
	

	public void go(){
		OutStreams=new ArrayList();
		try{
			ServerSocket server=new ServerSocket(5000);
			
			while(true){
				Socket clSock=server.accept();
				if(numberOfRequests==0){
					start = System.nanoTime();  
					System.out.println(System.nanoTime());
				}
				
				Thread t=new Thread(new ClientHandler(clSock));
				t.start();
				//System.out.println("got cockNection");
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

	public static void main(String[] args) {
		Server neo=new Server();
		//for(int i=0;i<100;i++)
		//randomPayloads.add(neo.name());
		 ok=neo.name();
		new Server().go();
		
	}
	

}
