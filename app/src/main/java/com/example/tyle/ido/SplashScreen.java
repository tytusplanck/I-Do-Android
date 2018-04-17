package com.example.tyle.ido;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


/**
 * Class to display a loading screen briefly when the app is started
 */
public class SplashScreen extends AppCompatActivity {

    private final String TAG = "SPLASHSCREEN";
    private static final int SPLASH_DISPLAY_LENGTH = 1500;
    private FirebaseAuth auth;
    private String name, id, email;
    private Encryption encrypter;
    String KEYFORENCRYPTION = "This is the Key I guess";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        encrypter = new Encryption(KEYFORENCRYPTION);

        auth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_splash_screen);
        if (auth.getCurrentUser() == null) {
            //Start the Login/Homescreen activity if the user is not logged in
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

                    Intent i = new Intent(SplashScreen.this, MainActivity.class);
                    try {
                        i.putExtra("username", encrypter.encryptText(currentUser.getDisplayName(), KEYFORENCRYPTION));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        i.putExtra("id", encrypter.encryptText(currentUser.getUid(), KEYFORENCRYPTION));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        i.putExtra("email", encrypter.encryptText(currentUser.getEmail(), KEYFORENCRYPTION));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    startActivity(i);
                    finish();
                }
            }, SPLASH_DISPLAY_LENGTH);
        }
    }
}
