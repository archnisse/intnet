import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

import com.sun.net.ssl.SSLContext;
import com.sun.net.ssl.TrustManager;
import com.sun.net.ssl.TrustManagerFactory;

import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

	
	// TO DO
	//Create an encrypted connection
	//Create a certificate with the help of a keytool
	//Create a server that waits for an encrypted connection
	//Server only needs to send hello world over the connection to prove itself
	//When connection established - acts like normal sockets
	//must use sslsockets
	//connect with browser after accepting self-signed certificate


public class Server {
	
	public static void main(String[] args) throws NoSuchAlgorithmException, CertificateException, FileNotFoundException, IOException, KeyStoreException {
		
		try {
			InputStream infil = new FileInputStream("server.cer");
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			X509Certificate cert = (X509Certificate)cf.generateCertificate(infil);
			infil.close();
		} catch(CertificateException e1){
			System.err.println(e1.getMessage());
		} catch(IOException e2){
			System.err.println(e2.getMessage());
		}

		KeyStore ks = null;
		try {
			ks = KeyStore.getInstance("JKS", "SUN");
		} catch(KeyStoreException e3){
			System.out.println(e3.getMessage());
		} catch(NoSuchProviderException e4){
			System.out.println(e4.getMessage());
		}

		InputStream is = null;
		try {
			is = new FileInputStream(new File("/.keystore"));
		} catch(FileNotFoundException e5){
			System.out.println(e5.getMessage());
		}

		try {
			ks.load(is,"rootroot".toCharArray()); // rootroot kanske måste vara batman
		} catch(IOException e6){
			System.out.println(e6.getMessage());
		} catch(NoSuchAlgorithmException e7){
			System.out.println(e7.getMessage());
		} catch(CertificateException e8){
			System.out.println(e8.getMessage());
		}
		
		TrustManagerFactory tmf = TrustManagerFactory.getInstance("SUNX509");
		tmf.init(ks);
		
		SSLContext ctxt = SSLContext.getInstance("TLS");
		TrustManager[] tm = tmf.getTrustManagers();
		try {
			ctxt.init(null, tm, null);
		} catch (KeyManagementException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		SSLServerSocketFactory ssf = ctxt.getServerSocketFactory();
		System.out.println("Stöder:");
		for(int i=0; i<ssf.getSupportedCipherSuites().length; i++) {
		    System.out.println(ssf.getSupportedCipherSuites()[i]);
		}
		SSLServerSocket ss = null;
		try{
			ss = (SSLServerSocket)ssf.createServerSocket(1235);
			String[] cipher = ss.getSupportedCipherSuites();//{"SSL_DH_anon_WITH_RC4_128_MD5"};
			ss.setEnabledCipherSuites(cipher);
			System.out.println("VALD:");
			for(int i=0; i<ss.getEnabledCipherSuites().length; i++) {
				System.out.println(ss.getEnabledCipherSuites()[i]);
			}
			SSLSocket s = (SSLSocket)ss.accept();
			BufferedReader infil = new BufferedReader(new InputStreamReader(s.getInputStream()));
			String rad = null;
			while((rad=infil.readLine())!=null) {
				System.out.println(rad);
			}
		} catch(IOException e) {
			System.out.println("Uh oh.. "+e.getMessage());
		}
	}

}
