package com.example.tyle.ido;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
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
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
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
    public ProgressDialog progress;

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
    ArrayList<String> currentListNames = new ArrayList<>();
    ArrayList<String> currentListFirebaseId;

    private Context context;

    ListOfToDoListsAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader = new ArrayList<>();
    HashMap<String, List<ListItem>> listDataChild = new HashMap<>();

    Button add;
    Button delete;

    private String itemName;
    private double itemCost;
    private String listToAddTo;

    ArrayAdapter<String> listSpinnerAdapter;
    Spinner listSpinner;
    private String dbVal = "";

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

        delete = findViewById(R.id.delete_btn);

        for (int i = 0; i < currentList.size(); i++) {
            currentListNames.add(currentList.get(i).getName());
        }

        setContentView(R.layout.activity_list);

        SharedPreferences settings = getSharedPreferences("UserInfo", 0);
        String userid = settings.getString("userid", "").toString();

        final DatabaseReference listActivity = FirebaseDatabase.getInstance().getReference("users/" + userid + "/lists");

        listAdapter = new ListOfToDoListsAdapter(this, listDataHeader, listDataChild);

        expListView = (ExpandableListView) findViewById(R.id.toDoListsView);
        expListView.setAdapter(listAdapter);
        inflater = this.getLayoutInflater();

        listActivity.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // the initial data has been loaded, hide the progress bar
                progress.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {

            }
        });

        ChildEventListener childEventListener = new ChildEventListener() {


            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                final DataSnapshot ds = dataSnapshot;

                Log.d(TAG, "onChildAdded:" + ds.getKey());
                ArrayList<ListItem> toDoList = new ArrayList<>();
                ToDoList newList;
                newList = ds.getValue(ToDoList.class);
                currentListNames.add(newList.name);
                listDataHeader.add(newList.name);

                List<ListItem> items = new ArrayList<>();
                for (int i = 0; i < newList.toDoList.size(); i++) {
                    items.add(newList.toDoList.get(i));
                }

                listDataChild.put(newList.name, items);


                currentList.add(newList);
                currentListFirebaseId.add(ds.getKey());

                listAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());

                ToDoList newList = dataSnapshot.getValue(ToDoList.class);
                currentList.add(newList);
                String listKey = dataSnapshot.getKey();
//                adapter.notifyDataSetChanged();

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

    public void addNewItem(View v) {
        final FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();

        final AlertDialog.Builder builder = new AlertDialog.Builder(ListActivity.this);

        final Context context = builder.getContext();
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View view = inflater.inflate(R.layout.new_item_dialog, null, false);
        final EditText itemNameET = (EditText) view.findViewById(R.id.itemName);
        final EditText cost = (EditText) view.findViewById(R.id.cost);
        listSpinner = view.findViewById(R.id.listSpinner);

        listSpinnerAdapter = new ArrayAdapter<String>(ListActivity.this, android.R.layout.simple_spinner_item, currentListNames);
        listSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        listSpinner.setAdapter(listSpinnerAdapter);

        builder.setView(view);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                itemName = itemNameET.getText().toString();
                itemCost = Double.parseDouble(cost.getText().toString());
                listToAddTo = listSpinner.getSelectedItem().toString();


                final DatabaseReference allLists = FirebaseDatabase.getInstance().getReference("users/" + userid + "/lists/");
                allLists.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            if (snapshot.child("name").getValue(String.class) == listToAddTo) {
                                dbVal = snapshot.getKey();
                                DatabaseReference addSome = mDatabase.getReference("users/" + userid + "/lists/" + dbVal + "/toDoList/");
                                Log.d(TAG, "Addsome: " + addSome.toString());

                                ListItem newItem = new ListItem(itemName, itemCost, 0);
                                listDataChild.get(listToAddTo).add(newItem);
                                addSome.child(String.valueOf(snapshot.child("toDoList").getChildrenCount())).setValue(newItem);
                                listAdapter.notifyDataSetChanged();


                            }
                        }


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

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
        progress = ProgressDialog.show(ListActivity.this, "Loading", "Loading", true);


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
