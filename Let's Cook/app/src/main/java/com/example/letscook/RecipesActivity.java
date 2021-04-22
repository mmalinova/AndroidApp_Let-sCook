package com.example.letscook;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class RecipesActivity extends AppCompatActivity {
    private TextView category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipes);

        category = findViewById(R.id.category);

        Intent intent = new Intent();
        category.setText(intent.getStringExtra("name"));
    }
}