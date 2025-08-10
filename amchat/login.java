package com.example.amchat;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class login extends AppCompatActivity {
    TextView logsignup ;
    Button button;
    EditText email, password;
    FirebaseAuth auth;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    android.app.ProgressDialog progressDialog;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

            auth = FirebaseAuth.getInstance();
            button = findViewById(R.id.logbutton);
            email = findViewById(R.id.editTextLogEmail);
            password = findViewById(R.id.editTextLogPassword);
            logsignup = findViewById(R.id.logsignup);

            logsignup.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(login.this,Registration.class);
                    startActivity(intent);
                    finish();
                }
            });


            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String Email = email.getText().toString();
                    String pass = password.getText().toString();

                    if ((TextUtils.isEmpty(Email))) {
                        progressDialog.dismiss();
                        Toast.makeText(login.this, "Enter the email", Toast.LENGTH_SHORT).show();
                    } else if (TextUtils.isEmpty(pass)) {
                        progressDialog.dismiss();
                        Toast.makeText(login.this, "Enter the password", Toast.LENGTH_SHORT).show();
                    } else if (!Email.matches(emailPattern)) {
                        progressDialog.dismiss();
                        email.setError("Give proper Email Address");
                    } else if (password.length() < 6) {
                        progressDialog.dismiss();
                        password.setError("More than Six Characters");
                        Toast.makeText(login.this, "Password needs to be longer than six characters", Toast.LENGTH_SHORT).show();
                    } else {

                        auth.signInWithEmailAndPassword(Email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    progressDialog.show();
                                    try {
                                        Intent intent = new Intent(login.this, MainActivity.class);
                                        startActivity(intent);

                                    } catch (Exception e) {
                                        Toast.makeText(login.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(login.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            });
    }
}