package com.warungku.ui.bnav.transaction;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.warungku.R;
import com.warungku.models.pos.TransactionModel;
import com.warungku.utils.api.Api;
import com.warungku.utils.api.response.ResponseTransactions;
import com.warungku.utils.retrofit.RetrofitClient;
import com.warungku.utils.retrofit.SessionManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransactionFragment extends Fragment {
    private NestedScrollView nestedScrollView;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private SessionManager sessionManager;
    private Api api;
    private List<TransactionModel> transactionList;
    private TransactionAdapter transactionAdapter;
    private int page = 1;
    private int last = 100;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_transaction, container, false);
        nestedScrollView = root.findViewById(R.id.transaction_scroll_view);
        recyclerView = root.findViewById(R.id.rv_transaction);
        progressBar = root.findViewById(R.id.progressbar_transaction);
        sessionManager = new SessionManager(getContext());
        api = RetrofitClient.getInstance().getApi();
        transactionList = new ArrayList<>();
        transactionAdapter = new TransactionAdapter(getActivity(), transactionList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(transactionAdapter);
        getData(sessionManager.getUserId(), page);
        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                    if (page <= last) {
                        page++;
                        progressBar.setVisibility(View.VISIBLE);
                        getData(sessionManager.getUserId(), page);
                    }
                }
            }
        });
        return root;
    }

    private void getData(int userId, int page) {
        api.getTransactions(userId, page).enqueue(new Callback<ResponseTransactions>() {
            @Override
            public void onResponse(Call<ResponseTransactions> call, Response<ResponseTransactions> response) {
                if (response.isSuccessful() && response.body() != null) {
                    progressBar.setVisibility(View.GONE);
                    last = response.body().getLast_page();
                    transactionList.addAll(response.body().getData());
                    transactionAdapter.notifyDataSetChanged();
                } else {
                    Log.e("CHECK", String.valueOf(response.code()));
                }
            }

            @Override
            public void onFailure(Call<ResponseTransactions> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.e("ERRRRRRRRRRRR", t.toString());
                Toast.makeText(getContext(), "Server Error!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}