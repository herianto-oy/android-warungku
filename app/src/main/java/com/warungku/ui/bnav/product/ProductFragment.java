package com.warungku.ui.bnav.product;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.warungku.R;
import com.warungku.models.product.ProductModel;
import com.warungku.utils.api.response.ResponseProductList;
import com.warungku.utils.retrofit.RetrofitClient;
import com.warungku.utils.retrofit.SessionManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductFragment extends Fragment {
    private NestedScrollView nestedScrollView;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private FloatingActionButton floatingButton;
    private List<ProductModel> productsList;
    private ProductAdapter productAdapter;
    private SessionManager sessionManager;
    private int page = 1;
    private int last = 100;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_product, container, false);
        nestedScrollView = root.findViewById(R.id.product_scroll_view);
        recyclerView = root.findViewById(R.id.rv_product);
        progressBar = root.findViewById(R.id.progressbar_product);
        floatingButton = root.findViewById(R.id.btn_add_product);
        sessionManager = new SessionManager(getContext());
        productsList = new ArrayList<>();
        productAdapter = new ProductAdapter(getActivity(), productsList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(productAdapter);
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

        floatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), ProductFormActivity.class));
            }
        });
        return root;
    }

    private void getData(int id, int page) {
        Call<ResponseProductList> call = RetrofitClient.getInstance().getApi().getProductList(id, page);
        call.enqueue(new Callback<ResponseProductList>() {
            @Override
            public void onResponse(Call<ResponseProductList> call, Response<ResponseProductList> response) {
                if (response.isSuccessful() && response.body() != null) {
                    progressBar.setVisibility(View.GONE);
                    last = response.body().getLast_page();
                    productsList.addAll(response.body().getData());
                    productAdapter.notifyDataSetChanged();
                }else {
                    Log.e("CHECK", String.valueOf(response.code()));
                }
            }

            @Override
            public void onFailure(Call<ResponseProductList> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.e("ERRRRRRRRRRRR", t.toString());
                Toast.makeText(getContext(), "Server Error!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}