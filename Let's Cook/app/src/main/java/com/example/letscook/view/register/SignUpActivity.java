package com.example.letscook.view.register;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.example.letscook.R;
import com.example.letscook.constants.Messages;
import com.example.letscook.database.RoomDB;
import com.example.letscook.database.user.User;
import com.example.letscook.database.user.UserDao;
import com.example.letscook.view.home.MainActivity;
import com.example.letscook.view.login.LoginActivity;

import java.util.List;

import static com.example.letscook.constants.Messages.EMAIL_ALREADY_EXIST;
import static com.example.letscook.constants.Messages.EMAIL_NOT_EXIST;
import static com.example.letscook.constants.Messages.EMAIL_REQ;
import static com.example.letscook.constants.Messages.EQUAL_PASS_REQ;
import static com.example.letscook.constants.Messages.PASS_REQ;
import static com.example.letscook.constants.Messages.REGISTER;
import static com.example.letscook.constants.Messages.REPEAT_PASS_REQ;
import static com.example.letscook.constants.Messages.UPDATE;
import static com.example.letscook.constants.Messages.USERNAME_LENGTH;

public class SignUpActivity extends AppCompatActivity {
    private TextView termsTextView, policyTextView, login, allFieldsReq, mess;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private Button okButton;
    private EditText editTextName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Make the activity on full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_sign_up);

        editTextName = findViewById(R.id.username_editText);
        allFieldsReq = findViewById(R.id.allReq_textView);
        mess = findViewById(R.id.textView);

        if (getIntent().getIntExtra("forgottenPassword", 0) == 1) {
            TextView title = findViewById(R.id.register_textView);
            title.setText(Messages.FORGOTTEN_TITLE);
            title.setTextSize(26);
            Button btn = findViewById(R.id.register_btn);
            btn.setText(UPDATE);
            editTextName.setVisibility(View.INVISIBLE);
            allFieldsReq.setVisibility(View.INVISIBLE);
            mess.setVisibility(View.VISIBLE);
        } else {
            // Check if the user is remembered
            boolean isChecked = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                    .getBoolean("remember", false);
            if (isChecked) {
                startActivity(new Intent(this, MainActivity.class));
            }
        }

        termsTextView = findViewById(R.id.terms_textView);
        termsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                termsDialog();
                termsTextView.setLinkTextColor(Color.parseColor("#FFFFFF"));
            }
        });
        policyTextView = findViewById(R.id.data_policy);
        policyTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                policyDialog();
                policyTextView.setLinkTextColor(Color.parseColor("#FFFFFF"));
            }
        });
        login = findViewById(R.id.haveAccount_textView);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.putExtra("isFromSignUp", true);
                startActivity(intent);
                Animatoo.animateSlideRight(SignUpActivity.this);
                login.setLinkTextColor(Color.parseColor("#FFFFFF"));
            }
        });
        CheckBox checked = findViewById(R.id.reg_checkBox);
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

    public void userRegistration(View view) {
        User userToReg = new User();
        // Get the information
        TextView required = findViewById(R.id.reqTextView);
        required.setVisibility(View.INVISIBLE);
        allFieldsReq = findViewById(R.id.allReq_textView);

        editTextName = findViewById(R.id.username_editText);
        String name = editTextName.getText().toString().trim();
        if (getIntent().getIntExtra("forgottenPassword", 0) == 0) {
            if (name.equals("")) {
                allFieldsReq.setVisibility(View.INVISIBLE);
                required.setVisibility(View.VISIBLE);
                return;
            } else if (name.length() < 2) {
                allFieldsReq.setVisibility(View.INVISIBLE);
                required.setText(USERNAME_LENGTH);
                required.setVisibility(View.VISIBLE);
                return;
            }
        }

        EditText editTextEmail = findViewById(R.id.email_editText);
        String email = editTextEmail.getText().toString().trim();
        if (getIntent().getIntExtra("forgottenPassword", 0) == 0) {
            if (email.equals("") || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                allFieldsReq.setVisibility(View.INVISIBLE);
                mess.setVisibility(View.INVISIBLE);
                required.setText(EMAIL_REQ);
                required.setVisibility(View.VISIBLE);
                return;
            }
        } else {
            if (email.equals("") || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                allFieldsReq.setVisibility(View.INVISIBLE);
                mess.setText(EMAIL_REQ);
                mess.setVisibility(View.VISIBLE);
                return;
            }
        }

        EditText editTextPass = findViewById(R.id.password_editText);
        String password = editTextPass.getText().toString().trim();
        if (getIntent().getIntExtra("forgottenPassword", 0) == 0) {
            if (password.length() < 3) {
                allFieldsReq.setVisibility(View.INVISIBLE);
                mess.setVisibility(View.INVISIBLE);
                required.setText(PASS_REQ);
                required.setVisibility(View.VISIBLE);
                return;
            }
        } else {
            if (password.length() < 3) {
                allFieldsReq.setVisibility(View.INVISIBLE);
                mess.setText(PASS_REQ);
                mess.setVisibility(View.VISIBLE);
                return;
            }
        }

        EditText editTextRePass = findViewById(R.id.rePass_editText);
        String rePass = editTextRePass.getText().toString().trim();
        if (getIntent().getIntExtra("forgottenPassword", 0) == 0) {
            if (rePass.equals("")) {
                allFieldsReq.setVisibility(View.INVISIBLE);
                mess.setVisibility(View.INVISIBLE);
                required.setText(REPEAT_PASS_REQ);
                required.setVisibility(View.VISIBLE);
                return;
            } else if (!password.equals(rePass)) {
                allFieldsReq.setVisibility(View.INVISIBLE);
                mess.setVisibility(View.INVISIBLE);
                required.setText(EQUAL_PASS_REQ);
                required.setVisibility(View.VISIBLE);
                return;
            }
        } else {
            if (rePass.equals("")) {
                allFieldsReq.setVisibility(View.INVISIBLE);
                mess.setText(REPEAT_PASS_REQ);
                mess.setVisibility(View.VISIBLE);
                return;
            } else if (!password.equals(rePass)) {
                allFieldsReq.setVisibility(View.INVISIBLE);
                mess.setText(EQUAL_PASS_REQ);
                mess.setVisibility(View.VISIBLE);
                return;
            }
        }
        userToReg.setName(name);
        userToReg.setEmail(email);
        userToReg.setPassword(password);
        // Initialize db
        RoomDB database = RoomDB.getInstance(this);
        final UserDao userDao = database.userDao();
        if (getIntent().getIntExtra("forgottenPassword", 0) == 1) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    User user = userDao.getUserByEmail(email);
                    if (user == null) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mess.setText(EMAIL_NOT_EXIST);
                                mess.setVisibility(View.VISIBLE);
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                allFieldsReq.setVisibility(View.INVISIBLE);
                                mess.setVisibility(View.INVISIBLE);
                                required.setText(REGISTER);
                                required.setVisibility(View.VISIBLE);
                            }
                        });
                        userDao.updatePass(user.getID(), password);
                        startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                        SharedPreferences sharedPreferences = getSharedPreferences("PREFERENCE", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("email", email);
                        editor.apply();
                    }
                }
            }).start();
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    User user = userDao.getUserByEmail(email);
                    if (user == null) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                allFieldsReq.setVisibility(View.INVISIBLE);
                                mess.setVisibility(View.INVISIBLE);
                                required.setText(REGISTER);
                                required.setVisibility(View.VISIBLE);
                            }
                        });
                        userDao.register(userToReg);
                        startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                        SharedPreferences sharedPreferences = getSharedPreferences("PREFERENCE", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("email", email);
                        editor.apply();
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                required.setText(EMAIL_ALREADY_EXIST);
                                required.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                }
            }).start();
        }
    }

    public void termsDialog() {
        dialogBuilder = new AlertDialog.Builder(this);
        final View popupView = getLayoutInflater().inflate(R.layout.terms_popup, null);

        okButton = popupView.findViewById(R.id.register_okBtn);

        dialogBuilder.setView(popupView);
        dialog = dialogBuilder.create();
        dialog.show();

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close the dialog
                dialog.dismiss();
            }
        });
    }

    public void policyDialog() {
        dialogBuilder = new AlertDialog.Builder(this);
        final View popupView = getLayoutInflater().inflate(R.layout.policy_popup, null);

        okButton = popupView.findViewById(R.id.register_okBtn);

        dialogBuilder.setView(popupView);
        dialog = dialogBuilder.create();
        dialog.show();

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close the dialog
                dialog.dismiss();
            }
        });
    }
}