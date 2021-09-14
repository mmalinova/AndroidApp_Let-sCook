package com.example.letscook.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.letscook.R;
import com.example.letscook.database.product.Product;
import com.example.letscook.database.RoomDB;

import java.util.List;

public class ProductsForRecipeAdapter extends RecyclerView.Adapter<ProductsForRecipeAdapter.ViewHolder> {
    private List<Product> productList;
    private Activity context;

    public ProductsForRecipeAdapter(Activity context, List<Product> productList, String listType) {
        this.context = context;
        this.productList = productList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Initialize view
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.products_for_recipe, parent, false);
        view.setBackgroundColor(Color.parseColor("#36FFCFA6"));
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Initialize products data
        Product product = productList.get(position);
        holder.name.setTextColor(Color.parseColor("#4E4E4E"));
        holder.name.setText(String.valueOf(product.getName()));
        holder.quantity.setTextColor(Color.parseColor("#4E4E4E"));
        if (product.getQuantity() > 0) {
            holder.quantity.setText(String.valueOf(product.getQuantity()));
        }
        holder.unit.setTextColor(Color.parseColor("#4E4E4E"));
        holder.unit.setText(product.getMeasureUnit() == null ? "" : String.valueOf(product.getMeasureUnit()));
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Initialize variable
        TextView name, quantity, unit;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.textViewProd);
            quantity = itemView.findViewById(R.id.textViewQuantity);
            unit = itemView.findViewById(R.id.textViewUnit);
        }
    }
}
