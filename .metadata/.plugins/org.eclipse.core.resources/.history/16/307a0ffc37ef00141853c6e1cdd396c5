import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;


public class Certificate {{
	
	
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
		is = new FileInputStream(new File("./keystore"));
	} catch(FileNotFoundException e5){
		System.out.println(e5.getMessage());
	}

	try {
		ks.load(is,"rootroot".toCharArray());
	} catch(IOException e6){
		System.out.println(e6.getMessage());
	} catch(NoSuchAlgorithmException e7){
		System.out.println(e7.getMessage());
	} catch(CertificateException e8){
		System.out.println(e8.getMessage());
	}


}}

