package com.warungku.utils.other;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;

import com.warungku.R;

public class LoadingDialog {
    Activity activity;
    AlertDialog alertDialog;

    public LoadingDialog(Activity activity) {
        this.activity = activity;
    }

    public void startDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater layoutInflater = activity.getLayoutInflater();
        builder.setView(layoutInflater.inflate(R.layout.custom_dialog, null));
        builder.setCancelable(false);
        alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();
    }

    public void dismissDialog(){
        alertDialog.dismiss();
    }

}
