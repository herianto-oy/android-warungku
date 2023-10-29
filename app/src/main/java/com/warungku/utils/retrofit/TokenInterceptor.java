package com.warungku.utils.retrofit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.warungku.R;
import com.warungku.ui.auth.login.LoginActivity;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class TokenInterceptor implements Interceptor {
    private Context context;
    private String token;
    private SessionManager sessionManager;

    public TokenInterceptor() {}
    public TokenInterceptor(String token, Context context) {
        this.token = token;
        this.context = context;
        this.sessionManager = new SessionManager(context);
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request().newBuilder().header("Authorization", "Bearer " + token).build();
        Response response = chain.proceed(request);
        if (response.code() == 401){
            sessionManager.logoutSession();
            context.startActivity(new Intent(context, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        }
        return response;
    }
}
