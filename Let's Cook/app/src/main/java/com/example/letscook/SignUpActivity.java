package com.example.letscook;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import static com.example.constants.Messages.EXTRA_MESSAGE;

public class SignUpActivity extends AppCompatActivity {

    private TextView textView, login;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private Button okButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Make the activity on full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_sign_up);
        textView = findViewById(R.id.terms_textView);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                termsDialog();
                textView.setLinkTextColor(Color.parseColor("#FFFFFF"));
            }
        });
        login = findViewById(R.id.haveAccount_textView);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                login.setLinkTextColor(Color.parseColor("#FFFFFF"));
            }
        });
    }

    public void userRegistration(View view) {
        // Get the information
        Intent intent = new Intent(this, MainActivity.class);
        EditText editTextName = findViewById(R.id.username_editText);
        String name = editTextName.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, name);

        EditText editTextEmail = findViewById(R.id.email_editText);
        String email = editTextEmail.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, email);

        EditText editTextPass = findViewById(R.id.password_editText);
        String password = editTextPass.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, password);

        EditText editTextRePass = findViewById(R.id.rePass_editText);
        String rePass = editTextRePass.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, rePass);
        startActivity(intent);
    }

    public void termsDialog() {
        dialogBuilder = new AlertDialog.Builder(this);
        final View popupView = getLayoutInflater().inflate(R.layout.register_popup, null);

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