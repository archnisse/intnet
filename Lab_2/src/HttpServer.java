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
	static	boolean correct = false;
	static ArrayList<Integer> guesses = new ArrayList<Integer>();
	static int lowguess = 1, highguess = 100;
	static String htmlAnswer = "";

	public static void main(String[] args) throws IOException{
		
		
		
		
		
		
		
		//Att göra:

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
				guess = Integer.valueOf(tokens.nextToken().split("=")[1]);
			} catch (Exception e) {
				System.err.println(e);
			}

			System.out.println("Guess: " + guess);
			
			
			
			//skriver ut info om host
			String value = "";
			while( (str = request.readLine()) != null && str.length() > 0) {
				System.out.println(str);
				String[] tmp = str.split(":");
				if (tmp[0].equals("Cookie")) {
					client = true;
					value = tmp[1];
					tmp = tmp[1].split(";");
					if (tmp.length > 1) {
						cookie = true;
					} else {
						cookie = false;
					}
				} else {
					client = false;
				}
			}
			
			boolean[] interval = guessControl(guess, randomNum);
			String answer = setAnswer(lowguess, highguess, correct, interval[1], interval[0]);
			
			if (cookie) {
				System.out.println("The cookie is set. This user should not get a new id.");
				System.out.println("value of cookie: " + value);
			}else {
				System.out.println("Could not read cookie.");
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
			
			if (!client) {
				response.println("Set-Cookie: clientId="+setClientId()+"; expires=Wednesday,31-Dec-15 21:00:00 GMT");
				response.println("Set-Cookie: sessionId="+setSessionId() + " NumGuess=0 LowGuess=1 Highguess=100;");
			} else if (!cookie) {
				response.println("Set-Cookie: sessionId="+setSessionId() + " NumGuess=0 LowGuess=1 Highguess=100;");
			} 
			if(cookie && client){
				response.println(setCookie(value, interval[1], interval[0], guess));
			}
			response.println();

			
			File f = new File("."+requestedDocument);
			FileInputStream infil = new FileInputStream(f);
			byte[] b = new byte[1024];
			while( infil.available() > 0){
				response.write(b,0,infil.read(b));
				response.println(setAnswer(lowguess, highguess, correct, interval[1], interval[0]));


			}

			s.shutdownOutput();
			s.close();
		}
	}
	
	private synchronized static String setAnswer(int low, int high, boolean corr, boolean tooHigh, boolean tooLow) {

		if (tooHigh) {
			return "<p>Too high! Guess again between "+low+" and "+high+"</p>";
		}
		if(low==1 && high==100) {
			return "<p>Guess between "+low+" and " + high + "</p>";
		
		}	
		if(tooLow) {
			return "<p>Too low! Guess again between "+low+" and " + high + "</p>";
		}
		
		else {
			return "<p>Correct</p>";
			}
			
		}
	
	
	private static String setCookie(String value, boolean low, boolean high, int guess) {
		int lowGuess = 0, numGuess = 0, highGuess= 100, clientId = 0, sessionId = 0;
		boolean reset = false;
		String[] cookies = value.split(";");
		String cookie1 = cookies[0];
		clientId = Integer.parseInt(cookie1.split("=")[1]);
		
		String cookie2 = cookies[1];
		String[] Guesses = cookie2.split(" ");
		sessionId = Integer.parseInt(Guesses[1].split("=")[1]);
		numGuess = Integer.parseInt(Guesses[2].split("=")[1]);
		lowGuess = Integer.parseInt(Guesses[3].split("=")[1]);
		highGuess = Integer.parseInt(Guesses[4].split("=")[1]);
		if (low) {
			lowGuess = guess;
		} else if (high) {
			highGuess = guess;
		} else {
			reset = true;
		}
		
		return getStringCookie(numGuess, lowGuess, highGuess, sessionId, reset);
	}
	
	private synchronized static int setClientId() {
		clients++;
		return clients;
	}
	
	private synchronized static int setSessionId() {
		sessions++;
		return sessions;
	}
	
	private synchronized static String getStringCookie(int numguess, int lowguess, int highguess, int sessions, boolean reset){
		String cookieContent ="";
		if (reset) {
			cookieContent+= "Set-Cookie: sessionId="+setSessionId()+" NumGuess="+0+" LowGuess="+0+" HighGuess="+100;
		} else {
			cookieContent+= "Set-Cookie: sessionId="+setSessionId()+" NumGuess="+numguess+" LowGuess="+lowguess+" HighGuess="+highguess;
		}
		
		return cookieContent;
	}
	
	private synchronized static boolean[] guessControl(int guess, int randomNum) {
		
		boolean tooHigh = false, tooLow = false;
		if(guess> 0 && guess==randomNum){
			System.out.println("Correct answer!");
			//htmlAnswer = "Correct answer!";
		}
		
		if(guess> 0 && guess < randomNum){
			tooLow = true;
			System.out.print("Too low, guess a new number between "+lowguess+" and " +highguess );
			if (lowguess <guess) {
				lowguess = guess;
			} else {
				//Användaren gissade lägre än innan. why.
			}
		}
		if(guess > randomNum){

			tooHigh = true;
			System.out.print("Too high, guess a new number between " + lowguess + " and " + highguess);
			if (highguess > guess) {
				highguess = guess;
			} else {
				//Användaren gissade högre än tidigare.
			}
		}
		
		boolean[] interval = new boolean[2];
		interval[0]=tooLow;
		interval[1]=tooHigh;
		
		return interval;
	}
	
}