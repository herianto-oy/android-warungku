package com.warungku.ui.bnav.transaction;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.warungku.R;
import com.warungku.models.pos.TransactionModel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {
    private Activity activity;
    private List<TransactionModel> transactionList;

    public TransactionAdapter(Activity activity, List<TransactionModel> transactionList) {
        this.activity = activity;
        this.transactionList = transactionList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_transaction, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        TransactionModel data = transactionList.get(position);
        holder.name.setText(data.getName());
        holder.date.setText(simpleDateFormat.format(new Date(data.getTimestamp())));
        String status = (data.getStatus() == 1) ? "Berhasil" : "Dibatalkan";
        holder.status.setText(status);
        holder.total.setText("Rp " + String.format("%,.2f", data.getTotal()));

        holder.details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putInt("id", transactionList.get(position).getId());
                activity.startActivity(new Intent(activity, DetailActivity.class).putExtras(bundle));
            }
        });
    }

    @Override
    public int getItemCount() { return transactionList.size(); }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private TextView date;
        private TextView status;
        private TextView total;
        private TextView details;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tv_name);
            date = itemView.findViewById(R.id.tv_date);
            status = itemView.findViewById(R.id.tv_status);
            total = itemView.findViewById(R.id.tv_total);
            details = itemView.findViewById(R.id.btn_details);
        }
    }
}
