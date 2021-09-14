package com.example.letscook.controller.recipeDetails;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.example.letscook.adapter.ProductsViewAdapter;
import com.example.letscook.constants.Messages;
import com.example.letscook.database.photo.Photo;
import com.example.letscook.database.product.Product;
import com.example.letscook.database.recipe.Recipe;
import com.example.letscook.database.relationships.UserMarksRecipeCrossRef;
import com.example.letscook.database.relationships.UserViewsRecipeCrossRef;
import com.example.letscook.database.typeconverters.DataConverter;
import com.example.letscook.database.user.User;
import com.example.letscook.controller.addRecipe.AddRecipeActivity;
import com.example.letscook.R;
import com.example.letscook.database.RoomDB;
import com.example.letscook.controller.home.MainActivity;
import com.example.letscook.controller.products.MyProductsActivity;
import com.example.letscook.controller.products.ShoppingListActivity;
import com.example.letscook.controller.recipesDashboard.RecipesActivity;
import com.example.letscook.controller.search.SearchActivity;
import com.example.letscook.controller.search.WhatToCookActivity;
import com.example.letscook.server_database.NetworkMonitor;
import com.example.letscook.server_database.SQLiteToMySQL.RecipeRequests;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecipeActivity extends AppCompatActivity {
    private int id;
    private TextView my_products;
    private Recipe recipe;
    private User user;
    private List<Photo> allPhotosFromRecipe = new ArrayList<>();
    private RoomDB database;
    private List<Product> productsList = new ArrayList<>();
    TextView favText;
    CircleImageView favourite;
    private long recipeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Make the activity on full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Initialize db
        database = RoomDB.getInstance(this);
        // Get the user
        String email = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("email", null);
        if (email != null) {
            user = database.userDao().getUserByEmail(email);
        }

        // Get destination
        boolean isAtMyRec = getIntent().getBooleanExtra("isAtMyRec", false);
        boolean isAtApprove = getIntent().getBooleanExtra("isAtApprove", false);
        if (isAtMyRec) {
            if (user.isAdmin()) {
                setContentView(R.layout.activity_recipe);
            } else {
                setContentView(R.layout.activity_my_recipe);
            }
        } else if (isAtApprove) {
            setContentView(R.layout.activity_to_approve_recipe);
        } else {
            setContentView(R.layout.activity_recipe);
        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Button isApproved = findViewById(R.id.is_approved);

        // Get current recipe and its images and products
        recipeId = getIntent().getLongExtra("recipeId", -1);
        recipe = database.recipeDao().getRecipeById(recipeId);
        if (isAtMyRec && !user.isAdmin()) {
            if (recipe.isApproved()) {
                isApproved.setBackgroundColor(Color.parseColor("#017330"));
                isApproved.setText(Messages.APPROVED);
            }
        } else if (isAtApprove) {
            findViewById(R.id.approve).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    database.recipeDao().approveRecipeById(recipeId);
                    if (NetworkMonitor.checkNetworkConnection(RecipeActivity.this)) {
                        recipe.setIsApproved(true);
                        RecipeRequests.recipePOST(RecipeActivity.this, recipe, database.userDao().getUserByServerID(recipe.getOwnerID()));
                    }
                    startActivity(new Intent(RecipeActivity.this, MainActivity.class));
                }
            });
            findViewById(R.id.unapprove).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    database.recipeDao().delete(recipe);
                    startActivity(new Intent(RecipeActivity.this, MainActivity.class));
                }
            });
        }
        allPhotosFromRecipe = database.photoDao().getAllPhotosFromRecipe(recipeId, recipe.getServerID());
        productsList = database.productDao().getRecipeProducts("toRecipe", recipeId, recipe.getServerID());

        // Set the recipe as viewed recipe
        database.recipeDao().insert(recipe);
        if (user != null) {
            UserViewsRecipeCrossRef byUserIDAndRecipeID = database.userViewsRecipeDao().getByLocalAndServerIDs(user.getID(), user.getServerID(), recipe.getID(), recipe.getServerID());
            if (byUserIDAndRecipeID == null) {
                UserViewsRecipeCrossRef userViewsRecipeCrossRef = new UserViewsRecipeCrossRef(user.getID(), recipe.getID(), false, 0);
                database.userDao().insertUserViewsRecipeCrossRef(userViewsRecipeCrossRef);
            }
        }

        // Initialize my products links
        my_products = findViewById(R.id.my_products);
        my_products.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.Q)
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MyProductsActivity.class));
                Animatoo.animateZoom(RecipeActivity.this);
                my_products.setTextColor(Color.parseColor("#fef6d8"));
                my_products.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_my_products_after, 0);
                my_products.getTextCursorDrawable().setColorFilter(Color.parseColor("#fef6d8"), PorterDuff.Mode.ADD);
            }
        });

        setLayout();

        // Initialize and assign variable
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);
        // Perform item selected list
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                id = item.getItemId();
                Intent intent = null;
                switch (id) {
                    case R.id.home:
                        intent = new Intent(getApplicationContext(), MainActivity.class);
                        break;
                    case R.id.what_to_cook:
                        intent = new Intent(getApplicationContext(), WhatToCookActivity.class);
                        break;
                    case R.id.add_recipe:
                        intent = new Intent(getApplicationContext(), AddRecipeActivity.class);
                        break;
                    case R.id.search:
                        intent = new Intent(getApplicationContext(), SearchActivity.class);
                        break;
                    case R.id.shopping_list:
                        intent = new Intent(getApplicationContext(), ShoppingListActivity.class);
                        break;
                }
                startActivity(intent);
                Animatoo.animateZoom(RecipeActivity.this);
                return true;
            }
        });
    }

    @SuppressLint("DefaultLocale")
    private void setLayout() {
        for (int i = 0; i < allPhotosFromRecipe.size(); i++) {
            ViewFlipper flip = findViewById(R.id.flipper);
            ImageView images = new ImageView(getApplicationContext());
            images.setImageBitmap(DataConverter.byteArrayToImage(allPhotosFromRecipe.get(i).getPhoto()));
            images.setScaleType(ImageView.ScaleType.FIT_XY);
            flip.setInAnimation(AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left));
            flip.addView(images);
            if (allPhotosFromRecipe.size() > 1) {
                flip.setFlipInterval(3000);
                flip.startFlipping();
            }
        }

        CollapsingToolbarLayout layout = findViewById(R.id.collapsing_toolbar);
        layout.setTitle(recipe.getName().substring(0, 1).toUpperCase() + recipe.getName().substring(1));
        favourite = findViewById(R.id.favourite);

        // Check if recipe is mark as favourite
        if (user != null) {
            if (favourite != null) {
                List<UserMarksRecipeCrossRef> recipesMark = database.userMarksRecipeDao().getRecipes(user.getID(), user.getServerID());
                for (UserMarksRecipeCrossRef userMarksRecipeCrossRef : recipesMark) {
                    Recipe recipeByLocalOrServerId = database.recipeDao().getRecipeByLocalOrServerId(userMarksRecipeCrossRef.getRecipe_id());
                    if (recipeByLocalOrServerId.getID() == recipeId && !userMarksRecipeCrossRef.isDeleted()) {
                        favourite.setImageResource(R.drawable.ic_favorite_after);
                        favText = findViewById(R.id.text_fav);
                        favText.setText(Messages.REMOVE_FAV);
                    }
                }
                favourite.setOnClickListener(new View.OnClickListener() {
                    @SuppressLint("UseCompatLoadingForDrawables")
                    @Override
                    public void onClick(View v) {
                        if (favourite.getDrawable().getConstantState() == getResources().getDrawable(R.drawable.ic_favorite_before).getConstantState()) {
                            favourite.setImageResource(R.drawable.ic_favorite_after);
                            database.userDao().insertUserMarksRecipeCrossRef(new UserMarksRecipeCrossRef(user.getID(), recipe.getID(), false, 0, false));
                        } else {
                            favourite.setImageResource(R.drawable.ic_favorite_before);
                            UserMarksRecipeCrossRef byUserIDAndRecipeID = database.userMarksRecipeDao().getByLocalAndServerIDs(user.getID(), user.getServerID(), recipe.getID(), recipe.getServerID());
                            if (byUserIDAndRecipeID.isIs_sync()) {
                                byUserIDAndRecipeID.setDeleted(true);
                                byUserIDAndRecipeID.setIs_sync(false);
                                byUserIDAndRecipeID.setUser_id(database.userDao().getUserByServerID(byUserIDAndRecipeID.getUser_id()).getID());
                                byUserIDAndRecipeID.setRecipe_id(database.recipeDao().getRecipeByServerId(byUserIDAndRecipeID.getRecipe_id()).getID());
                                database.userDao().insertUserMarksRecipeCrossRef(byUserIDAndRecipeID);
                            } else {
                                database.userDao().deleteUserMarksRecipeCrossRef(byUserIDAndRecipeID);
                            }
                        }
                        Objects.requireNonNull(RecipesActivity.recyclerView.getAdapter()).notifyDataSetChanged();
                    }
                });
            }
        }
        TextView category = findViewById(R.id.category);
        category.setText(String.format("%s", recipe.getCategory().substring(0, 1).toUpperCase() + recipe.getCategory().substring(1)));
        category.setSelected(true);
        category.setMovementMethod(new ScrollingMovementMethod());
        TextView prepTime = findViewById(R.id.preparing_time);
        prepTime.setText(String.format("%02d:%02dч.", recipe.getHours(), recipe.getMinutes()));
        TextView portions = findViewById(R.id.portions);
        portions.setText(String.format("%d", recipe.getPortions()));

        RecyclerView recyclerView = findViewById(R.id.products_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        ProductsViewAdapter productsAdapter;
        if (user != null) {
            productsAdapter = new ProductsViewAdapter(RecipeActivity.this, productsList, recipe.getID(), user.getID());
        } else {
            productsAdapter = new ProductsViewAdapter(RecipeActivity.this, productsList, recipe.getID(), 0);
        }
        recyclerView.setAdapter(productsAdapter);

        TextView steps = findViewById(R.id.steps);
        steps.setText(String.format("%s", recipe.getSteps().substring(0, 1).toUpperCase() + recipe.getSteps().substring(1)));
    }

    @Override
    protected void onStart() {
        my_products.setTextColor(Color.parseColor("#4E4E4E"));
        my_products.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_my_products, 0);
        super.onStart();
    }
}