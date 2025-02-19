package com.example.donotredeem.Classes;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.donotredeem.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Register extends AppCompatActivity {

    TextView editTextEmail, editTextPassword, editTextPhoneNumber;
    Button buttonReg;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        editTextUsername = findViewById(R.id.sign_up_username_text);
        editTextEmail = findViewById(R.id.sign_up_email_text);
        editTextPassword = findViewById(R.id.sign_up_password_text);
        editTextPhoneNumber = findViewById(R.id.sign_up_phone_number_text);
        buttonReg = findViewById(R.id.sign_up_username_button);

        buttonReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 String username, email, password;
                 int phoneNumber = 0;
                 username =  String.valueOf(editTextUsername.getText().toString());
                 email = String.valueOf(editTextEmail.getText().toString());
                 password = String.valueOf(editTextPassword.getText().toString());
                 phoneNumber = Integer.valueOf(editTextPhoneNumber.getText().toString());

                if (TextUtils.isEmpty(username)){
                    Toast.makeText(Register.this, "Enter username", Toast.LENGTH_SHORT).show();
                    return;
                }
                 if (TextUtils.isEmpty(email)){
                     Toast.makeText(Register.this, "Enter email", Toast.LENGTH_SHORT).show();
                     return;
                 }
                if (TextUtils.isEmpty(password)){
                    Toast.makeText(Register.this, "Enter password", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (phoneNumber == 0){
                    Toast.makeText(Register.this, "Enter phone number", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "createUserWithEmail:success");
                                    val user = mAuth.currentUser
                                    updateUI(user);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "createUserWithEmail:failure", task.exception);
                                    Toast.makeText(
                                            baseContext,
                                            "Authentication failed.",
                                            Toast.LENGTH_SHORT,
                                            ).show();
                                    updateUI(null);
                            }
                        }

                    }
                
            }
        });
    }
}