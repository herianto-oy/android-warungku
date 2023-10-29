package com.warungku.utils.retrofit;

import android.content.Context;

import com.warungku.utils.api.Api;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private  static String BASE_URL = "http://3.239.175.175/";
    private static RetrofitClient retrofitClient;
    private static Retrofit retrofit;

    public RetrofitClient() {
        if (retrofit == null){
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
    }

    public static synchronized RetrofitClient getInstance(){
        if (retrofitClient == null){
            retrofitClient = new RetrofitClient();
        }

        return retrofitClient;
    }

    public static void setupToken(String token, Context context) {
        TokenInterceptor tokenInterceptor = new TokenInterceptor(token, context);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(tokenInterceptor).build();
        retrofit = new Retrofit.Builder().baseUrl(BASE_URL).client(client).addConverterFactory(GsonConverterFactory.create()).build();
    }

    public Api getApi(){
        return retrofit.create(Api.class);
    }
}
