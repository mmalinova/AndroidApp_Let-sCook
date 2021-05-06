package com.example.letscook.view.home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.example.letscook.AddRecipeActivity;
import com.example.letscook.ContactsActivity;
import com.example.letscook.database.RoomDB;
import com.example.letscook.database.user.User;
import com.example.letscook.database.user.UserDao;
import com.example.letscook.view.info.DataPolicyActivity;
import com.example.letscook.view.info.InfoActivity;
import com.example.letscook.view.products.MyProductsActivity;
import com.example.letscook.view.profile.ProfileActivity;
import com.example.letscook.R;
import com.example.letscook.view.recipesDashboard.RecipesActivity;
import com.example.letscook.view.register.SignUpActivity;
import com.example.letscook.view.search.SearchActivity;
import com.example.letscook.view.products.ShoppingListActivity;
import com.example.letscook.view.info.TermsOfUseActivity;
import com.example.letscook.view.search.WhatToCookActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.util.List;

import static com.example.letscook.constants.Messages.*;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private CardView whatToCookCard, searchCard, myRecipesCard, favCard, shoppingListCard,
            myProductsCard, addRecipeCard, profileCard, lastViewedCard, lastAddedCard, contactsCard,
            infoCard, policyCard, termsCard;
    private ImageView profile, my_products;
    private NavigationView navigationView = null;
    private BottomNavigationView bottomNavigationView;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog = null;
    private Button okButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize profile  and my products links
        profile = findViewById(R.id.profile);
        my_products = findViewById(R.id.my_products);

        // Set view according session storage

        if (getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getString("email", null) == null) {
            navigationView = findViewById(R.id.login_view);
        }

        // Add click event listeners
        findViewById(R.id.constraint).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (navigationView != null && navigationView.getVisibility() == View.VISIBLE) {
                    hideNavView();
                }
            }
        });
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigationClickListeners();
            }
        });
        my_products.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MyProductsActivity.class));
                Animatoo.animateSlideDown(MainActivity.this);
                my_products.setColorFilter(Color.parseColor("#FFFEF6D8"));
            }
        });

        // Initialize cards
        whatToCookCard = findViewById(R.id.whatToCook_card);
        searchCard = findViewById(R.id.search_card);
        myRecipesCard = findViewById(R.id.myRecipes_card);
        favCard = findViewById(R.id.fav_card);
        shoppingListCard = findViewById(R.id.shopping_card);
        myProductsCard = findViewById(R.id.myProducts_card);
        addRecipeCard = findViewById(R.id.addRecipe_card);
        profileCard = findViewById(R.id.myProfile_card);
        lastViewedCard = findViewById(R.id.lastViewed_card);
        lastAddedCard = findViewById(R.id.lastAdded_card);
        contactsCard = findViewById(R.id.contacts_card);
        infoCard = findViewById(R.id.info_card);
        policyCard = findViewById(R.id.policy_card);
        termsCard = findViewById(R.id.terms_card);

        // Add click listeners to the cards
        whatToCookCard.setOnClickListener(this);
        searchCard.setOnClickListener(this);
        myRecipesCard.setOnClickListener(this);
        favCard.setOnClickListener(this);
        shoppingListCard.setOnClickListener(this);
        myProductsCard.setOnClickListener(this);
        addRecipeCard.setOnClickListener(this);
        profileCard.setOnClickListener(this);
        lastViewedCard.setOnClickListener(this);
        lastAddedCard.setOnClickListener(this);
        contactsCard.setOnClickListener(this);
        infoCard.setOnClickListener(this);
        policyCard.setOnClickListener(this);
        termsCard.setOnClickListener(this);

        // Initialize and assign variable
        bottomNavigationView = findViewById(R.id.bottom_nav);

        // Set home selected
        bottomNavigationView.setSelectedItemId(R.id.home);

        // Perform item selected list
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (navigationView != null && navigationView.getVisibility() == View.VISIBLE) {
                    hideNavView();
                } else {
                    Intent intent = null;
                    switch (item.getItemId()) {
                        case R.id.home:
                            return true;
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
                            if (getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                                    .getString("email", null) == null) {
                                deniedDialog();
                                return false;
                            } else {
                                intent = new Intent(getApplicationContext(), ShoppingListActivity.class);
                            }
                            break;
                    }
                    intent.putExtra("isFromMain", true);
                    startActivity(intent);
                    Animatoo.animateZoom(MainActivity.this);
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (navigationView != null && navigationView.getVisibility() == View.VISIBLE) {
            hideNavView();
        } else {
            Intent intent;
            switch (v.getId()) {
                case R.id.whatToCook_card:
                    intent = new Intent(this, WhatToCookActivity.class);
                    intent.putExtra("isFromMain", true);
                    startActivity(intent);
                    break;
                case R.id.search_card:
                    intent = new Intent(this, SearchActivity.class);
                    intent.putExtra("isFromMain", true);
                    startActivity(intent);
                    break;
                case R.id.myRecipes_card:
                    if (getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                            .getString("email", null) == null) {
                        deniedDialog();
                    } else {
                        intent = new Intent(this, RecipesActivity.class);
                        intent.putExtra("phrase", MY_RECIPES);
                        intent.putExtra("text", MY_RES);
                        startActivity(intent);
                    }
                    break;
                case R.id.fav_card:
                    if (getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                            .getString("email", null) == null) {
                        deniedDialog();
                    } else {
                        intent = new Intent(this, RecipesActivity.class);
                        intent.putExtra("phrase", FAV_RECIPES);
                        intent.putExtra("text", MY_FAV);
                        startActivity(intent);
                    }
                    break;
                case R.id.shopping_card:
                    if (getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                            .getString("email", null) == null) {
                        deniedDialog();
                    } else {
                        intent = new Intent(this, ShoppingListActivity.class);
                        intent.putExtra("isFromMain", true);
                        startActivity(intent);
                    }
                    break;
                case R.id.myProducts_card:
                    if (getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                            .getString("email", null) == null) {
                        deniedDialog();
                    } else {
                        intent = new Intent(this, MyProductsActivity.class);
                        intent.putExtra("isFromMain", true);
                        startActivity(intent);
                        my_products.setColorFilter(Color.parseColor("#FFFEF6D8"));
                    }
                    break;
                case R.id.addRecipe_card:
                    if (getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                            .getString("email", null) == null) {
                        deniedDialog();
                    } else {
                        intent = new Intent(this, AddRecipeActivity.class);
                        intent.putExtra("isFromMain", true);
                        startActivity(intent);
                    }
                    break;
                case R.id.myProfile_card:
                    navigationClickListeners();
                    break;
                case R.id.lastViewed_card:
                    intent = new Intent(this, RecipesActivity.class);
                    intent.putExtra("phrase", LAST_VIEW);
                    intent.putExtra("text", MY_VIEWED);
                    startActivity(intent);
                    break;
                case R.id.lastAdded_card:
                    intent = new Intent(this, RecipesActivity.class);
                    intent.putExtra("phrase", LAST_ADD);
                    startActivity(intent);
                    break;
                case R.id.contacts_card:
                    intent = new Intent(this, ContactsActivity.class);
                    startActivity(intent);
                    break;
                case R.id.info_card:
                    intent = new Intent(this, InfoActivity.class);
                    startActivity(intent);
                    break;
                case R.id.policy_card:
                    intent = new Intent(this, DataPolicyActivity.class);
                    startActivity(intent);
                    break;
                case R.id.terms_card:
                    intent = new Intent(this, TermsOfUseActivity.class);
                    startActivity(intent);
                    break;
            }
        }
    }

    public void navigationClickListeners() {
        if (navigationView != null) {
            if (navigationView.getVisibility() == View.INVISIBLE) {
                findViewById(R.id.forgotten_pass).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                        intent.putExtra("forgottenPassword", 1);
                        startActivity(intent);
                    }
                });
                findViewById(R.id.register).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                                .putBoolean("remember", false).apply();
                        startActivity(new Intent(getApplicationContext(), SignUpActivity.class));
                    }
                });
                navigationView.setVisibility(View.VISIBLE);
                profile.setColorFilter(Color.parseColor("#FFFEF6D8"));
            } else {
                hideNavView();
            }
        } else {
            Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
            startActivity(intent);
            Animatoo.animateSlideDown(MainActivity.this);
            profile.setColorFilter(Color.parseColor("#FFFEF6D8"));
        }
    }

    public void hideNavView() {
        TextView required = findViewById(R.id.reqTextView);
        required.setVisibility(View.INVISIBLE);
        // Check if no view has focus:
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        navigationView.setVisibility(View.INVISIBLE);
        profile.setColorFilter(Color.parseColor("#000000"));
    }

    public void login(View view) {
        // Get the information
        TextView required = findViewById(R.id.reqTextView);
        required.setVisibility(View.INVISIBLE);

        TextView email = findViewById(R.id.email_editText);
        TextView password = findViewById(R.id.password_editText);

        String userEmail = email.getText().toString().trim();
        if (userEmail.equals("") || !Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
            required.setText(EMAIL_REQ);
            required.setVisibility(View.VISIBLE);
            return;
        }
        String userPass = password.getText().toString().trim();
        if (userPass.length() < 3) {
            required.setText(PASS_REQ);
            required.setVisibility(View.VISIBLE);
            return;
        }
        // Initialize db
        RoomDB database = RoomDB.getInstance(this);
        final UserDao userDao = database.userDao();
        new Thread(new Runnable() {
            @Override
            public void run() {
                User user = userDao.getUserByEmail(userEmail);
                if (user == null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            required.setText(EMAIL_NOT_EXIST);
                            required.setVisibility(View.VISIBLE);
                        }
                    });
                } else if (!user.getPassword().equals(userPass)) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            required.setText(WRONG_PASS);
                            required.setVisibility(View.VISIBLE);
                        }
                    });
                } else {
                    String name = user.getName();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            required.setText(LOGIN);
                            required.setVisibility(View.VISIBLE);
                        }
                    });
                    SharedPreferences sharedPreferences = getSharedPreferences("PREFERENCE", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("email", userEmail);
                    editor.apply();
                    startActivity(new Intent(MainActivity.this, MainActivity.class));
                }
            }
        }).start();
    }

    public void deniedDialog() {
        dialogBuilder = new AlertDialog.Builder(this);
        final View popupView = getLayoutInflater().inflate(R.layout.denied_access, null);

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

    @Override
    protected void onStart() {
        profile.setColorFilter(Color.parseColor("#000000"));
        my_products.setColorFilter(Color.parseColor("#000000"));
        bottomNavigationView.setSelectedItemId(R.id.home);
        super.onStart();
    }

    @Override
    protected void onResume() {
        profile.setColorFilter(Color.parseColor("#000000"));
        my_products.setColorFilter(Color.parseColor("#000000"));
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        if (navigationView != null && navigationView.getVisibility() == View.VISIBLE) {
            hideNavView();
        } else {
            if (!getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                    .getBoolean("remember", false)) {
                SharedPreferences sharedPreferences = getSharedPreferences("PREFERENCE", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("email", null);
                editor.apply();
            }
            finishAffinity();
        }
    }
}