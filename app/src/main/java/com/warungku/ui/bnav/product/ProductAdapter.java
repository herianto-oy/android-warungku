package com.warungku.ui.bnav.product;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.warungku.R;
import com.warungku.models.product.ProductModel;
import com.warungku.utils.api.Api;
import com.warungku.utils.api.response.ResponseProduct;
import com.warungku.utils.retrofit.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {
    private List<ProductModel> productList;
    private Activity activity;

    public ProductAdapter(Activity activity, List<ProductModel> productList) {
        this.productList = productList;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProductModel data = productList.get(position);
        Glide.with(activity).load(data.getImg()).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.imageView);
        holder.code.setText(data.getCode());
        holder.name.setText(data.getName());
        holder.price.setText("Rp " + String.format("%,.2f", data.getPrice()));
        holder.stock.setText(String.format("%,.0f", data.getStock()));

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                CharSequence[] menuChoose = {"Ubah", "Hapus"};
                AlertDialog.Builder dialog = new AlertDialog.Builder(v.getContext())
                        .setTitle("Pilih Aksi").setItems(menuChoose, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        onEditProduct(position, activity);
                                        break;
                                    case 1:
                                        onDeleteProduct(position, activity);
                                        break;
                                }
                            }
                        });
                dialog.create();
                dialog.show();
                return true;
            }
        });
    }


    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView code;
        private TextView name;
        private TextView price;
        private TextView stock;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.img_product);
            code = itemView.findViewById(R.id.tv_code);
            name = itemView.findViewById(R.id.tv_name);
            price = itemView.findViewById(R.id.tv_price);
            stock = itemView.findViewById(R.id.tv_stock);
        }
    }

    private void onEditProduct(int position, Activity activity) {
        Bundle bundle = new Bundle();
        bundle.putInt("id", productList.get(position).getId());
        bundle.putString("img", productList.get(position).getImg());
        bundle.putString("code", productList.get(position).getCode());
        bundle.putString("name", productList.get(position).getName());
        bundle.putDouble("price", productList.get(position).getPrice());
        bundle.putDouble("stock", productList.get(position).getStock());
        activity.startActivity(new Intent(activity, ProductFormActivity.class).putExtras(bundle));
        notifyItemRangeChanged(position, productList.size());
    }

    private void onDeleteProduct(int position, Activity activity) {
        Api api = RetrofitClient.getInstance().getApi();
        api.deleteProduct(productList.get(position).getId()).enqueue(new Callback<ResponseProduct>() {
            @Override
            public void onResponse(Call<ResponseProduct> call, Response<ResponseProduct> response) {
                if (response.code() == 200) {
                    Toast.makeText(activity, "Hapus Berhasil!", Toast.LENGTH_SHORT).show();
                    productList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, productList.size());
                } else {
                    Toast.makeText(activity, "Hapus Gagal!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseProduct> call, Throwable t) {
                Toast.makeText(activity, "Server Error!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
