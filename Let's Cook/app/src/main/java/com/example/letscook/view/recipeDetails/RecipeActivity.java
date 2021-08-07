package com.example.letscook.view.recipeDetails;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toolbar;
import android.widget.ViewFlipper;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.example.letscook.adapter.MainAdapter;
import com.example.letscook.adapter.ProductsViewAdapter;
import com.example.letscook.database.photo.Photo;
import com.example.letscook.database.product.Product;
import com.example.letscook.database.recipe.Recipe;
import com.example.letscook.database.relationships.UserMarksRecipeCrossRef;
import com.example.letscook.database.relationships.UserViewsRecipeCrossRef;
import com.example.letscook.database.typeconverters.DataConverter;
import com.example.letscook.database.user.User;
import com.example.letscook.view.AddRecipeActivity;
import com.example.letscook.R;
import com.example.letscook.database.RoomDB;
import com.example.letscook.view.home.MainActivity;
import com.example.letscook.view.products.MyProductsActivity;
import com.example.letscook.view.products.ShoppingListActivity;
import com.example.letscook.view.recipesDashboard.RecipesActivity;
import com.example.letscook.view.search.SearchActivity;
import com.example.letscook.view.search.WhatToCookActivity;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import technolifestyle.com.imageslider.FlipperLayout;
import technolifestyle.com.imageslider.FlipperView;

public class RecipeActivity extends AppCompatActivity {
    private int id;
    private TextView my_products;
    private Recipe recipe;
    private User user;
    private List<Photo> allPhotosFromRecipe = new ArrayList<>();
    private RoomDB database;
    private List<Product> productsList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private RecyclerView recyclerView;
    private ProductsViewAdapter productsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Make the activity on full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_recipe);

        // Initialize db
        database = RoomDB.getInstance(this);

        // Get the user
        String email = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("email", null);
        if (email != null) {
            user = database.userDao().getUserByEmail(email);
        }

        // Get current recipe and its images and products
        long recipeId = getIntent().getLongExtra("recipeId", -1);
        recipe = database.recipeDao().getRecipeById(recipeId);
        allPhotosFromRecipe = database.photoDao().getAllPhotosFromRecipe(recipeId);
        productsList = database.productDao().getRecipeProducts("toRecipe", recipeId);

        // Set as the recipe as viewed recipe
        database.userDao().insertUserViewsRecipeCrossRef(new UserViewsRecipeCrossRef(user.getID(), recipe.getID()));

        // Initialize my products links
        my_products = findViewById(R.id.my_products);

        my_products.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.Q)
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MyProductsActivity.class));
                Animatoo.animateZoom(RecipeActivity.this);
                my_products.setTextColor(Color.parseColor("#fef6d8"));
                my_products.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_my_products_after,0);
                my_products.getTextCursorDrawable().setColorFilter(Color.parseColor("#fef6d8"), PorterDuff.Mode.ADD);
            }
        });

        setLayout();

        // Initialize and assign variable
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);

        // Perform item selected list
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                id = item.getItemId();
                Intent intent = null;
                switch(id) {
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
            flip.setFlipInterval(3000);
            flip.startFlipping();

            CollapsingToolbarLayout layout = findViewById(R.id.collapsing_toolbar);
            layout.setTitle(recipe.getName().substring(0, 1).toUpperCase() + recipe.getName().substring(1));
            CircleImageView favourite = findViewById(R.id.favourite);
            favourite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (favourite.getDrawable().getConstantState() == getResources().getDrawable(R.drawable.ic_favorite_before).getConstantState())
                    {
                        favourite.setImageResource(R.drawable.ic_favorite_after);
                        database.userDao().insertUserMarksRecipeCrossRef(new UserMarksRecipeCrossRef(user.getID(), recipe.getID()));
                    } else
                    {
                        favourite.setImageResource(R.drawable.ic_favorite_before);
                        database.userDao().deleteUserMarksRecipeCrossRef(new UserMarksRecipeCrossRef(user.getID(), recipe.getID()));
                    }
                }
            });
            TextView category = findViewById(R.id.category);
            category.setText(String.format("%s",recipe.getCategory().substring(0, 1).toUpperCase() + recipe.getCategory().substring(1)));
            category.setSelected(true);
            category.setMovementMethod(new ScrollingMovementMethod());
            TextView prepTime = findViewById(R.id.preparing_time);
            prepTime.setText(String.format("%02d:%02dч.", recipe.getHours(), recipe.getMinutes()));
            TextView portions = findViewById(R.id.portions);
            portions.setText(String.format("%d", recipe.getPortions()));

            recyclerView = findViewById(R.id.products_view);
            linearLayoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(linearLayoutManager);
            if (user != null) {
                productsAdapter = new ProductsViewAdapter(RecipeActivity.this, productsList, recipe.getID(), user.getID());
            } else {
                productsAdapter = new ProductsViewAdapter(RecipeActivity.this, productsList, recipe.getID(), 0);
            }
            recyclerView.setAdapter(productsAdapter);

            TextView steps = findViewById(R.id.steps);
            steps.setText(String.format("%s", recipe.getSteps().substring(0, 1).toUpperCase() + recipe.getSteps().substring(1)));
        }
    }

    @Override
    protected void onStart() {
        my_products.setTextColor(Color.parseColor("#4E4E4E"));
        my_products.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_my_products,0);
        super.onStart();
    }

}