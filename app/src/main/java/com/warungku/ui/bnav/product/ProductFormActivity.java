package com.warungku.ui.bnav.product;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.warungku.MainActivity;
import com.warungku.R;
import com.warungku.utils.api.Api;
import com.warungku.utils.api.response.ResponseProduct;
import com.warungku.utils.other.LoadingDialog;
import com.warungku.utils.retrofit.RetrofitClient;
import com.warungku.utils.retrofit.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductFormActivity extends AppCompatActivity {
    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;
    private String partImage;
    private File file;
    private CircleImageView img;
    private Integer id;
    private EditText code;
    private EditText name;
    private EditText price;
    private EditText stock;
    private TextView tvCreateUpdate;
    private Api api;
    private SessionManager sessionManager;
    private LoadingDialog loadingDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_form);
        sessionManager = new SessionManager(this);
        img = findViewById(R.id.img_product);
        code = findViewById(R.id.et_code);
        name = findViewById(R.id.et_name);
        price = findViewById(R.id.et_price);
        stock = findViewById(R.id.et_stock);
        tvCreateUpdate = findViewById(R.id.tv_create_update);
        api = RetrofitClient.getInstance().getApi();
        loadingDialog = new LoadingDialog(this);
        file = null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (getIntent().getExtras() != null) {
            DecimalFormat df = new DecimalFormat("####.##");
            Bundle bundle = getIntent().getExtras();
            Glide.with(this).load(bundle.getString("img")).diskCacheStrategy(DiskCacheStrategy.ALL).into(img);
            id = bundle.getInt("id");
            code.setText(bundle.getString("code"));
            name.setText(bundle.getString("name"));
            price.setText(new DecimalFormat("####.##").format(bundle.getDouble("price")));
            stock.setText(new DecimalFormat("####.##").format(bundle.getDouble("stock")));
            tvCreateUpdate.setText("Ubah");
            getSupportActionBar().setTitle("Ubah Product");
        } else {
            tvCreateUpdate.setText("Tambah");
            getSupportActionBar().setTitle("Tambah Product");
        }
    }

    public void imagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_CODE);
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

    public void createUpdate(View view) {
        loadingDialog.startDialog();
        RequestBody requestId = null;
        if (id != null) {
            requestId = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(id));
        }
        RequestBody requestUserId = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(sessionManager.getUserId()));
        RequestBody requestCode = RequestBody.create(MediaType.parse("multipart/form-data"), code.getText().toString());
        RequestBody requestName = RequestBody.create(MediaType.parse("multipart/form-data"), name.getText().toString());
        RequestBody requestPrice = RequestBody.create(MediaType.parse("multipart/form-data"), price.getText().toString());
        RequestBody requestStock = RequestBody.create(MediaType.parse("multipart/form-data"), stock.getText().toString());
        MultipartBody.Part partImage = null;
        if (file != null) {
            RequestBody requestImg = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            partImage = MultipartBody.Part.createFormData("img", file.getName(), requestImg);
        }
        api.createUpdateProduct(requestId, partImage, requestUserId, requestCode, requestName, requestPrice, requestStock)
                .enqueue(new Callback<ResponseProduct>() {
                    @Override
                    public void onResponse(Call<ResponseProduct> call, Response<ResponseProduct> response) {
                        loadingDialog.dismissDialog();
                        String messageOk;
                        if (id == null) {
                            messageOk = "Menambahkan Produk Berhasil!";
                        } else {
                            messageOk = "Mengubah Produk Berhasil!";
                        }

                        if (response.code() == 200) {
                            Toast.makeText(ProductFormActivity.this, messageOk, Toast.LENGTH_SHORT).show();
                            startActivity(new Intent (ProductFormActivity.this, MainActivity.class)
                                    .putExtra("state", "product")
                                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                            ProductFormActivity.this.finish();
                        } else {
                            try {
                                JSONObject jObjError = new JSONObject(response.errorBody().string());
                                Toast.makeText(ProductFormActivity.this, jObjError.getString("message"), Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                Log.e("JSON", e.getMessage());
                            } catch (IOException e) {
                                Log.e("IOException", e.getMessage());
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseProduct> call, Throwable t) {
                        loadingDialog.dismissDialog();
                        Toast.makeText(ProductFormActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                    }
                });

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

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}