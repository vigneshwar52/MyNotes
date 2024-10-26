package com.example.myapplication;

import android.os.Bundle;
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

public class UserRegistration extends AppCompatActivity {
    EditText emailEditText,passwordEditText,confirmPasswordEditText;
    Button createAccountBtn;
    ProgressBar progressBar;
    TextView loginBtnTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_registration);

        emailEditText = findViewById(R.id.email_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);
        confirmPasswordEditText = findViewById(R.id.confirm_password_edit_text);
        createAccountBtn = findViewById(R.id.create_account_btn);
        progressBar = findViewById(R.id.progress_bar);
        loginBtnTextView = findViewById(R.id.login_text_view_btn);

        createAccountBtn.setOnClickListener(v-> createUserAccountInFirebase());
        loginBtnTextView.setOnClickListener(v-> finish());
    }

    private void createUserAccountInFirebase() {
        String email  = emailEditText.getText().toString();
        String pwd  = passwordEditText.getText().toString();
        String confirmPwd  = confirmPasswordEditText.getText().toString();

        if(pwd.equals(confirmPwd)) {
            boolean isValid = Utils.validateUserData(emailEditText, passwordEditText);
            if (!isValid) {
                return;
            }
        }else {
            confirmPasswordEditText.setError("Password not matched");
            return;
        }
        createUserInFirebase(email,pwd);
    }

    private void createUserInFirebase(String email, String pwd) {
        Utils.progressBarStatus(progressBar,createAccountBtn,true);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        firebaseAuth.createUserWithEmailAndPassword(email,pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Utils.progressBarStatus(progressBar,createAccountBtn,false);
                if(task.isSuccessful()){
                    Toast.makeText(UserRegistration.this,"Successfully create account,Check email to verify",Toast.LENGTH_SHORT).show();
                    firebaseAuth.getCurrentUser().sendEmailVerification();
                    firebaseAuth.signOut();
                    finish();
                }else{
                    Toast.makeText(UserRegistration.this, Objects.requireNonNull(task.getException()).getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}