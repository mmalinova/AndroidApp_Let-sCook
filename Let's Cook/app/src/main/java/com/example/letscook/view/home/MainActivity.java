package com.example.letscook.view.home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.example.letscook.database.recipe.Recipe;
import com.example.letscook.view.AddRecipeActivity;
import com.example.letscook.view.ContactsActivity;
import com.example.letscook.database.RoomDB;
import com.example.letscook.database.typeconverters.DataConverter;
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
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.letscook.constants.Messages.*;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView my_products;
    private CircleImageView profile;
    private NavigationView navigationView = null;
    private BottomNavigationView bottomNavigationView;
    private AlertDialog dialog = null;
    private RoomDB database;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize profile  and my products links
        profile = findViewById(R.id.profile);
        my_products = findViewById(R.id.my_products);

        // Initialize db
        database = RoomDB.getInstance(this);
        // Set view according session storage
        String e = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("email", null);
        if (e == null) {
            navigationView = findViewById(R.id.login_view);
            profile.setImageResource(R.drawable.ic_profile);
        } else {
            user = database.userDao().getUserByEmail(e);
            if (user != null) {
                if (user.getPhoto() != null) {
                    profile.setImageBitmap(DataConverter.byteArrayToImage(user.getPhoto()));
                } else {
                    profile.setImageResource(R.drawable.ic_profile_photo);
                }
            } else {
                navigationView = findViewById(R.id.login_view);
            }
        }
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
                if (user == null) {
                    deniedDialog();
                } else {
                    startActivity(new Intent(getApplicationContext(), MyProductsActivity.class));
                    Animatoo.animateSlideDown(MainActivity.this);
                    my_products.setColorFilter(Color.parseColor("#FFFEF6D8"));
                }
            }
        });
        if (user != null && user.isAdmin()) {
            findViewById(R.id.to_approve).setVisibility(View.VISIBLE);
            TextView num = findViewById(R.id.number);
            List<Recipe> unapprovedRecipes = database.recipeDao().getAllUnapprovedRecipes();
            SpannableString content = new SpannableString(String.valueOf(unapprovedRecipes.size()));
            content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
            num.setText(content);
            num.setVisibility(View.VISIBLE);
            if (unapprovedRecipes.size() > 0) {
                num.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, RecipesActivity.class);
                        intent.putExtra("phrase", TO_APPROVE);
                        startActivity(intent);
                    }
                });
            }
        }

        // Initialize cards
        CardView whatToCookCard = findViewById(R.id.whatToCook_card);
        CardView searchCard = findViewById(R.id.search_card);
        CardView myRecipesCard = findViewById(R.id.myRecipes_card);
        CardView favCard = findViewById(R.id.fav_card);
        CardView shoppingListCard = findViewById(R.id.shopping_card);
        CardView myProductsCard = findViewById(R.id.myProducts_card);
        CardView addRecipeCard = findViewById(R.id.addRecipe_card);
        CardView profileCard = findViewById(R.id.myProfile_card);
        CardView lastViewedCard = findViewById(R.id.lastViewed_card);
        CardView lastAddedCard = findViewById(R.id.lastAdded_card);
        CardView contactsCard = findViewById(R.id.contacts_card);
        CardView infoCard = findViewById(R.id.info_card);
        CardView policyCard = findViewById(R.id.policy_card);
        CardView termsCard = findViewById(R.id.terms_card);

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
            @SuppressLint("NonConstantResourceId")
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
                            if (user == null) {
                                deniedDialog();
                                return false;
                            } else {
                                intent = new Intent(getApplicationContext(), AddRecipeActivity.class);
                            }
                            break;
                        case R.id.search:
                            intent = new Intent(getApplicationContext(), SearchActivity.class);
                            break;
                        case R.id.shopping_list:
                            if (user == null) {
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

    @SuppressLint("NonConstantResourceId")
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
                    if (user == null) {
                        deniedDialog();
                    } else {
                        intent = new Intent(this, RecipesActivity.class);
                        intent.putExtra("phrase", MY_RECIPES);
                        startActivity(intent);
                    }
                    break;
                case R.id.fav_card:
                    if (user == null) {
                        deniedDialog();
                    } else {
                        intent = new Intent(this, RecipesActivity.class);
                        intent.putExtra("phrase", FAV_RECIPES);
                        startActivity(intent);
                    }
                    break;
                case R.id.shopping_card:
                    if (user == null) {
                        deniedDialog();
                    } else {
                        intent = new Intent(this, ShoppingListActivity.class);
                        intent.putExtra("isFromMain", true);
                        startActivity(intent);
                    }
                    break;
                case R.id.myProducts_card:
                    if (user == null) {
                        deniedDialog();
                    } else {
                        intent = new Intent(this, MyProductsActivity.class);
                        intent.putExtra("isFromMain", true);
                        startActivity(intent);
                        my_products.setColorFilter(Color.parseColor("#FFFEF6D8"));
                    }
                    break;
                case R.id.addRecipe_card:
                    if (user == null) {
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
                    if (user == null) {
                        deniedDialog();
                    } else {
                        intent = new Intent(this, RecipesActivity.class);
                        intent.putExtra("phrase", LAST_VIEW);
                        startActivity(intent);
                    }
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
                profile.setBorderColor(Color.parseColor("#FFFEF6D8"));
            } else {
                hideNavView();
            }
        } else {
            Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
            startActivity(intent);
            Animatoo.animateSlideDown(MainActivity.this);
            profile.setBorderColor(Color.parseColor("#FFFEF6D8"));
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
        profile.setBorderColor(Color.parseColor("#000000"));
        // Set view according session storage
        String e = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("email", null);
        if (e == null) {
            navigationView = findViewById(R.id.login_view);
            profile.setImageResource(R.drawable.ic_profile);
        } else {
            user = database.userDao().getUserByEmail(e);
            if (user != null) {
                if (user.getPhoto() != null) {
                    profile.setImageBitmap(DataConverter.byteArrayToImage(user.getPhoto()));
                } else {
                    profile.setImageResource(R.drawable.ic_profile_photo);
                }
            } else {
                navigationView.setVisibility(View.INVISIBLE);
            }
        }
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
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (navigationView != null && navigationView.getVisibility() == View.VISIBLE) {
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                navigationView = null;
                            }
                        }
                    });
                }
            }
        }).start();
    }

    public void deniedDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        final View popupView = getLayoutInflater().inflate(R.layout.denied_access, null);

        Button okButton = popupView.findViewById(R.id.okBtn);

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
        profile.setBorderColor(Color.parseColor("#000000"));
        my_products.setColorFilter(Color.parseColor("#000000"));
        // Set view according session storage
        String e = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("email", null);
        if (e == null) {
            navigationView = findViewById(R.id.login_view);
            profile.setImageResource(R.drawable.ic_profile);
        } else {
            user = database.userDao().getUserByEmail(e);
            if (user != null) {
                if (user.getPhoto() != null) {
                    profile.setImageBitmap(DataConverter.byteArrayToImage(user.getPhoto()));
                } else {
                    profile.setImageResource(R.drawable.ic_profile_photo);
                }
            } else {
                navigationView = findViewById(R.id.login_view);
            }
        }
        bottomNavigationView.setSelectedItemId(R.id.home);
        super.onStart();
    }

    @Override
    protected void onResume() {
        profile.setBorderColor(Color.parseColor("#000000"));
        my_products.setColorFilter(Color.parseColor("#000000"));
        // Set view according session storage
        String e = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("email", null);
        if (e == null) {
            navigationView = findViewById(R.id.login_view);
            profile.setImageResource(R.drawable.ic_profile);
        } else {
            user = database.userDao().getUserByEmail(e);
            if (user != null) {
                if (user.getPhoto() != null) {
                    profile.setImageBitmap(DataConverter.byteArrayToImage(user.getPhoto()));
                } else {
                    profile.setImageResource(R.drawable.ic_profile_photo);
                }
            } else {
                navigationView = findViewById(R.id.login_view);
            }
        }
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