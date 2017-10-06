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
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Arrays;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLHandshakeException;
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

            OkHttpClient client_unsafe = WorkingOkHttpClient.getUnsafeOkHttpClient(context);

            Gson gson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                    .create();

            Retrofit retrofit=null;

            try {
                retrofit = new Retrofit.Builder()
                        .baseUrl(uri)
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .client(client_unsafe)
                        .build();
            } catch (Exception e){
                Log.e("tag", "mal mal mal");
                e.printStackTrace();
            }

            service = retrofit.create(APIService.class);
        }
        return service;
    }
}
