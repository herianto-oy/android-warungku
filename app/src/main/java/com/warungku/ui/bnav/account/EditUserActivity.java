package com.warungku.ui.bnav.account;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.warungku.R;
import com.warungku.models.user.UserModel;
import com.warungku.utils.api.Api;
import com.warungku.utils.api.response.ResponseUser;
import com.warungku.utils.other.LoadingDialog;
import com.warungku.utils.retrofit.RetrofitClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditUserActivity extends AppCompatActivity {
    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;
    private File file;
    private CircleImageView img;
    private EditText owner;
    private EditText phoneNumber;
    private EditText email;
    private EditText name;
    private EditText desc;
    private EditText address;
    private String partImage;
    private Api api;
    private SharedPreferences sharedPreferences;
    private LoadingDialog loadingDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);
        getSupportActionBar().hide();

        img = findViewById(R.id.pp);
        owner = findViewById(R.id.et_owner);
        phoneNumber = findViewById(R.id.et_phone);
        email = findViewById(R.id.et_email);
        name = findViewById(R.id.et_name);
        desc = findViewById(R.id.et_description);
        address = findViewById(R.id.et_address);
        api = RetrofitClient.getInstance().getApi();
        sharedPreferences = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        loadingDialog = new LoadingDialog(this);

        if (getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getExtras();
            Glide.with(this).load(bundle.getString("img")).diskCacheStrategy(DiskCacheStrategy.ALL).into(img);
            owner.setText(bundle.getString("owner"));
            phoneNumber.setText(bundle.getString("phoneNumber"));
            email.setText(bundle.getString("email"));
            name.setText(bundle.getString("name"));
            desc.setText(bundle.getString("desc"));
            address.setText(bundle.getString("address"));
        }
    }

    public void btnSelect(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                String[] permission = {Manifest.permission.READ_EXTERNAL_STORAGE};
                requestPermissions(permission, PERMISSION_CODE);
            } else {
                imagePicker();
            }
        } else {
            imagePicker();
        }
    }

    public void imagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    public void edit(View view){
        loadingDialog.startDialog();
        uploadImg();
        UserModel user = new UserModel();
        user.setId(sharedPreferences.getInt("USER_ID", 0));
        user.setOwner(owner.getText().toString());
        user.setEmail(email.getText().toString());
        user.setPhoneNumber(phoneNumber.getText().toString());
        user.setName(name.getText().toString());
        user.setDescription(desc.getText().toString());
        user.setAddress(address.getText().toString());

        api.updateUser(user).enqueue(new Callback<ResponseUser>() {
            @Override
            public void onResponse(Call<ResponseUser> call, Response<ResponseUser> response) {
                loadingDialog.dismissDialog();
                if (response.code() == 200){
                    Toast.makeText(EditUserActivity.this, "Mengubah berhasil!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    EditUserActivity.this.setResult(RESULT_OK, intent);
                    EditUserActivity.this.finish();
                }else {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(EditUserActivity.this, jObjError.getString("message"), Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        Log.e("JSON", e.getMessage());
                    } catch (IOException e) {
                        Log.e("IOException", e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseUser> call, Throwable t) {
                loadingDialog.dismissDialog();
                Toast.makeText(EditUserActivity.this, "Server Error!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadImg() {
        if (file != null) {
            RequestBody userId = RequestBody.create(MediaType.parse("multipart/form-file"), String.valueOf(sharedPreferences.getInt("USER_ID", 0)));
            RequestBody requestImg = RequestBody.create(MediaType.parse("multipart/form-file"), file);
            MultipartBody.Part partImage = MultipartBody.Part.createFormData("img", file.getName(), requestImg);
            api.uploadImg(partImage, userId).enqueue(new Callback<ResponseUser>() {
                @Override
                public void onResponse(Call<ResponseUser> call, Response<ResponseUser> response) {
                    if (response.code() == 200) {
                        Log.e("Upload Image", "Success");
                    } else {
                        Log.e("Error", String.valueOf(response.message()));
                    }
                }

                @Override
                public void onFailure(Call<ResponseUser> call, Throwable t) {
                    Toast.makeText(EditUserActivity.this, "Server Error!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    imagePicker();
                } else {
                    Toast.makeText(this, "Permission Denied...!", Toast.LENGTH_SHORT).show();
                }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            Uri dataImage = data.getData();
            String[] imageProjection = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(dataImage, imageProjection, null, null, null);

            if (cursor != null) {
                cursor.moveToFirst();
                int indexImage = cursor.getColumnIndex(imageProjection[0]);
                partImage = cursor.getString(indexImage);

                if (partImage != null) {
                    file = new File(partImage);
                    Glide.with(this).load(new File(file.getAbsolutePath())).into(img);
                }
            }
        }
    }
}