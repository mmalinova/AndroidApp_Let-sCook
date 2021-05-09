package com.example.letscook.view.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.letscook.R;
import com.example.letscook.database.RoomDB;
import com.example.letscook.database.user.User;
import com.example.letscook.database.user.UserDao;
import com.example.letscook.view.home.MainActivity;
import com.example.letscook.view.register.SignUpActivity;

import java.util.List;

import static com.example.letscook.constants.Messages.EMAIL_NOT_EXIST;
import static com.example.letscook.constants.Messages.EMAIL_REQ;
import static com.example.letscook.constants.Messages.LOGIN;
import static com.example.letscook.constants.Messages.PASS_REQ;
import static com.example.letscook.constants.Messages.WRONG_PASS;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Check if the user is remembered
        boolean isChecked = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getBoolean("remember", false);
        if (isChecked) {
            if (!getIntent().getBooleanExtra("isFromSignUp", false)) {
                startActivity(new Intent(this, MainActivity.class));
            }
        }

        findViewById(R.id.forgotten_pass).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                intent.putExtra("forgottenPassword", 1);
                startActivity(intent);
            }
        });

        CheckBox checked = findViewById(R.id.checkBox);
        checked.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isChecked()) {
                    SharedPreferences sharedPreferences = getSharedPreferences("PREFERENCE", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("remember", true);
                    editor.apply();
                } else {
                    SharedPreferences sharedPreferences = getSharedPreferences("PREFERENCE", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("remember", false);
                    editor.apply();
                }
            }
        });
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
        // Initialize db
        RoomDB database = RoomDB.getInstance(this);
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
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                }
            }
        }).start();
    }
}