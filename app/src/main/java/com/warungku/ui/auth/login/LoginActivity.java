package com.warungku.ui.auth.login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.warungku.MainActivity;
import com.warungku.R;
import com.warungku.ui.auth.register.RegisterActivity;
import com.warungku.utils.api.Api;
import com.warungku.utils.api.request.RequestLogin;
import com.warungku.utils.api.response.ResponseLogin;
import com.warungku.utils.other.LoadingDialog;
import com.warungku.utils.retrofit.RetrofitClient;
import com.warungku.utils.retrofit.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private EditText email;
    private EditText password;
    private Api api;
    private SessionManager sessionManager;
    private CheckBox checkBox;
    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        sessionManager = new SessionManager(getApplicationContext());
        loadingDialog = new LoadingDialog(this);
        api = RetrofitClient.getInstance().getApi();
        email = findViewById(R.id.et_email);
        password = findViewById(R.id.et_password);
        checkBox = findViewById(R.id.checkbox);
        getSupportActionBar().hide();

        if (sessionManager.isLoggedIn()) {
            RetrofitClient.setupToken(sessionManager.getToken(), getApplicationContext());
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    public void login(View v) {
        if (checkBox.isChecked() && !email.getText().toString().equals("") && !password.getText().toString().equals("")) {
            loadingDialog.startDialog();
            RequestLogin requestLogin = new RequestLogin();
            requestLogin.setEmail(email.getText().toString());
            requestLogin.setPassword(password.getText().toString());
            api.login(requestLogin).enqueue(new Callback<ResponseLogin>() {
                @Override
                public void onResponse(Call<ResponseLogin> call, Response<ResponseLogin> response) {
                    loadingDialog.dismissDialog();
                    if (response.code() == 200) {
                        String token = response.body().getData().getApi_token();
                        Integer user_id = response.body().getData().getUser().getId();
                        sessionManager.createLoginSession(user_id, token);
                        RetrofitClient.setupToken(token, getApplicationContext());
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        LoginActivity.this.finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "Login Gagal!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseLogin> call, Throwable t) {
                    loadingDialog.dismissDialog();
                    Log.e("ERROR", t.toString());
                    Toast.makeText(LoginActivity.this, "Server Error!", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(LoginActivity.this, "Login Gagal!", Toast.LENGTH_SHORT).show();
        }
    }

    public void signup(View view) {
        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
    }
}