import java.io.*;
import java.net.*;
import java.util.StringTokenizer; 
import java.util.Random;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpServer{
	
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

	public static void main(String[] args) throws IOException{

		//Att göra:

		// Se till att flera klienter kan gissa samtidigt.
		// uppdatera numguess
		// strukturera main mer

		//HAR FIXAT
		//RESET så nytt nummer genereras när man gissat rätt
		//delat upp lite av main i mindre metoder
		
		randomNum = generateRandomNum();
		System.out.println(randomNum);
	
		
		System.out.println("Skapar Serversocket");
		ServerSocket ss = new ServerSocket(8080);
		
		while(true){
			if(reset){
				randomNum = generateRandomNum();
				System.out.println(randomNum);
				reset=false;
			}
			
			System.out.println("Väntar på klient...");
			Socket s = ss.accept();
			System.out.println("Klient är ansluten");
			BufferedReader request = new BufferedReader(new InputStreamReader(s.getInputStream()));
			
			String str = request.readLine();
			//DETTA NEDAN FUNKAR INTE, fick nullpointer iaf
//			if (str.equals(null)) {
//				continue;
//			}
			
			StringTokenizer tokens = new StringTokenizer(str," ?");
			tokens.nextToken(); // Ordet GET
			
			String requestedDocument = tokens.nextToken();
			int guess = 0;
			try {
				guess = Integer.valueOf(tokens.nextToken().split("=")[1]);
			} catch (Exception e) {
				System.err.println(e);
			}

			System.out.println("Guess: " + guess);
			System.out.println(numguess);
			//skriver ut info om host
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
					//Kollar om kaka finns
					if (tmp.length > 1) {
						cookie = true;
					} else {
						cookie = false;
					}
				} else {
					client = false;
				}
			}
			
			
			if (cookie) {
				System.out.println("The cookie is set. This user should not get a new id.");
				System.out.println("value of cookie: " + value);
			}else {
				System.out.println("Could not read cookie.");
			}
			System.out.println("Förfrågan klar.");
			s.shutdownInput();

			
			//Output
			PrintStream response = new PrintStream(s.getOutputStream());
			
			
			
			
			
			response.println("HTTP/1.1 200 OK");
			response.println("Server : Slask 0.1 Beta");
			
			if(requestedDocument.indexOf(".html") != -1)
				response.println("Content-Type: text/html");
			
			if(requestedDocument.indexOf(".gif") != -1)
				response.println("Content-Type: image/gif");
			
			
			//kontrollerar gissningsinput, returnerar lista med booleans
			boolean[] interval = guessControl(guess);
			//Kollar vilken typ av kak-info som finns och sätter kakan därefter
			if (!client) {
				//ClientId existerar inte (ny klient) så all typ av kak-info sätts
				response.println("Set-Cookie: clientId="+setClientId()+"; expires=Wednesday,31-Dec-15 21:00:00 GMT");
				response.println("Set-Cookie: sessionId="+setSessionId() + " NumGuess=0 LowGuess=1 Highguess=100;");
			} else if (!cookie) {
				//clientId existerar så endast kompletterande info sätts om den inte finns
				response.println("Set-Cookie: sessionId="+setSessionId() + " NumGuess=0 LowGuess=1 Highguess=100;");
			} 
			if(cookie && client){
				//både clientId och övrig kak-info finns redan så övrig kak-info blir reset till original
				response.println(setCookie(value, interval[0], interval[1], guess));
			}
			
			response.println();

			
//			################## WRITES TO HTML ##########################################
			String answer = setAnswer(lowguess, highguess, correct, interval[1], interval[0], guess);
			writeHTML(requestedDocument, response, guess, interval, answer);
			

			s.shutdownOutput();
			s.close();
		}
	}
	
	private synchronized static void writeHTML(String requestedDocument, PrintStream response, int guess, boolean[] interval, String answer) throws IOException{
		//skriver HTML koden som styr det som syns för användaren
		File f = new File("."+requestedDocument);
		FileInputStream infil = new FileInputStream(f);
		byte[] b = new byte[1024];
		while( infil.available() > 0){
			response.write(b,0,infil.read(b));
			response.println(answer);


		}
	}


	private synchronized static String setAnswer(int low, int high, boolean corr, boolean tooHigh, boolean tooLow, int guess) {
		//genererar en sträng med HTML som innehåller korrekt feedback till användaren beroende på vad den gissat
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
		
		else {
			return "<p>Correct! Press Enter for a new guess.</p>";
			}
			
		}
	

	private static String setCookie(String value, boolean low, boolean high, int guess) {
		//Sätter kakan 
		int lowGuess = 1, numGuess = 0, highGuess= 100, clientId = 0, sessionId = 0;
		//Läser in existerande kak-info
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
		} else if (high) {
			highGuess = guess;
		} 
		if(guess==randomNum){
			//om användaren gissat rätt, leder till ny spelomgång
			reset = true;

		}
		
		return getStringCookie(numguess, lowGuess, highGuess, sessionId);
	}
	
	private synchronized static int setClientId() {
		clients++;
		return clients;
	}
	
	private synchronized static int setSessionId() {
		sessions++;
		return sessions;
	}
	
	private synchronized static String getStringCookie(int numguess, int lowguess, int highguess, int sessions){
		//SKapar strängrepresentation av kakan
		String cookieContent ="";
		if (reset) {
			//om spelet börjar på nytt, allt sätts till ursprungsvärden
			System.out.println("============= RESET ==========");
			correct=false;
			cookieContent+= "Set-Cookie: sessionId="+setSessionId()+" NumGuess="+0+" LowGuess="+1+" HighGuess="+100;
			
		} else {
			//aktuella attributvärden
			cookieContent+= "Set-Cookie: sessionId="+setSessionId()+" NumGuess="+numguess+" LowGuess="+lowguess+" HighGuess="+highguess;
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
		//kontrollerar användarens gissningsinput
		boolean tooHigh = false, tooLow = false;
		
		//om gissat rätt, allt resets
		if(guess> 0 && guess==randomNum){
			correct=true;
			lowguess=1;
			highguess=100;
			guess=0;
			numguess=0;
			System.out.println("Correct answer!");

		}
		
		//gissat för lågt
		if(guess> 0 && guess < randomNum){
			tooLow = true;
			numguess++;
			System.out.print("Too low, guess a new number between "+lowguess+" and " +highguess );
			if (lowguess <guess) {
				lowguess = guess;
			} else {
				//AnvÃ¤ndaren gissade lÃ¤gre Ã¤n innan. why.
			}
		}
		//gissat för högt
		if(guess > randomNum){
			numguess++;
			tooHigh = true;
			System.out.print("Too high, guess a new number between " + lowguess + " and " + highguess);
			if (highguess > guess) {
				highguess = guess;
			} else {
				//AnvÃ¤ndaren gissade hÃ¶gre Ã¤n tidigare.
			}
		}
		
		//returnerar booleans i form av tooLow och tooHigh
		boolean[] interval = new boolean[2];
		interval[0]=tooLow;
		interval[1]=tooHigh;
		
		return interval;
	}
	
}