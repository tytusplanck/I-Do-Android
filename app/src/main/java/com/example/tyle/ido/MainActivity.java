/**
 * Created by kylerossman on 10/2/17.
 */

package com.example.tyle.ido;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.tyle.ido.dataObjects.ToDoList;
import com.example.tyle.ido.dataObjects.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
    private static final String TAG = "MainActivity";
   // private Session currentSession;
//    UserSession currentUserSession;

    private String username;
    private String email;
    private String userid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        username = getIntent().getStringExtra("username");
        email = getIntent().getStringExtra("email");
        userid = getIntent().getStringExtra("id");

        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = mDatabase.getReference("users");

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (!snapshot.hasChild(userid)) {
                    ArrayList<ToDoList> lists = new ArrayList<>();
                    User user = new User(username, email, lists);
                    myRef.child(userid).setValue(user);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

//        DatabaseReference addSome = mDatabase.getReference("users/"+userid);
//        addSome.child("lists").setValue("Food");

//        DatabaseReference addSome2 = mDatabase.getReference("users/"+userid +"/lists/Hell");
//        addSome2.push().setValue("Need to buy some food for the wedding.");
//        addSome2.push().setValue("Not enough food!");


//        DatabaseReference addSome2 = mDatabase.getReference("users/"+userid+"list/");
//        addSome.child("lists").setValue(to);

    }

    /** Called when the user taps the Send button */
    public void jumpToLists(View view) {
        Intent intent = new Intent(this, ListActivity.class);
        intent.putExtra("username", username);
        intent.putExtra("id", userid);
        intent.putExtra("email", email);
        startActivity(intent);
    }

    /** Called when the user taps the Send button */
    public void jumpToVenue(View view) {
        Intent intent = new Intent(this, VenueMapSearch.class);
        intent.putExtra("username", username);
        intent.putExtra("id", userid);
        intent.putExtra("email", email);
        startActivity(intent);
    }

}
