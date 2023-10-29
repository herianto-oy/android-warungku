package com.warungku.utils.retrofit;

import android.content.Context;
import android.content.SharedPreferences;

import com.warungku.R;

public class SessionManager {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    public static final String IS_LOGGED_IN = "isLoggedIn";

    public SessionManager(Context context) {
        sharedPreferences = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void createLoginSession(Integer user_id, String token){
        editor.putInt("USER_ID", user_id);
        editor.putString("TOKEN", token);
        editor.putBoolean(IS_LOGGED_IN, true);
        editor.apply();
    }

    public void logoutSession() {
        editor.clear();
        editor.apply();
    }

    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(IS_LOGGED_IN, false);
    }

    public String getToken(){
        return sharedPreferences.getString("TOKEN", "");
    }
    public int getUserId() {return sharedPreferences.getInt("USER_ID", 0); }
}
