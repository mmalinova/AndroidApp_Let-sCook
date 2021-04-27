package com.example.letscook.view.search;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

public class WhatToCookActivity extends AppCompatActivity {
    private int id;
    private TextView actionText;
    private ImageView backIcon, profile, my_products;
    private NavigationView navigationView = null;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog = null;
    private Button yesButton, noButton, searchBtn;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_what_to_cook);

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
                    Animatoo.animateSlideDown(WhatToCookActivity.this);
                    profile.setColorFilter(Color.parseColor("#FFFEF6D8"));
                }
            }
        });
        my_products.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MyProductsActivity.class));
                Animatoo.animateSlideDown(WhatToCookActivity.this);
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
        actionText.setText(WHAT_TO_COOK);

        // Initialize search
        searchBtn = findViewById(R.id.searchBtn);
        //searchBtn.setEnabled(false);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                catTermsDialog();
            }
        });

        // Initialize and assign variable
        bottomNavigationView = findViewById(R.id.bottom_nav);

        // Set selected
        bottomNavigationView.setSelectedItemId(R.id.what_to_cook);

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
                        return true;
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
                Animatoo.animateZoom(WhatToCookActivity.this);
                return true;
            }
        });
    }

    public void catTermsDialog() {
        dialogBuilder = new AlertDialog.Builder(this);
        final View popupView = getLayoutInflater().inflate(R.layout.category_popup, null);

        RadioGroup radioGroup = popupView.findViewById(R.id.radioGroup);
        RadioGroup secondRadioGroup = popupView.findViewById(R.id.secondRadioGroup);

        dialogBuilder.setView(popupView);
        dialog = dialogBuilder.create();
        dialog.show();

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int id = radioGroup.getCheckedRadioButtonId();
                RadioButton radioButton = radioGroup.findViewById(id);
                String text = radioButton.getText().toString();
                // Attempt to query according to index
                switch (text) {
                    case "Закуска":
                        vegTermsDialog(0);
                        break;
                    case "Обяд":
                        vegTermsDialog(1);
                        break;
                }
                radioButton.setChecked(false);
            }
        });
        secondRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int id = secondRadioGroup.getCheckedRadioButtonId();
                RadioButton radioButton = secondRadioGroup.findViewById(id);
                String text = radioButton.getText().toString();
                // Attempt to query according to index
                switch (text) {
                    case "Вечеря":
                        vegTermsDialog(2);
                        break;
                    case "Десерт":
                        vegTermsDialog(3);
                        break;
                }
                radioButton.setChecked(false);
            }
        });
    }

    public void vegTermsDialog(int catIndex) {
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
                // Append filter to request
                prodTermsDialog(catIndex, 0);
            }
        });
        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start activity
                prodTermsDialog(catIndex, 1);
            }
        });
    }

    public void prodTermsDialog(int catIndex, int vegIndex) {
        dialogBuilder = new AlertDialog.Builder(this);
        final View popupView = getLayoutInflater().inflate(R.layout.products_popup, null);

        RadioGroup radioGroup = popupView.findViewById(R.id.radioGroup);

        dialogBuilder.setView(popupView);
        dialog = dialogBuilder.create();
        dialog.show();

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int id = radioGroup.getCheckedRadioButtonId();
                View radioButton = radioGroup.findViewById(id);
                int prodIndex = radioGroup.indexOfChild(radioButton);

                Intent intent = new Intent(getApplicationContext(), RecipesActivity.class);
                intent.putExtra("category", catIndex);
                intent.putExtra("vegetarian", vegIndex);
                // Attempt to query according to index
                switch (prodIndex) {
                    case 0:
                        intent.putExtra("products", 0);
                        break;
                    case 1:
                        intent.putExtra("products", 1);
                        break;
                    case 2:
                        intent.putExtra("products", 2);
                        break;
                }
                intent.putExtra("products", prodIndex);
                intent.putExtra("phrase", APPROPRIATE_MESS);
                // Query
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        profile.setColorFilter(Color.parseColor("#000000"));
        my_products.setColorFilter(Color.parseColor("#000000"));
        bottomNavigationView.setSelectedItemId(R.id.what_to_cook);
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
}