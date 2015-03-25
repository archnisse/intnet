import java.io.*;
import java.net.*;
import java.util.StringTokenizer; 
import java.util.Random;
import java.util.ArrayList;


public class HttpServer{
	
	static int clients = 0;
	static int sessions = 0;
//	static int numguess = 0;
//	static boolean cookie = false;
//	static boolean client = false;
	static boolean reset = false;
//	static boolean correct = false;
//	//static ArrayList<Integer> guesses = new ArrayList<Integer>();
//	static int lowguess = 1, highguess = 100;
//	static int randomNum=0;
	
	//################################ MAIN #############################################
	
	
	//Att göra:

	// 1. Se till att flera klienter kan gissa samtidigt.
	// 1.1 Se till att de olika klienterna gissar på olika nummer. Lagra svaret i kakan?
	// 2. Strukturera upp koden lite, nog lite som inte används nu.
	// 3. BOnusuppgiften?

	//HAR FIXAT
	//RESET så nytt nummer genereras när man gissat rätt
	//delat upp  main i mindre metoder
	//lite bugfix av olika slag
	
	/*
	 * Nisse har fixat 20/2:
	 * 
	 * Tråd agerar nu som klient, det som behövs för att fler
	 * klienter ska kunna gissa är att varje klient typ har koll på sitt egna
	 * värde som den letar efter.
	 * 
	 * Har skrivit om några metoder för att strukturera lite bättre,
	 * introducerat en klass för alla klientens värden så att de hålls koll
	 * på och kan uppdateras av metoderna.
	 */
	
	

	public static void main(String[] args) throws IOException{
		
		//Genererar nummer första omgången
		//randomNum = generateRandomNum();
		//System.out.println(randomNum);
	
		
		System.out.println("Skapar Serversocket");
		ServerSocket ss = new ServerSocket(8080);
		
		while(true){
			if(reset){
				reset=false;
			}
			
			System.out.println("Väntar på klient...");
			Socket s = ss.accept();
			System.out.println("Klient är ansluten");
			
			System.out.println("s.getInetAddress: " + s.getInetAddress());
			
			Thread t = new Thread(new Client(s));
			t.start();
			s = null;
			

		}
	}
	
	
	
	
	
	//######################################### METHODS #############################################################
	
	private synchronized static String cookieStatus(BufferedReader request, String str, Cookie cookie) throws IOException{
		
		String value = "";
		while((str = request.readLine()) != null && str.length() > 0) {
			System.out.println("str: " + str);
			String[] tmp = str.split(":");
			//Kollar om kaka finns
			if (tmp[0].equals("Cookie")) {
				cookie.client = true;
				System.out.println("Client id is set.");
				//Plockar ut kaka infon
				value = tmp[1];
				tmp = tmp[1].split(";");
				//Kollar vilka typer av kakor som finns
				if (tmp.length > 1) {
					cookie.cookie = true;
					System.out.println("Entire cookie is set");
					System.out.println("client: " + cookie.client);
				} else {
					cookie.cookie = false;
					System.out.println("cookie set to false in cookieStatus.");
				}
			} /*else {
				cookie.client = false;
				System.out.println("client set to false in cookieStatus.");
			}*/
		}
		//Baserat på undersökningen ovan skrivs det ut information om kakans status
		if (cookie.cookie) {
			System.out.println("The cookie is set. This user should not get a new id.");
			System.out.println("value of cookie: " + value);
		}else {
			System.out.println("Could not read cookie.");
		}
		
		return value;
	}
	
	private synchronized static void serverInfo(PrintStream response, String requestedDocument){

		response.println("HTTP/1.1 200 OK");
		response.println("Server : Mjau 0.1 Beta");
		
		if(requestedDocument.indexOf(".html") != -1)
			response.println("Content-Type: text/html");
		
		if(requestedDocument.indexOf(".gif") != -1)
			response.println("Content-Type: image/gif");
		
	}
	
	private synchronized static boolean checkTypeOfCookie(PrintStream response, Boolean client, Boolean cookie) {
		if (!client) {
			//ClientId existerar inte (ny klient) så all typ av kak-info sätts
			response.println("Set-Cookie: clientId="+setClientId()+"; expires=Wednesday,31-Dec-15 21:00:00 GMT");
			response.println("Set-Cookie: sessionId="+setSessionId() + " NumGuess=0 LowGuess=1 Highguess=100 goal="+generateRandomNum()+";");
			return false;
		} else if (!cookie) {
			//clientId existerar så endast kompletterande info sätts om den inte finns
			response.println("Set-Cookie: sessionId="+setSessionId() + " NumGuess=0 LowGuess=1 Highguess=100 goal="+generateRandomNum()+";");
			return false;
		} 
		return true;
	}
	
	/*private synchronized static void checkTypeOfCookie(PrintStream response, boolean[] interval, String value, int guess, Boolean client, Boolean cookie, Integer highguess, Integer lowguess, Integer numguess, Integer sessionId) {
		//Kollar vilken typ av kak-info som finns och sätter kakan därefter
		if (!client) {
			//ClientId existerar inte (ny klient) så all typ av kak-info sätts
			response.println("Set-Cookie: clientId="+setClientId()+"; expires=Wednesday,31-Dec-15 21:00:00 GMT");
			response.println("Set-Cookie: sessionId="+setSessionId() + " NumGuess=0 LowGuess=1 Highguess=100 goal="+generateRandomNum()+";");
		} else if (!cookie) {
			//clientId existerar så endast kompletterande info sätts om den inte finns
			response.println("Set-Cookie: sessionId="+setSessionId() + " NumGuess=0 LowGuess=1 Highguess=100 goal="+generateRandomNum()+";");
		} 
		if(cookie && client){
			//både clientId och övrig kak-info finns redan så övrig kak-info blir reset till original
			response.println(setCookie(value, interval[0], interval[1], guess, numguess, highguess, lowguess, sessionId));
		}
	}*/
	
	private synchronized static void writeHTML(String requestedDocument, PrintStream response, int guess, boolean[] interval, String answer) throws IOException{
		//skriver HTML koden som styr det som syns för användaren
		File f = new File("."+requestedDocument);
		FileInputStream infil = new FileInputStream(f);
		byte[] b = new byte[1024];
		while( infil.available() > 0){
			response.write(b,0,infil.read(b));
			response.println(answer);


		}
		infil.close();
	}


	private synchronized static String setAnswer(Cookie cookie, boolean tooHigh, boolean tooLow, int guess) {
		//genererar en sträng med HTML som innehåller korrekt feedback till användaren beroende på vad den gissat
		if (tooHigh) {
			return "<p>Too high! Guess again between "+cookie.lowguess+" and "+cookie.highguess+"</p>";
		}
		if(cookie.lowguess==1 && cookie.highguess==100) {
			if(guess==0){
				return "<p>Guess between "+cookie.lowguess+" and " + cookie.highguess + "</p>";
			}
			if(cookie.correct){
				return "<p>Correct! Press Enter for a new guess.</p>";
			}
		
		}	
		if(tooLow) {
			return "<p>Too low! Guess again between "+cookie.lowguess+" and " + cookie.highguess + "</p>";
		}
		
		if(guess==cookie.goal){
			return "<p>Correct! Press Enter for a new guess.</p>";
			}
		
		else{
			return "<p>Guess again between "+ cookie.lowguess +" and " + cookie.highguess + "</p>";
		}
	}
	
	
	private static void readCookie(String value, Cookie cookie) {
		String[] cookies = value.split(";");
		String cookie1 = cookies[0];
		cookie.clientId = Integer.parseInt(cookie1.split("=")[1]);
		
		String cookie2 = cookies[1];
		String[] Guesses = cookie2.split(" ");
		cookie.sessionId = Integer.parseInt(Guesses[1].split("=")[1]);
		cookie.numguess = Integer.parseInt(Guesses[2].split("=")[1]);
		cookie.lowguess = Integer.parseInt(Guesses[3].split("=")[1]);
		cookie.highguess = Integer.parseInt(Guesses[4].split("=")[1]);
		cookie.goal = Integer.parseInt(Guesses[5].split("=")[1]);
	}

	
	private synchronized static int setClientId() {
		clients++;
		return clients;
	}
	
	private synchronized static int setSessionId() {
		sessions++;
		return sessions;
	}
	
	private synchronized static String getStringCookie(Cookie cookie){
		//SKapar strängrepresentation av kakan
		String cookieContent ="";
		if (cookie.correct) {
			//om spelet börjar på nytt, allt sätts till ursprungsvärden
			System.out.println("============= RESET ==========");
			//correct=false;
			cookieContent+= "Set-Cookie: sessionId="+cookie.sessionId+" NumGuess="+0+" LowGuess="+1+" HighGuess="+100+" goal="+generateRandomNum();
			
		} else {
			//aktuella attributvärden
			cookieContent+= "Set-Cookie: sessionId="+cookie.sessionId+" NumGuess="+cookie.numguess+" LowGuess="+cookie.lowguess+" HighGuess="+cookie.highguess+" goal="+cookie.goal;
		}
		
		return cookieContent;
	}
	
	private synchronized static int generateRandomNum(){
		//genererar ett nytt random nummer 
		Random randomGenerator = new Random();
		int randomNum = randomGenerator.nextInt(100)+1;
		return randomNum;
	}
	
	private synchronized static boolean[] guessControl(int guess, Cookie cookie) {
		//kontrollerar användarens gissningsinput
		boolean tooHigh = false, tooLow = false;
		
		//om gissat rätt, allt resets
		if(guess> 0 && guess==cookie.goal){
			cookie.correct=true;
			cookie.lowguess=1;
			cookie.highguess=100;
			guess=0;
			cookie.numguess=0;
			cookie.goal = generateRandomNum();
			System.out.println("Correct answer!");

		}
		
		//gissat för lågt
		if(guess> 0 && guess < cookie.goal){
			tooLow = true;
			cookie.numguess++;
			System.out.print("Too low, guess a new number between "+cookie.lowguess+" and " +cookie.highguess );
			if (cookie.lowguess < guess) {
				cookie.lowguess = guess;
			} else {
				
				//Användaren gissade lägre än innan. why.
			}
		}
		//gissat för högt
		if(guess > cookie.goal){
			cookie.numguess++;
			tooHigh = true;
			System.out.print("Too high, guess a new number between " + cookie.lowguess + " and " + cookie.highguess);
			if (cookie.highguess > guess) {
				cookie.highguess = guess;
			} else {
				
				System.out.println("--------------NEIN----------------");
				//Användaren gissade högre än tidigare.
			}
		}
		
		//returnerar booleans i form av tooLow och tooHigh
		boolean[] interval = new boolean[2];
		interval[0]=tooLow;
		interval[1]=tooHigh;
		
		return interval;
	}
	
	private static class Cookie {
		
		int numguess, highguess, lowguess, clientId, sessionId;
		boolean cookie, client, correct;
		
		int goal;
		
		Cookie() {
		}
	}
	
	private static class Client implements Runnable {
		
		Cookie cookie;
		
		Socket s;
		
		public Client(Socket s) {
			this.s = s;
			cookie = new Cookie();
		}
		
		public void run() {
			try {
				BufferedReader request = new BufferedReader(new InputStreamReader(s.getInputStream()));
				String str = request.readLine();
				
				int guess = -1;
				
				StringTokenizer tokens = new StringTokenizer(str," ?");
				tokens.nextToken(); // Ordet GET
				System.out.println("str: " + str);
				String requestedDocument = tokens.nextToken();
				System.out.println(requestedDocument);
				String tmp = tokens.nextToken(); 
				if (!requestedDocument.equals("/html.html")) {
					//Do nothing with this request.
					System.out.println("bad request, dump.");
					s.shutdownInput();
					s.close();
					return;
				} else {
					System.out.println("html.html, good request.");
				}
				try {
					guess = Integer.valueOf(tmp.split("=")[1]);
					System.out.println("guess: " + guess);
				} catch (Exception e) {
					System.err.println("erroret: " + e);
				}
				System.out.println("guess: " + guess);
	
				//System.out.println("GuessguessControl: " + guess);
				//System.out.println(numguess);
				
				//Output
				PrintStream response = new PrintStream(s.getOutputStream());
				serverInfo(response, requestedDocument);
				System.out.println("Förfrågan klar.");
				s.shutdownInput();
	
				
		
				//undersöker vilken typ av kakor som existerar och returnerar en sträng med eventuell kakas innehåll
				String value = cookieStatus(request, str, cookie);
				//kontrollerar gissningsinput, returnerar lista med booleans
				System.out.println("Cookie: " + cookie.cookie);
				System.out.println("Client: " + cookie.client);
				
				// Initialisering av variablerna för gissningen
				if (cookie.cookie) {
					readCookie(value, cookie);
				} else {
					//New client with no cookie. Set default values for numguess, highguess and lowguess and get client and session id.
					cookie.numguess = 0;
					cookie.highguess = 100;
					cookie.correct = false;
					cookie.lowguess = 1;
					cookie.clientId = setClientId();
					cookie.sessionId = setSessionId();
					cookie.goal = generateRandomNum();
				}
				System.out.println("guess later: " + guess);
				boolean[] interval = guessControl(guess, cookie);
				//Kollar vilken typ av kak-info som finns och sätter kakan därefter samt tar hänsyn till gissningen
				
				if (checkTypeOfCookie(response, cookie.client, cookie.cookie)) {
					//Den hade kakor och denna ska uppdateras.
					response.println(getStringCookie(cookie));
				}
				
				//checkTypeOfCookie(response, interval, value, guess, client, cookie, highguess, lowguess, numguess, sessionId);	
	//			response.println()
				String answer = setAnswer(cookie, interval[1], interval[0], guess);
				//skriver rätt answer till HTML filen
				writeHTML(requestedDocument, response, guess, interval, answer);
			} catch (Exception e) {
				System.err.println(e);
			}
			try {
				s.shutdownOutput();
				s.close();
			} catch (Exception e) {
				System.err.println(e);
			}
		}
	}
	
}



/*
//BufferedReader request = new BufferedReader(new InputStreamReader(s.getInputStream()));
//String str = request.readLine();
//int guess = 0;
//
//StringTokenizer tokens = new StringTokenizer(str," ?");
//tokens.nextToken(); // Ordet GET
//String requestedDocument = tokens.nextToken();
//try {
//	guess = Integer.valueOf(tokens.nextToken().split("=")[1]);
//} catch (Exception e) {
//	System.err.println(e);
//}
//
////System.out.println("Guess: " + guess);
////System.out.println(numguess);
//
////Output
//PrintStream response = new PrintStream(s.getOutputStream());
//serverInfo(response, requestedDocument);
//System.out.println("Förfrågan klar.");
//s.shutdownInput();
//
//
//
////undersöker vilken typ av kakor som existerar och returnerar en sträng med eventuell kakas innehåll
//String value = cookieStatus(request, str);
////kontrollerar gissningsinput, returnerar lista med booleans
//boolean[] interval = guessControl(guess);
////Kollar vilken typ av kak-info som finns och sätter kakan därefter samt tar hänsyn till gissningen
//checkTypeOfCookie(response, interval, value, guess);	
////response.println()
//String answer = setAnswer(lowguess, highguess, correct, interval[1], interval[0], guess);
////skriver rätt answer till HTML filen
//writeHTML(requestedDocument, response, guess, interval, answer);
//
//
//s.shutdownOutput();
//s.close();*/