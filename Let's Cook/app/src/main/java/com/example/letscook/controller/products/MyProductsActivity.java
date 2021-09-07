package com.example.letscook.controller.products;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.example.letscook.controller.addRecipe.AddRecipeActivity;
import com.example.letscook.R;
import com.example.letscook.adapter.MainAdapter;
import com.example.letscook.database.product.Product;
import com.example.letscook.database.RoomDB;
import com.example.letscook.database.typeconverters.DataConverter;
import com.example.letscook.database.user.User;
import com.example.letscook.controller.home.MainActivity;
import com.example.letscook.controller.profile.ProfileActivity;
import com.example.letscook.controller.search.SearchActivity;
import com.example.letscook.controller.search.WhatToCookActivity;
import com.example.letscook.server_database.NetworkMonitor;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.letscook.constants.Messages.*;

public class MyProductsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private int id;
    private ImageView my_products;
    private CircleImageView profile;
    private TextView textView;
    private ConstraintLayout constraintLayout = null;
    private Spinner spinner;
    private EditText name, quantity;
    private RecyclerView recyclerView;
    private List<Product> dataList = new ArrayList<>();
    private RoomDB database;
    private MainAdapter mainAdapter;
    private BottomNavigationView bottomNavigationView;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog = null;
    private Button okButton;
    private ColorStateList myList;
    private User user;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list);

        // Initialize profile  and my products links
        profile = findViewById(R.id.profile);
        my_products = findViewById(R.id.my_products);
        my_products.setColorFilter(Color.parseColor("#FFFEF6D8"));

        // Initialize db
        database = RoomDB.getInstance(this);
        // Set view according session storage
        String e = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("email", null);
        if (e == null) {
            profile.setImageResource(R.drawable.ic_profile);
        } else {
            user = database.userDao().getUserByEmail(e);
            if (user != null) {
                if (user.getPhoto() != null) {
                    profile.setImageBitmap(DataConverter.byteArrayToImage(user.getPhoto()));
                } else {
                    profile.setImageResource(R.drawable.ic_profile_photo);
                }
            }
        }

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(intent);
                Animatoo.animateSlideDown(MyProductsActivity.this);
                profile.setBorderColor(Color.parseColor("#FFFEF6D8"));
            }
        });
        my_products.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                my_products.setColorFilter(Color.parseColor("#000000"));
                MyProductsActivity.super.onBackPressed();
                if (!getIntent().getBooleanExtra("isFromMain", false)) {
                    Animatoo.animateSlideUp(MyProductsActivity.this);
                }
            }
        });
        // Initialize action bar variables
        ImageView backIcon = findViewById(R.id.back_icon);
        TextView actionText = findViewById(R.id.action_bar_text);
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                my_products.setColorFilter(Color.parseColor("#000000"));
                MyProductsActivity.super.onBackPressed();
                if (!getIntent().getBooleanExtra("isFromMain", false)) {
                    Animatoo.animateSlideUp(MyProductsActivity.this);
                }
            }
        });
        actionText.setText(MY_PRODUCTS);

        // Initialize attributes
        Button addBtn = findViewById(R.id.addProdBtn);
        name = findViewById(R.id.editTextProduct);
        name.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (v.getId() == R.id.editTextProduct) {
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                    }
                }
                return false;
            }
        });
        quantity = findViewById(R.id.editTextQuantity);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Set layout visible
                constraintLayout = findViewById(R.id.layout);
                constraintLayout.setVisibility(View.VISIBLE);
                ConstraintLayout constraintLayout = findViewById(R.id.constraint);
                ConstraintSet constraintSet = new ConstraintSet();
                constraintSet.clone(constraintLayout);
                constraintSet.connect(R.id.recycler_view, ConstraintSet.TOP, R.id.delAllProd, ConstraintSet.BOTTOM, 560);
                constraintSet.applyTo(constraintLayout);
            }
        });
        recyclerView = findViewById(R.id.recycler_view);
        textView = findViewById(R.id.textView);

        // Get data
        String[] units = {MEASURING_UNITS, ML, L, GR, KG, GLASS, SMALL_GLASS, SPOON, SMALL_SPOON, PINCH, PINCHES, PACKET, PACKETS};
        spinner = findViewById(R.id.spinner);
        spinner.setBackgroundColor(Color.parseColor("#56FFCFA6"));
        ArrayAdapter<String> adapter = new ArrayAdapter(this, R.layout.spiner_item, units);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        // Get current user
        String userEmail = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getString("email", null);
        user = database.userDao().getUserByEmail(userEmail);
        // Store db value in product list
        dataList = database.productDao().getUserProducts("myProducts", user.getID());
        if (dataList.size() > 0) {
            textView.setVisibility(View.INVISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.INVISIBLE);
            textView.setVisibility(View.VISIBLE);
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        mainAdapter = new MainAdapter(MyProductsActivity.this, dataList, "myProducts", user.getID());
        recyclerView.setAdapter(mainAdapter);

        Button deleteAll = findViewById(R.id.delAllProd);
        deleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dataList.size() > 0) {
                    deleteDialog();
                } else {
                    noProdDialog();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            dialog.dismiss();
                        }
                    }, 2000);
                }
            }
        });
        Button addedBtn = findViewById(R.id.added);
        addedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.firstTextView).setVisibility(View.INVISIBLE);
                findViewById(R.id.secondTextView).setVisibility(View.INVISIBLE);
                Product product = new Product();
                float sQuantity = 0;
                String sName = name.getText().toString().trim();
                String q = quantity.getText().toString().trim();
                String sMeasureUnit = spinner.getSelectedItem().toString();
                if (!sName.equals("")) {
                    if (sMeasureUnit.contains("Мерна")) {
                        sMeasureUnit = "";
                    }
                    if (!q.equals("") && !q.equals(".")) {
                        sQuantity = Float.parseFloat(q);
                        if (sQuantity <= 0) {
                            findViewById(R.id.secondTextView).setVisibility(View.VISIBLE);
                            return;
                        }
                    }
                    product.setName(sName);
                    product.setMeasureUnit(sMeasureUnit);
                    product.setQuantity(sQuantity);
                    product.setBelonging("myProducts");
                    product.setOwnerId(user.getID());
                    // Insert in db
                    database.productDao().insert(product);
                    // Clear edit texts
                    name.setText("");
                    spinner.setSelection(0);
                    quantity.setText("");
                    dataList.clear();
                    dataList.addAll(database.productDao().getUserProducts("myProducts", user.getID()));
                    if (dataList.size() > 0) {
                        textView.setVisibility(View.INVISIBLE);
                        recyclerView.setVisibility(View.VISIBLE);
                    } else {
                        recyclerView.setVisibility(View.INVISIBLE);
                        textView.setVisibility(View.VISIBLE);
                    }
                    mainAdapter.notifyDataSetChanged();
                    findViewById(R.id.firstTextView).setVisibility(View.INVISIBLE);
                    constraintLayout.setVisibility(View.INVISIBLE);
                    ConstraintLayout constraintLayout = findViewById(R.id.constraint);
                    ConstraintSet constraintSet = new ConstraintSet();
                    constraintSet.clone(constraintLayout);
                    constraintSet.connect(R.id.recycler_view, ConstraintSet.TOP, R.id.delAllProd, ConstraintSet.BOTTOM, 48);
                    constraintSet.applyTo(constraintLayout);
                } else {
                    findViewById(R.id.firstTextView).setVisibility(View.VISIBLE);
                }
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        });
        // Initialize and assign variable
        bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setLabelVisibilityMode(NavigationBarView.LABEL_VISIBILITY_UNLABELED);
        int[][] states = new int[][]{
                new int[]{android.R.attr.state_enabled}, // enabled
                new int[]{-android.R.attr.state_enabled}, // disabled
                new int[]{-android.R.attr.state_checked}, // unchecked
                new int[]{android.R.attr.state_pressed}  // pressed
        };

        int[] colors = new int[]{
                Color.BLACK,
                Color.BLACK,
                Color.BLACK,
                Color.BLACK
        };

        myList = new ColorStateList(states, colors);
        bottomNavigationView.setItemIconTintList(myList);
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
                Animatoo.animateZoom(MyProductsActivity.this);
                return true;
            }
        });
    }

    public void noProdDialog() {
        dialogBuilder = new AlertDialog.Builder(this);
        final View popupView = getLayoutInflater().inflate(R.layout.no_products_popup, null);

        okButton = popupView.findViewById(R.id.okBtn);

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

    public void deleteDialog() {
        dialogBuilder = new AlertDialog.Builder(this);
        final View popupView = getLayoutInflater().inflate(R.layout.delete_popup, null);

        okButton = popupView.findViewById(R.id.okBtn);
        Button noButton = popupView.findViewById(R.id.noBtn);

        dialogBuilder.setView(popupView);
        dialog = dialogBuilder.create();
        dialog.show();

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                database.productDao().deleteAll(dataList);
                dataList.clear();
                dataList.addAll(database.productDao().getUserProducts("myProducts", user.getID()));
                if (dataList.size() > 0) {
                    textView.setVisibility(View.INVISIBLE);
                    recyclerView.setVisibility(View.VISIBLE);
                } else {
                    recyclerView.setVisibility(View.INVISIBLE);
                    textView.setVisibility(View.VISIBLE);
                }
                mainAdapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });
        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    @Override
    protected void onStart() {
        profile.setBorderColor(Color.parseColor("#000000"));
        bottomNavigationView.setItemIconTintList(myList);
        // Set view according session storage
        String e = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("email", null);
        if (e == null) {
            profile.setImageResource(R.drawable.ic_profile);
        } else {
            user = database.userDao().getUserByEmail(e);
            if (user != null) {
                if (user.getPhoto() != null) {
                    profile.setImageBitmap(DataConverter.byteArrayToImage(user.getPhoto()));
                } else {
                    profile.setImageResource(R.drawable.ic_profile_photo);
                }
            }
        }
        super.onStart();
    }

    @Override
    protected void onResume() {
        profile.setBorderColor(Color.parseColor("#000000"));
        // Set view according session storage
        String e = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("email", null);
        if (e == null) {
            profile.setImageResource(R.drawable.ic_profile);
        } else {
            user = database.userDao().getUserByEmail(e);
            if (user != null) {
                if (user.getPhoto() != null) {
                    profile.setImageBitmap(DataConverter.byteArrayToImage(user.getPhoto()));
                } else {
                    profile.setImageResource(R.drawable.ic_profile_photo);
                }
            }
        }
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        if (constraintLayout != null && constraintLayout.getVisibility() == View.VISIBLE) {
            findViewById(R.id.firstTextView).setVisibility(View.INVISIBLE);
            findViewById(R.id.secondTextView).setVisibility(View.INVISIBLE);
            constraintLayout.setVisibility(View.INVISIBLE);
            ConstraintLayout constraintLayout = findViewById(R.id.constraint);
            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(constraintLayout);
            constraintSet.connect(R.id.recycler_view, ConstraintSet.TOP, R.id.delAllProd, ConstraintSet.BOTTOM, 48);
            constraintSet.applyTo(constraintLayout);
        } else {
            my_products.setColorFilter(Color.parseColor("#000000"));
            super.onBackPressed();
            if (!getIntent().getBooleanExtra("isFromMain", false)) {
                Animatoo.animateSlideUp(MyProductsActivity.this);
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