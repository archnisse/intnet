import java.io.*;
import java.net.*;

public class HttpClient{
	//args[0] is the host ex. 127.0.0.1
	//args[1] is the port nr ex. 8080
	//args[2] is the name of the html file ex html.html in this case
    
    public static void main(String[] args) throws Exception{
	String host = args[0];
	int port = Integer.parseInt(args[1]);
	String fil = args[2];
	Socket s = new Socket(host,port);
	
	PrintStream utdata = new PrintStream(s.getOutputStream());
	utdata.println("GET /" + fil + " HTTP/1.0");
	utdata.println();
	s.shutdownOutput();
	
	BufferedReader indata = new BufferedReader(new InputStreamReader(s.getInputStream()));
	String str = "";
	
	
	
	while( (str = indata.readLine()) != null){
	    System.out.println(str);
	}
	
	s.close();
	
    }
}