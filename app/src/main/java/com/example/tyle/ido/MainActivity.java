/**
 * Created by kylerossman on 10/2/17.
 */

package com.example.tyle.ido;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.example.tyle.ido.R;


public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Session currentSession = Session.getSingletonObject();
        LoginActivity currentLoginActivity =  currentSession.getLoginActivity();

        Log.d(TAG, "Here is current user name" +currentLoginActivity.getCurrentUser().getDisplayName());
    }

    /** Called when the user taps the Send button */
    public void jumpToBudget(View view) {
        Intent intent = new Intent(this, UserProfileActivity.class);
        startActivity(intent);
    }

}
