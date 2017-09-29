package com.osequeiros.sslexampleretrofit;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by osequeiros on 9/28/17.
 */

public interface APIService {

    @GET("api/Interfaz/ValidarConexion")
    Call<String> verifyConnection();
}
