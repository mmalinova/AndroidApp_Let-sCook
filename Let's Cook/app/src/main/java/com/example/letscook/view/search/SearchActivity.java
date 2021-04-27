package com.example.letscook.view.search;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.example.letscook.AddRecipeActivity;
import com.example.letscook.view.products.MyProductsActivity;
import com.example.letscook.R;
import com.example.letscook.view.products.ShoppingListActivity;
import com.example.letscook.view.home.MainActivity;
import com.example.letscook.view.profile.ProfileActivity;
import com.example.letscook.view.recipesDashboard.RecipesActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import static com.example.letscook.constants.Messages.*;

public class SearchActivity extends AppCompatActivity {
    private int id;
    private ImageView backIcon;
    private TextView actionText;
    private ImageView profile, my_products;
    private NavigationView navigationView = null;
    private ListView listView;
    private String[] categories = {BREAKFAST, LUNCH, DINNER, DESSERT};
    int[] listImages = {R.drawable.breakfast, R.drawable.lunch, R.drawable.dinner, R.drawable.dessert};
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog = null;
    private Button yesButton, noButton;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

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
                    Animatoo.animateSlideDown(SearchActivity.this);
                    profile.setColorFilter(Color.parseColor("#FFFEF6D8"));
                }
            }
        });
        my_products.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MyProductsActivity.class));
                Animatoo.animateSlideDown(SearchActivity.this);
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
        actionText.setText(SEARCH_RECIPE);

        // Initialize list view
        listView = findViewById(R.id.list);
        CustomAdapter customAdapter = new CustomAdapter();
        listView.setAdapter(customAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                termsDialog(position);
            }
        });

        // Initialize and assign variable
        bottomNavigationView = findViewById(R.id.bottom_nav);

        // Set selected
        bottomNavigationView.setSelectedItemId(R.id.search);

        // Perform item selected list
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
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
                        return true;
                    case R.id.shopping_list:
                        intent = new Intent(getApplicationContext(), ShoppingListActivity.class);
                        break;
                }
                startActivity(intent);
                Animatoo.animateZoom(SearchActivity.this);
                return true;
            }
        });
    }

    public void termsDialog(int position) {
        dialogBuilder = new AlertDialog.Builder(this);
        final View popupView = getLayoutInflater().inflate(R.layout.veg_popup, null);

        yesButton = popupView.findViewById(R.id.yesBtn);
        noButton = popupView.findViewById(R.id.noBtn);

        dialogBuilder.setView(popupView);
        dialog = dialogBuilder.create();
        dialog.show();

        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start activity
                Intent intent = new Intent(getApplicationContext(), RecipesActivity.class);
                intent.putExtra("vegetarian", 0);
                intent.putExtra("category", categories[position]);
                startActivity(intent);
            }
        });
        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start activity
                Intent intent = new Intent(getApplicationContext(), RecipesActivity.class);
                intent.putExtra("vegetarian", 1);
                intent.putExtra("category", categories[position]);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        profile.setColorFilter(Color.parseColor("#000000"));
        my_products.setColorFilter(Color.parseColor("#000000"));
        bottomNavigationView.setSelectedItemId(R.id.search);
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
                if (!getIntent().getBooleanExtra("isFromMain", false)) {
                    overridePendingTransition(0,0);
                }
            }
        } else {
            super.onBackPressed();
            if (!getIntent().getBooleanExtra("isFromMain", false)) {
                overridePendingTransition(0,0);
            }
        }
    }

    private class CustomAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return listImages.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = getLayoutInflater().inflate(R.layout.category_list_data, null);

            TextView category = view.findViewById(R.id.cat);
            ImageView image = view.findViewById(R.id.image);

            category.setText(categories[position]);
            image.setImageResource(listImages[position]);

            return view;
        }
    }
}