/**
 * Created by .
 */

package com.example.tyle.ido;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
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

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * Not all original code
 *
 * Contributors: Kenton, Kyle, Jenn, Tytus
 *
 * The main activity which displays the to-do lists.  Contains a gateway to signout, previously unimplemented, and change password, which gives users the ability to
 * change their password after resetting the project.
 */
public class MainActivity extends AppCompatActivity {

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

    private Encryption encrypter;
    String KEYFORENCRYPTION = "This is the Key I guess";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();

        encrypter = new Encryption(KEYFORENCRYPTION);

        Bundle extras = getIntent().getExtras();
        userid = encrypter.decryptText(extras.getByteArray("id"), KEYFORENCRYPTION);
        username = encrypter.decryptText(extras.getByteArray("username"), KEYFORENCRYPTION);
        email = encrypter.decryptText(extras.getByteArray("email"), KEYFORENCRYPTION);


        Log.d(TAG, "Userid from oncreate: " + userid);

        User.userid = userid;
        User.email = email;
        User.name = username;

        currentUserList = new ArrayList<>();
        currentListNames = new ArrayList<>();

        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

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
                    User.name = username;
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

        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if(networkInfo != null ) {
            if(networkInfo.isConnected()) {
                progress = ProgressDialog.show(MainActivity.this, "Loading", "Loading", true);
            }
        } else {
            progress = ProgressDialog.show(MainActivity.this, "Network Connection Failed", "Please try again later.", true);
        }

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
                Intent intent = new Intent(MainActivity.this, ListActivity.class);
                try {
                    intent.putExtra("username", encrypter.encryptText(username, KEYFORENCRYPTION));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    intent.putExtra("id", encrypter.encryptText(userid, KEYFORENCRYPTION));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    intent.putExtra("email", encrypter.encryptText(email, KEYFORENCRYPTION));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                startActivity(intent);
                finish();
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
        try {
            intent.putExtra("username", encrypter.encryptText(username, KEYFORENCRYPTION));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            intent.putExtra("id", encrypter.encryptText(userid, KEYFORENCRYPTION));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            intent.putExtra("email", encrypter.encryptText(email, KEYFORENCRYPTION));
        } catch (Exception e) {
            e.printStackTrace();
        }
        startActivity(intent);
    }

    /**
     * New method, inflates the menu with change password and sign out options
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_activity, menu);
        return true;
    }

    /**
     * New method
     * Calls methods based on what you click from the inflated menu
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_out:
                signOut();
                return true;
            case R.id.change_password:
                changePassword();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Logs user out of account and returns them to the homescreen
     */
    public void signOut() {
        mAuth.signOut();
        Toast.makeText(MainActivity.this, "Successfully Logged Out!", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, LoginActivity.class));
        finish();

    }


    /**
     * Starts the ChangePassword activity so user can change password
     */
    public void changePassword() {
        Intent intent = new Intent(this, ChangePassword.class);
        try {
            intent.putExtra("username", encrypter.encryptText(username, KEYFORENCRYPTION));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            intent.putExtra("id", encrypter.encryptText(userid, KEYFORENCRYPTION));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            intent.putExtra("email", encrypter.encryptText(email, KEYFORENCRYPTION));
        } catch (Exception e) {
            e.printStackTrace();
        }
        startActivity(intent);
    }

    /** Starts the VenueSearch activity when user clicks Search Vendor button */
    public void jumpToVenue(View view) {
        Intent intent = new Intent(this, VenueMapSearch.class);
        try {
            intent.putExtra("username", encrypter.encryptText(username, KEYFORENCRYPTION));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            intent.putExtra("id", encrypter.encryptText(userid, KEYFORENCRYPTION));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            intent.putExtra("email", encrypter.encryptText(email, KEYFORENCRYPTION));
        } catch (Exception e) {
            e.printStackTrace();
        }
        startActivity(intent);
    }

}
