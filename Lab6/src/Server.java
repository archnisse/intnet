import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;




	
	// TO DO
	
	//Create a certificate with the help of a keytool
		//skapar server.cer via script i terminalen
		//server.cer måste läggas in i klienten som godkänd/secure (firefox browser)
		//Inställningar > Avancerat >Certifikat > Servrar > Importera
	
	
	
	//must use sslsockets
	//connect with browser after accepting self-signed certificate

//Algorithm for creating SSL Server socket
//	1.Register the JSSE provider
//	2.Set System property for keystore by specifying the keystore which contains the server certificate
//	3.Set System property for the password of the keystore which contains the server certificate
//	4.Create an instance of SSLServerSocketFactory
//	5.Create an instance of SSLServerSocket by specifying the port to which the SSL Server socket needs to bind with
//	6.Initialize an object of SSLSocket
//	7.Create InputStream object to read data sent by clients
//	8.Create an OutputStream object to write data back to clients.

//Source: https://www.owasp.org/index.php/Using_the_Java_Secure_Socket_Extensions#The_JSSE_Implementation_of_SSL




public class Server {
	
	public static void main(String[] args) throws FileNotFoundException, IOException {
		
		
		
		try{
			//Create an encrypted connection, a secure connection
			//måste stoppa keystore i rätt directory: home/?
			//setProperty sets the system property indicated by the specified key
			//det är här handskaket sker?
			System.setProperty("javax.net.ssl.keyStore", "/.keystore");
	        System.setProperty("javax.net.ssl.keyStorePassword", "batman");
	        //getDefault returns the default SSL server socket factory
			SSLServerSocketFactory ssf = (SSLServerSocketFactory)SSLServerSocketFactory.getDefault();
			System.out.println("OK; factory succesful");
			//Create a server that waits for an encrypted connection
			//Returns a server socket bound to the specified  port
			SSLServerSocket ss = (SSLServerSocket)ssf.createServerSocket(1235);
			
			//Connect with client
			while(true) {
				
				SSLSocket s = (SSLSocket)ss.accept();
				//When connection established - acts like normal sockets
				System.out.println("We have landed on the moon!");
				
				BufferedReader infil = new BufferedReader(new InputStreamReader(s.getInputStream()));
                PrintWriter out = new PrintWriter(new OutputStreamWriter(s.getOutputStream()));
                //Server only needs to send hello world over the connection to prove itself
                out.write("Hello World");
                s.shutdownOutput ();
			}
			
		} catch(IOException e) {
			//System.out.println("Uh oh.. "+e.getMessage());
			e.printStackTrace();
		}
	}

}
