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

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {
    private List<Product> productList;
    private Activity context;
    private RoomDB database;
    private String listType;

    public MainAdapter(Activity context, List<Product> productList, String listType) {
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
                .inflate(R.layout.products_list_data, parent, false);
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
        holder.unit.setText(String.valueOf(product.getMeasureUnit()));
        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Product p = productList.get(holder.getAdapterPosition());
                long sID = p.getID();
                String sName = p.getName();
                float sQuantity = p.getQuantity();
                String sMeasure_unit = p.getMeasureUnit();
                // Create dialog
                Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.edit_dialog);
                int width = WindowManager.LayoutParams.MATCH_PARENT;
                int height = WindowManager.LayoutParams.WRAP_CONTENT;
                dialog.getWindow().setLayout(width, height);
                dialog.show();

                // Initialize and assign variables
                EditText name = dialog.findViewById(R.id.editName);
                EditText quantity = dialog.findViewById(R.id.editQuantity);
                EditText unit = dialog.findViewById(R.id.editUnit);
                TextView nameReq = dialog.findViewById(R.id.firstTextView);
                TextView quantityReq = dialog.findViewById(R.id.secondTextView);
                TextView unitReq = dialog.findViewById(R.id.thirdTextView);
                Button update = dialog.findViewById(R.id.update);
                update.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        nameReq.setVisibility(View.INVISIBLE);
                        quantityReq.setVisibility(View.INVISIBLE);
                        unitReq.setVisibility(View.INVISIBLE);
                        float uQuantity = 0;
                        String uName = name.getText().toString().trim();
                        String q = quantity.getText().toString().trim();
                        String uMeasure_unit = unit.getText().toString().trim();
                        String[] units = {MEASURING_UNITS, ML, L, GR, KG, GLASS, SMALL_GLASS, SPOON, SMALL_SPOON, PINCH, PINCHES, PACKET, PACKETS};
                        if (uName.equals("")) {
                            nameReq.setVisibility(View.VISIBLE);
                            return;
                        } else if (q.equals(".")) {
                            quantityReq.setVisibility(View.VISIBLE);
                            return;
                        } else if (!uMeasure_unit.equals("") && !Arrays.asList(units).contains(uMeasure_unit)) {
                            unitReq.setVisibility(View.VISIBLE);
                            return;
                        } else if (!q.equals("")) {
                            uQuantity = Float.parseFloat(q);
                            if (uQuantity <= 0) {
                                quantityReq.setVisibility(View.VISIBLE);
                                return;
                            }
                        }
                        // Update in database
                        database.productDao().update(sID, uName, uMeasure_unit, uQuantity);
                        // Notify
                        productList.clear();
                        productList.addAll(database.productDao().getUserProducts(listType));
                        notifyDataSetChanged();
                        nameReq.setVisibility(View.INVISIBLE);
                        quantityReq.setVisibility(View.INVISIBLE);
                        unitReq.setVisibility(View.INVISIBLE);
                        dialog.dismiss();

                    }
                });
                // Set text on edit text
                name.setText(sName);
                if (product.getQuantity() > 0) {
                    holder.quantity.setText(String.valueOf(product.getQuantity()));
                }
                unit.setText(sMeasure_unit);
            }
        });
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Product p = productList.get(holder.getAdapterPosition());
                database.productDao().delete(p);
                // Notify
                int position = holder.getAdapterPosition();
                productList.remove(position);
                if (productList.size() < 1) {
                    context.findViewById(R.id.recycler_view).setVisibility(View.INVISIBLE);
                    context.findViewById(R.id.textView).setVisibility(View.VISIBLE);
                }
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, productList.size());
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
        ImageView edit, delete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.textViewProd);
            quantity = itemView.findViewById(R.id.textViewQuantity);
            unit = itemView.findViewById(R.id.textViewUnit);
            edit = itemView.findViewById(R.id.update_btn);
            delete = itemView.findViewById(R.id.delete_btn);
        }
    }
}
