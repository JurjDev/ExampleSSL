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

        Call<String> callConnection = API.get().getRetrofitService(getBaseContext())
                .verifyConnection();
        callConnection.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call,
                                   @NonNull Response<String> response) {
                if (response.isSuccessful()) {
                    String apiResponse = response.body();
                    Log.d("tag", apiResponse);
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                t.printStackTrace();
                Log.e("tag", t.getMessage());
            }
        });
    }
}
