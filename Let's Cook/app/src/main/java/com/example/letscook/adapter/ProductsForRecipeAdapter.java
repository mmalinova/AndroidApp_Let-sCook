package com.example.letscook.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.letscook.R;
import com.example.letscook.database.product.Product;
import com.example.letscook.database.RoomDB;

import java.util.Arrays;
import java.util.List;

import static com.example.letscook.constants.Messages.GLASS;
import static com.example.letscook.constants.Messages.GR;
import static com.example.letscook.constants.Messages.KG;
import static com.example.letscook.constants.Messages.L;
import static com.example.letscook.constants.Messages.MEASURING_UNITS;
import static com.example.letscook.constants.Messages.ML;
import static com.example.letscook.constants.Messages.PACKET;
import static com.example.letscook.constants.Messages.PACKETS;
import static com.example.letscook.constants.Messages.PINCH;
import static com.example.letscook.constants.Messages.PINCHES;
import static com.example.letscook.constants.Messages.SMALL_GLASS;
import static com.example.letscook.constants.Messages.SMALL_SPOON;
import static com.example.letscook.constants.Messages.SPOON;

public class ProductsForRecipeAdapter extends RecyclerView.Adapter<ProductsForRecipeAdapter.ViewHolder> {
    private List<Product> productList;
    private Activity context;
    private RoomDB database;
    private String listType;

    public ProductsForRecipeAdapter(Activity context, List<Product> productList, String listType) {
        this.context = context;
        this.productList = productList;
        this.listType = listType;
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
        // Initialize database
        database = RoomDB.getInstance(context);
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

    public class ViewHolder extends RecyclerView.ViewHolder {
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
