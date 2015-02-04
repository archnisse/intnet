import java.io.*;


import java.net.Socket;
import java.net.ServerSocket;

public class Server{
	

	Broadcast broadcast;
	public Server() {
		try {
			//Initierar en tråd som lyssnar efter klienter
			ServerSocket ss = new ServerSocket(1235);
			Socket s = null;
			System.out.println("Server initiated.");
			Thread t = new Thread(new newClients(s, ss));
			broadcast = new Broadcast();
			t.start();
		} catch (Exception e) {
			System.err.println("error: " + e);
		}
		
		
	}
	
	public static void main(String[] args) throws Exception{
		//Startar server
		new Server();
		
		
	}
	
	private class newClients implements Runnable {
		//Skapar klienter
		Socket s;
		ServerSocket ss;
		int currentid;
		
		public newClients(Socket s, ServerSocket ss) {
			this.s = s;
			this.ss = ss;
		}
 		
		public void run() {
			try {
				//Lyssnar efter nya klienter
				while( (s = ss.accept()) != null){
					//lägger till ny klient
					currentid = broadcast.addClient(new PrintWriter(s.getOutputStream(), true));
					System.out.println("Accepted client nr: " + currentid);
					System.out.println("Number of currently active threads: " + Thread.getAllStackTraces().keySet().size());
					//skapar en tråd per klient
					Thread t = new Thread(new ClientHandler(s, currentid));
					t.start();
					s = null;
				}
			} catch (Exception e) {
				System.err.println("error in newClients run: " + e);
			}
		}
	}
	
	private class ClientHandler implements Runnable {
		
		String text;
		BufferedReader indata;
		Socket s;
		int id;
		
		public ClientHandler(Socket s, int id) {
			this.s = s;
			this.id = id;
			text = "";

			try {
				//läser in det som klienten skriver
				indata = new BufferedReader(new InputStreamReader(s.getInputStream()));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		public void run() {
			try {
				while( (text = indata.readLine()) != null){
					//lägger medddelandena i en kö
					broadcast.addMessage(text);
					//skriver ut klientens meddelande till alla andra anslutna klienter
					broadcast.printBroadcast(id);
				}
				// Körs när klienten kopplar ifrån
				System.out.println("Client nr " + id + " is now disconnected.");
				//tar bort klient
				broadcast.removeClient(id);
				System.out.println("The number of active clients are now: " + broadcast.getActiveClients());
				//stänger socket
				s.shutdownInput();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	
}