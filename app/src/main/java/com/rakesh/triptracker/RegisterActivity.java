package com.rakesh.triptracker;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rakesh.triptracker.ui.login.LoginActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "RegisterActivity";

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private String fullName, email, password, confirmPassword;

//    @BindView(R.id.username)
//    EditText userNameText;
//    @BindView(R.id.email)
//    EditText emailText;
//    @BindView(R.id.password)
//    EditText passwordText;
//    @BindView(R.id.confirm_password)
//    EditText confirmText;
//    @BindView(R.id.login)
//    Button loginBtn;
//    @BindView(R.id.register)
//    Button registerBtn;
//
//    @BindView(R.id.loading)
//    ProgressBar progressBar;

    EditText userNameText, emailText, passwordText, confirmText;
    Button registerBtn, loginBtn;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
//        ButterKnife.bind(this);

        userNameText = findViewById(R.id.username);
        emailText = findViewById(R.id.email);
        passwordText = findViewById(R.id.password);
        confirmText = findViewById(R.id.confirm_password);
        registerBtn = findViewById(R.id.register);
        loginBtn = findViewById(R.id.login);

        // Firebase Work
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        progressDialog = new ProgressDialog(this);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFieldEmpty(userNameText) && isFieldEmpty(emailText) && isFieldEmpty(passwordText) &&
                        isFieldEmpty(confirmText) && isPaswordMatch(passwordText, confirmText)) {
                    if (isEmailValid(emailText)) {
                        registerUser();
                    }
                }
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == registerBtn) {
            Log.d(TAG, "register btn clicked");
            if (isFieldEmpty(userNameText) && isFieldEmpty(emailText) && isFieldEmpty(passwordText) &&
                    isFieldEmpty(confirmText) && isPaswordMatch(passwordText, confirmText)) {
                if (isEmailValid(emailText)) {
                    registerUser();
                }
            }
        }
        if (v == loginBtn) {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        }
    }

    public void registerUser() {

        fullName = userNameText.getText().toString().trim();
        email = emailText.getText().toString().trim();
        password = passwordText.getText().toString().trim();
        confirmPassword = confirmText.getText().toString().trim();

        Toast.makeText(this, "email " + email, Toast.LENGTH_SHORT).show();
        progressDialog.setMessage("Creating account, Please wait!");
        progressDialog.show();

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    String currentUserid = mAuth.getCurrentUser().getUid();

                    //add to db
                    Map<String, Object> user = new HashMap<>();
                    user.put("fullName", fullName);
                    user.put("email", email);
                    user.put("password", password);
                    user.put("device_token", currentUserid);

                    // register user to firebase
                    db.collection(Constants.USER_COLLECTION)
                            .add(user)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    progressDialog.dismiss();
                                    Toast.makeText(RegisterActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                    finish();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    Log.w(TAG, "Error adding document", e);
                                    Toast.makeText(RegisterActivity.this, "Error: " + e, Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    progressDialog.dismiss();
                    String error = task.getException().toString();
                    Toast.makeText(RegisterActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                }
            }
        });

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

    public Boolean isPaswordMatch(EditText text1, EditText text2) {
        String value1 = text1.getText().toString().trim();
        String value2 = text2.getText().toString().trim();

        if (value1.equals(value2)) {
            return true;
        } else {
            text2.setError("Password don't Match");
            return false;
        }
    }
}
