package com.example.tyle.ido;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


/**
 * Class to display a loading screen briefly when the app is started
 */
public class SplashScreen extends Activity {

    private final String TAG = "SPLASHSCREEN";
    private static final int SPLASH_DISPLAY_LENGTH = 1500;
    private FirebaseAuth auth;
    private String name, id, email;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "In onCreate");
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_splash_screen);
        if (auth.getCurrentUser() == null) {
            //Start the HomeScreen activity if the user is not logged in
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(SplashScreen.this, LoginActivity.class));
                    finish();
                }
            }, SPLASH_DISPLAY_LENGTH);
        } else {
            //Start the main activity  if the user is logged in
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    FirebaseUser currentUser = auth.getCurrentUser();
                    email = currentUser.getEmail();
                    id = currentUser.getUid();
                    name = currentUser.getDisplayName();
                    Log.d(TAG, "Current User: " + name);
                    Log.d(TAG, email);
                    Log.d(TAG, id);
                    SharedPreferences settings = getSharedPreferences("UserInfo", 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("username", name);
                    editor.putString("email", email);
                    editor.putString("userid", id);
                    editor.commit();
                    startActivity(new Intent(SplashScreen.this, MainActivity.class));
                    finish();
                }
            }, SPLASH_DISPLAY_LENGTH);
        }
    }
}
