package com.example.letscook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.database.Product;
import com.example.database.RoomDB;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

import static com.example.constants.Messages.*;

public class MyProductsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    private int id;
    private ImageView backIcon, profile, my_products;
    private TextView actionText, textView;
    private NavigationView navigationView = null;
    private Button addBtn, deleteAll, addedBtn;
    private ConstraintLayout constraintLayout = null;
    private Spinner spinner;
    private EditText name, quantity;
    private RecyclerView recyclerView;
    private List<Product> dataList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private RoomDB database;
    private MainAdapter mainAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list);

        // Initialize profile  and my products links
        profile = findViewById(R.id.profile);
        my_products = findViewById(R.id.my_products);
        my_products.setColorFilter(Color.parseColor("#FFFEF6D8"));

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
                    profile.setColorFilter(Color.parseColor("#FFFEF6D8"));
                }
            }
        });
        // Initialize action bar variables
        backIcon = findViewById(R.id.back_icon);
        actionText = findViewById(R.id.action_bar_text);

        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                overridePendingTransition(0, 0);
            }
        });
        actionText.setText(MY_PRODUCTS);

        // Initialize attributes
        addBtn = findViewById(R.id.addProdBtn);
        name = findViewById(R.id.editTextProduct);
        quantity = findViewById(R.id.editTextQuantity);
        textView = findViewById(R.id.textView);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Set layout visible
                constraintLayout = findViewById(R.id.layout);
                constraintLayout.setVisibility(View.VISIBLE);
                ConstraintLayout constraintLayout = findViewById(R.id.constraint);
                ConstraintSet constraintSet = new ConstraintSet();
                constraintSet.clone(constraintLayout);
                constraintSet.connect(R.id.recycler_view, ConstraintSet.TOP, R.id.delAllProd, ConstraintSet.BOTTOM,520);
                constraintSet.applyTo(constraintLayout);
            }
        });
        recyclerView = findViewById(R.id.recycler_view);

        // Get data
        String[] units = {MEASURING_UNITS, ML, L, GR, KG, GLASS, SMALL_GLASS, SPOON, SMALL_SPOON, PINCH, PINCHES, PACKET, PACKETS};
        spinner = findViewById(R.id.spinner);
        spinner.setBackgroundColor(Color.parseColor("#56FFCFA6"));
        ArrayAdapter<String> adapter = new ArrayAdapter(this, R.layout.spiner_item, units);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        // Initialize db
        database = RoomDB.getInstance(this);
        // Store db value in product list
        dataList = database.productDao().getAllProducts();
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        mainAdapter = new MainAdapter(MyProductsActivity.this, dataList);
        recyclerView.setAdapter(mainAdapter);

        deleteAll = findViewById(R.id.delAllProd);
        deleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                database.productDao().deleteAll(dataList);
                dataList.clear();
                dataList.addAll(database.productDao().getAllProducts());
                mainAdapter.notifyDataSetChanged();
            }
        });
        addedBtn = findViewById(R.id.added);
        addedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.firstTextView).setVisibility(View.INVISIBLE);
                findViewById(R.id.secondTextView).setVisibility(View.INVISIBLE);
                String sName = name.getText().toString().trim();
                String sQuantity = quantity.getText().toString().trim();
                String sMeasure_unit = spinner.getSelectedItem().toString();
                if (!sName.equals("")) {
                    if (sMeasure_unit.contains("Мерна")) {
                        sMeasure_unit = "";
                    }
                    if (sQuantity.equals("0")) {
                        findViewById(R.id.secondTextView).setVisibility(View.VISIBLE);
                        return;
                    }
                    Product product = new Product();
                    product.setName(sName);
                    product.setMeasure_unit(sMeasure_unit);
                    product.setQuantity(sQuantity);
                    // Insert in db
                    database.productDao().insert(product);
                    // Clear edit texts
                    name.setText("");
                    spinner.setSelection(0);
                    quantity.setText("");
                    dataList.clear();
                    dataList.addAll(database.productDao().getAllProducts());
                    mainAdapter.notifyDataSetChanged();
                    findViewById(R.id.firstTextView).setVisibility(View.INVISIBLE);
                    constraintLayout.setVisibility(View.INVISIBLE);
                    ConstraintLayout constraintLayout = findViewById(R.id.constraint);
                    ConstraintSet constraintSet = new ConstraintSet();
                    constraintSet.clone(constraintLayout);
                    constraintSet.connect(R.id.recycler_view,ConstraintSet.TOP,R.id.delAllProd,ConstraintSet.BOTTOM,48);
                    constraintSet.applyTo(constraintLayout);
                } else {
                    findViewById(R.id.firstTextView).setVisibility(View.VISIBLE);
                }
            }
        });
        // Initialize and assign variable
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);

        // Perform item selected list
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                id = item.getItemId();
                switch(id) {
                    case R.id.home:
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.what_to_cook:
                        startActivity(new Intent(getApplicationContext(), WhatToCookActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.add_recipe:
                        startActivity(new Intent(getApplicationContext(), AddRecipeActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.search:
                        startActivity(new Intent(getApplicationContext(), SearchActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.shopping_list:
                        startActivity(new Intent(getApplicationContext(), ShoppingListActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    protected void onStart() {
        profile.setColorFilter(Color.parseColor("#000000"));
        super.onStart();
    }

    @Override
    public void onBackPressed() {
        if (constraintLayout != null ) {
            if (constraintLayout.getVisibility() == View.VISIBLE) {
                constraintLayout.setVisibility(View.INVISIBLE);
                ConstraintLayout constraintLayout = findViewById(R.id.constraint);
                ConstraintSet constraintSet = new ConstraintSet();
                constraintSet.clone(constraintLayout);
                constraintSet.connect(R.id.recycler_view,ConstraintSet.TOP,R.id.delAllProd,ConstraintSet.BOTTOM,48);
                constraintSet.applyTo(constraintLayout);
                return;
            }
        }
        if (navigationView != null) {
            if (navigationView.getVisibility() == View.VISIBLE) {
                navigationView.setVisibility(View.INVISIBLE);
                profile.setColorFilter(Color.parseColor("#000000"));
            } else {
                super.onBackPressed();
            }
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Object pos = parent.getItemAtPosition(position);
        if (!pos.equals("Мерна единица:")) {
            String text = pos.toString();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}