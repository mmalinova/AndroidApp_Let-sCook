package com.example.letscook.view.profile;

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
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.example.letscook.view.AddRecipeActivity;
import com.example.letscook.R;
import com.example.letscook.database.RoomDB;
import com.example.letscook.database.typeconverters.DataConverter;
import com.example.letscook.database.user.User;
import com.example.letscook.database.user.UserDao;
import com.example.letscook.view.search.SearchActivity;
import com.example.letscook.view.products.ShoppingListActivity;
import com.example.letscook.view.search.WhatToCookActivity;
import com.example.letscook.view.home.MainActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.letscook.constants.Messages.*;

public class ProfileActivity extends AppCompatActivity {
    private CircleImageView userPhoto, uploadPhoto;
    private Bitmap bmpImage = null, selectedImage = null;
    private final int CAMERA_INTENT = 1;
    private final int SELECT_PHOTO = 2;
    private RoomDB database;
    private User user;
    private AlertDialog dialog = null;
    private Uri imageUri;

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
        uploadPhoto = findViewById(R.id.photo);
        uploadPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if device has a camera
                PackageManager pm = ProfileActivity.this.getPackageManager();
                if (pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
                    if (ContextCompat.checkSelfPermission(ProfileActivity.this, Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(ProfileActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(ProfileActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE}, CAMERA_INTENT);
                    }  else {
                        uploadPhoto.setColorFilter(Color.parseColor("#000000"));
                        uploadPictureDialog();
                    }
                } else {
                    if (ContextCompat.checkSelfPermission(ProfileActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(ProfileActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, CAMERA_INTENT);
                    }  else {
                        uploadPhoto.setColorFilter(Color.parseColor("#000000"));
                        uploadFromGallery();
                    }
                }
            }
        });
        userPhoto = findViewById(R.id.user_photo);

        // Initialize and assign variable
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);

        // Set home selected
        bottomNavigationView.setSelectedItemId(R.id.home);

        // Perform item selected list
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent = null;
                switch (item.getItemId()) {
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
        database = RoomDB.getInstance(this);
        // Initialize action text
        TextView greetingText = findViewById(R.id.bar_text);
        // Get data
        String userEmail = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("email", null);
        user = database.userDao().getUserByEmail(userEmail);
        // Set user photo
        if (user.getPhoto() != null) {
            userPhoto.setImageBitmap(DataConverter.byteArrayToImage(user.getPhoto()));
        }
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

    public void uploadPictureDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        final View popupView = getLayoutInflater().inflate(R.layout.upload_image_dialog, null);

        TextView chooseFromGallery = popupView.findViewById(R.id.gallery);
        TextView takeAPhoto = popupView.findViewById(R.id.takeAPhoto);
        TextView removePhoto = popupView.findViewById(R.id.remove);

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
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, CAMERA_INTENT);
                }
                dialog.dismiss();
            }
        });
        removePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userPhoto.setImageResource(R.drawable.ic_profile_photo);
                final UserDao userDao = database.userDao();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        userDao.removePhoto(user.getID());
                    }
                }).start();
                dialog.dismiss();
            }
        });
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                uploadPhoto.setColorFilter(Color.parseColor("#d2a57f"));
            }
        });
    }

    public void uploadWithCamera() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        final View popupView = getLayoutInflater().inflate(R.layout.upload_with_camera, null);

        TextView takeAPhoto = popupView.findViewById(R.id.takeAPhoto);
        TextView removePhoto = popupView.findViewById(R.id.remove);

        dialogBuilder.setView(popupView);
        dialog = dialogBuilder.create();
        dialog.show();

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
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, CAMERA_INTENT);
                }
                dialog.dismiss();
            }
        });
        removePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userPhoto.setImageResource(R.drawable.ic_profile_photo);
                final UserDao userDao = database.userDao();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        userDao.removePhoto(user.getID());
                    }
                }).start();
                dialog.dismiss();
            }
        });
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                uploadPhoto.setColorFilter(Color.parseColor("#d2a57f"));
            }
        });
    }

    public void uploadFromGallery() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        final View popupView = getLayoutInflater().inflate(R.layout.upload_from_gallery, null);

        TextView chooseFromGallery = popupView.findViewById(R.id.gallery);
        TextView removePhoto = popupView.findViewById(R.id.remove);

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
        removePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userPhoto.setImageResource(R.drawable.ic_profile_photo);
                final UserDao userDao = database.userDao();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        userDao.removePhoto(user.getID());
                    }
                }).start();
                dialog.dismiss();
            }
        });
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                uploadPhoto.setColorFilter(Color.parseColor("#d2a57f"));
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_INTENT) {
            if (resultCode == Activity.RESULT_OK) {
                try {
                    bmpImage = MediaStore.Images.Media.getBitmap(
                            getContentResolver(), imageUri);
                    bmpImage = getResizedBitmap(bmpImage, 900, 1000);
                    userPhoto.setImageBitmap(bmpImage);
                    final UserDao userDao = database.userDao();
                    userDao.setPhoto(user.getID(), DataConverter.imageToByteArray(bmpImage));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if (requestCode == SELECT_PHOTO) {
            if (resultCode == Activity.RESULT_OK) {
                try {
                    assert data != null;
                    final Uri image = data.getData();
                    final InputStream stream = getContentResolver().openInputStream(image);
                    selectedImage = BitmapFactory.decodeStream(stream);
                    selectedImage = getResizedBitmap(selectedImage, 900, 1000);
                    userPhoto.setImageBitmap(selectedImage);
                    final UserDao userDao = database.userDao();
                    userDao.setPhoto(user.getID(), DataConverter.imageToByteArray(selectedImage));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                uploadPhoto.setColorFilter(Color.parseColor("#000000"));
                uploadPictureDialog();
            } else if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] != PackageManager.PERMISSION_GRANTED) {
                uploadPhoto.setColorFilter(Color.parseColor("#000000"));
                uploadWithCamera();
            } else if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                uploadPhoto.setColorFilter(Color.parseColor("#000000"));
                uploadFromGallery();
            }
        }
    }

    @Override
    protected void onStart() {
        // Set view according session storage
        String e = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("email", null);
        if (e == null) {
            userPhoto.setImageResource(R.drawable.ic_profile);
        } else {
            user = database.userDao().getUserByEmail(e);
            if (user.getPhoto() != null) {
                userPhoto.setImageBitmap(DataConverter.byteArrayToImage(user.getPhoto()));
            } else {
                userPhoto.setImageResource(R.drawable.ic_profile_photo);
            }
        }
        super.onStart();
    }

    @Override
    protected void onResume() {
        // Set view according session storage
        String e = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("email", null);
        if (e == null) {
            userPhoto.setImageResource(R.drawable.ic_profile);
        } else {
            user = database.userDao().getUserByEmail(e);
            if (user.getPhoto() != null) {
                userPhoto.setImageBitmap(DataConverter.byteArrayToImage(user.getPhoto()));
            } else {
                userPhoto.setImageResource(R.drawable.ic_profile_photo);
            }
        }
        super.onResume();
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
    public void onBackPressed() {
        super.onBackPressed();
        if (!getIntent().getBooleanExtra("isFromMain", false)) {
            Animatoo.animateSlideUp(ProfileActivity.this);
        }
    }
}