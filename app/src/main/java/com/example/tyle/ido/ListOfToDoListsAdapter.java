package com.example.tyle.ido;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.example.tyle.ido.dataObjects.ListItem;
import com.example.tyle.ido.dataObjects.ToDoList;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.example.tyle.ido.dataObjects.User.userid;

/**
 * Created by tytusplanck on 11/3/17.
 */

public class ListOfToDoListsAdapter extends BaseExpandableListAdapter {
    private static final String TAG = "ListOfToDoListsAdapter";
    private Context _context;
    private List<String> _listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<String, List<ListItem>> _listDataChild = new HashMap<>();
    private List<String> itemNames = new ArrayList<>();
    private String listThatAddedNewItem;

    private String itemName;
    private double itemCost;

    private String currentListForChild;
    private String currentItem;


    public ListOfToDoListsAdapter(Context context, List<String> listDataHeader,
                                  HashMap<String, List<ListItem>> listChildData) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition)).get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        //final String childText = (String) getChild(groupPosition, childPosition);
        final ListItem childItem = (ListItem) getChild(groupPosition, childPosition);
        final String toDoListName = this._listDataHeader.get(groupPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.row_item, null);
        }


        CheckBox myCheck = (CheckBox) convertView.findViewById(R.id.myCheckBox);
        TextView txtListChild = (TextView) convertView
                .findViewById(R.id.lblListItem);
        TextView txtListChildCost = (TextView) convertView
                .findViewById(R.id.listItemCost);


        if (childItem.getIsCompleted() == 0) {
            myCheck.setChecked(false);
        } else {
            myCheck.setChecked(true);
        }
        txtListChild.setText(childItem.getName());
        DecimalFormat df = new DecimalFormat("#.00");
        txtListChildCost.setText(String.valueOf("$" + String.format("%.2f", childItem.getCost())));

        myCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final DatabaseReference completedRef = FirebaseDatabase.getInstance().getReference("users/" + userid + "/lists/");
                completedRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.d(TAG, "We in boys");
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            if (snapshot.child("name").getValue(String.class) == toDoListName) {
                                currentListForChild = snapshot.getKey();
                                Log.d(TAG, currentListForChild);

                            }
                            for (DataSnapshot secondSnapshot : snapshot.child("toDoList").getChildren()) {
                                Log.d(TAG, secondSnapshot.getKey());
                                if (secondSnapshot.child("name").getValue(String.class) == childItem.getName()) {
                                    final DatabaseReference completedRef = FirebaseDatabase.getInstance().getReference("users/" + userid + "/lists/" + currentListForChild + "/toDoList/" + secondSnapshot.getKey());
                                    Log.d(TAG, completedRef.toString());
                                    currentItem = childItem.getName();
                                    if (childItem.isCompleted == 0) {
                                        Log.d(TAG, "Attempting to set stuff");
                                        childItem.setIsCompleted(1);
                                        completedRef.child("isCompleted").setValue(1);

                                    } else {
                                        childItem.setIsCompleted(0);
                                        completedRef.child("isCompleted").setValue(0);
                                    }
                                }

                            }

                        }


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
        });
        Log.d(TAG, "Attempted to add child view");


        notifyDataSetChanged();
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        final String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.row, null);
        }

        final TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.list_item_string);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);

        Button delete = convertView.findViewById(R.id.delete_btn);

        SharedPreferences settings = _context.getSharedPreferences("UserInfo", 0);
        final String userid = settings.getString("userid", "").toString();

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatabaseReference allLists = FirebaseDatabase.getInstance().getReference("users/" + userid + "/lists/");
                allLists.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            if (snapshot.child("name").getValue(String.class) == headerTitle) {
                                allLists.child(snapshot.getKey()).removeValue();
                            }
                        }
                    }


                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
                _listDataHeader.remove(groupPosition);
                notifyDataSetChanged();
            }
        });

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }


}
