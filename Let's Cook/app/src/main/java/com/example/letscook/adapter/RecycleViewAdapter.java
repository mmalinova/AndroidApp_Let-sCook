package com.example.letscook.adapter;

import android.app.Activity;
import android.content.Intent;
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
import com.example.letscook.database.typeconverters.DataConverter;
import com.example.letscook.view.recipeDetails.RecipeActivity;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

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
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Initialize recipes data
        Recipe recipe = recipeList.get(position);
        // Initialize database
        database = RoomDB.getInstance(context);
        //holder.imageView.setImageResource(imagesList.get(position));
        holder.textView.setTextColor(Color.parseColor("#4E4E4E"));
        holder.textView.setText(recipe.getName());
        holder.imageView.setImageBitmap(DataConverter.byteArrayToImage(recipe.getImage()));
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (v.getContext(), RecipeActivity.class);
                intent.putExtra("recipeId", recipe.getID());
                v.getContext().startActivity(intent);
            }
        });
        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (v.getContext(), RecipeActivity.class);
                intent.putExtra("recipeId", recipe.getID());
                v.getContext().startActivity(intent);
            }
        });

        holder.favourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.favourite.getDrawable().getConstantState() == context.getResources().getDrawable(R.drawable.ic_favorite_before).getConstantState())
                {
                    holder.favourite.setImageResource(R.drawable.ic_favorite_after);
                } else
                {
                    holder.favourite.setImageResource(R.drawable.ic_favorite_before);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;
        CircleImageView favourite;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.recipe_image);
            textView = itemView.findViewById(R.id.recipe_name);
            favourite = itemView.findViewById(R.id.favourite);
        }
    }
}
