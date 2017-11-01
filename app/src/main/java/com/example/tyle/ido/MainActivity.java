/**
 * Created by kylerossman on 10/2/17.
 */

package com.example.tyle.ido;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.tyle.ido.dataObjects.ListItem;
import com.example.tyle.ido.dataObjects.ToDoList;
import com.example.tyle.ido.dataObjects.User;
import com.google.firebase.database.ChildEventListener;
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

    private ArrayList<ToDoList> currentUserList = new ArrayList<>();

    public User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences settings = getSharedPreferences("UserInfo", 0);
        userid = settings.getString("userid", "").toString();
        username = settings.getString("userName", "").toString();
        email = settings.getString("email", "").toString();

        User.userid = userid;
        User.email = email;
        User.username = username;

        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = mDatabase.getReference("users");

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (!snapshot.hasChild(userid)) {
                    ArrayList<ToDoList> lists = new ArrayList<>();
                    user = new User(username, email, userid, lists);
                    myRef.child(userid).setValue(user);

                } else {
                    User.userid = userid;
                    User.email = email;
                    User.username = username;
                }

             }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        final DatabaseReference listRef = FirebaseDatabase.getInstance().getReference("users/" + userid + "/lists");
        Log.d(TAG, listRef.toString());

        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());

                ArrayList<ListItem> toDoList = new ArrayList<>();
                ToDoList newList;
                newList = dataSnapshot.getValue(ToDoList.class);
                Log.d(TAG, "Here is list info: " + newList.name + newList.description + newList.toDoList);
                currentUserList.add(newList);
                Log.d(TAG, "Current size from main activity in the listener." + String.valueOf(currentUserList.size()));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());

                //TODO need to use list key to find which to change from the list.
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());

                // A comment has changed, use the key to determine if we are displaying this
                // comment and if so remove it.
                String listKey = dataSnapshot.getKey();

                //TODO need to use list key to find which to remove from the list.

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildMoved:" + dataSnapshot.getKey());

                // A comment has changed position, use the key to determine if we are
                // displaying this comment and if so move it.
                ToDoList newList = dataSnapshot.getValue(ToDoList.class);
                String listKey = dataSnapshot.getKey();

                //TODO need to use list key to move the list in the current list.
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "postComments:onCancelled", databaseError.toException());

            }
        };
        listRef.addChildEventListener(childEventListener);
    }

    /** Called when the user taps the Send button */
    public void jumpToLists(View view) {
        Intent intent = new Intent(this, ListActivity.class);
        intent.putExtra("username", username);
        intent.putExtra("id", userid);
        intent.putExtra("email", email);
        intent.putExtra("currentUserList", currentUserList);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
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
