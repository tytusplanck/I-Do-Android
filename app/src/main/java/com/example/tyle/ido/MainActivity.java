/**
 * Created by kylerossman on 10/2/17.
 */

package com.example.tyle.ido;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.tyle.ido.dataObjects.ListItem;
import com.example.tyle.ido.dataObjects.ToDoList;
import com.example.tyle.ido.dataObjects.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;


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
        DatabaseReference myRef = mDatabase.getReference("users");

//        ListItem item = new ListItem("sick item");
//        ToDoList lil = new ToDoList();
//        lil.toDoList.add(item);
//        List<ToDoList> big = new ArrayList<>();
//        big.add(lil);

        List<ToDoList> big = new ArrayList<>();
        User user = new User(username, email, big);
        myRef.child(userid).setValue(user);

        DatabaseReference addSome = mDatabase.getReference("users/"+userid);
        addSome.child("lists").setValue("Food");

        DatabaseReference addSome2 = mDatabase.getReference("users/"+userid +"/lists/food");
        addSome2.setValue("Need to buy some food for the wedding.");


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

}
