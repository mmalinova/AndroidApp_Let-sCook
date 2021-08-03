package com.example.letscook.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.letscook.R;
import com.example.letscook.database.product.Product;
import com.example.letscook.database.RoomDB;

import java.util.List;

public class ProductsViewAdapter extends RecyclerView.Adapter<ProductsViewAdapter.ViewHolder> {
    private List<Product> productList;
    private Activity context;
    private RoomDB database;
    private long ownerId;
    private long userId;

    public ProductsViewAdapter(Activity context, List<Product> productList, long ownerId, long userId) {
        this.context = context;
        this.productList = productList;
        this.ownerId = ownerId;
        this.userId = userId;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Initialize view
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recipe_product_list, parent, false);
        view.setBackgroundColor(Color.parseColor("#36FFCFA6"));
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Initialize products data
        Product product = productList.get(position);
        // Initialize database
        database = RoomDB.getInstance(context);

        holder.name.setTextColor(Color.parseColor("#4E4E4E"));
        holder.name.setText(String.valueOf(product.getName().substring(0, 1).toUpperCase() + product.getName().substring(1)));
        holder.quantity.setTextColor(Color.parseColor("#4E4E4E"));
        if (product.getQuantity() > 0) {
            holder.quantity.setText(String.valueOf(product.getQuantity()));
        }
        holder.unit.setTextColor(Color.parseColor("#4E4E4E"));
        holder.unit.setText(String.valueOf(product.getMeasureUnit()));
        holder.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Product toBuy = new Product();
                toBuy.setOwnerId(userId);
                toBuy.setBelonging("shoppingList");
                toBuy.setQuantity(product.getQuantity());
                toBuy.setMeasureUnit(product.getMeasureUnit());
                toBuy.setName(String.valueOf(product.getName().substring(0, 1).toUpperCase() + product.getName().substring(1)));

                holder.add.setImageResource(R.drawable.ic_add_shopping_after);
                // Insert in db
                database.productDao().insert(toBuy);
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // Initialize variable
        TextView name, quantity, unit;
        ImageView add;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.product_name);
            quantity = itemView.findViewById(R.id.quantity);
            unit = itemView.findViewById(R.id.measure);
            add = itemView.findViewById(R.id.add_to_list);
        }
    }
}
