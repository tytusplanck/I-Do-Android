package com.example.tyle.ido;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

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
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

public class ListActivity extends AppCompatActivity {
    private static final String TAG = "ListActivity";

    int clickCounter = 0;

    private Button btn;

    //private DatabaseReference mDatabase;


    private String username = "";
    private String email;
    private String userid;

    LayoutInflater inflater;
    private String name;
    private String des;
//    private ArrayAdapter<String> adapter;

    ListView listView;
    ArrayList<ToDoList> currentList;
    ArrayList<String> currentListNames;
    ArrayList<String> currentListFirebaseId;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("My To-Do Lists");


        currentListNames = new ArrayList<>();
        currentList = new ArrayList<>();
        currentListFirebaseId = new ArrayList<>();

        username = getIntent().getStringExtra("username");
        email = getIntent().getStringExtra("email");
        userid = getIntent().getStringExtra("id");
        User.username = username;
        User.email = email;
        User.userid = userid;


        for (int i = 0; i < currentList.size(); i++){
            currentListNames.add(currentList.get(i).getName());
        }

        setContentView(R.layout.activity_list);

        SharedPreferences settings = getSharedPreferences("UserInfo", 0);
        String userid = settings.getString("userid", "").toString();

        final DatabaseReference listActivity = FirebaseDatabase.getInstance().getReference("users/" + userid + "/lists");
        Log.d(TAG, listActivity.toString());
        Log.d(TAG, String.valueOf(currentList.size()));

        listView = (ListView) findViewById(R.id.listview);
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, currentListNames);
        listView.setAdapter(adapter);
        context = this;
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                Intent intent = new Intent(context, IndividualList.class);
                final ToDoList selectedList = currentList.get(position);
                intent.putExtra("list", selectedList);
                intent.putExtra("listId", currentListFirebaseId.get(position));
                startActivityForResult(intent, 1);
            }
        });
        inflater = this.getLayoutInflater();

        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());

                // A new comment has been added, add it to the displayed list
                ArrayList<ListItem> toDoList = new ArrayList<>();
                ToDoList newList;
                newList = dataSnapshot.getValue(ToDoList.class);
                currentListNames.add(newList.name);
                Log.d(TAG, "Length of names list: " + currentListNames.size());
                currentList.add(newList);
                currentListFirebaseId.add(dataSnapshot.getKey());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());

                ToDoList newList = dataSnapshot.getValue(ToDoList.class);
                currentList.add(newList);
                String listKey = dataSnapshot.getKey();
                adapter.notifyDataSetChanged();

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
        listActivity.addChildEventListener(childEventListener);

    }


    public void addList(View v) {
        final FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = mDatabase.getReference("users");

        final AlertDialog.Builder builder = new AlertDialog.Builder(ListActivity.this);

        final Context context = builder.getContext();
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View view = inflater.inflate(R.layout.new_list_dialog, null, false);
        final EditText listName = (EditText) view.findViewById(R.id.listName);
        final EditText description = (EditText) view.findViewById(R.id.description);

        builder.setView(view);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                name = listName.getText().toString();
                des = description.getText().toString();

                DatabaseReference addSome = mDatabase.getReference("users/" + userid);

                ArrayList<ListItem> toDoList = new ArrayList<>();
                ToDoList newList = new ToDoList(name, des, toDoList);
                addSome.child("lists").push().setValue(newList);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//
//        if(item.getItemId() == android.R.id.home) {
//            onBackPressed();
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
