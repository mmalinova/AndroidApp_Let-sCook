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

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.example.letscook.AddRecipeActivity;
import com.example.letscook.ContactsActivity;
import com.example.letscook.view.info.DataPolicyActivity;
import com.example.letscook.view.info.InfoActivity;
import com.example.letscook.view.products.MyProductsActivity;
import com.example.letscook.view.profile.ProfileActivity;
import com.example.letscook.R;
import com.example.letscook.view.recipeDetails.RecipeActivity;
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
    private BottomNavigationView bottomNavigationView;

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
                    Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                    startActivity(intent);
                    Animatoo.animateSlideDown(MainActivity.this);
                    profile.setColorFilter(Color.parseColor("#FFFEF6D8"));
                }
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
                        intent = new Intent(getApplicationContext(), ShoppingListActivity.class);
                        break;
                }
                intent.putExtra("isFromMain", true);
                startActivity(intent);
                Animatoo.animateZoom(MainActivity.this);
                return true;
            }
        });
    }

    @Override
    public void onClick(View v) {
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
                intent = new Intent(this, RecipesActivity.class);
                intent.putExtra("phrase", MY_RECIPES);
                intent.putExtra("text", MY_RES);
                startActivity(intent);
                break;
            case R.id.fav_card:
                intent = new Intent(this, RecipesActivity.class);
                intent.putExtra("phrase", FAV_RECIPES);
                intent.putExtra("text", MY_FAV);
                startActivity(intent);
                break;
            case R.id.shopping_card:
                intent = new Intent(this, ShoppingListActivity.class);
                intent.putExtra("isFromMain", true);
                startActivity(intent);
                break;
            case R.id.myProducts_card:
                intent = new Intent(this, MyProductsActivity.class);
                intent.putExtra("isFromMain", true);
                startActivity(intent);
                break;
            case R.id.addRecipe_card:
                intent = new Intent(this, AddRecipeActivity.class);
                intent.putExtra("isFromMain", true);
                startActivity(intent);
                break;
            case R.id.myProfile_card:
                intent = new Intent(this, ProfileActivity.class);
                intent.putExtra("isFromMain", true);
                //i = new Intent(this, LoginActivity.class);
                startActivity(intent);
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

    @Override
    protected void onStart() {
        profile.setColorFilter(Color.parseColor("#000000"));
        my_products.setColorFilter(Color.parseColor("#000000"));
        bottomNavigationView.setSelectedItemId(R.id.home);
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