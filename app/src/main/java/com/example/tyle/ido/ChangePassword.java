package com.example.tyle.ido;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePassword extends AppCompatActivity implements View.OnClickListener {

    private EditText oldPassword, newPassword, confirmNewPassword;
    private Button update, cancel;

    private FirebaseAuth mAuth;
    private FirebaseUser current;

    private String username, email, userid;
    private Encryption encrypter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        mAuth = FirebaseAuth.getInstance();
        encrypter = new Encryption();

        current = mAuth.getCurrentUser();

        Bundle extras = getIntent().getExtras();
        userid = encrypter.decryptText(extras.getByteArray("id"));
        username = encrypter.decryptText(extras.getByteArray("username"));
        email = encrypter.decryptText(extras.getByteArray("email"));

        oldPassword = findViewById(R.id.old_password);
        newPassword = findViewById(R.id.new_password);
        confirmNewPassword = findViewById(R.id.confirm_new_password);
        update = findViewById(R.id.update_button);
        cancel = findViewById(R.id.cancel_button);

        update.setOnClickListener(this);
        cancel.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel_button:
                cancelUpdate();
                break;
            case R.id.update_button:
                changePassword();
                break;
        }
    }


    private void cancelUpdate() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("username", username);
        intent.putExtra("id", userid);
        intent.putExtra("email", email);
        startActivity(intent);
    }


    private void changePassword() {

        oldPassword.setError(null);
        newPassword.setError(null);
        confirmNewPassword.setError(null);

        boolean cancel = false;
        View focusView = null;


        String old_pass = oldPassword.getText().toString();
        final String new_pass = newPassword.getText().toString();
        String confirm_pass = confirmNewPassword.getText().toString();

        if (TextUtils.isEmpty(old_pass)) {
            focusView = oldPassword;
            oldPassword.setError("Please enter your current password");
            cancel = true;
        }
        if (TextUtils.isEmpty(new_pass)) {
            focusView = newPassword;
            newPassword.setError("Please enter a new Password");
            cancel = true;
        }
        if (TextUtils.isEmpty(confirm_pass)) {
            focusView = confirmNewPassword;
            confirmNewPassword.setError("Please confirm your new password");
            cancel = true;
        }

        if (old_pass.equals(new_pass)) {
            focusView = newPassword;
            newPassword.setError("Your new password cannot be the same as the old password!");
            cancel = true;
        }

        if (!new_pass.equals(confirm_pass)) {
            focusView = confirmNewPassword;
            confirmNewPassword.setError("Passwords do not match!");
            cancel = true;
        }

        boolean hasUppercase = !new_pass.equals(new_pass.toLowerCase());
        boolean hasLowercase = !new_pass.equals(new_pass.toUpperCase());

        if (new_pass.length() < 8) {
            newPassword.setError("Password must be at least 8 characters long");
            focusView = newPassword;
            cancel = true;
        } else if (!hasUppercase || !hasLowercase) {
            newPassword.setError("Password must be have at least one uppercase and one lowercase letter");
            focusView = newPassword;
            cancel = true;
        } else if (!new_pass.matches(".*\\d.*")) {
            newPassword.setError("Password must contain at least one number");
            focusView = newPassword;
            cancel = true;
        } else if (!new_pass.matches(".*[!@#$%^&*].*")) {
            newPassword.setError("Password must contain a special character");
            focusView = newPassword;
            cancel = true;
        } else if (new_pass.contains("AND") || new_pass.contains("NOT")) {
            newPassword.setError("Password must be not contain keywords AND/NOT");
            focusView = newPassword;
            cancel = true;
        }


        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Update the password for the current user
            AuthCredential credential = EmailAuthProvider.getCredential(email, old_pass);

            current.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        current.updatePassword(new_pass).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (!task.isSuccessful()) {
                                    Toast.makeText(ChangePassword.this, "Password Update Failed",
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(ChangePassword.this, "Password Successfully Changed",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        Toast.makeText(ChangePassword.this, "Unable to Authenticate User",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }
}
