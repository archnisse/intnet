import java.io.*;
import java.net.*;
import java.util.*;

public class HttpClient{

	static HttpURLConnection con;// = new HttpURLConnection("127.0.0.1:8080/html.html");

	public static void main(String[] args) throws Exception{

		String host = args[0];
		int port = Integer.parseInt(args[1]);
		String fil = args[2];
		
		boolean correct = false;
		int guess = 0;
		Cookie cookie = new Cookie();
		
		while(!correct) {
			String strGuess = "";
			if (guess == 0) {
				strGuess = "";
			} else {
				strGuess = Integer.toString(guess);
			}
			String url = "http://" + host + ":" + port + "/" + fil + "?guess=" + strGuess;
			URL obj = new URL(url);
			System.out.println("Sent url: " + url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setDoOutput(true);
			con.setDoInput(true);
			//cookieContent+= "Set-Cookie: sessionId="+cookie.sessionId+" NumGuess="+0+" LowGuess="+1+" HighGuess="+100+" goal="+generateRandomNum();
			con.setRequestProperty("Cookie", "sessionId="+cookie.id+" NumGuess="+cookie.numGuess+" LowGuess="+cookie.low+" HighGuess="+cookie.high+" goal="+cookie.goal);
			con.connect();
			Map<String, List<String>> resp = con.getHeaderFields();
			List<String> cookies = resp.get("Set-Cookie");
			String frstCookie = cookies.get(0);
			String[] recievedCookie = frstCookie.split(" ");
			String session = recievedCookie[0].split("=")[0];
			if (session.equals("sessionId")) {
				System.out.println("The cookie was set and it was the first one");
				// Session Id caught, this is the right cookie.
				int nmguess = Integer.parseInt(recievedCookie[1].split("=")[1]);
				if (nmguess < cookie.numGuess) {
					System.out.println("Guess wass correct.");
					correct = true;
					continue;
				} else {
					System.out.println("The guess was not correct. retrying.");
					cookie.numGuess = nmguess;
				}
				cookie.low = Integer.parseInt(recievedCookie[2].split("=")[1]);
				cookie.high = Integer.parseInt(recievedCookie[3].split("=")[1]);
				System.out.println("Interval: " + cookie.low + " - " + cookie.high);
			} else {
				System.out.println("This was not the session Id cookie. Reversed order?");
			}
			guess = cookie.low + ((cookie.high - cookie.low)/2);
			System.out.println("Guessing for: " + guess);
			con.disconnect();
		}
		System.out.println("Outside of while loop, terminating.");
		/*
		String url = "http://" + host + ":" + port + "/" + fil;
		System.out.println("url: " + url);
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("GET");
		int responsecode = con.getResponseCode();
		System.out.println("response code: " + responsecode);
		//String get = con.getHeaderField("Set-cookie");
		Map<String, List<String>> resp = con.getHeaderFields();
		/*Set<String> cookies = resp.keySet();
	for (String tmp : cookies) {
		System.out.println("tmp: " + tmp);
	}
		List<String> cookies = resp.get("Set-Cookie");
		for (int i = 0; i < cookies.size(); i++) {
			String cook = cookies.get(i);
			System.out.println("cook: " + cook);
		}
		//System.out.println("test, get: " + get);


		/*Socket s =
	    new Socket(host,port);

	PrintStream utdata =
	    new PrintStream(s.getOutputStream());
	utdata.println("GET /" + fil + " HTTP/1.0");
	utdata.println();
	s.shutdownOutput();

	BufferedReader indata =
	    new BufferedReader(new InputStreamReader(s.getInputStream()));
	String str = "";
	while( (str = indata.readLine()) != null){
	    System.out.println("str:" + str);
	}
	s.close();*/
	}
	
	private static class Cookie {
		
		int numGuess, high, low, id, goal;
		
		public Cookie() {
			
		}
		
	}
}