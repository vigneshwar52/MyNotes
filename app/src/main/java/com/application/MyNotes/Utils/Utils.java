package com.application.MyNotes.Utils;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    public static Boolean validateUserData(EditText emailEditText, EditText pwdEditText) {
        String email = emailEditText.getText().toString().trim();
        String pwd = pwdEditText.getText().toString().trim();

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Invalid Email Address");
            return false;
        }

        String regexPattern = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#\\$%^&*(),]).{8,}$";
        Pattern pattern = Pattern.compile(regexPattern);
        Matcher matcher = pattern.matcher(pwd);
        if (!matcher.matches()) {
            pwdEditText.setError("Password must be at least 8 characters long, with one uppercase, one lowercase, one number, and one special character");
            return false;
        }

        return true;
    }
    public static void progressBarStatus(ProgressBar progressBar, Button loginBtn, Boolean status) {
        if(status){
            progressBar.setVisibility(View.VISIBLE);
            loginBtn.setVisibility(View.GONE);
        }else{
            progressBar.setVisibility(View.GONE);
            loginBtn.setVisibility(View.VISIBLE);
        }
    }
}