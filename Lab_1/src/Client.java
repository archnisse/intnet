import java.io.*;
import java.net.*;

public class Client {
	
	int port = 1235;
	PrintWriter out = null;
    BufferedReader input = null;
    BufferedReader serverInput = null;
    String inputLine, serverInputLine;
	
	public Client(String ip, String alias) {
		
		try {
			//initierar klienten
			Socket socket = new Socket(ip, port);
			//output från klienten
			out = new PrintWriter(socket.getOutputStream(), true);
			//input från klienten
			input = new BufferedReader(new InputStreamReader(System.in));
			//input från server
			serverInput = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			//lyssnar efter inkommande meddelanden
			Thread listen = new Thread(new Listen());
			listen.start();
			
		} catch(Exception e) {
			System.err.println("error: " + e);
		}
		
		while (true) {
			try {
				//läser klientens input
				inputLine = input.readLine();
				//skriver ut inputLine till servern
				out.println(alias + ": " + inputLine);
				
			} catch(Exception e) {
				System.err.println("error: " + e);
				break;
			}
		}
		
	}
	
	
	private class Listen implements Runnable {
		
		public Listen() {
			serverInputLine = "";
		}
		
		public void run() {
			try {
				while (true) {
					//läser input från servern
					serverInputLine = serverInput.readLine();
					
					if (serverInputLine.equals(null)) {
						break;
					}
					//skriver ut meddelande från servern
					System.out.println(serverInputLine);
				}
			} catch (Exception e) {
				System.out.println("Error: Server disconnected");
			}
		}
	}
	
	/**
	 * @param args
	 * args[0] - ip-adressen till servern.
	 * args[1] - ett användarnamn till användaren enligt labbspecifikationen.
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		new Client(args[0], args[1]);
		
		

	}

}