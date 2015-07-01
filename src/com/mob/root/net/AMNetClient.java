package com.mob.root.net;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import org.apache.http.HttpEntity;
import org.apache.http.conn.ssl.SSLSocketFactory;
import android.content.Context;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.MySSLSocketFactory;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;
import com.mob.root.AMApplication;
import com.mob.root.tools.AMLogger;

class AMNetClient {
	
	public AMNetClient() {
//		initCA();
//		client.setSSLSocketFactory(socketFactory);
	}
	
	private static AsyncHttpClient client = new AsyncHttpClient();
	private static SSLSocketFactory socketFactory;
	
    static void post(Context context, String url, HttpEntity entity, String contentType, ResponseHandlerInterface responseHandler) {
    	client.post(context, url, entity, contentType, responseHandler);
    }
    
    static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(url, params, responseHandler);
    }
    
    private void initCA() {
		try {
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			InputStream caInput = new BufferedInputStream(AMApplication.instance.getAssets().open("ca.crt.der"));
			Certificate ca = cf.generateCertificate(caInput);
			String keyStoreType = KeyStore.getDefaultType();
			KeyStore keyStore = KeyStore.getInstance(keyStoreType);
			keyStore.load(null, null);
			keyStore.setCertificateEntry("ca", ca);
			
//			String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
//			TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
//			tmf.init(keyStore);

//			SSLContext sslContext = SSLContext.getInstance("TLS");
//			sslContext.init(null, tmf.getTrustManagers(), null);
			
			socketFactory = new MySSLSocketFactory(keyStore);
		} catch (Exception e) {
			AMLogger.e(null, e.getMessage());
		}
	}
}
