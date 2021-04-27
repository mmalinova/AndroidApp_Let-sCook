package com.example.letscook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.example.letscook.view.home.MainActivity;
import com.example.letscook.view.products.MyProductsActivity;
import com.example.letscook.view.products.ShoppingListActivity;
import com.example.letscook.view.profile.ProfileActivity;
import com.example.letscook.view.search.SearchActivity;
import com.example.letscook.view.search.WhatToCookActivity;
import com.example.letscook.view.userRecipes.MyRecipesActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import static com.example.letscook.constants.Messages.*;

public class AddRecipeActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private int id;
    private ImageView backIcon;
    private TextView actionText;
    private ImageView profile, my_products;
    private NavigationView navigationView = null;
    private Spinner spinner;
    private Button addProduct, addRecipe;
    private EditText steps;
    private BottomNavigationView bottomNavigationView;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipe);

        // Initialize profile  and my products links
        profile = findViewById(R.id.profile);
        my_products = findViewById(R.id.my_products);

        // Set view according session storage
        navigationView = findViewById(R.id.login_view);

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
                    Animatoo.animateSlideDown(AddRecipeActivity.this);
                    profile.setColorFilter(Color.parseColor("#FFFEF6D8"));
                }
            }
        });
        my_products.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MyProductsActivity.class));
                Animatoo.animateSlideDown(AddRecipeActivity.this);
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
        actionText.setText(ADD_RECIPE);

        addProduct = findViewById(R.id.addProduct);
        addProduct.setEnabled(false);
        addRecipe = findViewById(R.id.addBtn);
        addRecipe.setEnabled(false);

        // Get data
        String[] units = {MEASURING_UNITS_REQ, ML, L, GR, KG, GLASS, SMALL_GLASS, SPOON, SMALL_SPOON, PINCH, PINCHES, PACKET, PACKETS};
        spinner = findViewById(R.id.spinner);
        spinner.setBackgroundColor(Color.parseColor("#56FFCFA6"));
        ArrayAdapter<String> adapter = new ArrayAdapter(this, R.layout.spiner_item, units);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        steps = findViewById(R.id.editTextPrep);
        steps.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (v.getId() == R.id.editTextPrep) {
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    switch (event.getAction()&MotionEvent.ACTION_MASK){
                        case MotionEvent.ACTION_UP:
                            v.getParent().requestDisallowInterceptTouchEvent(false);
                            break;
                    }
                }
                return false;
            }
        });
        // Initialize and assign variable
        bottomNavigationView = findViewById(R.id.bottom_nav);

//        BottomNavigation bottomNavigation = new BottomNavigation();
//        bottomNavigation.bottomNavActions(bottomNavigationView);

        // Set selected
        bottomNavigationView.setSelectedItemId(R.id.add_recipe);

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
                        return true;
                    case R.id.search:
                        intent = new Intent(getApplicationContext(), SearchActivity.class);
                        break;
                    case R.id.shopping_list:
                        intent = new Intent(getApplicationContext(), ShoppingListActivity.class);
                        break;
                }
                startActivity(intent);
                Animatoo.animateZoom(AddRecipeActivity.this);
                return true;
            }
        });
    }

    @Override
    protected void onStart() {
        profile.setColorFilter(Color.parseColor("#000000"));
        my_products.setColorFilter(Color.parseColor("#000000"));
        bottomNavigationView.setSelectedItemId(R.id.add_recipe);
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}