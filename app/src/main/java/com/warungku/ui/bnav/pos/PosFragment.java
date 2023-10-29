package com.warungku.ui.bnav.pos;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.BeepManager;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.warungku.R;
import com.warungku.models.pos.TransactionModel;
import com.warungku.models.pos.TrxDetailModel;
import com.warungku.models.product.ProductModel;
import com.warungku.utils.api.Api;
import com.warungku.utils.api.response.ResponseProduct;
import com.warungku.utils.api.response.ResponseTransaction;
import com.warungku.utils.retrofit.RetrofitClient;
import com.warungku.utils.retrofit.SessionManager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PosFragment extends Fragment {
    private static final int PERMISSION_CAMERA = 1002;
    private Context mContext;
    private Activity mActivity;
    private DecoratedBarcodeView mBarcodeView;
    private BeepManager mBeepManager;
    private Api api;
    private SessionManager sessionManager;
    private List<TrxDetailModel> productsList;
    private DetailAdapter detailAdapter;
    private RecyclerView recyclerView;
    private TextView tvTotal;
    private EditText etManual;
    private LinearLayout btnScanManual;
    private LinearLayout btnPay;
    private Double total;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pos, container, false);
        mBarcodeView = view.findViewById(R.id.barcode_view);
        recyclerView = view.findViewById(R.id.rc_trx_detail);
        tvTotal = view.findViewById(R.id.tv_total_price);
        etManual = view.findViewById(R.id.et_code);
        btnScanManual = view.findViewById(R.id.btn_manual_scan);
        btnPay = view.findViewById(R.id.btn_pay);
        mActivity = getActivity();
        mContext = getContext();
        tvTotal.setText(String.valueOf(0));

        api = RetrofitClient.getInstance().getApi();
        sessionManager = new SessionManager(getContext());
        productsList = new ArrayList<>();
        detailAdapter = new DetailAdapter(getActivity(), productsList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(detailAdapter);

        btnScanManual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!etManual.getText().toString().equals("")) {
                    searchProduct(etManual.getText().toString());
                } else {
                    Toast.makeText(mContext, "Kode produk tidak boleh kosong!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (productsList.isEmpty()) {
                    Toast.makeText(mContext, "Produk tidak boleh kosong!", Toast.LENGTH_SHORT).show();
                } else {
                    CharSequence[] menuChoose = {"Iya", "Batal"};
                    AlertDialog.Builder dialog = new AlertDialog.Builder(v.getContext())
                            .setTitle("Anda yakin ini bayar?")
                            .setCancelable(true)
                            .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    payTransaction();
                                }
                            }).setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    dialog.create();
                    dialog.show();
                }
            }
        });
        permission();
        doPreRequisites();
        doScan();

        return view;
    }

    private void doScan() {
        mBarcodeView.decodeContinuous(new BarcodeCallback() {
            @Override
            public void barcodeResult(BarcodeResult result) {
                mBarcodeView.pause();
                mBeepManager.playBeepSoundAndVibrate();

                if (result != null && !TextUtils.isEmpty(result.getText()) && !TextUtils.isEmpty(result.getBarcodeFormat().name())) {
                    searchProduct(result.getText());
                } else {
                    mBarcodeView.resume();
                    Toast.makeText(mContext, "Scan Error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void possibleResultPoints(List<ResultPoint> resultPoints) {

            }
        });
    }

    private void searchProduct(String code) {
        api.getProductByCode(sessionManager.getUserId(), code)
                .enqueue(new Callback<ResponseProduct>() {
                    @Override
                    public void onResponse(Call<ResponseProduct> call, Response<ResponseProduct> response) {
                        if (response.code() == 200) {
                            ProductModel product = response.body().getData();
                            Boolean isNew = true;
                            for (int i = 0; i < productsList.size(); i++) {
                                if (productsList.get(i).getProduct().getCode().equals(code)) {
                                    isNew = false;
                                    if (productsList.get(i).getQty() < product.getStock()) {
                                        productsList.get(i).setQty(productsList.get(i).getQty() + 1);
                                        detailAdapter.notifyItemRangeChanged(i, productsList.size());
                                        etManual.setText("");
                                        Toast.makeText(mContext, response.body().getData().getName() + " Ditambahkan", Toast.LENGTH_SHORT).show();
                                        break;
                                    } else {
                                        Toast.makeText(mContext, "Stok produk tidak cukup!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }

                            if (isNew) {
                                TrxDetailModel detailModel = new TrxDetailModel();
                                detailModel.setProduct(product);
                                detailModel.setPrice(product.getPrice());
                                detailModel.setQty(1d);
                                productsList.add(detailModel);
                                detailAdapter.notifyDataSetChanged();
                                etManual.setText("");
                                Toast.makeText(mContext, response.body().getData().getName() + " Ditambahkan", Toast.LENGTH_SHORT).show();
                            }
                            updateTotal();
                        } else {
                            Toast.makeText(mContext, "Produk tidak ditemukan", Toast.LENGTH_SHORT).show();
                        }
                        mBarcodeView.resume();
                    }

                    @Override
                    public void onFailure(Call<ResponseProduct> call, Throwable t) {
                        Toast.makeText(mContext, "Server Error", Toast.LENGTH_SHORT).show();
                        mBarcodeView.resume();
                    }
                });

    }

    private void payTransaction() {
        TransactionModel transaction = new TransactionModel();
        transaction.setUserId(sessionManager.getUserId());
        transaction.setName(String.valueOf(new Date().getTime()) + sessionManager.getUserId());
        transaction.setDetails(productsList);
        transaction.setTimestamp(new Date().getTime());
        transaction.setStatus(1);
        transaction.setTotal(total);
        api.createTransaction(transaction).enqueue(new Callback<ResponseTransaction>() {
            @Override
            public void onResponse(Call<ResponseTransaction> call, Response<ResponseTransaction> response) {
                if(response.code() == 200){
                    Toast.makeText(mContext, "Transaksi Berhasil!", Toast.LENGTH_SHORT).show();
                    productsList.clear();
                    detailAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(mContext, "Transaksi Gagal!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseTransaction> call, Throwable t) {

            }
        });
    }

    public void updateTotal() {
        total = 0d;
        for (TrxDetailModel x : productsList) {
            total += x.getPrice() * x.getQty();
        }

        tvTotal.setText("Rp. " + String.format("%,.2f", total));
    }

    private void doPreRequisites() {
        mBeepManager = new BeepManager(mActivity);
        mBeepManager.setVibrateEnabled(true);
        mBeepManager.setBeepEnabled(true);
        mBarcodeView.setStatusText("");
    }

    @Override
    public void onResume() {
        super.onResume();
        doPreRequisites();
        mBarcodeView.resume();
        doScan();
    }

    @Override
    public void onPause() {
        super.onPause();
        mBarcodeView.pause();
    }

    public void permission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (mContext.checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
                String[] permission = {Manifest.permission.CAMERA};
                mActivity.requestPermissions(permission, PERMISSION_CAMERA);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_CAMERA:
                if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Toast.makeText(getContext(), "Permission Denied...!", Toast.LENGTH_SHORT).show();
                }
        }
    }
}