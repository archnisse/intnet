import java.io.*;
import java.net.*;
import java.util.StringTokenizer;

public class HttpServer{

	public static void main(String[] args) throws IOException{
		
		System.out.println("Skapar Serversocket");
		ServerSocket ss = new ServerSocket(8080);
		
		while(true){
			System.out.println("Väntar på klient...");
			Socket s = ss.accept();
			System.out.println("Klient är ansluten");
			BufferedReader request = new BufferedReader(new InputStreamReader(s.getInputStream()));
			String str = request.readLine();
			//printar request
			System.out.println("str: " + str);
			
			StringTokenizer tokens = new StringTokenizer(str," ?");
			System.out.println(tokens.nextToken()); // Ordet GET
			
			String requestedDocument = tokens.nextToken();
			String guess = tokens.nextToken();
			guess = guess.substring(6,guess.length());

			
			//skriver ut info om host
			while( (str = request.readLine()) != null && str.length() > 0){
				System.out.println(str);
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

			response.println("Set-Cookie: clientId=1; expires=Wednesday,31-Dec-12 21:00:00 GMT");

			response.println();
			File f = new File("."+requestedDocument);
			FileInputStream infil = new FileInputStream(f);
			byte[] b = new byte[1024];
			
			while( infil.available() > 0){
				response.write(b,0,infil.read(b));
			}
			
			s.shutdownOutput();
			s.close();
		}
	}
}