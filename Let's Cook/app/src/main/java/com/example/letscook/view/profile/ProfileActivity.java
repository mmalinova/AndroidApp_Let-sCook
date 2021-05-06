package com.example.letscook.view.profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.example.letscook.AddRecipeActivity;
import com.example.letscook.R;
import com.example.letscook.database.RoomDB;
import com.example.letscook.database.user.User;
import com.example.letscook.view.search.SearchActivity;
import com.example.letscook.view.products.ShoppingListActivity;
import com.example.letscook.view.search.WhatToCookActivity;
import com.example.letscook.view.home.MainActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.example.letscook.constants.Messages.*;

public class ProfileActivity extends AppCompatActivity {

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        findViewById(R.id.exit_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getSharedPreferences("PREFERENCE", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("email", null);
                editor.apply();
                startActivity(new Intent(ProfileActivity.this, MainActivity.class));
            }
        });

        FloatingActionButton floatingBtn = findViewById(R.id.floating_btn);
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
        // Initialize db
        RoomDB database = RoomDB.getInstance(this);
        // Initialize action text
        TextView greetingText = findViewById(R.id.bar_text);
        // Get data
        String userEmail = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("email", null);
        User user = database.userDao().getUserByEmail(userEmail);
        // Set data
        String username = user.getName();
        TextView name = findViewById(R.id.name);
        name.setText(username);
        TextView email = findViewById(R.id.email);
        email.setText(user.getEmail());
        // Get time of the day
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        // Set greeting
        String greeting;
        if (hour >= 6 && hour <= 10) {
            greeting = MORNING_GREETING + username + "!";
        } else if (hour > 10 && hour <= 18) {
            greeting = AFTERNOON_GREETING + username + "!";
        } else {
            greeting = NIGHT_GREETING + username + "!";
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