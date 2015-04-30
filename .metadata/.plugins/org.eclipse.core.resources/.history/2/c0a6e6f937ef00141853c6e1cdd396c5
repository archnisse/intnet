import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;


public class Client {
	public static void main(String[] args) {
		SSLSocketFactory sf = 
				(SSLSocketFactory)SSLSocketFactory.getDefault();
		System.out.println("Stöder:");
		for(int i=0; i<sf.getSupportedCipherSuites().length; i++) {
		    System.out.println(sf.getSupportedCipherSuites()[i]);
		}
		SSLSocket s = null;
		try{
			System.out.print("före");
			s = (SSLSocket)sf.createSocket("localhost", 1235);
			System.out.print("efter");
			
		} catch (MalformedURLException e) {
			System.out.println("Uh oh, spagetthios.. " +e.getMessage());
		} catch (IOException e) {
			System.out.println("Uh oh, spagetthios.. mu" +e.getMessage());
		} 
		
		for(int i=0; i<s.getSupportedCipherSuites().length; i++) {
		    System.out.println(s.getSupportedCipherSuites()[i]);
		    System.out.println(i);
		}
		String[] cipher = {"SSL_DH_anon_WITH_RC4_128_MD5"};
		s.setEnabledCipherSuites(cipher);
		
		for(int i=0; i<s.getEnabledCipherSuites().length; i++) {
			System.out.println(s.getEnabledCipherSuites()[i]);
			System.out.println(i);
		}
		
		PrintWriter utfil = null;
		try {
			utfil = new PrintWriter(s.getOutputStream());
		} catch (IOException e) {
			System.out.println("Uh oh, spagetthios.. " +e.getMessage());
		} 
		
		utfil.println("Hej");
		utfil.close();
		
	}
}
