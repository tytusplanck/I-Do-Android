package com.example.tyle.ido;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class Registration extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "Registration";
    //defining view objects
    private ProgressDialog progressDialog;
    private EditText emailField, passwordField, confirmPassword, fullName;
    private FirebaseAuth.AuthStateListener authchange;
    private Boolean mAllowNavigation = true;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        // Set up the current page
        auth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        emailField = findViewById(R.id.userEmailId);
        passwordField = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirm_password);
        fullName = findViewById(R.id.fullName);
        Button register = findViewById(R.id.register_button);
        TextView login = findViewById(R.id.sign_in_here);



        authchange = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:user signed in");
                    if (mAllowNavigation) {
                        mAllowNavigation = false;
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        SharedPreferences settings = getSharedPreferences("UserInfo", 0);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString("name", user.getDisplayName());
                        editor.putString("email", user.getEmail());
                        editor.putString("userid", user.getUid());
                        editor.commit();
                        finish();
                    }
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }};

        // Set onClickListeners
        register.setOnClickListener(this);
        login.setOnClickListener(this);
    }


    // Add onStart and onStop methods to start and stop the authlistener
    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "SignUp Activity has started.");
        auth.addAuthStateListener(authchange);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authchange != null) {
            auth.removeAuthStateListener(authchange);
        }
    }


    //Attempt to register the user after the create account button is click
    private void registerUser() {

        // Reset errors.
        emailField.setError(null);
        passwordField.setError(null);
        confirmPassword.setError(null);
        fullName.setError(null);

        // Store values at the time of the login attempt.
        String email = emailField.getText().toString();
        String password = passwordField.getText().toString();
        String confirm_password =confirmPassword.getText().toString();
        String name = fullName.getText().toString();

        boolean cancel = false;
        View focusView = null;


        if (TextUtils.isEmpty(name)) {
            fullName.setError(getString(R.string.error_field_required));
            focusView = fullName;
            cancel = true;
        }

        //TODO: Create a password strength checking algorithm to test initial passwords
        // Check for matching passwords, if the user entered one.
        if (!TextUtils.isEmpty(password) ) {
            passwordField.setError(getString(R.string.no_password));
            focusView = passwordField;
            cancel = true;
        } else if (!confirm_password.equals(password)) {
            passwordField.setError(getString(R.string.password_confirmation_error));
        }

        // Check for a valid email address.

        //Email Validation pattern
        String regEx = "[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+.[A-Za-z]{2,4}";
        Pattern pattern = Pattern.compile(regEx, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        if (TextUtils.isEmpty(email)) {
            emailField.setError(getString(R.string.no_email));
            focusView = emailField;
            cancel = true;
        } else if (!matcher.matches()) {
            emailField.setError(getString(R.string.error_invalid_email));
            focusView = emailField;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            progressDialog.setMessage("Registering...");
            progressDialog.show();
            auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            if (!task.isSuccessful()) {
                                Toast.makeText(Registration.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(Registration.this, "Successfully Registered!", Toast.LENGTH_SHORT).show();


                            }
                            progressDialog.dismiss();
                        }
                    });
        }
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
        switch (v.getId()) {
            case R.id.sign_in_here:
                existingUserLogin(v);
                break;
            case R.id.register_button:
                if(hasNetworkConnection()) {
                    registerUser();
                } else {
                    showDialog();
                }
                break;
        }
    }

    /**
     * Activity for signing in an existing user
     */
    public void existingUserLogin(View v) {
        startActivity(new Intent(Registration.this, LoginActivity.class));
        finish();
    }

    /**
     * Determines if the current device has a network connection
     * @return - true or false, if the device is connected to a network
     */
    private boolean hasNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    /**
     * Show an alert dialog to take the user to the Network settings screen or quit the app
     */
    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("You do not have a network connection.  Connect? ")
                .setPositiveButton("Connection Settings", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
                        Registration.this.finish();
                    }
                })
                .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Registration.this.finish();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}

