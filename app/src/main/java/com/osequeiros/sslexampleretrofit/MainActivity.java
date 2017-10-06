package com.osequeiros.sslexampleretrofit;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("Init","Init");

        Call<BaseResponse> callConnection = API.get().getRetrofitService(getBaseContext())
                .verifyConnection();

        callConnection.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(@NonNull Call<BaseResponse> call,
                                   @NonNull Response<BaseResponse> response) {
                Log.d("Init","Response");
                if (response.isSuccessful()) {
                    BaseResponse apiResponse = response.body();
                    Log.d("tag", apiResponse.getMensaje());
                    Log.d("tag", ""+apiResponse.getCodigo());
                    Log.d("tag", ""+response.code());
                }else{
                    Log.d("Init",""+response.code());
                    Log.d("Init",""+response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<BaseResponse> call,
                                  @NonNull Throwable t) {
                t.printStackTrace();
                Log.e("tag", t.getMessage());
            }
        });
    }
}
