import java.io.*;
import java.net.*;
import java.util.StringTokenizer; 
import java.util.Random;
import java.util.ArrayList;


public class threadServer{

	static int clients = 0;
	static int sessions = 0;
	static int numguess = 0;
	static boolean cookie = false;
	static boolean client = false;
	static boolean reset = false;
	static boolean correct = false;
	static ArrayList<Integer> guesses = new ArrayList<Integer>();
	static int lowguess = 1, highguess = 100;
	static int randomNum=0;

	//################################ MAIN #############################################


	//Att g�ra:

	// 1. Se till att flera klienter kan gissa samtidigt.
	// 2. strukturera main mer?
	// 3. BOnusuppgiften?

	//HAR FIXAT
	//RESET s� nytt nummer genereras n�r man gissat r�tt
	//delat upp  main i mindre metoder
	//lite bugfix av olika slag



	public static void main(String[] args) throws Exception{
		new threadServer();
	}





	//######################################### METHODS #############################################################

	public threadServer() throws IOException{

		ServerSocket ss = new ServerSocket(8080);
		//Genererar nummer f�rsta omg�ngen
		randomNum = generateRandomNum();
		System.out.println(randomNum);

		//while(true){

		if(reset){
			//Genererar nummer alla nya omg�ngar efter f�rsta
			randomNum = generateRandomNum();
			System.out.println(randomNum);
			reset=false;
		}

		System.out.println("Skapar Serversocket");

		System.out.println("V�ntar p� klient...");
		Socket s = null;
		
		newClients c = new newClients(s,ss);
		c.run();
//		Thread t = new Thread(new newClients(s,ss));
//		t.start();
		System.out.println("Klient �r ansluten");

		//}

	}



	private synchronized static String cookieStatus(BufferedReader request, String str) throws IOException{

		String value = "";
		while((str = request.readLine()) != null && str.length() > 0) {
			System.out.println(str);
			String[] tmp = str.split(":");
			//Kollar om kaka finns
			if (tmp[0].equals("Cookie")) {
				client = true;
				//Plockar ut kaka infon
				value = tmp[1];
				tmp = tmp[1].split(";");
				//Kollar vilka typer av kakor som finns
				if (tmp.length > 1) {
					cookie = true;
				} else {
					cookie = false;
				}
			} else {
				client = false;
			}
		}
		//Baserat p� unders�kningen ovan skrivs det ut information om kakans status
		if (cookie) {
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

	private synchronized static void checkTypeOfCookie(PrintStream response, boolean[] interval, String value, int guess) {
		//Kollar vilken typ av kak-info som finns och s�tter kakan d�refter
		if (!client) {
			//ClientId existerar inte (ny klient) s� all typ av kak-info s�tts
			response.println("Set-Cookie: clientId="+setClientId()+"; expires=Wednesday,31-Dec-15 21:00:00 GMT");
			response.println("Set-Cookie: sessionId="+setSessionId() + " NumGuess=0 LowGuess=1 Highguess=100;");
		} else if (!cookie) {
			//clientId existerar s� endast kompletterande info s�tts om den inte finns
			response.println("Set-Cookie: sessionId="+setSessionId() + " NumGuess=0 LowGuess=1 Highguess=100;");
		} 
		if(cookie && client){
			//b�de clientId och �vrig kak-info finns redan s� �vrig kak-info blir reset till original
			response.println(setCookie(value, interval[0], interval[1], guess));
		}
	}

	private synchronized static void writeHTML(String requestedDocument, PrintStream response, int guess, boolean[] interval, String answer) throws IOException{
		//skriver HTML koden som styr det som syns f�r anv�ndaren
		File f = new File("."+requestedDocument);
		FileInputStream infil = new FileInputStream(f);
		byte[] b = new byte[1024];
		while( infil.available() > 0){
			response.write(b,0,infil.read(b));
			response.println(answer);


		}
	}


	private synchronized static String setAnswer(int low, int high, boolean corr, boolean tooHigh, boolean tooLow, int guess) {
		//genererar en str�ng med HTML som inneh�ller korrekt feedback till anv�ndaren beroende p� vad den gissat
		if (tooHigh) {
			return "<p>Too high! Guess again between "+low+" and "+high+"</p>";
		}
		if(low==1 && high==100) {
			if(guess==0){
				return "<p>Guess between "+low+" and " + high + "</p>";
			}
			if(correct){
				return "<p>Correct! Press Enter for a new guess.</p>";
			}

		}	
		if(tooLow) {
			return "<p>Too low! Guess again between "+low+" and " + high + "</p>";
		}

		if(guess==randomNum){
			return "<p>Correct! Press Enter for a new guess.</p>";
		}

		else{
			return "<p>Guess again between "+low+" and " + high + "</p>";
		}
	}


	private static String setCookie(String value, boolean low, boolean high, int guess) {
		//S�tter kakan 
		int lowGuess = 1, numGuess = 0, highGuess= 100, clientId = 0, sessionId = 0;
		//L�ser in existerande kak-info
		String[] cookies = value.split(";");
		String cookie1 = cookies[0];
		clientId = Integer.parseInt(cookie1.split("=")[1]);

		String cookie2 = cookies[1];
		String[] Guesses = cookie2.split(" ");
		sessionId = Integer.parseInt(Guesses[1].split("=")[1]);
		numGuess = Integer.parseInt(Guesses[2].split("=")[1]);
		lowGuess = Integer.parseInt(Guesses[3].split("=")[1]);
		highGuess = Integer.parseInt(Guesses[4].split("=")[1]);



		//uppdaterar kak-info
		if (low) {
			lowGuess = guess;
		} 
		if (high) {
			highGuess = guess;
		} 
		if(guess==randomNum){
			//om anv�ndaren gissat r�tt, leder till ny spelomg�ng
			reset = true;

		}

		return getStringCookie(numguess, lowguess, highguess, sessionId);
	}

	private synchronized static int setClientId() {
		clients++;
		return clients;
	}

	private synchronized static int setSessionId() {
		sessions++;
		return sessions;
	}

	private synchronized static String getStringCookie(int numguess, int low, int high, int sessions){
		//SKapar str�ngrepresentation av kakan
		String cookieContent ="";
		if (reset) {
			//om spelet b�rjar p� nytt, allt s�tts till ursprungsv�rden
			System.out.println("============= RESET ==========");
			correct=false;
			cookieContent+= "Set-Cookie: sessionId="+setSessionId()+" NumGuess="+0+" LowGuess="+1+" HighGuess="+100;

		} else {
			//aktuella attributv�rden
			cookieContent+= "Set-Cookie: sessionId="+setSessionId()+" NumGuess="+numguess+" LowGuess="+low+" HighGuess="+high;
		}

		return cookieContent;
	}

	private synchronized static int generateRandomNum(){
		//genererar ett nytt random nummer 
		Random randomGenerator = new Random();
		int randomNum = randomGenerator.nextInt(100)+1;
		return randomNum;
	}

	private synchronized static boolean[] guessControl(int guess) {
		//kontrollerar anv�ndarens gissningsinput
		boolean tooHigh = false, tooLow = false;

		//om gissat r�tt, allt resets
		if(guess> 0 && guess==randomNum){
			correct=true;
			lowguess=1;
			highguess=100;
			guess=0;
			numguess=0;
			System.out.println("Correct answer!");

		}

		//gissat f�r l�gt
		if(guess> 0 && guess < randomNum){
			tooLow = true;
			numguess++;
			System.out.print("Too low, guess a new number between "+lowguess+" and " +highguess );
			if (lowguess <guess) {
				lowguess = guess;
			} else {

				//Användaren gissade lägre än innan. why.
			}
		}
		//gissat f�r h�gt
		if(guess > randomNum){
			numguess++;
			tooHigh = true;
			System.out.print("Too high, guess a new number between " + lowguess + " and " + highguess);
			if (highguess > guess) {
				highguess = guess;
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

	//######################### CLASSES ###############################

	private class ClientSession implements Runnable {

		BufferedReader request;
		String str;
		int guess=0;
		StringTokenizer tokens;
		String requestedDocument; 

		private int id;
		private Socket s;

		public ClientSession(Socket s, int sessionId) {
			this.s = s;
			this.id = setSessionId();
		}

		public void run() {
			try {
				//input
				request = new BufferedReader(new InputStreamReader(s.getInputStream()));
				str = request.readLine();
				System.out.println("str" + str);
				tokens = new StringTokenizer(str," ?");
				String get = tokens.nextToken(); // Ordet GET
				System.out.println("Get " + get);
				requestedDocument = tokens.nextToken();
				System.out.println("DOC" + requestedDocument);
				try{
				guess = Integer.valueOf(tokens.nextToken().split("=")[1]);
				} catch (Exception e){
					System.err.println("GUESS ERROR:"+e);
				}
				System.out.println(guess);
				
				//Output
				PrintStream response = new PrintStream(s.getOutputStream());
				serverInfo(response, requestedDocument);
				System.out.println("F�rfr�gan klar.");

				//unders�ker vilken typ av kakor som existerar och returnerar en str�ng med eventuell kakas inneh�ll
				String value = cookieStatus(request, str);
				//kontrollerar gissningsinput, returnerar lista med booleans
				boolean[] interval = guessControl(guess);
				//Kollar vilken typ av kak-info som finns och s�tter kakan d�refter samt tar h�nsyn till gissningen
				checkTypeOfCookie(response, interval, value, guess);	
				//						response.println()
				String answer = setAnswer(lowguess, highguess, correct, interval[1], interval[0], guess);
				//skriver r�tt answer till HTML filen
				writeHTML(requestedDocument, response, guess, interval, answer);



				//stänger socket
				s.shutdownInput();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
	private class newClients implements Runnable {
		//Skapar klienter
		Socket s;
		ServerSocket ss;
		int sessionId;

		public newClients(Socket s, ServerSocket ss) {
			this.s = s;
			this.ss = ss;
		}

		public void run() {
			try {
				//Lyssnar efter nya sessioner
				while((s=ss.accept()) != null){
					Thread t = new Thread(new ClientSession(s, sessionId));
					t.start();
					s=null;
				}

			} catch (Exception e) {
				System.err.println("error in newClients run: " + e);
			}
		}
	}

}