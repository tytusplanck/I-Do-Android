package com.example.tyle.ido;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ListActivity extends Activity {
    private static final String TAG = "ListActivity";

    ArrayList<String> listItems = new ArrayList<String>();
    ArrayAdapter<String> adapter;

    int clickCounter = 0;

    private Button btn;

    //private DatabaseReference mDatabase;


    private String username;
    private String email;
    private String userid;

    LayoutInflater inflater;
    private String name;
    private String des;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        username = getIntent().getStringExtra("username");
        email = getIntent().getStringExtra("username");
        userid = getIntent().getStringExtra("id");

        setContentView(R.layout.activity_list);
        inflater = this.getLayoutInflater();
//        listName.setText("nothing");
//        description.setText("nothing");

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listItems);

    }

    public void addList(View v) {
        final FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = mDatabase.getReference("users");

        final AlertDialog.Builder builder = new AlertDialog.Builder(ListActivity.this);
        //AlertDialog dialog = builder.create();
//        builder.setTitle("List Name");

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        final Context context = builder.getContext();
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View view = inflater.inflate(R.layout.new_list_dialog, null, false);
        final EditText listName = (EditText)view.findViewById(R.id.listName);
        final EditText description = (EditText)view.findViewById(R.id.description);

//        final EditText input = new EditText(this);
//        input.setInputType(InputType.TYPE_CLASS_TEXT);
//
//        final EditText input2 = new EditText(this);
//        input2.setInputType(InputType.TYPE_CLASS_TEXT);
//
//        LinearLayout layout = new LinearLayout(this);
//        layout.setOrientation(LinearLayout.VERTICAL);
//        layout.addView(input);
//        layout.addView(input2);
          builder.setView(view);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                name = listName.getText().toString();
                des = description.getText().toString();

                DatabaseReference addSome = mDatabase.getReference("users/"+userid);
                addSome.child("lists").child(name).setValue("");
                addSome.child("lists").child(name).child("Description").setValue(des);
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
}
