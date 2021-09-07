package com.example.letscook.controller.search;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.example.letscook.adapter.ProductsForRecipeAdapter;
import com.example.letscook.database.product.Product;
import com.example.letscook.controller.addRecipe.AddRecipeActivity;
import com.example.letscook.database.RoomDB;
import com.example.letscook.database.typeconverters.DataConverter;
import com.example.letscook.database.user.User;
import com.example.letscook.database.user.UserDao;
import com.example.letscook.controller.products.MyProductsActivity;
import com.example.letscook.R;
import com.example.letscook.controller.products.ShoppingListActivity;
import com.example.letscook.controller.home.MainActivity;
import com.example.letscook.controller.profile.ProfileActivity;
import com.example.letscook.controller.recipesDashboard.RecipesActivity;
import com.example.letscook.controller.register.SignUpActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.letscook.constants.Messages.*;

public class WhatToCookActivity extends AppCompatActivity {
    private int id;
    private TextView textView;
    private TextView initialTextView;
    private ImageView my_products;
    private CircleImageView profile;
    private NavigationView navigationView = null;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog = null;
    private Button searchBtn;
    private BottomNavigationView bottomNavigationView;
    private EditText productName;
    private RoomDB database;
    private User user;
    private RecyclerView recyclerView;
    private ArrayList<Product> dataList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private ProductsForRecipeAdapter productsForRecipeAdapter;
    private final String[] categories = {BREAKFAST, LUNCH, DINNER, DESSERT};

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_what_to_cook);

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
                    Animatoo.animateSlideDown(WhatToCookActivity.this);
                    my_products.setColorFilter(Color.parseColor("#FFFEF6D8"));
                }
            }
        });

        // Initialize action bar variables
        ImageView backIcon = findViewById(R.id.back_icon);
        TextView actionText = findViewById(R.id.action_bar_text);
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WhatToCookActivity.super.onBackPressed();
            }
        });
        actionText.setText(WHAT_TO_COOK);
        productName = findViewById(R.id.product_name);
        productName.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if (navigationView != null && navigationView.getVisibility() == View.VISIBLE) {
                    hideNavView();
                } else {
                    if (v.getId() == R.id.product_name) {
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        switch (event.getAction() & MotionEvent.ACTION_MASK) {
                            case MotionEvent.ACTION_UP:
                                v.getParent().requestDisallowInterceptTouchEvent(false);
                                break;
                        }
                    }
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        if(event.getRawX() >= (productName.getRight() - productName.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                            Product product = new Product();
                            String name = productName.getText().toString().trim();
                            if (!name.equals("")) {
                                product.setName(name);
                                dataList.add(product);
                                productName.setText("");
                                if (dataList.size() > 0) {
                                    initialTextView.setVisibility(View.INVISIBLE);
                                    textView.setVisibility(View.INVISIBLE);
                                    recyclerView.setVisibility(View.VISIBLE);
                                    searchBtn.setEnabled(true);
                                }
                                linearLayoutManager = new LinearLayoutManager(WhatToCookActivity.this);
                                recyclerView.setLayoutManager(linearLayoutManager);
                                productsForRecipeAdapter = new ProductsForRecipeAdapter(WhatToCookActivity.this, dataList, "myProducts");
                                recyclerView.setAdapter(productsForRecipeAdapter);
                            }
                            return true;
                        }
                    }
                }
                return false;
            }
        });
        // Initialize search
        searchBtn = findViewById(R.id.searchBtn);
        searchBtn.setEnabled(false);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (navigationView != null && navigationView.getVisibility() == View.VISIBLE) {
                    hideNavView();
                } else {
                    catTermsDialog();
                }
            }
        });
        recyclerView = findViewById(R.id.recycler_view);
        initialTextView = findViewById(R.id.initialTextView);
        textView = findViewById(R.id.textView);
        SpannableString spannableString = new SpannableString(WHAT_TO_COOK_INFO);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                if (user == null) {
                    deniedDialog();
                } else {
                    initialTextView.setVisibility(View.INVISIBLE);
                    // Store db value in product list
                    dataList = (ArrayList<Product>) database.productDao().getUserProducts("myProducts", user.getID());
                    if (dataList.size() > 0) {
                        textView.setVisibility(View.INVISIBLE);
                        recyclerView.setVisibility(View.VISIBLE);
                        searchBtn.setEnabled(true);
                    } else {
                        recyclerView.setVisibility(View.INVISIBLE);
                        textView.setVisibility(View.VISIBLE);
                    }
                    linearLayoutManager = new LinearLayoutManager(WhatToCookActivity.this);
                    recyclerView.setLayoutManager(linearLayoutManager);
                    productsForRecipeAdapter = new ProductsForRecipeAdapter(WhatToCookActivity.this, dataList, "myProducts");
                    recyclerView.setAdapter(productsForRecipeAdapter);
                }
            }
            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.parseColor("#FFAB00"));
                ds.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD_ITALIC));
            }
        };
        spannableString.setSpan(clickableSpan, 90, 96, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        initialTextView.setText(spannableString);
        initialTextView.setMovementMethod(LinkMovementMethod.getInstance());

        // Initialize and assign variable
        bottomNavigationView = findViewById(R.id.bottom_nav);
        // Set selected
        bottomNavigationView.setSelectedItemId(R.id.what_to_cook);
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
                            return true;
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
                    Animatoo.animateZoom(WhatToCookActivity.this);
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
            Animatoo.animateSlideDown(WhatToCookActivity.this);
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
                                hideNavView();
                                navigationView = null;
                            }
                        }
                    });
                }
            }
        }).start();
    }

    public void deniedDialog() {
        dialogBuilder = new AlertDialog.Builder(this);
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

    public void catTermsDialog() {
        dialogBuilder = new AlertDialog.Builder(this);
        final View popupView = getLayoutInflater().inflate(R.layout.category_popup, null);

        RadioGroup radioGroup = popupView.findViewById(R.id.radioGroup);
        RadioGroup secondRadioGroup = popupView.findViewById(R.id.secondRadioGroup);

        dialogBuilder.setView(popupView);
        dialog = dialogBuilder.create();
        dialog.show();

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int id = radioGroup.getCheckedRadioButtonId();
                RadioButton radioButton = radioGroup.findViewById(id);
                String text = radioButton.getText().toString();
                // Attempt to query according to index
                switch (text) {
                    case "Закуска":
                        vegTermsDialog(categories[0]);
                        break;
                    case "Вечеря":
                        vegTermsDialog(categories[2]);
                        break;
                }
                radioButton.setChecked(false);
            }
        });
        secondRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int id = secondRadioGroup.getCheckedRadioButtonId();
                RadioButton radioButton = secondRadioGroup.findViewById(id);
                String text = radioButton.getText().toString();
                // Attempt to query according to index
                switch (text) {
                    case "Обяд":
                        vegTermsDialog(categories[1]);
                        break;
                    case "Десерт":
                        vegTermsDialog(categories[3]);
                        break;
                }
                radioButton.setChecked(false);
            }
        });
    }

    public void vegTermsDialog(String cat) {
        dialogBuilder = new AlertDialog.Builder(this);
        final View popupView = getLayoutInflater().inflate(R.layout.veg_popup, null);

        Button yesButton = popupView.findViewById(R.id.okBtn);
        Button noButton = popupView.findViewById(R.id.noBtn);
        Button noMatter = popupView.findViewById(R.id.noMatter);

        dialogBuilder.setView(popupView);
        dialog = dialogBuilder.create();
        dialog.show();

        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Append filter to request
                prodTermsDialog(cat, 1);
            }
        });
        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start activity
                prodTermsDialog(cat, 0);
            }
        });
        noMatter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start activity
                prodTermsDialog(cat, -1);
            }
        });
    }

    public void prodTermsDialog(String cat, int vegIndex) {
        dialogBuilder = new AlertDialog.Builder(this);
        final View popupView = getLayoutInflater().inflate(R.layout.products_popup, null);

        RadioGroup radioGroup = popupView.findViewById(R.id.radioGroup);

        dialogBuilder.setView(popupView);
        dialog = dialogBuilder.create();
        dialog.show();

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int id = radioGroup.getCheckedRadioButtonId();
                RadioButton radioButton = radioGroup.findViewById(id);
                int prodIndex = radioGroup.indexOfChild(radioButton);

                Intent intent = new Intent(getApplicationContext(), RecipesActivity.class);
                intent.putExtra("category", cat);
                intent.putExtra("vegetarian", vegIndex);
                switch (prodIndex) {
                    case 0:
                        intent.putExtra("products", 3);
                        break;
                    case 1:
                        intent.putExtra("products", 5);
                        break;
                    case 2:
                        intent.putExtra("products", 0);
                        break;
                }
                radioButton.setChecked(false);
                intent.putExtra("phrase", APPROPRIATE_MESS);
                intent.putParcelableArrayListExtra("myProductsList", dataList);
                startActivity(intent);
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
        bottomNavigationView.setSelectedItemId(R.id.what_to_cook);
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
            if (!getIntent().getBooleanExtra("isFromMain", false)) {
                overridePendingTransition(0, 0);
            }
        }
    }
}