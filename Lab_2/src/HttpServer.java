import java.io.*;
import java.net.*;
import java.util.StringTokenizer; 
import java.util.Random;
import java.util.ArrayList;

public class HttpServer{
	
	static int sessions = 0;
	static boolean cookie = false, correct = false;
	static ArrayList<Integer> guesses = new ArrayList<Integer>();
	static int lowguess = 1, highguess = 100;
	static String htmlAnswer = "";

	public static void main(String[] args) throws IOException{
		
		
		
		
		
		
		
		//Att göra:
		//Spara klients Session ID
		//Hantera/ta ut gissning
		//kontrollera gissning
		//metod för att sätta ihop html kod - se längst ner - plus att hantera intervall
		//lösa sparning av gissningar
		// Se till att flera klienter kan gissa samtidigt.
		
		
		
		
		//sätt svar till ett random nummer
		Random randomGenerator = new Random();
		int randomNum = randomGenerator.nextInt(100)+1;
		System.out.println(randomNum);
	
		
		System.out.println("Skapar Serversocket");
		ServerSocket ss = new ServerSocket(8080);
		
		while(true){
			System.out.println("Väntar på klient...");
			Socket s = ss.accept();
			System.out.println("Klient är ansluten");
			BufferedReader request = new BufferedReader(new InputStreamReader(s.getInputStream()));
			
			String str = request.readLine();
			//printar request
			System.out.println(str);
			
			StringTokenizer tokens = new StringTokenizer(str," ?");
			tokens.nextToken(); // Ordet GET
			
			String requestedDocument = tokens.nextToken();
			int guess = 0;
			try {
				guess = Integer.parseInt(tokens.nextToken().split("=")[1]);
			} catch (Exception e) {
				System.err.println(e);
			}
			//char[] charGuess = guess.toCharArray();
			//int guessed = checkGuess(charGuess);
			//guess = guess.substring(6,guess.length());
			System.out.println("guess: " + guess);
			//System.out.println("guess length: " + guess.length() );
			
			int interval = 0;
//			boolean tooHigh = false, tooLow = false;
//			if(guess.length() > 0 && Integer.valueOf(guess)==randomNum){
//				//System.out.println("Correct answer!");
//				//htmlAnswer = "Correct answer!";
//				correct = true;
//			}
//			if(guess.length() > 0 && Integer.valueOf(guess) < randomNum){
//				interval=0;
//				tooLow = true;
//				//System.out.print("Too low, guess a new number between "+guess+" and " + String.valueOf(interval));
//				if (lowguess < Integer.parseInt(guess)) {
//					lowguess = Integer.parseInt(guess);
//				} else {
//					//Användaren gissade lägre än innan. why.
//				}
//			}
//			if(Integer.valueOf(guess) > randomNum){
//				interval=0;
//				tooHigh = true;
//				//System.out.print("Too high, guess a new number between " + guess + " and " + String.valueOf(interval));
//				if (highguess > Integer.parseInt(guess)) {
//					highguess = Integer.parseInt(guess);
//				} else {
//					//Användaren gissade högre än tidigare.
//				}
//			}
//			String answer = setAnswer(lowguess, highguess, correct, tooHigh, tooLow);
//			
			//skriver ut info om host
			int value = 0;
			while( (str = request.readLine()) != null && str.length() > 0) {
				System.out.println(str);
				String tmp = str.substring(0, 6);
				if (tmp.equals("Cookie:")) {
					tmp = str.substring(7);
					String[] tmplist = tmp.split("=");
					String name = tmplist[0];
					value = Integer.parseInt(tmplist[1]);
					cookie = true;
				}
			}
			if (cookie) {
				System.out.println("The cookie is set. This user should not get a new id.");
				System.out.println("value: " + value);
			}
			System.out.println("Förfrågan klar.");
			s.shutdownInput();

			PrintStream response = new PrintStream(s.getOutputStream());
			response.println("HTTP/1.1 200 OK");
			response.println("Server : Slask 0.1 Beta");
			
			if(requestedDocument.indexOf(".html") != -1)
				response.println("Content-Type: text/html");
			
			if(requestedDocument.indexOf(".gif") != -1)
				response.println("Content-Type: image/gif");

			String clientId = null;
			if (!cookie) {
				response.println("Set-Cookie: clientId="+setSessionId()+"; expires=Wednesday,31-Dec-15 21:00:00 GMT");
			}

			response.println();
			File f = new File("."+requestedDocument);
			FileInputStream infil = new FileInputStream(f);
			byte[] b = new byte[1024];
			while( infil.available() > 0){
				response.write(b,0,infil.read(b));
				String debug = new String(b);
				if (debug.equals("<split here>")) {
					
					if (guess > 0) {
						//response.println(answer);
						System.out.println("Good for you.");
					} else {
						// Här behövs ingen html?
					}
				}
				//System.out.println(debug);
			}

			s.shutdownOutput();
			s.close();
		}
	}
	
	private synchronized static String setAnswer(int low, int high, boolean corr, boolean tooHigh, boolean tooLow) {
		if (corr) { // Användaren har gissat korrekt.
			return "Correct!";
		} else {
			if (tooHigh) {
				return "Too high! Guess again between "+low+" and "+high+"";
			}
			return "Too low! Guess again between "+low+" and " + high + "";
		}
	}
	
	private synchronized static int setSessionId() {
		sessions++;
		return sessions;
	}
	
//private class ClientHandler implements Runnable {
//		
//		BufferedReader indata;
//		Socket s;
//		int id;
//		ArrayList<Integer> guesses;
//		
//		public ClientHandler(Socket s, int id) {
//			this.s = s;
//			this.id = id;
//		
//
//			try {
//				//läser in det som klienten skriver
//				indata = new BufferedReader(new InputStreamReader(s.getInputStream()));
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//		
//		public void run() {
//			try {
//				while( (text = indata.readLine()) != null){
//					//lägger medddelandena i en kö
//					broadcast.addMessage(text);
//					//skriver ut klientens meddelande till alla andra anslutna klienter
//					broadcast.printBroadcast(id);
//				}
//				// Körs när klienten kopplar ifrån
//				System.out.println("Client nr " + id + " is now disconnected.");
//				//tar bort klient
//				broadcast.removeClient(id);
//				System.out.println("The number of active clients are now: " + broadcast.getActiveClients());
//				//stänger socket
//				s.shutdownInput();
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//		
//	}
	
}