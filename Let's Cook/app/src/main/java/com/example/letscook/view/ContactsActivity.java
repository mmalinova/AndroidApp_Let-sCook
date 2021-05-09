package com.example.letscook.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.example.letscook.R;
import com.example.letscook.database.RoomDB;
import com.example.letscook.database.typeconverters.DataConverter;
import com.example.letscook.database.user.User;
import com.example.letscook.database.user.UserDao;
import com.example.letscook.view.home.MainActivity;
import com.example.letscook.view.products.MyProductsActivity;
import com.example.letscook.view.products.ShoppingListActivity;
import com.example.letscook.view.profile.ProfileActivity;
import com.example.letscook.view.register.SignUpActivity;
import com.example.letscook.view.search.SearchActivity;
import com.example.letscook.view.search.WhatToCookActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.letscook.constants.Messages.*;

public class ContactsActivity extends AppCompatActivity {
    private int id;
    private TextView facebookText, linkedInText, instText;
    private ImageView backIcon;
    private TextView actionText;
    private CircleImageView profile;
    private ImageView my_products;
    private NavigationView navigationView = null;
    private Button sendBtn;
    private EditText username, email, message;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog = null;
    private Button okButton;
    private RoomDB database;
    private User user;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

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
            if (user.getPhoto() != null) {
                profile.setImageBitmap(DataConverter.byteArrayToImage(user.getPhoto()));
            } else {
                profile.setImageResource(R.drawable.ic_profile_photo);
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
                if (getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                        .getString("email", null) == null) {
                    deniedDialog();
                } else {
                    startActivity(new Intent(getApplicationContext(), MyProductsActivity.class));
                    Animatoo.animateSlideDown(ContactsActivity.this);
                    my_products.setColorFilter(Color.parseColor("#FFFEF6D8"));
                }
            }
        });

        // Initialize action bar variables
        backIcon = findViewById(R.id.back_icon);
        actionText = findViewById(R.id.action_bar_text);
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContactsActivity.super.onBackPressed();
            }
        });
        actionText.setText(CONTACTS);

        sendBtn = findViewById(R.id.button);
        sendBtn.setEnabled(false);
        username = findViewById(R.id.textViewName);
        username.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (navigationView != null && navigationView.getVisibility() == View.VISIBLE) {
                    hideNavView();
                }
                return false;
            }
        });
        email = findViewById(R.id.textViewEmail);
        email.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (navigationView != null && navigationView.getVisibility() == View.VISIBLE) {
                    hideNavView();
                }
                return false;
            }
        });
        message = findViewById(R.id.textViewMess);
        message.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (v.getId() == R.id.textViewMess) {
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    switch (event.getAction()&MotionEvent.ACTION_MASK){
                        case MotionEvent.ACTION_UP:
                            v.getParent().requestDisallowInterceptTouchEvent(false);
                            break;
                    }
                }
                return false;
            }
        });
        // Initialize and assign variable
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);

        // Perform item selected list
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
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
                            if (getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                                    .getString("email", null) == null) {
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
                            if (getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                                    .getString("email", null) == null) {
                                deniedDialog();
                                return false;
                            } else {
                                intent = new Intent(getApplicationContext(), ShoppingListActivity.class);
                            }
                            break;
                    }
                    startActivity(intent);
                    Animatoo.animateZoom(ContactsActivity.this);
                    return true;
                }
                return false;
            }
        });

        facebookText = findViewById(R.id.textViewFb);
        facebookText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(FB_URI)));
                    //100000685270756
                    facebookText.setLinkTextColor(Color.parseColor("#55067D"));
                } catch (Exception e) {
                }
            }
        });

        linkedInText = findViewById(R.id.textViewLi);
        linkedInText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(LINKED_IN_URI)));
                linkedInText.setLinkTextColor(Color.parseColor("#55067D"));
            }
        });

        instText = findViewById(R.id.textViewInst);
        instText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(INSTAGRAM_URI)));
                instText.setLinkTextColor(Color.parseColor("#55067D"));
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
            Animatoo.animateSlideDown(ContactsActivity.this);
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
            if (user.getPhoto() != null) {
                profile.setImageBitmap(DataConverter.byteArrayToImage(user.getPhoto()));
            } else {
                profile.setImageResource(R.drawable.ic_profile_photo);
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

        okButton = popupView.findViewById(R.id.okBtn);

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
            if (user.getPhoto() != null) {
                profile.setImageBitmap(DataConverter.byteArrayToImage(user.getPhoto()));
            } else {
                profile.setImageResource(R.drawable.ic_profile_photo);
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
            if (user.getPhoto() != null) {
                profile.setImageBitmap(DataConverter.byteArrayToImage(user.getPhoto()));
            } else {
                profile.setImageResource(R.drawable.ic_profile_photo);
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