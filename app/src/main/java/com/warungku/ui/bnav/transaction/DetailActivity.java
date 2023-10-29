package com.warungku.ui.bnav.transaction;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.warungku.R;
import com.warungku.models.pos.TransactionModel;
import com.warungku.models.pos.TrxDetailModel;
import com.warungku.ui.bnav.pos.DetailAdapter;
import com.warungku.utils.api.Api;
import com.warungku.utils.api.response.ResponseTransaction;
import com.warungku.utils.retrofit.RetrofitClient;
import com.warungku.utils.retrofit.SessionManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivity extends AppCompatActivity {
    private Api api;
    private SessionManager sessionManager;
    private TransactionModel transaction;
    private List<TrxDetailModel> productsList;
    private DetailAdapter detailAdapter;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView name;
    private TextView date;
    private TextView status;
    private TextView total;
    private LinearLayout btnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        name = findViewById(R.id.tv_name);
        date = findViewById(R.id.tv_date);
        status = findViewById(R.id.tv_status);
        total = findViewById(R.id.tv_total);
        btnCancel = findViewById(R.id.btn_cancel);

        recyclerView = findViewById(R.id.rc_trx_detail);
        progressBar = findViewById(R.id.pgbar);
        api = RetrofitClient.getInstance().getApi();
        sessionManager = new SessionManager(this);
        productsList = new ArrayList<>();
        detailAdapter = new DetailAdapter(this, productsList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(detailAdapter);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Transaksi Detail");

        if (getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getExtras();
            progressBar.setVisibility(View.VISIBLE);
            getData(bundle.getInt("id", 0));
        }else {
            this.finish();
        }
    }

    private void getData(Integer id) {
        api.getTransactionById(id).enqueue(new Callback<ResponseTransaction>() {
            @Override
            public void onResponse(Call<ResponseTransaction> call, Response<ResponseTransaction> response) {
                if(response.code() == 200){
                    transaction = response.body().getData();
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                    name.setText(transaction.getName());
                    date.setText(simpleDateFormat.format(new Date(transaction.getTimestamp())));
                    status.setText((transaction.getStatus() == 1) ? "Berhasil" : "Dibatalkan");
                    total.setText(String.format("%,.2f", transaction.getTotal()));
                    productsList.addAll(transaction.getDetails());
                    detailAdapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);

                    if(transaction.getStatus() == 1){
                        btnCancel.setVisibility(View.VISIBLE);
                    }

                }else {
                    Toast.makeText(DetailActivity.this, "Transaksi tidak ditemukan", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseTransaction> call, Throwable t) {
                Toast.makeText(DetailActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void btnCancel(View view) {
        api.cancelTransaction(transaction.getId()).enqueue(new Callback<ResponseTransaction>() {
            @Override
            public void onResponse(Call<ResponseTransaction> call, Response<ResponseTransaction> response) {
                if (response.code() == 200){
                    Toast.makeText(DetailActivity.this, "Transaksi dibatalkan!", Toast.LENGTH_SHORT).show();
                    getData(transaction.getId());
                } else {
                    Toast.makeText(DetailActivity.this, "Transaksi tidak ditemukan", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseTransaction> call, Throwable t) {
                Toast.makeText(DetailActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

}