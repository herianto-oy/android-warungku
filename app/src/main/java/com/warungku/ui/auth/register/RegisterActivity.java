package com.warungku.ui.auth.register;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.warungku.R;
import com.warungku.utils.api.Api;
import com.warungku.utils.api.request.RequestRegister;
import com.warungku.utils.api.response.ResponseRegister;
import com.warungku.utils.other.LoadingDialog;
import com.warungku.utils.retrofit.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
    private Api api;
    private EditText owner;
    private EditText name;
    private EditText email;
    private EditText password;
    private EditText passwordRepeat;
    private EditText address;
    private CheckBox checkBox;
    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().hide();
        api = RetrofitClient.getInstance().getApi();

        owner = findViewById(R.id.et_owner);
        name = findViewById(R.id.et_name);
        email = findViewById(R.id.et_email);
        password = findViewById(R.id.et_password);
        passwordRepeat = findViewById(R.id.et_password_repeat);
        address = findViewById(R.id.et_address);
        checkBox = findViewById(R.id.checkbox);
        loadingDialog = new LoadingDialog(this);
    }

    public void signup(View view) {
        if (checkBox.isChecked()) {
            if (password.getText().toString().equals(passwordRepeat.getText().toString())) {
                loadingDialog.startDialog();
                RequestRegister requestRegister = new RequestRegister();
                requestRegister.setOwner(owner.getText().toString());
                requestRegister.setName(name.getText().toString());
                requestRegister.setEmail(email.getText().toString());
                requestRegister.setPassword(password.getText().toString());
                requestRegister.setAddress(address.getText().toString());

                api.register(requestRegister).enqueue(new Callback<ResponseRegister>() {
                    @Override
                    public void onResponse(Call<ResponseRegister> call, Response<ResponseRegister> response) {
                        loadingDialog.dismissDialog();
                        if (response.code() == 200) {
                            Log.e("Message", response.message());
                            Toast.makeText(RegisterActivity.this, "Daftar berhasil, Silakan login!", Toast.LENGTH_SHORT).show();
                            RegisterActivity.this.finish();
                        } else {
                            Toast.makeText(RegisterActivity.this, "Daftar gagal, Silakan coba lagi!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseRegister> call, Throwable t) {
                        loadingDialog.dismissDialog();
                        Toast.makeText(RegisterActivity.this, "Server Error!", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(RegisterActivity.this, "Password tidak sesuai!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Pastikan anda bukan robot!", Toast.LENGTH_SHORT).show();
        }
    }
}