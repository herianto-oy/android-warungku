package com.warungku.ui.bnav.account;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.warungku.MainActivity;
import com.warungku.R;
import com.warungku.models.user.UserModel;
import com.warungku.utils.api.Api;
import com.warungku.utils.api.response.ResponseUser;
import com.warungku.utils.other.LoadingDialog;
import com.warungku.utils.retrofit.RetrofitClient;
import com.warungku.utils.retrofit.SessionManager;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountFragment extends Fragment {
    private CircleImageView img;
    private TextView name;
    private TextView desc;
    private TextView owner;
    private TextView email;
    private TextView phoneNumber;
    private TextView countToday;
    private TextView countWeek;
    private TextView address;
    private LinearLayout btnLogout;
    private LinearLayout btnEdit;

    private Api api;
    private SessionManager sessionManager;
    private Activity activity;
    private UserModel user;
    private LoadingDialog loadingDialog;
    private int EDIT_REQUEST_CODE = 1996;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_account, container, false);
        img = root.findViewById(R.id.pp);
        name = root.findViewById(R.id.tv_name);
        desc = root.findViewById(R.id.tv_desc);
        owner = root.findViewById(R.id.tv_owner);
        email = root.findViewById(R.id.tv_email);
        phoneNumber = root.findViewById(R.id.tv_phone);
        address = root.findViewById(R.id.tv_address);
        countToday = root.findViewById(R.id.count_today);
        countWeek = root.findViewById(R.id.count_week);
        btnLogout = root.findViewById(R.id.btn_logout);
        btnEdit = root.findViewById(R.id.btn_edit);
        api = RetrofitClient.getInstance().getApi();
        activity = getActivity();
        loadingDialog = new LoadingDialog(activity);

        sessionManager = new SessionManager(getContext());
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SessionManager sessionManager = new SessionManager(getContext());
                sessionManager.logoutSession();
                ((MainActivity) getActivity()).checkSession();
            }
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user != null) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("id", user.getId());
                    bundle.putString("img", user.getImg());
                    bundle.putString("owner", user.getOwner());
                    bundle.putString("phoneNumber", user.getPhoneNumber());
                    bundle.putString("email", user.getEmail());
                    bundle.putString("name", user.getName());
                    bundle.putString("desc", user.getDescription());
                    bundle.putString("address", user.getAddress());
                    startActivityForResult(new Intent(getActivity(), EditUserActivity.class).putExtras(bundle), EDIT_REQUEST_CODE);
                }
            }
        });
        getData();
        return root;
    }

    private void getData() {
        loadingDialog.startDialog();
        api.findUserById(sessionManager.getUserId()).enqueue(new Callback<ResponseUser>() {
            @Override
            public void onResponse(Call<ResponseUser> call, Response<ResponseUser> response) {
                loadingDialog.dismissDialog();
                if (response.code() == 200) {
                    user = response.body().getData();
                    setData();
                    countToday.setText(String.valueOf(response.body().getTrxToday()));
                    countWeek.setText(String.valueOf(response.body().getTrxWeek()));
                } else {
                    Log.e("ERROR", String.valueOf(response.code()));
                    Toast.makeText(getContext(), "Sesi telah berakhir...", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseUser> call, Throwable t) {
                loadingDialog.dismissDialog();
                Log.e("ERROR", t.toString());
                Toast.makeText(getContext(), "Server Error!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setData() {
        if (user != null) {
            Glide.with(activity).load(user.getImg()).diskCacheStrategy(DiskCacheStrategy.ALL).into(img);
            name.setText(user.getName());

            if (user.getDescription() == null || user.getDescription().equals("")) {
                desc.setText("Belum diatur");
            } else {
                desc.setText(user.getDescription());
            }

            owner.setText(user.getOwner());
            email.setText(user.getEmail());

            if (user.getPhoneNumber() == null || user.getPhoneNumber().equals("")) {
                phoneNumber.setText("Belum diatur");
            } else {
                phoneNumber.setText(user.getPhoneNumber());
            }
            address.setText(user.getAddress());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EDIT_REQUEST_CODE) {
            getData();
        }
    }
}
