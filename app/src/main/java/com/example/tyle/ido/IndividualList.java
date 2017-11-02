package com.example.tyle.ido;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.EditText;
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

    private double cost;
    private String name;

    ArrayList<String> currentListItemsNames;
    private String currentListName;
    private String firebaseId;

    ToDoList selectedList;

    ArrayList<ListItem> currentListItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_list);

        Intent i = getIntent();
        selectedList = (ToDoList) i.getParcelableExtra("list");
        firebaseId = getIntent().getStringExtra("listId");

        setTitle(selectedList.name);
        currentListName = selectedList.name;
        currentListItemsNames = new ArrayList<>();
        currentListItems = new ArrayList<>();
        listView = (ListView) findViewById(R.id.listview);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView.setItemsCanFocus(false);

        for (int k = 0; k < selectedList.toDoList.size(); k++) {
            currentListItemsNames.add(selectedList.toDoList.get(k).getName());
        }

        SharedPreferences settings = getSharedPreferences("UserInfo", 0);
        final String userid = settings.getString("userid", "").toString();
        Log.d(TAG, "Fuck: " + userid);

        final DatabaseReference listActivity = FirebaseDatabase.getInstance().getReference("users/" + userid + "/lists/" + firebaseId);
        Log.d(TAG, listActivity.toString());
        Log.d(TAG, String.valueOf(selectedList.toDoList.size()));
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, currentListItemsNames);
        listView.setAdapter(adapter);
        context = this;

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {

                ListItem currentItem = selectedList.toDoList.get(position);
                final DatabaseReference completedRef = FirebaseDatabase.getInstance().getReference("users/" + userid + "/lists/" + firebaseId + "/Item " + position);
                if (currentItem.isCompleted == 0) {
                    currentItem.setIsCompleted(1);
                    completedRef.child("isCompleted").setValue(1);
                    listView.setItemChecked(position, true);
                } else {
                    currentItem.setIsCompleted(0);
                    completedRef.child("isCompleted").setValue(0);
                    listView.setItemChecked(position, false);
                }

            }
        });
        inflater = this.getLayoutInflater();

        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                if (!(dataSnapshot.getKey().equals("description")) && !(dataSnapshot.getKey().equals("name"))) {
                    Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());

                    ListItem newItem = new ListItem("", 0, 0);

                    newItem = dataSnapshot.getValue(ListItem.class);
                    currentListItemsNames.add(newItem.name);
                    selectedList.toDoList.add(newItem);
                    currentListItems.add(newItem);
                    if(newItem.isCompleted == 0){
                        for(int u = 0; u < currentListItemsNames.size(); u++){
                            if(currentListItems.get(u).name.equals(newItem.name)) {
                                if(newItem.isCompleted == 0) {
                                    Log.d(TAG, "Solid try moron");
                                    listView.setItemChecked(u, false);
                                } else {
                                    listView.setItemChecked(u, true);
                                }
                            }
                        }
                    }
                    Log.d(TAG, "Length of names list: " + currentListItemsNames.size());
                    adapter.notifyDataSetChanged();
                }
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

    public void addNewItem(View v) {
        final FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = mDatabase.getReference("users");

        final AlertDialog.Builder builder = new AlertDialog.Builder(IndividualList.this);

        final Context context = builder.getContext();
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View view = inflater.inflate(R.layout.new_item_dialog, null, false);
        final EditText itemName = (EditText) view.findViewById(R.id.itemName);
        final EditText itemCost = (EditText) view.findViewById(R.id.cost);

        builder.setView(view);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                name = itemName.getText().toString();
                cost = Double.parseDouble(itemCost.getText().toString());

                SharedPreferences settings = getSharedPreferences("UserInfo", 0);
                String userid = settings.getString("userid", "").toString();

                DatabaseReference addSome = mDatabase.getReference("users/" + userid + "/lists/" + firebaseId);
                Log.d(TAG, "Attempting to add new item: " + addSome);

                ListItem newItem = new ListItem(name, cost, 0);
                addSome.child("Item " + currentListItemsNames.size()).setValue(newItem);

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
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
