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
import com.example.letscook.database.recipe.Recipe;
import com.example.letscook.database.RoomDB;

import java.util.List;

public class RecycleViewAdapter extends RecyclerView.Adapter<RecycleViewAdapter.ViewHolder> {
    private List<Recipe> recipeList;
    private Activity context;
    private RoomDB database;

    public RecycleViewAdapter(Activity context, List<Recipe> recipeList) {
        this.context = context;
        this.recipeList = recipeList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Initialize view
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recipes_list_data, parent, false);
        //view.setBackgroundColor(Color.parseColor("#36FFCFA6"));
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Initialize recipes data
        Recipe recipe = recipeList.get(position);
        // Initialize database
        database = RoomDB.getInstance(context);
        holder.imageView.setImageResource((int) recipe.getImages());
        holder.textView.setTextColor(Color.parseColor("#4E4E4E"));
        holder.textView.setText(recipe.getName());
    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.recipe_image);
            textView = itemView.findViewById(R.id.recipe_name);
        }
    }
}
