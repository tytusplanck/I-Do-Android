package com.example.tyle.ido;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.tyle.ido.dataObjects.ListItem;
import com.example.tyle.ido.dataObjects.ToDoList;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class IndividualList extends AppCompatActivity {
    private static final String TAG = "ListActivity";

    ListView listView;
    LayoutInflater inflater;
    private Context context;

    ArrayList<String> currentListItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_list);

        Intent i = getIntent();
        ToDoList selectedList = (ToDoList) i.getParcelableExtra("list");
        String firebaseId = getIntent().getStringExtra("listId");

        setTitle(selectedList.name);
        currentListItems = new ArrayList<>();

        for (int k = 0; k < selectedList.toDoList.size(); k++){
            currentListItems.add(selectedList.toDoList.get(k).getName());
        }

        SharedPreferences settings = getSharedPreferences("UserInfo", 0);
        String userid = settings.getString("userid", "").toString();
        Log.d(TAG, "Fuck: " + userid);

        final DatabaseReference listActivity = FirebaseDatabase.getInstance().getReference("users/" + userid + "/lists/" + firebaseId);
        Log.d(TAG, listActivity.toString());
        Log.d(TAG, String.valueOf(selectedList.toDoList.size()));

        listView = (ListView) findViewById(R.id.listview);
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, currentListItems);
        listView.setAdapter(adapter);
        context = this;
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {

            }
        });
        inflater = this.getLayoutInflater();

        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
