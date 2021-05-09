package com.example.letscook.view;

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
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.example.letscook.R;
import com.example.letscook.database.RoomDB;
import com.example.letscook.database.typeconverters.DataConverter;
import com.example.letscook.database.user.User;
import com.example.letscook.view.home.MainActivity;
import com.example.letscook.view.products.MyProductsActivity;
import com.example.letscook.view.products.ShoppingListActivity;
import com.example.letscook.view.profile.ProfileActivity;
import com.example.letscook.view.search.SearchActivity;
import com.example.letscook.view.search.WhatToCookActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.letscook.constants.Messages.*;

public class AddRecipeActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private int id;
    private ImageView backIcon;
    private TextView actionText;
    private ImageView my_products;
    private CircleImageView profile;
    private Spinner spinner;
    private Button addProduct, addRecipe, browseBtn;
    private RadioGroup firstRG, secondRG, vegRG;
    private EditText recipeName, portions, productName, quantity, steps, hours, minutes;
    private BottomNavigationView bottomNavigationView;
    private RoomDB database;
    private User user;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipe);

        // Initialize profile  and my products links
        profile = findViewById(R.id.profile);
        my_products = findViewById(R.id.my_products);

        // Initialize db
        database = RoomDB.getInstance(this);
        // Set view according session storage
        String e = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("email", null);
        if (e == null) {
            profile.setImageResource(R.drawable.ic_profile);
        } else {
            user = database.userDao().getUserByEmail(e);
            if (user.getPhoto() != null) {
                profile.setImageBitmap(DataConverter.byteArrayToImage(user.getPhoto()));
            } else {
                profile.setImageResource(R.drawable.ic_profile_photo);
            }
        }

        // Add click event listeners
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(intent);
                Animatoo.animateSlideDown(AddRecipeActivity.this);
                profile.setBorderColor(Color.parseColor("#FFFEF6D8"));
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
                AddRecipeActivity.super.onBackPressed();
            }
        });
        actionText.setText(ADD_RECIPE);

        recipeName = findViewById(R.id.editTextName);
        firstRG = findViewById(R.id.radioGroup);
        secondRG = findViewById(R.id.secondRadioGroup);
        vegRG = findViewById(R.id.radioGroupVeg);
        browseBtn = findViewById(R.id.uploadBtn);
        portions = findViewById(R.id.editTextPortion);
        productName = findViewById(R.id.editTextProd);
        quantity = findViewById(R.id.editTextQuantity);

        // Get data
        String[] units = {MEASURING_UNITS_REQ, ML, L, GR, KG, GLASS, SMALL_GLASS, SPOON, SMALL_SPOON, PINCH, PINCHES, PACKET, PACKETS};
        spinner = findViewById(R.id.spinner);
        spinner.setBackgroundColor(Color.parseColor("#56FFCFA6"));
        ArrayAdapter<String> adapter = new ArrayAdapter(this, R.layout.spiner_item, units);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        addProduct = findViewById(R.id.addProduct);
        addProduct.setEnabled(false);

        steps = findViewById(R.id.editTextPrep);
        steps.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (v.getId() == R.id.editTextPrep) {
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    switch (event.getAction() & MotionEvent.ACTION_MASK) {
                        case MotionEvent.ACTION_UP:
                            v.getParent().requestDisallowInterceptTouchEvent(false);
                            break;
                    }
                }
                return false;
            }
        });

        hours = findViewById(R.id.editTextH);
        minutes = findViewById(R.id.editTextMin);

        addRecipe = findViewById(R.id.addBtn);
        addRecipe.setEnabled(false);

        // Initialize and assign variable
        bottomNavigationView = findViewById(R.id.bottom_nav);

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
        profile.setBorderColor(Color.parseColor("#000000"));
        my_products.setColorFilter(Color.parseColor("#000000"));
        // Set view according session storage
        String e = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("email", null);
        if (e == null) {
            profile.setImageResource(R.drawable.ic_profile);
        } else {
            user = database.userDao().getUserByEmail(e);
            if (user.getPhoto() != null) {
                profile.setImageBitmap(DataConverter.byteArrayToImage(user.getPhoto()));
            } else {
                profile.setImageResource(R.drawable.ic_profile_photo);
            }
        }
        bottomNavigationView.setSelectedItemId(R.id.add_recipe);
        super.onStart();
    }

    @Override
    protected void onResume() {
        profile.setBorderColor(Color.parseColor("#000000"));
        my_products.setColorFilter(Color.parseColor("#000000"));
        // Set view according session storage
        String e = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("email", null);
        if (e == null) {
            profile.setImageResource(R.drawable.ic_profile);
        } else {
            user = database.userDao().getUserByEmail(e);
            if (user.getPhoto() != null) {
                profile.setImageBitmap(DataConverter.byteArrayToImage(user.getPhoto()));
            } else {
                profile.setImageResource(R.drawable.ic_profile_photo);
            }
        }
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (!getIntent().getBooleanExtra("isFromMain", false)) {
            overridePendingTransition(0, 0);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}