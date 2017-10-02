package com.osequeiros.sslexampleretrofit;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by osequeiros on 29/06/17.
 * Clase API Retrofit
 */
public class API {

    private static API instance;
    private APIService service;

    String uri = "https://190.81.47.200:1414/";

    /** Getter */
    public static API get() {
        if (instance == null) {
            instance = new API();
        }
        return instance;
    }

    /** Método de construcción del servicio */
    APIService getRetrofitService(Context context) {
        if (service == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient okHttpClient = UnsafeOkHttpClient.getUnsafeOkHttpClient();

            OkHttpClient client = null;
            try {
                client = new OkHttpClient.Builder()
                        .addInterceptor(logging)
                        .sslSocketFactory(getSSLConfig(context).getSocketFactory())
                        //.sslSocketFactory(newSslSocketFactory(context))
                        //.certificatePinner()
                        .hostnameVerifier(new HostnameVerifier() {
                            @Override
                            public boolean verify(String s, SSLSession sslSession) {
                                return true;
                            }
                        })
                        .build();
            } catch (Exception e) {
                //client = new OkHttpClient.Builder().addInterceptor(logging).build();
                Log.e("tag", "mal mal mal");
                e.printStackTrace();
            }
            /*client = new OkHttpClient.Builder().addInterceptor(logging)
                    .build();*/

            Gson gson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                    .create();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(uri)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(okHttpClient)
                    .build();

            service = retrofit.create(APIService.class);
        }
        return service;
    }

    private SSLSocketFactory newSslSocketFactory(Context context) {
        try {
            KeyStore trusted = KeyStore.getInstance("BKS");
            InputStream in = context.getResources().openRawResource(R.raw.mykeystore);
            try {
                trusted.load(in, "sslexample".toCharArray());
            } finally {
                in.close();
            }
            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(trusted);

            final KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory
                    .getDefaultAlgorithm());
            kmf.init(trusted, "sslexample".toCharArray());


            // Creating an SSLSocketFactory that uses our TrustManager
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, tmf.getTrustManagers(), null);
            //sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), new SecureRandom());

            return sslContext.getSocketFactory();
        } catch (Exception e) {
            throw new AssertionError(e);
        }
    }

    /**Módulo para recuperar el certificado SSL */
    private static SSLContext getSSLConfig(Context context) throws CertificateException, IOException,
            KeyStoreException, NoSuchAlgorithmException, KeyManagementException {

        // Loading CAs from an InputStream
        CertificateFactory cf = CertificateFactory.getInstance("X.509");

        X509Certificate ca;
        // I'm using Java7. If you used Java6 close it manually with finally.
        try (InputStream cert = context.getResources().openRawResource(R.raw.uiclientcertificate)) {
            ca = (X509Certificate) cf.generateCertificate(cert);
        }

        // Creating a KeyStore containing our trusted CAs
        String keyStoreType = KeyStore.getDefaultType();
        KeyStore keyStore   = KeyStore.getInstance(keyStoreType);
        keyStore.load(null, null);

        String alias = ca.getSubjectX500Principal().getName();

        keyStore.setCertificateEntry(alias, ca);
        //keyStore.setCertificateEntry("ca", ca2);

        // Creating a TrustManager that trusts the CAs in our KeyStore.
        /*String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
        tmf.init(keyStore);*/

        KeyManagerFactory kmf = KeyManagerFactory.getInstance("X509");
        try {
            kmf.init(keyStore, "bcpclient".toCharArray());
        } catch (Exception e) {
            Log.e("tag", "recontra mal");
        }
        KeyManager[] keyManagers = kmf.getKeyManagers();

        TrustManagerFactory tmf = TrustManagerFactory.getInstance("X509");
        TrustManager[] trustManagers = { new CustomTrustManager(keyStore)};
        tmf.init(keyStore);
        //TrustManager[] trustManagers = tmf.getTrustManagers();

        final TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            @Override
            public X509Certificate[] getAcceptedIssuers() {
                X509Certificate[] cArrr = new X509Certificate[0];
                return cArrr;
            }

            @Override
            public void checkServerTrusted(final X509Certificate[] chain,
                                           final String authType) throws CertificateException { }

            @Override
            public void checkClientTrusted(final X509Certificate[] chain,
                                           final String authType) throws CertificateException { }
        }};

        // Creating an SSLSocketFactory that uses our TrustManager
        SSLContext sslContext = SSLContext.getInstance("TLS");
        //sslContext.init(null, trustAllCerts, null);
        sslContext.init(keyManagers, trustManagers, null);

        return sslContext;
    }
}
