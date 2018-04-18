package com.example.tyle.ido;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tyle.ido.dataObjects.ToDoList;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;


import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * All original code.
 *
 * Contributors: Kenton, Jenn, Kyle, Tytus
 *
 * This is the default display activity that displays if a user has not previously logged in.
 * If the user has an account, they are able to log in with the email and password used during registration,
 * or they can click to register for an account, or log-in through their google account.
 * Also is the launch point for the forgot password functionality, if the user can't remember their password.
 *
 * Contains code for checking if the device accessing the application is rooted, and disallows access if this is the case.
 */
public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

    private static final String TAG = "LoginActivity";
    private static final int RC_SIGN_IN = 9001;
    private ProgressDialog progress;

    // UI references.
    public GoogleApiClient mGoogleApiClient;
    private FirebaseAuth auth;

    private EditText emailField, passwordField;
    private Button loginButton;
    private SignInButton signInButton;
    private TextView forgotPassword, register;
    private CheckBox showHidePassword;
    private Encryption encrypter;

    private FirebaseAuth.AuthStateListener authchange;
    private Boolean mAllowNavigation = true;
    private String password;

    ArrayList<ToDoList> currentUserList = new ArrayList<>();
    String KEYFORENCRYPTION = "This is the Key I guess";

    /**
    * This method is run at the creation of the activity and setups all that is needed for this screen.
     * It also sets up the beginnings of our authentication connections with Firebase.
    *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setUpViewElements();

        encrypter = new Encryption(KEYFORENCRYPTION);

        auth = FirebaseAuth.getInstance();


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        Log.d(TAG, "Building new Session in login activity");
        setOnClickListeners();

        /** Sets up a listen for when the user is attempting to login and starts the next screen with
         * the encrypted bundle containing user info. */
        authchange = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:user signed in");
                    if (mAllowNavigation) {
                        mAllowNavigation = false;
                        Intent i = new Intent(getApplicationContext(), SMSVerify.class);
                        try {
                            i.putExtra("username", encrypter.encryptText(user.getDisplayName(), KEYFORENCRYPTION));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            i.putExtra("id", encrypter.encryptText(user.getUid(), KEYFORENCRYPTION));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            i.putExtra("email", encrypter.encryptText(user.getEmail(), KEYFORENCRYPTION));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            i.putExtra("password", encrypter.encryptText(password, KEYFORENCRYPTION));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        startActivity(i);
                    }
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }};
    }

    /**
     * Sets the onClickListeners for each of the display elements that trigger new activities
     */
    private void setOnClickListeners() {
        loginButton.setOnClickListener(this);
        forgotPassword.setOnClickListener(this);
        register.setOnClickListener(this);
        signInButton.setOnClickListener(this);

        // Set check listener over checkbox for showing and hiding password
        showHidePassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton button, boolean isChecked) {

                // If it is checkec then show password else hide password
                if (isChecked) {
                    showHidePassword.setText(R.string.hide_password);// change checkbox text
                    passwordField.setInputType(InputType.TYPE_CLASS_TEXT);
                    passwordField.setTransformationMethod(HideReturnsTransformationMethod.getInstance());// show password
                } else {
                    showHidePassword.setText(R.string.show_password);// change checkbox text
                    passwordField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    passwordField.setTransformationMethod(PasswordTransformationMethod.getInstance());// hide password
                }
            }});
    }


    /**
     * Assigns each of the view elements to their designated variables
     */
    private void setUpViewElements() {
        emailField = findViewById(R.id.login_email);
        passwordField = findViewById(R.id.login_password);
        loginButton = findViewById(R.id.loginBtn);
        forgotPassword = findViewById(R.id.forgot_password);
        register = findViewById(R.id.createAccount);
        showHidePassword = findViewById(R.id.show_hide_password);
        progress = new ProgressDialog(this);

        signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        TextView googleButton = (TextView) signInButton.getChildAt(0);
        googleButton.setText(R.string.google_sign_in);
    }


    /** Add onStart and onStop methods to start and stop the authlistener */
    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "Homescreen Login Activity has started.");
        auth.addAuthStateListener(authchange);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authchange != null) {
            auth.removeAuthStateListener(authchange);
        }
    }

    /** Runs when activity restarts and makes sure the device is not rooted*/
    @Override
    protected void onResume() {
        super.onResume();
        RootedCheck rooted = new RootedCheck();
        // If device is rooted, disallow access by exiting the app
        if(rooted.isDeviceRooted()){
            showAlertDialogAndExitApp("This device is rooted. You can't use this app.");
        }
    }

    /** Exit app with error and takes care of showing more information on exception problems.*/
    public void showAlertDialogAndExitApp(String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(LoginActivity.this).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                });

        alertDialog.show();
    }

    /**
     * The next four methods pertain to logging in with an existing Google account
     */
    public void googleSignIn() {
        mGoogleApiClient.clearDefaultAccountAndReconnect();
        if (hasNetworkConnection()) {
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
            startActivityForResult(signInIntent, RC_SIGN_IN);
            Log.d(TAG, "Here is the connection from Login: " + mGoogleApiClient.isConnected());
        } else {
            showDialog();
        }
    }

    /** This is the method that is called when the authentication process comes back from Firebase*/
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                Log.d(TAG, "Success: " + result.isSuccess());
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                Log.d(TAG, "Sucess: " + account.getDisplayName());
                firebaseAuthWithGoogle(account);
            } else {
                Toast.makeText(LoginActivity.this, "Login Failed!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /** Method use to authoize a user using Google Account credentials.*/
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        /** Calls sign in with firebase with Google credentials. */
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential:success");
                            Log.d(TAG, "Userid:" + auth.getCurrentUser().getUid());
                            Toast.makeText(LoginActivity.this, "Successfully Signed In!", Toast.LENGTH_SHORT).show();
                            progress.dismiss();
                        } else {
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            progress.dismiss();
                            Toast.makeText(LoginActivity.this, "Login Failed!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /** called if there is no connection to Firebase */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }



    /**
     * Sign the user into their account
     */
    private void emailSignIn() {
        Log.d(TAG, "signIn:");

        // Initialize the email and password variables from the fields entered by the user
        String email = emailField.getText().toString();
        password = passwordField.getText().toString();

        //Check if the fields are empty
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "You must enter an email address", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "You must enter a password", Toast.LENGTH_LONG).show();
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


        Log.d("NETWORK", "" + hasNetworkConnection());
        if (hasNetworkConnection()) {

            //Display progress dialog if fields are not empty
            progress.setMessage("Logging in...");
            progress.show();

            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d(TAG, "signIn:onComplete:" + task.isSuccessful());
                            progress.dismiss();
                            if (!task.isSuccessful()) {
                                Toast.makeText(LoginActivity.this, "Sign In Failed",
                                        Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "signIn:failed:" + task.getException());
                            }
                        }
                    });
        } else {
            showDialog();
        }
    }

    /** Setups elements within the screen and their click methods. */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.createAccount:
                createAccount(v);
                break;
            case R.id.forgot_password:
                forgotPassword(v);
                break;
            case R.id.sign_in_button:
                if (hasNetworkConnection()) {
                    googleSignIn();
                } else {
                    showDialog();
                }
                break;
            case R.id.loginBtn:
                if(hasNetworkConnection()) {
                    emailSignIn();
                } else {
                    showDialog();
                }
                break;
        }
    }


    /**
     * Start Activity for recovering a user's password
     */
    public void forgotPassword(View v) {
        startActivity(new Intent(LoginActivity.this, ForgotPassword.class));
        finish();
    }

    /**
     * Start Activity for creating a new account
     */
    public void createAccount(View v) {
        startActivity(new Intent(LoginActivity.this, Registration.class));
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
                        LoginActivity.this.finish();
                    }
                })
                .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        LoginActivity.this.finish();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}


