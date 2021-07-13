package com.example.letscook.view.register;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.example.letscook.R;
import com.example.letscook.constants.Messages;
import com.example.letscook.database.RoomDB;
import com.example.letscook.database.typeconverters.DataConverter;
import com.example.letscook.database.user.User;
import com.example.letscook.database.user.UserDao;
import com.example.letscook.view.home.MainActivity;
import com.example.letscook.view.login.LoginActivity;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import org.json.JSONException;
import org.json.JSONObject;

import static com.example.letscook.constants.Messages.AUTH_MESS;
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
    private TextView termsTextView, policyTextView, login, allFieldsReq, mess, required;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private Button okButton;
    private EditText editTextName;
    private CallbackManager callbackManager;
    private FirebaseAuth firebaseAuth;
    private AccessTokenTracker accessTokenTracker;
    private Button fbLogin, googleLogin;
    private GoogleSignInClient mGoogleSignInClient;
    private final static int RC_SIGN_IN = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
        FacebookSdk.sdkInitialize(getApplicationContext());
        //AppEventsLogger.activateApp(this);

        // Make the activity on full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_sign_up);

        createRequest();

        required = findViewById(R.id.reqTextView);
        required.setVisibility(View.INVISIBLE);
        editTextName = findViewById(R.id.username_editText);
        allFieldsReq = findViewById(R.id.allReq_textView);
        mess = findViewById(R.id.textView);

        googleLogin = findViewById(R.id.google_btn);
        googleLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
        fbLogin = findViewById(R.id.facebook_btn);
        callbackManager = CallbackManager.Factory.create();
        fbLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doFBLoginForProfile();
            }
        });

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

    private void createRequest() {
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    protected void doFBLoginForProfile() {
        LoginManager loginManager = LoginManager.getInstance();
        loginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("FacebookAuth", "onSuccess" + loginResult);
                handleFacebookToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d("FacebookAuth", "onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d("FacebookAuth", "onError" + error);
                Toast.makeText(SignUpActivity.this, AUTH_MESS, Toast.LENGTH_SHORT).show();

            }
        });
        loginManager.logInWithReadPermissions(this, getReadPermissions());
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                if (currentAccessToken == null) {
                    firebaseAuth.signOut();
                }
            }
        };
    }

    private ArrayList<String> getReadPermissions() {
        ArrayList<String> fbPermissions = new ArrayList<String>();
        fbPermissions.add("public_profile");
        fbPermissions.add("email");
        return fbPermissions;
    }

    public void handleFacebookToken(AccessToken token) {
        Log.d("FacebookAuth", "handleToken" + token);
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        GraphRequest request = GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {
                        try {
                            String id = object.getString("id");
                            String first_name = object.getString("first_name");
                            Bitmap image = DataConverter.getBitmapFromURL("https://graph.facebook.com/" + id + "/picture?type=large");

                            String email = null;
                            if (object.has("email")) {
                                email = object.getString("email");
                            }

                            User userToReg = new User();
                            userToReg.setName(first_name);
                            userToReg.setEmail(email);
                            userToReg.setPassword(id);
                            if (image != null) {
                                userToReg.setPhoto(DataConverter.imageToByteArray(image));
                            }

                            // Initialize db
                            RoomDB database = RoomDB.getInstance(getBaseContext());
                            final UserDao userDao = database.userDao();
                            if (getIntent().getIntExtra("forgottenPassword", 0) == 1) {
                                String finalEmail = email;
                                String finalEmail1 = email;
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        User user = userDao.getUserByEmail(finalEmail);
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
                                            userDao.updatePass(user.getID(), id);
                                            startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                                            SharedPreferences sharedPreferences = getSharedPreferences("PREFERENCE", MODE_PRIVATE);
                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                            editor.putString("email", finalEmail1);
                                            editor.apply();
                                        }
                                    }
                                }).start();
                            } else {
                                String finalEmail2 = email;
                                String finalEmail3 = email;
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        User user = userDao.getUserByEmail(finalEmail2);
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
                                            editor.putString("email", finalEmail3);
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
                                SharedPreferences sharedPreferences = getSharedPreferences("PREFERENCE", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putBoolean("remember", true);
                                editor.apply();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,first_name,last_name,email,gender,birthday"); // id,first_name,last_name,email,gender,birthday,cover,picture.type(large)
        request.setParameters(parameters);
        request.executeAsync();
    }

    public void userRegistration(View view) {
        User userToReg = new User();
        // Get the information
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        callbackManager.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                assert account != null;
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acc) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acc.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            reg(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.d("GoogleAuth", "Error");
                            Toast.makeText(SignUpActivity.this, AUTH_MESS, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void reg(FirebaseUser user) {
        User userToReg = new User();
        userToReg.setName(user.getDisplayName());
        userToReg.setEmail(user.getEmail());
        userToReg.setPassword(user.getUid());

        Bitmap image = DataConverter.getBitmapFromURL(String.valueOf(user.getPhotoUrl()));
        if (image != null) {
            userToReg.setPhoto(DataConverter.imageToByteArray(image));
        }

        // Initialize db
        RoomDB database = RoomDB.getInstance(getBaseContext());
        final UserDao userDao = database.userDao();
        if (getIntent().getIntExtra("forgottenPassword", 0) == 1) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    User user = userDao.getUserByEmail(userToReg.getEmail());
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
                        userDao.updatePass(user.getID(), userToReg.getPassword());
                        startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                        SharedPreferences sharedPreferences = getSharedPreferences("PREFERENCE", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("email", userToReg.getEmail());
                        editor.apply();
                    }
                }
            }).start();
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    User user = userDao.getUserByEmail(userToReg.getEmail());
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
                        editor.putString("email", userToReg.getEmail());
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
            SharedPreferences sharedPreferences = getSharedPreferences("PREFERENCE", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("remember", true);
            editor.apply();
        }
    }
}