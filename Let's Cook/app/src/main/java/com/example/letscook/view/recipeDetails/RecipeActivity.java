package com.example.letscook.view.recipeDetails;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.example.letscook.view.AddRecipeActivity;
import com.example.letscook.R;
import com.example.letscook.database.RoomDB;
import com.example.letscook.view.home.MainActivity;
import com.example.letscook.view.products.MyProductsActivity;
import com.example.letscook.view.products.ShoppingListActivity;
import com.example.letscook.view.search.SearchActivity;
import com.example.letscook.view.search.WhatToCookActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import technolifestyle.com.imageslider.FlipperLayout;
import technolifestyle.com.imageslider.FlipperView;

public class RecipeActivity extends AppCompatActivity {
    private int id;
    private ImageView my_products;
    private FlipperLayout flipperLayout;
    private RoomDB database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Make the activity on full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_recipe);

        // Initialize my products links
        my_products = findViewById(R.id.my_products);

        my_products.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MyProductsActivity.class));
                Animatoo.animateZoom(RecipeActivity.this);
                my_products.setColorFilter(Color.parseColor("#fef6d8"));
            }
        });

        flipperLayout = findViewById(R.id.flipper);
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

    private void setLayout() {
        String url[] = new String[]{
                "https://www.thespruceeats.com/thmb/y7DaqIVN0WdRnpNHTJmBidyJLZE=/960x0/filters:no_upscale():max_bytes(150000):strip_icc():format(webp)/how-to-make-pancakes-from-scratch-995800-11-5b3f987cc9e77c0037d98e28.jpg",
                "https://cdn.loveandlemons.com/wp-content/uploads/2020/10/almond-flour-pancakes.jpg",
                "https://www.inspiredtaste.net/wp-content/uploads/2020/04/Vegan-Pancakes-Recipe-2-1200-1200x800.jpg"
        };
        for (int i = 0; i < url.length; i++) {
            FlipperView flipperView = new FlipperView(getBaseContext());
            flipperView.setImageUrl(url[i]);
            flipperLayout.addFlipperView(flipperView);
            flipperView.setOnFlipperClickListener(new FlipperView.OnFlipperClickListener() {
                @Override
                public void onFlipperClick(FlipperView flipperView) {

                }
            });
        }
    }

    @Override
    protected void onStart() {
        my_products.setColorFilter(Color.parseColor("#000000"));
        super.onStart();
    }

}