package com.example.letscook.topnav;

import android.content.Intent;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.letscook.view.AddRecipeActivity;
import com.example.letscook.R;
import com.example.letscook.view.search.SearchActivity;
import com.example.letscook.view.products.ShoppingListActivity;
import com.example.letscook.view.search.WhatToCookActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class BottomNavigation extends AppCompatActivity {

    public void bottomNavActions(BottomNavigationView bottomNavigationView) {
        // Set home selected
        bottomNavigationView.setSelectedItemId(R.id.home);

        // Perform item selected list
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()) {
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
}
