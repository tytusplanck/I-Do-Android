/**
 * Created by kylerossman on 10/2/17.
 */

package com.example.tyle.ido;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.TextView;
import android.widget.Toast;

import com.example.tyle.ido.dataObjects.ListItem;
import com.example.tyle.ido.dataObjects.ToDoList;
import com.example.tyle.ido.dataObjects.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
    private static final String TAG = "MainActivity";
    private FirebaseAuth mAuth;
   // private Session currentSession;
//    UserSession currentUserSession;

    private String username;
    private String email;
    private String userid;

    ListView listView;
    TextView budgetView;

    private ArrayList<ToDoList> currentUserList;
    ArrayList<String> currentListNames;

    private ProgressDialog progress;

    public User user;

    private Context context;
    double totalCost;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();

        SharedPreferences settings = getSharedPreferences("UserInfo", 0);
        userid = settings.getString("userid", "").toString();
        username = settings.getString("userName", "").toString();
        email = settings.getString("email", "").toString();

        Log.d(TAG, "Userid from oncreate: " + userid);

        User.userid = userid;
        User.email = email;
        User.username = username;

        currentUserList = new ArrayList<>();
        currentListNames = new ArrayList<>();

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
        progress = ProgressDialog.show(MainActivity.this, "Loading", "Loading", true);

        Log.d(TAG, "Userid: " + userid);
        final DatabaseReference listRef = FirebaseDatabase.getInstance().getReference("users/" + userid + "/lists");
        Log.d(TAG, listRef.toString());

        listView = (ListView) findViewById(R.id.listview);
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, currentListNames);
        listView.setAdapter(adapter);

        context = this;
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                Intent intent = new Intent(context, ListActivity.class);
                intent.putExtra("username", username);
                intent.putExtra("id", userid);
                intent.putExtra("email", email);
                startActivity(intent);
            }
        });

        listRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // the initial data has been loaded, hide the progress bar
                progress.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {

            }
        });

        totalCost = 0;
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());

                ArrayList<ListItem> toDoList = new ArrayList<>();
                ToDoList newList;
                newList = dataSnapshot.getValue(ToDoList.class);
                currentListNames.add(newList.name);
                Log.d(TAG, "Length of names list: " + currentListNames.size());
                currentUserList.add(newList);



                for(DataSnapshot snap : dataSnapshot.getChildren()) {
                    for(DataSnapshot snap2 : snap.getChildren()) {
                        totalCost += snap2.child("cost").getValue(Double.class);
                    }
                }
                budgetView = (TextView) findViewById(R.id.budgetView);
                DecimalFormat df = new DecimalFormat("#.00");
                budgetView.setText(String.valueOf("Total Cost of To-Do Lists: $" + String.format("%.2f", totalCost)));

                adapter.notifyDataSetChanged();
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
        startActivity(intent);
    }

    public void logout(View view) {
        // Firebase sign out
        mAuth.signOut();
        Toast.makeText(MainActivity.this, "Successfully Logged Out!", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
        MainActivity.this.finish();
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

    public void showDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("You do not have a network connection.  Connect? ")
                .setCancelable(false)
                .setPositiveButton("Connection Settings", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
                        MainActivity.this.finish();
                    }
                })
                .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        MainActivity.this.finish();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }


}
