package com.example.letscook.view.profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.example.letscook.AddRecipeActivity;
import com.example.letscook.R;
import com.example.letscook.view.products.MyProductsActivity;
import com.example.letscook.view.search.SearchActivity;
import com.example.letscook.view.products.ShoppingListActivity;
import com.example.letscook.view.search.WhatToCookActivity;
import com.example.letscook.view.home.MainActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

import static com.example.letscook.constants.Messages.*;

public class ProfileActivity extends AppCompatActivity {
    private TextView greetingText;
    private FloatingActionButton floatingBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        floatingBtn = findViewById(R.id.floating_btn);
        floatingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ApplicationInfo api = getApplicationContext().getApplicationInfo();
                String apkPath = api.sourceDir;
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("application/vnd.android.package-archive");
                intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(apkPath)));
                startActivity(Intent.createChooser(intent, SHARE));
            }
        });

        // Initialize and assign variable
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);

        // Set home selected
        bottomNavigationView.setSelectedItemId(R.id.home);

        // Perform item selected list
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent = null;
                switch(item.getItemId()) {
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
                Animatoo.animateZoom(ProfileActivity.this);
                return true;
            }
        });

        // Initialize action text
        greetingText = findViewById(R.id.bar_text);

        // Get time of the day
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int hour = cal.get(Calendar.HOUR_OF_DAY);

        // Get name
        String name = "Михаела";

        // Set greeting
        String greeting = null;
        if (hour >= 6 && hour <= 10) {
            greeting = MORNING_GREETING + name + "!";
        } else if (hour > 10 && hour <= 18) {
            greeting = AFTERNOON_GREETING + name + "!";
        } else {
            greeting = NIGHT_GREETING + name + "!";
        }

        //Change text
        greetingText.setText(greeting);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (!getIntent().getBooleanExtra("isFromMain", false)) {
            Animatoo.animateSlideUp(ProfileActivity.this);
        }
    }
}