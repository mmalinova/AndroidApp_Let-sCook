package com.example.letscook.topnav;

import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.letscook.view.products.MyProductsActivity;
import com.example.letscook.view.profile.ProfileActivity;
import com.google.android.material.navigation.NavigationView;

public class TopNavigation extends AppCompatActivity {

    public void topNavActions(ImageView profile, ImageView my_products, NavigationView navigationView) {
        // Add click event listeners
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (navigationView.getVisibility() == View.VISIBLE) {
                    navigationView.setVisibility(View.INVISIBLE);
                    profile.setColorFilter(Color.parseColor("#000000"));
                } else {
//                    Button button = findViewById(R.id.login_btn);
//                    button.setOnClickListener(new View.OnClickListener() {
//                        public void onClick(View v) {
//                            Toast.makeText(getApplicationContext(),"Clicked", Toast.LENGTH_LONG).show();
//                        }
//                    });
                    startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                    //navigationView.setVisibility(View.VISIBLE);
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
    }
}
