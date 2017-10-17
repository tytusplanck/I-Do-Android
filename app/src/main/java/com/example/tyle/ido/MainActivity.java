/**
 * Created by kylerossman on 10/2/17.
 */

package com.example.tyle.ido;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;


public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
    private static final String TAG = "MainActivity";
   // private Session currentSession;
//    UserSession currentUserSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //currentSession = new Session(getApplicationContext());
//
//        UserSession currentUserSession = UserSession.getSingletonObject();
//        final LoginActivity loginActivity=  currentUserSession.getLoginActivity();

//        Button logoutButton = findViewById(R.id.logout_button);
//        logoutButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //currentSession.googleLogOut(currentSession);
//
////                loginActivity.signOut();
//            }
//        });
    }

    /** Called when the user taps the Send button */
    public void jumpToLists(View view) {
        Intent intent = new Intent(this, ListActivity.class);
        startActivity(intent);
    }

}
