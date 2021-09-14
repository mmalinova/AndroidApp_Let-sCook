package com.example.letscook.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.letscook.R;
import com.example.letscook.database.recipe.Recipe;
import com.example.letscook.database.RoomDB;
import com.example.letscook.database.relationships.UserMarksRecipeCrossRef;
import com.example.letscook.database.relationships.UserMarksRecipes;
import com.example.letscook.database.typeconverters.DataConverter;
import com.example.letscook.controller.recipeDetails.RecipeActivity;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.letscook.constants.Messages.TO_MARK;

public class RecycleViewAdapter extends RecyclerView.Adapter<RecycleViewAdapter.ViewHolder> {
    private List<Recipe> recipeList;
    private Activity context;
    private long userId;
    private boolean isAtFav;
    private boolean isAtMyRec;
    private boolean isAtApprove;
    private RoomDB database;
    private AlertDialog dialog = null;

    public RecycleViewAdapter(Activity context, List<Recipe> recipeList, long userId, boolean isAtFav, boolean isAtMyRec, boolean isAtApprove) {
        this.context = context;
        this.recipeList = recipeList;
        this.userId = userId;
        this.isAtFav = isAtFav;
        this.isAtMyRec = isAtMyRec;
        this.isAtApprove = isAtApprove;
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
        // Check for favourite recipe
        boolean isMarked = false;
        List<UserMarksRecipeCrossRef> recipesMark = database.userMarksRecipeDao().getRecipes(userId, database.userDao().getUserByID(userId).getServerID());
        for (UserMarksRecipeCrossRef userMarksRecipeCrossRef : recipesMark) {
            Recipe recipeByLocalOrServerId = database.recipeDao().getRecipeByLocalOrServerId(userMarksRecipeCrossRef.getRecipe_id());
            if (recipeByLocalOrServerId.getID() == recipe.getID() && !userMarksRecipeCrossRef.isDeleted()) {
                holder.favourite.setImageResource(R.drawable.ic_favorite_after);
                isMarked = true;
            }
        }
        if (!isMarked) {
            holder.favourite.setImageResource(R.drawable.ic_favorite_before);
        }

        holder.textView.setTextColor(Color.parseColor("#4E4E4E"));
        holder.textView.setText(recipe.getName());
        holder.imageView.setImageBitmap(DataConverter.byteArrayToImage(recipe.getImage()));
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), RecipeActivity.class);
                intent.putExtra("recipeId", recipe.getID());
                if (isAtMyRec) {
                    intent.putExtra("isAtMyRec", true);
                } else if (isAtApprove) {
                    intent.putExtra("isAtApprove", true);
                }
                v.getContext().startActivity(intent);
            }
        });
        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), RecipeActivity.class);
                intent.putExtra("recipeId", recipe.getID());
                if (isAtMyRec) {
                    intent.putExtra("isAtMyRec", true);
                } else if (isAtApprove) {
                    intent.putExtra("isAtApprove", true);
                }
                v.getContext().startActivity(intent);
            }
        });
        holder.favourite.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onClick(View v) {
                if (userId != 0) {
                    if (holder.favourite.getDrawable().getConstantState() == context.getResources().getDrawable(R.drawable.ic_favorite_before).getConstantState()) {
                        holder.favourite.setImageResource(R.drawable.ic_favorite_after);
                        database.userDao().insertUserMarksRecipeCrossRef(new UserMarksRecipeCrossRef(userId, recipe.getID(), false, 0, false));
                        notifyDataSetChanged();
                    } else {
                        holder.favourite.setImageResource(R.drawable.ic_favorite_before);
                        UserMarksRecipeCrossRef byUserIDAndRecipeID = database.userMarksRecipeDao().getByLocalAndServerIDs(userId, database.userDao().getUserByID(userId).getServerID(), recipe.getID(), recipe.getServerID());
                        if (byUserIDAndRecipeID.isIs_sync()) {
                            byUserIDAndRecipeID.setDeleted(true);
                            byUserIDAndRecipeID.setIs_sync(false);
                            byUserIDAndRecipeID.setUser_id(database.userDao().getUserByServerID(byUserIDAndRecipeID.getUser_id()).getID());
                            byUserIDAndRecipeID.setRecipe_id(database.recipeDao().getRecipeByServerId(byUserIDAndRecipeID.getRecipe_id()).getID());
                            database.userDao().insertUserMarksRecipeCrossRef(byUserIDAndRecipeID);
                        } else {
                            database.userDao().deleteUserMarksRecipeCrossRef(byUserIDAndRecipeID);
                        }
                        notifyDataSetChanged();
                        if (isAtFav) {
                            notifyItemRemoved(position);
                            recipeList.remove(recipe);
                            if (getItemCount() <= 0) {
                                context.findViewById(R.id.recycler_view).setVisibility(View.INVISIBLE);
                                context.findViewById(R.id.textView).setVisibility(View.VISIBLE);
                            }
                        }
                    }
                } else {
                    deniedDialog();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
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

    public void deniedDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        final View popupView = context.getLayoutInflater().inflate(R.layout.denied_access, null);
        TextView textView = popupView.findViewById(R.id.veg_question);
        textView.setText(TO_MARK);

        Button okButton = popupView.findViewById(R.id.okBtn);

        dialogBuilder.setView(popupView);
        dialog = dialogBuilder.create();
        dialog.show();

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    /**
     * No Predictive Animations GridLayoutManager
     */
    private static class NpaGridLayoutManager extends GridLayoutManager {
        /**
         * Disable predictive animations. There is a bug in RecyclerView which causes views that
         * are being reloaded to pull invalid ViewHolders from the internal recycler stack if the
         * adapter size has decreased since the ViewHolder was recycled.
         */
        @Override
        public boolean supportsPredictiveItemAnimations() {
            return false;
        }

        public NpaGridLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
        }

        public NpaGridLayoutManager(Context context, int spanCount) {
            super(context, spanCount);
        }

        public NpaGridLayoutManager(Context context, int spanCount, int orientation, boolean reverseLayout) {
            super(context, spanCount, orientation, reverseLayout);
        }
    }
}
