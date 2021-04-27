package com.example.letscook.view.recipesDashboard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.example.letscook.AddRecipeActivity;
import com.example.letscook.view.products.MyProductsActivity;
import com.example.letscook.view.profile.ProfileActivity;
import com.example.letscook.R;
import com.example.letscook.view.recipeDetails.RecipeActivity;
import com.example.letscook.view.search.SearchActivity;
import com.example.letscook.view.products.ShoppingListActivity;
import com.example.letscook.view.search.WhatToCookActivity;
import com.example.letscook.adapter.RecycleViewAdapter;
import com.example.letscook.database.recipe.Recipe;
import com.example.letscook.database.RoomDB;
import com.example.letscook.view.home.MainActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

import static com.example.letscook.constants.Messages.BREAKFAST;
import static com.example.letscook.constants.Messages.BREAKFAST_MESS;
import static com.example.letscook.constants.Messages.DESSERT;
import static com.example.letscook.constants.Messages.DESSERT_MESS;
import static com.example.letscook.constants.Messages.DINNER;
import static com.example.letscook.constants.Messages.DINNER_MESS;
import static com.example.letscook.constants.Messages.LUNCH;
import static com.example.letscook.constants.Messages.LUNCH_MESS;

public class RecipesActivity extends AppCompatActivity {
    private int id;
    private ImageView backIcon, profile, my_products;
    private TextView actionText, textView;
    private NavigationView navigationView = null;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private List<Recipe> dataList = new ArrayList<>();
    private RoomDB database;
    private RecycleViewAdapter recycleViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipes);

        // Initialize profile  and my products links
        profile = findViewById(R.id.profile);
        my_products = findViewById(R.id.my_products);

        // Set view according session storage
        //navigationView = findViewById(R.id.login_view);

        // Add click event listeners
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (navigationView != null) {
                    if (navigationView.getVisibility() == View.INVISIBLE) {
                        navigationView.setVisibility(View.VISIBLE);
                        profile.setColorFilter(Color.parseColor("#FFFEF6D8"));

//                      Button button = findViewById(R.id.login_btn);
//                      button.setOnClickListener(new View.OnClickListener() {
//                        public void onClick(View v) {
//                            Toast.makeText(getApplicationContext(),"Clicked", Toast.LENGTH_LONG).show();
//                        }
//                      });
                    } else {
                        navigationView.setVisibility(View.INVISIBLE);
                        profile.setColorFilter(Color.parseColor("#000000"));
                    }
                } else {
                    startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                    Animatoo.animateSlideDown(RecipesActivity.this);
                    profile.setColorFilter(Color.parseColor("#FFFEF6D8"));
                }
            }
        });
        my_products.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MyProductsActivity.class));
                Animatoo.animateSlideDown(RecipesActivity.this);
                my_products.setColorFilter(Color.parseColor("#FFFEF6D8"));
            }
        });

        // Initialize action bar variables
        backIcon = findViewById(R.id.back_icon);
        actionText = findViewById(R.id.action_bar_text);

        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });
        textView = findViewById(R.id.textView);
        recyclerView = findViewById(R.id.recycler_view);
        layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);
        recycleViewAdapter = new RecycleViewAdapter(RecipesActivity.this, dataList);
        recyclerView.setAdapter(recycleViewAdapter);
        recyclerView.setHasFixedSize(true);

        String phrase = getIntent().getStringExtra("phrase");
        String text = getIntent().getStringExtra("text");
        String category = getIntent().getStringExtra("category");
        if (phrase != null) {
            actionText.setText(phrase);
            if (text != null) {
                textView.setText(text);
            }
        } else if (category != null) {
            switch (category) {
                case BREAKFAST:
                    actionText.setText(BREAKFAST_MESS);
                    break;
                case LUNCH:
                    actionText.setText(LUNCH_MESS);
                    break;
                case DINNER:
                    actionText.setText(DINNER_MESS);
                    break;
                case DESSERT:
                    actionText.setText(DESSERT_MESS);
                    break;
            }
        }

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
                Animatoo.animateZoom(RecipesActivity.this);
                return true;
            }
        });
    }

    @Override
    protected void onStart() {
        profile.setColorFilter(Color.parseColor("#000000"));
        my_products.setColorFilter(Color.parseColor("#000000"));
        super.onStart();
    }

    @Override
    public void onBackPressed() {
        if (navigationView != null) {
            if (navigationView.getVisibility() == View.VISIBLE) {
                navigationView.setVisibility(View.INVISIBLE);
                profile.setColorFilter(Color.parseColor("#000000"));
            } else {
                super.onBackPressed();
            }
        }
        super.onBackPressed();
    }
}