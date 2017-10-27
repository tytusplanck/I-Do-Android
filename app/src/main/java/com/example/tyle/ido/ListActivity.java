package com.example.tyle.ido;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ListActivity extends AppCompatActivity {
    private static final String TAG = "ListActivity";

    ArrayList<String> listItems = new ArrayList<String>();
    ArrayAdapter<String> adapter;

    int clickCounter=0;

    private Button btn;

    //private DatabaseReference mDatabase;


    private String username;
    private String email;
    private String userid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        username = getIntent().getStringExtra("username");
        email = getIntent().getStringExtra("username");
        userid = getIntent().getStringExtra("id");

        setContentView(R.layout.activity_list);


        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listItems);

    }

    public void addList(){

    }
}
