package com.rakesh.triptracker.ui.login;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProviders;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.rakesh.triptracker.Constants;
import com.rakesh.triptracker.MainActivity;
import com.rakesh.triptracker.R;
import com.rakesh.triptracker.RegisterActivity;
import com.rakesh.triptracker.data.model.User;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "LoginActivity";

    private FirebaseFirestore db;

    @BindView(R.id.email)
    EditText emailText;
    @BindView(R.id.password)
    EditText passwordText;

    @BindView(R.id.login)
    Button loginBtn;
    @BindView(R.id.register)
    Button registerBtn;

    @BindView(R.id.loading)
    ProgressBar progressBar;

    ProgressDialog progressDialog;

    // For firebase Auth
    private FirebaseAuth mAuth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        progressDialog = new ProgressDialog(this);

        final EditText usernameEditText = findViewById(R.id.email);
        final EditText passwordEditText = findViewById(R.id.password);
        final Button loginButton = findViewById(R.id.login);
        final ProgressBar loadingProgressBar = findViewById(R.id.loading);

        // Initialize firebase auth
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
//                loadingProgressBar.setVisibility(View.VISIBLE);
                if ((isFieldEmpty(emailText)) && isFieldEmpty(passwordEditText)) {
                    if ((isEmailValid(emailText))) {
                        userLogin(emailText.getText().toString().trim(), passwordEditText.getText().toString().trim());
                    }
                }

            }
        });
    }

    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle(R.string.app_name);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setMessage("Exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();

    }

    private void userLogin(String email, String password) {
        progressDialog.setMessage("Logging In...");
        progressDialog.show();

        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
//        updateUI(currentUser);

        // login using firebase Auth
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    String currentUid = mAuth.getCurrentUser().getUid();
                    String deviceToken = FirebaseInstanceId.getInstance().getToken();

                    Map<String, Object> user = new HashMap<>();
                    user.put("email", email);
                    user.put("password", password);


                    db.collection(Constants.USER_COLLECTION)
                            .whereEqualTo(currentUid, "device_token")
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        User userInfo = null;
                                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                            Log.d(TAG, "Doc: " + documentSnapshot.getId() + " => " + documentSnapshot.getData());

                                            userInfo = new User(documentSnapshot.get("fullName").toString(),
                                                    documentSnapshot.get("email").toString(),
                                                    documentSnapshot.get("password").toString());
                                        }
                                        //login success
                                        progressDialog.dismiss();
                                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                        finish();
                                    } else {
                                        progressDialog.dismiss();
                                        Toast.makeText(LoginActivity.this, "Invalid Email or Password", Toast.LENGTH_SHORT).show();
                                        Log.d(TAG, "Error getting documents: ", task.getException());
                                    }
                                }
                            });
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(LoginActivity.this, "Invalid Email or Password", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Error getting documents: ", task.getException());

                }

            }
        });
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }

    public void RegisterActivity(View v) {
        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
    }

    @Override
    public void onClick(View v) {
        if (v == registerBtn) {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        }
    }

    public Boolean isFieldEmpty(EditText view) {
        if (view.getText().toString().length() > 0) {
            return true;
        } else {
            view.setError("Field Required");
            return false;
        }
    }

    public Boolean isEmailValid(EditText view) {
        String value = view.getText().toString();
        if (Patterns.EMAIL_ADDRESS.matcher(value).matches()) {
            return true;
        } else {
            view.setError("Invalid email");
            return false;
        }
    }

    public void firebaseLogin(String email, String password) {
        // login using firebase Auth
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                String currentUid = mAuth.getCurrentUser().getUid();
                String deviceToken = FirebaseInstanceId.getInstance().getToken();

                Map<String, Object> user = new HashMap<>();
                user.put("email", email);
                user.put("password", password);


                db.collection(Constants.USER_COLLECTION)
                        .whereEqualTo(email, "email")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    User userInfo = null;
                                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                        Log.d(TAG, documentSnapshot.getId() + " => " + documentSnapshot.getData());

                                        userInfo = new User(documentSnapshot.get("fullName").toString(),
                                                documentSnapshot.get("email").toString(),
                                                documentSnapshot.get("password").toString());
                                    }
                                    //login success

                                } else {
                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                }
                            }
                        });


            }
        });
    }

}
