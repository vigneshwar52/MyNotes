package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.Utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;


public class LoginActivity extends AppCompatActivity {
    EditText emailEditText,pwdEditText;
    Button loginBtn;
    ProgressBar progressBar;
    TextView createAccountBtnTextView,connectToRoom;
    SharedPreferences sharedPreferences;SharedPreferences.Editor editor;
    String local = "local";
    String remote = "remote";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        getViews();
        sharedPreferences = getSharedPreferences("user_prefs",MODE_PRIVATE);
        editor = sharedPreferences.edit();

        loginBtn.setOnClickListener(v -> {
            proceedToLogin();
        });
        createAccountBtnTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putString("user_type",remote);
                startActivity(new Intent(LoginActivity.this,UserRegistration.class));
            }
        });
        connectToRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putString("user_type",local);
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
            }
        });
    }

    private void getViews() {
        emailEditText = findViewById(R.id.email_edit_text);
        pwdEditText = findViewById(R.id.password_edit_text);
        loginBtn = findViewById(R.id.login_btn);
        progressBar = findViewById(R.id.progress_bar);
        createAccountBtnTextView = findViewById(R.id.create_account_text_view_btn);
        connectToRoom = findViewById(R.id.connectToRoom);
    }

    private void proceedToLogin() {
        String email = emailEditText.getText().toString();
        String pwd = pwdEditText.getText().toString();

        boolean isValidated = Utils.validateUserData(emailEditText,pwdEditText);
        if(!isValidated){
            return;
        }
        loginAccountInFirebase(email,pwd);
    }

    private void loginAccountInFirebase(String email, String pwd) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        Utils.progressBarStatus(progressBar,loginBtn,true);

        firebaseAuth.signInWithEmailAndPassword(email,pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Utils.progressBarStatus(progressBar,loginBtn,false);
                if(task.isSuccessful()){
                    if(Objects.requireNonNull(firebaseAuth.getCurrentUser()).isEmailVerified()){
                        startActivity(new Intent(LoginActivity.this,MainActivity.class));
                        finish();
                    }else{
                        String errorMessage = task.getException() != null ? task.getException().getLocalizedMessage() : "Email is not verified or an unknown error occurred.";
                        Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                }else{
                    String errorMessage = task.getException() != null ? task.getException().getLocalizedMessage() : "Email is not verified or an unknown error occurred.";
                    Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}