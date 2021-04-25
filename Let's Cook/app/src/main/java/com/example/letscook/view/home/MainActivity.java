package com.example.letscook.view.home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.example.letscook.AddRecipeActivity;
import com.example.letscook.ContactsActivity;
import com.example.letscook.view.info.DataPolicyActivity;
import com.example.letscook.view.info.InfoActivity;
import com.example.letscook.view.products.MyProductsActivity;
import com.example.letscook.view.profile.ProfileActivity;
import com.example.letscook.R;
import com.example.letscook.view.recipesDashboard.RecipesActivity;
import com.example.letscook.view.search.SearchActivity;
import com.example.letscook.view.products.ShoppingListActivity;
import com.example.letscook.view.info.TermsOfUseActivity;
import com.example.letscook.view.search.WhatToCookActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import static com.example.letscook.constants.Messages.*;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private CardView whatToCookCard, searchCard, myRecipesCard, favCard, shoppingListCard,
            myProductsCard, addRecipeCard, profileCard, lastViewedCard, lastAddedCard, contactsCard,
            infoCard, policyCard, termsCard;
    private ImageView profile, my_products;
    private NavigationView navigationView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                    profile.setColorFilter(Color.parseColor("#FFFEF6D8"));
                }
            }
        });
        my_products.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MyProductsActivity.class));
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
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);

        // Set home selected
        bottomNavigationView.setSelectedItemId(R.id.home);

        // Perform item selected list
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
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
    public void onClick(View v) {
        Intent i;
        switch (v.getId()) {
            case R.id.whatToCook_card:
                i = new Intent(this, WhatToCookActivity.class);
                startActivity(i);
                break;
            case R.id.search_card:
                i = new Intent(this, SearchActivity.class);
                startActivity(i);
                break;
            case R.id.myRecipes_card:
                i = new Intent(this, RecipesActivity.class);
                i.putExtra("phrase", MY_RECIPES);
                i.putExtra("text", MY_RES);
                startActivity(i);
                break;
            case R.id.fav_card:
                i = new Intent(this, RecipesActivity.class);
                i.putExtra("phrase", FAV_RECIPES);
                i.putExtra("text", MY_FAV);
                startActivity(i);
                break;
            case R.id.shopping_card:
                i = new Intent(this, ShoppingListActivity.class);
                startActivity(i);
                break;
            case R.id.myProducts_card:
                i = new Intent(this, MyProductsActivity.class);
                startActivity(i);
                break;
            case R.id.addRecipe_card:
                i = new Intent(this, AddRecipeActivity.class);
                startActivity(i);
                break;
            case R.id.myProfile_card:
                i = new Intent(this, ProfileActivity.class);
                //i = new Intent(this, LoginActivity.class);
                startActivity(i);
                break;
            case R.id.lastViewed_card:
                i = new Intent(this, RecipesActivity.class);
                i.putExtra("phrase", LAST_VIEW);
                i.putExtra("text", MY_VIEWED);
                startActivity(i);
                break;
            case R.id.lastAdded_card:
                i = new Intent(this, RecipesActivity.class);
                i.putExtra("phrase", LAST_ADD);
                startActivity(i);
                break;
            case R.id.contacts_card:
                i = new Intent(this, ContactsActivity.class);
                startActivity(i);
                break;
            case R.id.info_card:
                i = new Intent(this, InfoActivity.class);
                startActivity(i);
                break;
            case R.id.policy_card:
                i = new Intent(this, DataPolicyActivity.class);
                startActivity(i);
                break;
            case R.id.terms_card:
                i = new Intent(this, TermsOfUseActivity.class);
                startActivity(i);
                break;
        }
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
                finishAffinity();
            }
        } else {
            finishAffinity();
        }
    }
}