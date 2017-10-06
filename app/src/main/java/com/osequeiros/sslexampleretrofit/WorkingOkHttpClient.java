package com.osequeiros.sslexampleretrofit;

import android.content.Context;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;

/**
 * Created by Usuario on 06/10/2017.
 * Yay!
 */

public class WorkingOkHttpClient {

    public static OkHttpClient getUnsafeOkHttpClient(Context mContext) {

        try {

            //Generador de certificados de alg X.509 aka .cer
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            //Certificado RootCA
            Certificate ca = null;
            //password del keystore del cliente
            char[] passphrase = "bcpuiclient".toCharArray();

            //Cargamos el RootCA de la carpeta RAW
            try (InputStream cert = mContext.getResources().openRawResource(R.raw.bcpuicertificateui)) {
                //Generamos el Certificado con el RootCA
                ca = cf.generateCertificate(cert);

            } catch (SSLHandshakeException e) {
                e.printStackTrace();
            }

            //String con el TypoDeAlgo
            String keyStoreType = KeyStore.getDefaultType();
            //Creamos un Keystore Con el RootCA para confiar de este
            KeyStore keyStoreCA = KeyStore.getInstance(keyStoreType);
            //Se crea sin pass
            keyStoreCA.load(null, null);
            //Se almacena el Certificado CA Obtennido
            keyStoreCA.setCertificateEntry("ca", ca);

            //Creamos el Keystore para el ClienteKey, en base al Algo PKCS12 aka .p12,.pfx
            KeyStore ClientkeyStore = KeyStore.getInstance("pkcs12");

            //Cargamos el Cliente-Key ValuePair de la carpeta RAW
            try (InputStream fis = mContext.getResources().openRawResource(
                    R.raw.uiclientcertificateui)) {
                //Abrimos el Keystore con su respectiva PassWord
                ClientkeyStore.load(fis, passphrase);

            } catch (Exception e) {
                e.printStackTrace();
            }

            //Cargamos el Kmf Default Algo aka PKIS
            String kmfAlgorithm = KeyManagerFactory.getDefaultAlgorithm();
            //Creamos el Kmf Con el Algo default
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(kmfAlgorithm);

            try {
                //Abrimos el keystore con el Pass Phrase creo..... aka PEM
                kmf.init(ClientkeyStore, "bcpui".toCharArray());

            } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
                e.printStackTrace();
            }

            //Obtenemos el Algo Para el Tmf
            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            //Generamos el Tmf con el algo default
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            //Iniciamos el Tmd con el Keystore del RootCA aka Confiamos en el RootCA
            tmf.init(keyStoreCA);

            //Creamos el SSLContext con el Protocolo TLSv1.2

            //final SSLContext sslContext = SSLContext.getInstance("SSL");
            final SSLContext sslContext = SSLContext.getInstance("TLSv1.2");

            //Iniciamos el SSLContext con las credenciales del cliente y los Ca a connfiar
            sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

            // Creamos el Socket del SSlContext
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            //Creamos nuestro OkHttpClient
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            //Seteamos el Socket
            builder.sslSocketFactory(sslSocketFactory,(X509TrustManager) tmf.getTrustManagers()[0]);
            //Confiamos el todos los host names,
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
            //Construinmos y retornamos el OkHttpClient a retroFit;
            OkHttpClient okHttpClient = builder.build();
            return okHttpClient;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
