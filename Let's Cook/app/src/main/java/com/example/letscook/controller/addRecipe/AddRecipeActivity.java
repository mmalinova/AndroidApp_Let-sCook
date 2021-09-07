package com.example.letscook.controller.addRecipe;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.example.letscook.R;
import com.example.letscook.database.RoomDB;
import com.example.letscook.database.photo.Photo;
import com.example.letscook.database.product.Product;
import com.example.letscook.database.recipe.Recipe;
import com.example.letscook.database.recipe.RecipeDao;
import com.example.letscook.database.typeconverters.ConvertDate;
import com.example.letscook.database.typeconverters.DataConverter;
import com.example.letscook.database.user.User;
import com.example.letscook.controller.home.MainActivity;
import com.example.letscook.controller.products.MyProductsActivity;
import com.example.letscook.controller.products.ShoppingListActivity;
import com.example.letscook.controller.profile.ProfileActivity;
import com.example.letscook.controller.search.SearchActivity;
import com.example.letscook.controller.search.WhatToCookActivity;
import com.example.letscook.server_database.MySingleton;
import com.example.letscook.server_database.NetworkMonitor;
import com.example.letscook.server_database.URLs;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.letscook.constants.Messages.*;

public class AddRecipeActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private int id;
    private ImageView my_products;
    private CircleImageView profile;
    private Spinner spinner;
    private Button addProduct;
    private Button addRecipe;
    private RadioGroup firstRG, secondRG, vegRG;
    private EditText recipeName, portions, productName, quantity, steps, hours, minutes;
    private BottomNavigationView bottomNavigationView;
    private RoomDB database;
    private User user;
    private ArrayList<Product> productList = new ArrayList<>();
    private AlertDialog dialog = null;
    private Uri imageUri;
    private final int CAMERA_INTENT = 1;
    private final int SELECT_PHOTO = 2;
    private int count = 0;
    private ArrayList<Photo> photos = new ArrayList<>();
    private ImageView firstRecImg, secondRecImg, thirdRecImg;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipe);

        // Initialize profile  and my products links
        profile = findViewById(R.id.profile);
        my_products = findViewById(R.id.my_products);

        // Initialize db
        database = RoomDB.getInstance(this);
        // Set view according session storage
        String e = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("email", null);
        if (e == null) {
            profile.setImageResource(R.drawable.ic_profile);
        } else {
            user = database.userDao().getUserByEmail(e);
            if (user != null) {
                if (user.getPhoto() != null) {
                    profile.setImageBitmap(DataConverter.byteArrayToImage(user.getPhoto()));
                } else {
                    profile.setImageResource(R.drawable.ic_profile_photo);
                }
            }
        }

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(intent);
                Animatoo.animateSlideDown(AddRecipeActivity.this);
                profile.setBorderColor(Color.parseColor("#FFFEF6D8"));
            }
        });
        my_products.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MyProductsActivity.class));
                Animatoo.animateSlideDown(AddRecipeActivity.this);
                my_products.setColorFilter(Color.parseColor("#FFFEF6D8"));
            }
        });

        // Initialize action bar variables
        ImageView backIcon = findViewById(R.id.back_icon);
        TextView actionText = findViewById(R.id.action_bar_text);
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddRecipeActivity.super.onBackPressed();
            }
        });
        actionText.setText(ADD_RECIPE);

        // Get current user
        String userEmail = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getString("email", null);
        user = database.userDao().getUserByEmail(userEmail);
        TextView required = findViewById(R.id.textViewRequired);
        recipeName = findViewById(R.id.editTextName);
        recipeName.addTextChangedListener(watcher);
        final String[] firstCategory = new String[1];
        boolean isSecond = false;
        firstRG = findViewById(R.id.radioGroup);
        firstRG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int id = firstRG.getCheckedRadioButtonId();
                RadioButton radioButton = firstRG.findViewById(id);
                String text = radioButton.getText().toString();
                // Attempt to query according to index
                switch (text) {
                    case "Закуска":
                        firstCategory[0] = "закуска";
                        break;
                    case "Вечеря":
                        firstCategory[0] = "вечеря";
                        break;
                }
            }
        });
        final String[] secondCategory = new String[1];
        secondRG = findViewById(R.id.secondRadioGroup);
        secondRG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int id = secondRG.getCheckedRadioButtonId();
                RadioButton radioButton = secondRG.findViewById(id);
                String text = radioButton.getText().toString();
                // Attempt to query according to index
                switch (text) {
                    case "Обяд":
                        secondCategory[0] = "обяд";
                        break;
                    case "Десерт":
                        secondCategory[0] = "десерт";
                        break;
                }
            }
        });
        vegRG = findViewById(R.id.radioGroupVeg);
        final boolean[] veg = new boolean[1];
        vegRG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int id = vegRG.getCheckedRadioButtonId();
                RadioButton radioButton = vegRG.findViewById(id);
                String text = radioButton.getText().toString();
                // Attempt to query according to index
                switch (text) {
                    case "Да":
                        veg[0] = true;
                        break;
                    case "Не":
                        veg[0] = false;
                        break;
                }
            }
        });
        Button browseBtn = findViewById(R.id.uploadBtn);
        browseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if device has a camera
                PackageManager pm = AddRecipeActivity.this.getPackageManager();
                if (pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
                    if (ContextCompat.checkSelfPermission(AddRecipeActivity.this, Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(AddRecipeActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(AddRecipeActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE}, CAMERA_INTENT);
                    } else {
                        uploadPictureDialog();
                    }
                } else {
                    if (ContextCompat.checkSelfPermission(AddRecipeActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(AddRecipeActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, CAMERA_INTENT);
                    } else {
                        uploadFromGallery();
                    }
                }
            }
        });
        firstRecImg = findViewById(R.id.firstRecipeImg);
        secondRecImg = findViewById(R.id.secondRecipeImg);
        thirdRecImg = findViewById(R.id.thirdRecipeImg);
        portions = findViewById(R.id.editTextPortion);
        portions.addTextChangedListener(watcher);
        productName = findViewById(R.id.editTextProd);
        productName.addTextChangedListener(watcher);
        quantity = findViewById(R.id.editTextQuantity);
        quantity.addTextChangedListener(watcher);

        // Get data
        String[] units = {MEASURING_UNITS_REQ, ML, L, GR, KG, GLASS, SMALL_GLASS, SPOON, SMALL_SPOON, PINCH, PINCHES, PACKET, PACKETS};
        spinner = findViewById(R.id.spinner);
        spinner.setBackgroundColor(Color.parseColor("#56FFCFA6"));
        ArrayAdapter<String> adapter = new ArrayAdapter(this, R.layout.spiner_item, units);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        addProduct = findViewById(R.id.addProduct);
        addProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Product product = new Product();
                float sQuantity = 0;
                String sName = productName.getText().toString().trim().toLowerCase();
                String q = quantity.getText().toString().trim();
                String sMeasureUnit = spinner.getSelectedItem().toString();
                if (!sName.equals("")) {
                    if (sMeasureUnit.contains("Мерна")) {
                        sMeasureUnit = "";
                    }
                    if (!q.equals("") && !q.equals(".")) {
                        sQuantity = Float.parseFloat(q);
                    }
                    product.setName(sName);
                    product.setMeasureUnit(sMeasureUnit);
                    product.setQuantity(sQuantity);
                    product.setBelonging("toRecipe");
                    productList.add(product);
                    // Clear edit texts
                    productName.setText("");
                    spinner.setSelection(0);
                    quantity.setText("");
                }
                addProduct.setEnabled(false);
            }
        });

        steps = findViewById(R.id.editTextPrep);
        steps.addTextChangedListener(watcher);
        steps.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (v.getId() == R.id.editTextPrep) {
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                    }
                }
                return false;
            }
        });
        hours = findViewById(R.id.editTextH);
        hours.addTextChangedListener(watcher);
        minutes = findViewById(R.id.editTextMin);
        minutes.addTextChangedListener(watcher);

        addRecipe = findViewById(R.id.addBtn);
        addRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Recipe recipe = new Recipe();
                String name = recipeName.getText().toString().trim().toLowerCase();
                int portion = Integer.parseInt(String.valueOf(portions.getText()));
                String step = steps.getText().toString();
                int hour = Integer.parseInt(String.valueOf(hours.getText()));
                int min = Integer.parseInt(String.valueOf(minutes.getText()));

                if (firstRG.getCheckedRadioButtonId() == -1 && secondRG.getCheckedRadioButtonId() == -1) {
                    requiredDialog();
                    return;
                }
                if (vegRG.getCheckedRadioButtonId() == -1) {
                    requiredDialog();
                    return;
                }
                if (photos.size() <= 0) {
                    requiredDialog();
                    return;
                }
                if (productList.size() <= 0) {
                    requiredDialog();
                    return;
                }
                recipe.setName(name);
                if (firstCategory[0] != null && secondCategory[0] != null) {
                    recipe.setCategory(firstCategory[0] + ", " + secondCategory[0]);
                } else {
                    if (firstCategory[0] != null) {
                        recipe.setCategory(firstCategory[0]);
                    } else {
                        recipe.setCategory(secondCategory[0]);
                    }
                }
                if (veg[0]) {
                    recipe.setVegetarian(1);
                } else {
                    recipe.setVegetarian(0);
                }
                recipe.setPortions(portion);
                recipe.setSteps(step);
                recipe.setHours(hour);
                recipe.setMinutes(min);
                recipe.setImage(photos.get(0).getPhoto());
                if (user.isAdmin()) {
                    recipe.setIsApproved(true);
                } else {
                    recipe.setIsApproved(false);
                }
                Date currentTime = Calendar.getInstance().getTime();
                recipe.setCreatedOn(currentTime);
                recipe.setOwnerID(user.getID());
                database.recipeDao().insert(recipe);
                Recipe rec = database.recipeDao().getRecipeByName(name);
                if (NetworkMonitor.checkNetworkConnection(AddRecipeActivity.this)) {
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.RECIPES_URL,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        JSONObject jsonObject = new JSONObject(response);
                                        String stringResponse = jsonObject.getString("response");
                                        if (stringResponse.equals("OK")) {
                                            database.recipeDao().recipeSync(rec.getID());
                                            // Get recipe ID from MySQL
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> params = new HashMap<>();
                            params.put("name", rec.getName());
                            params.put("category", rec.getCategory());
                            params.put("vegetarian", String.valueOf(rec.getVegetarian()));
                            params.put("image", String.valueOf(DataConverter.byteArrayToImage(rec.getImage())));
                            params.put("portions", String.valueOf(rec.getPortions()));
                            params.put("steps", rec.getSteps());
                            params.put("hours", String.valueOf(rec.getHours()));
                            params.put("minutes", String.valueOf(rec.getMinutes()));
                            params.put("created_on", String.valueOf(ConvertDate.dateToTimestamp(rec.getCreatedOn())));
                            params.put("is_approved", String.valueOf(rec.isApproved() ? 1 : 0));
                            params.put("is_SQLite_sync", "1");
                            params.put("owner_id", String.valueOf(rec.getOwnerID()));
                            return params;
                        }
                    };
                    MySingleton.getInstance(AddRecipeActivity.this).addToRequestQueue(stringRequest);
                }
                for (Photo photo : photos) {
                    photo.setRecipe_id(rec.getID());
                    database.photoDao().insert(photo);
                    if (NetworkMonitor.checkNetworkConnection(AddRecipeActivity.this)) {
                        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.PHOTOS_URL,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        try {
                                            JSONObject jsonObject = new JSONObject(response);
                                            String stringResponse = jsonObject.getString("response");
                                            if (stringResponse.equals("OK")) {
                                                database.photoDao().photoSync(photo.getID());
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                            }
                        }) {
                            @Override
                            protected Map<String, String> getParams() {
                                Map<String, String> params = new HashMap<>();
                                params.put("photo", String.valueOf(DataConverter.byteArrayToImage(photo.getPhoto())));
                                params.put("is_SQLite_sync", "1");
                                params.put("recipe_id", String.valueOf(//Set ID));
                                return params;
                            }
                        };
                        MySingleton.getInstance(AddRecipeActivity.this).addToRequestQueue(stringRequest);
                    }
                }
                for (Product product : productList) {
                    product.setOwnerId(rec.getID());
                    database.productDao().insert(product);
                    if (NetworkMonitor.checkNetworkConnection(AddRecipeActivity.this)) {
                        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.PRODUCTS_URL,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        try {
                                            JSONObject jsonObject = new JSONObject(response);
                                            String stringResponse = jsonObject.getString("response");
                                            if (stringResponse.equals("OK")) {
                                                database.productDao().productSync(product.getID());
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                            }
                        }) {
                            @Override
                            protected Map<String, String> getParams() {
                                Map<String, String> params = new HashMap<>();
                                params.put("name", product.getName());
                                params.put("measure_unit", product.getMeasureUnit());
                                params.put("quantity", String.valueOf(product.getQuantity()));
                                params.put("belonging", product.getBelonging());
                                params.put("is_SQLite_sync", "1");
                                params.put("owner_id", String.valueOf(//Set ID));
                                return params;
                            }
                        };
                        MySingleton.getInstance(AddRecipeActivity.this).addToRequestQueue(stringRequest);
                    }
                }
                if (user.isAdmin()) {
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                } else {
                    deniedDialog();
                }
            }
        });

        // Initialize and assign variable
        bottomNavigationView = findViewById(R.id.bottom_nav);
        // Set selected
        bottomNavigationView.setSelectedItemId(R.id.add_recipe);
        // Perform item selected list
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
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
                        return true;
                    case R.id.search:
                        intent = new Intent(getApplicationContext(), SearchActivity.class);
                        break;
                    case R.id.shopping_list:
                        intent = new Intent(getApplicationContext(), ShoppingListActivity.class);
                        break;
                }
                startActivity(intent);
                Animatoo.animateZoom(AddRecipeActivity.this);
                return true;
            }
        });
    }

    public void deniedDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        final View popupView = getLayoutInflater().inflate(R.layout.delete_popup, null);
        TextView textView = popupView.findViewById(R.id.confirm);
        textView.setText(SUCCESS_ADD);

        Button yesButton = popupView.findViewById(R.id.okBtn);
        Button noButton = popupView.findViewById(R.id.noBtn);

        dialogBuilder.setView(popupView);
        dialog = dialogBuilder.create();
        dialog.show();

        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), AddRecipeActivity.class));
                dialog.dismiss();
            }
        });
        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                dialog.dismiss();
            }
        });
    }

    public void requiredDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        final View popupView = getLayoutInflater().inflate(R.layout.denied_access, null);
        TextView textView = popupView.findViewById(R.id.veg_question);
        textView.setText(ALL_FIELDS_REQ);

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

    private final TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            String name = recipeName.getText().toString().trim().toLowerCase();
            if (name.equals("")) {
                return;
            }
            String portion = portions.getText().toString().trim();
            if (portion.equals("")) {
                return;
            }
            String prodName = productName.getText().toString().trim().toLowerCase();
            String q = quantity.getText().toString().trim();
            if (!prodName.equals("") && !q.equals("")) {
                addProduct.setEnabled(true);
            }

            String step = steps.getText().toString().trim();
            if (step.equals("")) {
                return;
            }
            String hour = hours.getText().toString().trim();
            if (hour.equals("")) {
                return;
            }
            String min = minutes.getText().toString().trim();
            if (min.equals("")) {
                return;
            }
            addRecipe.setEnabled(true);
        }
    };

    public void uploadPictureDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        final View popupView = getLayoutInflater().inflate(R.layout.upload_from_gallery, null);

        TextView title = popupView.findViewById(R.id.choose);
        title.setText(BROWSE_TITLE);
        TextView chooseFromGallery = popupView.findViewById(R.id.gallery);
        TextView takeAPhoto = popupView.findViewById(R.id.remove);
        takeAPhoto.setText(BROWSE_FROM_CAMERA);

        dialogBuilder.setView(popupView);
        dialog = dialogBuilder.create();
        dialog.show();

        chooseFromGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, SELECT_PHOTO);
                }
                dialog.dismiss();
            }
        });
        takeAPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.TITLE, "New Picture");
                values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
                imageUri = getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                //if (intent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(intent, CAMERA_INTENT);
                //}
                dialog.dismiss();
            }
        });
    }

    public void uploadFromGallery() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        final View popupView = getLayoutInflater().inflate(R.layout.upload_from_gallery, null);

        TextView chooseFromGallery = popupView.findViewById(R.id.gallery);
        TextView removePhoto = popupView.findViewById(R.id.remove);
        removePhoto.setVisibility(View.INVISIBLE);

        dialogBuilder.setView(popupView);
        dialog = dialogBuilder.create();
        dialog.show();

        chooseFromGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, SELECT_PHOTO);
                }
                dialog.dismiss();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (count >= 3) {
            count = 0;
        }
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_INTENT) {
            if (resultCode == Activity.RESULT_OK) {
                try {
                    Bitmap bmpImage = MediaStore.Images.Media.getBitmap(
                            getContentResolver(), imageUri);
                    bmpImage = getResizedBitmap(bmpImage, 900, 1000);
                    switch (count) {
                        case 0:
                            firstRecImg.setImageBitmap(bmpImage);
                            count++;
                            break;
                        case 1:
                            secondRecImg.setImageBitmap(bmpImage);
                            count++;
                            break;
                        case 2:
                            thirdRecImg.setImageBitmap(bmpImage);
                            count++;
                            break;
                    }
                    Photo photo = new Photo();
                    photo.setPhoto(DataConverter.imageToByteArray(bmpImage));
                    photos.add(photo);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if (requestCode == SELECT_PHOTO) {
            if (resultCode == Activity.RESULT_OK) {
                try {
                    assert data != null;
                    final Uri img = data.getData();
                    final InputStream stream = getContentResolver().openInputStream(img);
                    Bitmap selectedImage = BitmapFactory.decodeStream(stream);
                    selectedImage = getResizedBitmap(selectedImage, 900, 1000);
                    final RecipeDao recipeDao = database.recipeDao();
                    switch (count) {
                        case 0:
                            firstRecImg.setImageBitmap(selectedImage);
                            count++;
                            break;
                        case 1:
                            secondRecImg.setImageBitmap(selectedImage);
                            count++;
                            break;
                        case 2:
                            thirdRecImg.setImageBitmap(selectedImage);
                            count++;
                            break;
                    }
                    Photo photo = new Photo();
                    photo.setPhoto(DataConverter.imageToByteArray(selectedImage));
                    photos.add(photo);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);
        // "RECREATE" THE NEW BITMAP
        return Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
    }

    @Override
    protected void onStart() {
        profile.setBorderColor(Color.parseColor("#000000"));
        my_products.setColorFilter(Color.parseColor("#000000"));
        // Set view according session storage
        String e = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("email", null);
        if (e == null) {
            profile.setImageResource(R.drawable.ic_profile);
        } else {
            user = database.userDao().getUserByEmail(e);
            if (user != null) {
                if (user.getPhoto() != null) {
                    profile.setImageBitmap(DataConverter.byteArrayToImage(user.getPhoto()));
                } else {
                    profile.setImageResource(R.drawable.ic_profile_photo);
                }
            }
        }
        bottomNavigationView.setSelectedItemId(R.id.add_recipe);
        super.onStart();
    }

    @Override
    protected void onResume() {
        profile.setBorderColor(Color.parseColor("#000000"));
        my_products.setColorFilter(Color.parseColor("#000000"));
        // Set view according session storage
        String e = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("email", null);
        if (e == null) {
            profile.setImageResource(R.drawable.ic_profile);
        } else {
            user = database.userDao().getUserByEmail(e);
            if (user != null) {
                if (user.getPhoto() != null) {
                    profile.setImageBitmap(DataConverter.byteArrayToImage(user.getPhoto()));
                } else {
                    profile.setImageResource(R.drawable.ic_profile_photo);
                }
            }
        }
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (!getIntent().getBooleanExtra("isFromMain", false)) {
            overridePendingTransition(0, 0);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}