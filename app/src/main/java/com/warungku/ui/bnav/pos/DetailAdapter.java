package com.warungku.ui.bnav.pos;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.warungku.R;
import com.warungku.models.pos.TrxDetailModel;

import java.util.List;

public class DetailAdapter extends RecyclerView.Adapter<DetailAdapter.ViewHolder> {
    private List<TrxDetailModel> productList;
    private Activity activity;

    public DetailAdapter(Activity activity, List<TrxDetailModel> productList) {
        this.activity = activity;
        this.productList = productList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_trx_detail, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DetailAdapter.ViewHolder holder, int position) {
        TrxDetailModel data = productList.get(position);
        Glide.with(activity).load(data.getProduct().getImg()).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.imageView);
        holder.code.setText(data.getProduct().getCode());
        holder.product.setText(data.getProduct().getName());
        holder.price.setText("Rp " + String.format("%,.2f", data.getPrice()));
        holder.qty.setText(String.format("%,.0f", data.getQty()));
    }

    @Override
    public int getItemCount() {return productList.size(); }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView code;
        private TextView product;
        private TextView price;
        private TextView qty;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.img_product);
            code = itemView.findViewById(R.id.tv_code);
            product = itemView.findViewById(R.id.tv_name);
            price = itemView.findViewById(R.id.tv_price);
            qty = itemView.findViewById(R.id.tv_qty);
        }
    }
}
