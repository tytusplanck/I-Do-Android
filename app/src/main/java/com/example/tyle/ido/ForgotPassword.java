package com.example.tyle.ido;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ForgotPassword extends AppCompatActivity implements View.OnClickListener{
    private EditText registeredEmail;
    private TextView cancel, submit;
    private FirebaseAuth auth;
    private ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        

        registeredEmail = findViewById(R.id.registered_email);
        cancel = findViewById(R.id.cancel_button);
        submit = findViewById(R.id.submit_button);
        auth = FirebaseAuth.getInstance();
        progress = findViewById(R.id.forgot_password_progress);

        // Set Listeners
        cancel.setOnClickListener(this);
        submit.setOnClickListener(this);
    }

    private void submitEmailForPassword() {
        String email = registeredEmail.getText().toString();

        if(TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please enter the email associated with your account", Toast.LENGTH_LONG).show();
            return;
        }

        //Email Validation pattern
        String regEx = "[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+.[A-Za-z]{2,4}";
        Pattern pattern = Pattern.compile(regEx, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);

        if(!matcher.matches()) {
            Toast.makeText(this, "You must enter a valid email address!", Toast.LENGTH_LONG).show();
            return;
        }
        progress.setVisibility(View.VISIBLE);

        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ForgotPassword.this, "We have sent you instructions to reset your password!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ForgotPassword.this, "Failed to send reset email!", Toast.LENGTH_SHORT).show();
                        }

                        progress.setVisibility(View.GONE);
                    }
                });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.cancel_button:
                startActivity(new Intent(ForgotPassword.this, LoginActivity.class));
                finish();
                break;
            case R.id.submit_button:
                submitEmailForPassword();
                break;
        }
    }
}
