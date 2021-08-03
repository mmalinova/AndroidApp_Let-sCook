package com.example.letscook.view.slider;

import android.content.Intent;
import android.os.Bundle;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.example.letscook.R;
import com.example.letscook.view.home.MainActivity;
import com.example.letscook.view.register.SignUpActivity;
import com.example.letscook.adapter.SliderAdp;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

public class SliderActivity extends AppCompatActivity {
    //Initialize variables
    SliderView sliderView;
    int[] images = {R.drawable.firsth, R.drawable.second, R.drawable.third, R.drawable.fourth, R.drawable.fifth,
            R.drawable.sixth, R.drawable.seventh, R.drawable.eight, R.drawable.nineth, R.drawable.last};
    SliderAdp sliderAdp;

    Button btnSkip;
    Button btnRegister;
    Animation btnAnim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.MainTheme);

        //Make the activity on full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_slider);

        // Check if the app is open for the first time
        boolean isFirstRun = getSharedPreferences("FIRST_RUN", MODE_PRIVATE)
                .getBoolean("isFirstRun", true);
        boolean isRemembered = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getBoolean("remember", false);
        if (isFirstRun) {
            getSharedPreferences("FIRST_RUN", MODE_PRIVATE).edit()
                    .putBoolean("isFirstRun", false).apply();
        } else if (isRemembered) {
            startActivity(new Intent(this, MainActivity.class));
            //must finish this activity (the login activity will not be shown when click back in main activity)
            finish();
        } else {
            startActivity(new Intent(this, SignUpActivity.class));
            //must finish this activity (the login activity will not be shown when click back in main activity)
            finish();
        }

        sliderView = findViewById(R.id.slider_view);
        btnSkip = findViewById(R.id.skip_btn);
        btnRegister = findViewById(R.id.btn_slider);
        btnAnim = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.button_animation);

        //Initialize adapter
        sliderAdp = new SliderAdp(images);
        //Set adapter
        sliderView.setSliderAdapter(sliderAdp);
        //Set indicator images
        sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM);
        //Set transformation animation
        sliderView.setSliderTransformAnimation(SliderAnimations.DEPTHTRANSFORMATION);
        //Start auto cycle
        sliderView.startAutoCycle();

        //When we reach to the last screen
        sliderView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (sliderView.getCurrentPagePosition() == images.length - 1) {
                    loadLastScreen();
                } else {
                    btnRegister.setVisibility(View.INVISIBLE);
                    btnSkip.setVisibility(View.VISIBLE);
                    sliderView.startAutoCycle();
                }
            }
        });

        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sliderView.setCurrentPagePosition(images.length - 1);
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSignActivity(v);
                Animatoo.animateFade(SliderActivity.this);
            }
        });
    }

    public void openSignActivity(View view) {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }

    private void loadLastScreen() {
        sliderView.stopAutoCycle();
        btnSkip.setVisibility(View.INVISIBLE);
        btnRegister.setVisibility(View.VISIBLE);
        btnRegister.setAnimation(btnAnim);
    }
}