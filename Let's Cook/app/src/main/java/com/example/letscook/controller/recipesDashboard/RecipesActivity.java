package com.example.letscook.controller.recipesDashboard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.example.letscook.database.AESCrypt;
import com.example.letscook.database.photo.Photo;
import com.example.letscook.database.product.Product;
import com.example.letscook.database.relationships.UserMarksRecipeCrossRef;
import com.example.letscook.database.relationships.UserViewsRecipeCrossRef;
import com.example.letscook.controller.addRecipe.AddRecipeActivity;
import com.example.letscook.database.typeconverters.DataConverter;
import com.example.letscook.database.user.User;
import com.example.letscook.database.user.UserDao;
import com.example.letscook.controller.products.MyProductsActivity;
import com.example.letscook.controller.profile.ProfileActivity;
import com.example.letscook.R;
import com.example.letscook.controller.register.SignUpActivity;
import com.example.letscook.controller.search.SearchActivity;
import com.example.letscook.controller.products.ShoppingListActivity;
import com.example.letscook.controller.search.WhatToCookActivity;
import com.example.letscook.adapter.RecycleViewAdapter;
import com.example.letscook.database.recipe.Recipe;
import com.example.letscook.database.RoomDB;
import com.example.letscook.controller.home.MainActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.letscook.constants.Messages.APPROPRIATE_MESS;
import static com.example.letscook.constants.Messages.BREAKFAST;
import static com.example.letscook.constants.Messages.BREAKFAST_MESS;
import static com.example.letscook.constants.Messages.DESSERT;
import static com.example.letscook.constants.Messages.DESSERT_MESS;
import static com.example.letscook.constants.Messages.DINNER;
import static com.example.letscook.constants.Messages.DINNER_MESS;
import static com.example.letscook.constants.Messages.EMAIL_NOT_EXIST;
import static com.example.letscook.constants.Messages.EMAIL_REQ;
import static com.example.letscook.constants.Messages.FAV_RECIPES;
import static com.example.letscook.constants.Messages.LAST_ADD;
import static com.example.letscook.constants.Messages.LAST_VIEW;
import static com.example.letscook.constants.Messages.LOGIN;
import static com.example.letscook.constants.Messages.LUNCH;
import static com.example.letscook.constants.Messages.LUNCH_MESS;
import static com.example.letscook.constants.Messages.MY_FAV;
import static com.example.letscook.constants.Messages.MY_RECIPES;
import static com.example.letscook.constants.Messages.MY_RES;
import static com.example.letscook.constants.Messages.MY_VIEWED;
import static com.example.letscook.constants.Messages.NO_REC;
import static com.example.letscook.constants.Messages.NO_REC_FOR_PRODUCTS;
import static com.example.letscook.constants.Messages.PASS_REQ;
import static com.example.letscook.constants.Messages.TO_APPROVE;
import static com.example.letscook.constants.Messages.WRONG_PASS;

public class RecipesActivity extends AppCompatActivity {
    private int id;
    private ImageView my_products;
    private CircleImageView profile;
    private NavigationView navigationView = null;
    public static RecyclerView recyclerView;
    public static List<Recipe> dataList = new ArrayList<>();
    private List<Photo> images = new ArrayList<>();
    private RoomDB database;
    private AlertDialog dialog = null;
    private User user;
    private long userId;
    private boolean isAtFav = false;
    private boolean isAtMyRec = false;
    private boolean isAtApprove = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipes);

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
                userId = user.getID();
                if (user.getPhoto() != null) {
                    profile.setImageBitmap(DataConverter.byteArrayToImage(user.getPhoto()));
                } else {
                    profile.setImageResource(R.drawable.ic_profile_photo);
                }
            } else {
                navigationView = findViewById(R.id.login_view);
            }
        }

        // Initialize action bar variables
        ImageView backIcon = findViewById(R.id.back_icon);
        TextView actionText = findViewById(R.id.action_bar_text);
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecipesActivity.super.onBackPressed();
            }
        });
        TextView textView = findViewById(R.id.textView);
        recyclerView = findViewById(R.id.recycler_view);

        String phrase = getIntent().getStringExtra("phrase");
        String category = getIntent().getStringExtra("category");
        String recipeName = getIntent().getStringExtra("recipeName");
        int veg = getIntent().getIntExtra("vegetarian", -1);
        int productsNeed = getIntent().getIntExtra("products", -1);
        ArrayList<? extends Parcelable> productsList = getIntent().getParcelableArrayListExtra("myProductsList");

        if (phrase != null) {
            actionText.setText(phrase);
            switch (phrase) {
                case MY_RECIPES:
                    textView.setText(MY_RES);
                    textView.setVisibility(View.VISIBLE);
                    dataList = database.recipeDao().getRecipesByOwnerId(userId, user.getServerID());
                    isAtMyRec = true;
                    if (dataList.size() > 0) {
                        textView.setVisibility(View.INVISIBLE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                    break;
                case FAV_RECIPES:
                    dataList = new ArrayList<>();
                    textView.setText(MY_FAV);
                    textView.setVisibility(View.VISIBLE);
                    List<UserMarksRecipeCrossRef> recipesMark = database.userMarksRecipeDao().getRecipes(user.getID(), user.getServerID());
                    for (UserMarksRecipeCrossRef userMarksRecipeCrossRef : recipesMark) {
                        Recipe recipeByLocalOrServerId = database.recipeDao().getRecipeByLocalOrServerId(userMarksRecipeCrossRef.getRecipe_id());
                        if (recipeByLocalOrServerId != null && !userMarksRecipeCrossRef.isDeleted()) {
                            dataList.add(recipeByLocalOrServerId);
                        }
                    }
                    isAtFav = true;
                    if (dataList.size() > 0) {
                        textView.setVisibility(View.INVISIBLE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                    break;
                case LAST_VIEW:
                    dataList = new ArrayList<>();
                    textView.setText(MY_VIEWED);
                    textView.setVisibility(View.VISIBLE);
                    List<UserViewsRecipeCrossRef> recipes = database.userViewsRecipeDao().getRecipes(user.getID(), user.getServerID());
                    for (UserViewsRecipeCrossRef userViewsRecipeCrossRef : recipes) {
                        Recipe recipeByLocalOrServerId = database.recipeDao().getRecipeByLocalOrServerId(userViewsRecipeCrossRef.getRecipe_id());
                        if (recipeByLocalOrServerId != null) {
                            dataList.add(recipeByLocalOrServerId);
                        }
                    }
                    if (dataList.size() > 0) {
                        textView.setVisibility(View.INVISIBLE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                    break;
                case LAST_ADD:
                    dataList = database.recipeDao().getAllLastAddedRecipes();
                    if (dataList.size() > 0) {
                        textView.setVisibility(View.INVISIBLE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                    break;
                case APPROPRIATE_MESS:
                    textView.setText(NO_REC_FOR_PRODUCTS);
                    if (veg < 0) {
                        dataList = database.recipeDao().getAllRecipeByCategory(category.trim());
                    } else {
                        dataList = database.recipeDao().getAllRecipeByCategoryAndVeg(category.trim(), veg);
                    }
                    for (Recipe recipe : dataList) {
                        List<Product> products = database.productDao().getRecipeProducts("toRecipe", recipe.getID(), recipe.getServerID());
                        if (products.size() > productsList.size() + productsNeed) {
                            textView.setVisibility(View.VISIBLE);
                        } else {
                            int count = 0;
                            for (Product product : products) {
                                for (Parcelable prod : productsList) {
                                    Product myProduct = (Product) prod;
                                    if (product.getName().toLowerCase().equals(myProduct.getName().toLowerCase())) {
                                        count++;
                                    }
                                }
                            }
                            if (products.size() <= count + productsNeed) {
                                recyclerView.setVisibility(View.VISIBLE);
                            } else {
                                textView.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                    break;
                case TO_APPROVE:
                    isAtApprove = true;
                    dataList = database.recipeDao().getAllUnapprovedRecipes();
                    textView.setVisibility(View.INVISIBLE);
                    recyclerView.setVisibility(View.VISIBLE);
                    break;
            }
        } else if (recipeName != null) {
            actionText.setText(APPROPRIATE_MESS);
            textView.setText(NO_REC);
            textView.setVisibility(View.VISIBLE);
            dataList = database.recipeDao().getAllRecipeByName(recipeName);
            if (dataList.size() > 0) {
                textView.setVisibility(View.INVISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
            }
        } else if (category != null) {
            switch (category) {
                case BREAKFAST:
                    actionText.setText(BREAKFAST_MESS);
                    break;
                case LUNCH:
                    actionText.setText(LUNCH_MESS);
                    break;
                case DINNER:
                    actionText.setText(DINNER_MESS);
                    break;
                case DESSERT:
                    actionText.setText(DESSERT_MESS);
                    break;
            }
            textView.setText(NO_REC);
            textView.setVisibility(View.VISIBLE);
            if (veg < 0) {
                dataList = database.recipeDao().getAllRecipeByCategory(category.toLowerCase().trim());
            } else {
                dataList = database.recipeDao().getAllRecipeByCategoryAndVeg(category.toLowerCase().trim(), veg);
            }
            if (dataList.size() > 0) {
                textView.setVisibility(View.INVISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
            }
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
                if (user == null) {
                    deniedDialog();
                } else {
                    startActivity(new Intent(getApplicationContext(), MyProductsActivity.class));
                    Animatoo.animateSlideDown(RecipesActivity.this);
                    my_products.setColorFilter(Color.parseColor("#FFFEF6D8"));
                }
            }
        });

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);
        RecycleViewAdapter recycleViewAdapter = new RecycleViewAdapter(RecipesActivity.this, dataList, userId, isAtFav, isAtMyRec, isAtApprove);
        recyclerView.setAdapter(recycleViewAdapter);
        recyclerView.setHasFixedSize(true);

        // Initialize and assign variable
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);
        // Perform item selected list
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (navigationView != null && navigationView.getVisibility() == View.VISIBLE) {
                    hideNavView();
                } else {
                    id = item.getItemId();
                    Intent intent = null;
                    switch (id) {
                        case R.id.home:
                            intent = new Intent(getApplicationContext(), MainActivity.class);
                            break;
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
                    startActivity(intent);
                    Animatoo.animateZoom(RecipesActivity.this);
                    return true;
                }
                return false;
            }
        });
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
            Animatoo.animateSlideDown(RecipesActivity.this);
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
                navigationView = findViewById(R.id.login_view);
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
                } else {
                    try {
                        if (!user.getPassword().equals(AESCrypt.encrypt(userPass))) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    required.setText(WRONG_PASS);
                                    required.setVisibility(View.VISIBLE);
                                }
                            });
                        } else {
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
                                        hideNavView();
                                        navigationView = null;
                                    }
                                }
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
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
                navigationView = null;
                if (user.getPhoto() != null) {
                    profile.setImageBitmap(DataConverter.byteArrayToImage(user.getPhoto()));
                } else {
                    profile.setImageResource(R.drawable.ic_profile_photo);
                }
            } else {
                navigationView = findViewById(R.id.login_view);
            }
        }
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
                navigationView = null;
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
            super.onBackPressed();
        }
    }
}