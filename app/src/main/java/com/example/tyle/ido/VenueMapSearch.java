package com.example.tyle.ido;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

public class VenueMapSearch extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venue_map_search);

        //jumpToMaps(theText);

    }

    public void jumpToMaps(View view) {
        EditText et = (EditText) findViewById(R.id.editView1);
        String theText = et.getText().toString();
        //jumpToMaps(theText);
        Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + theText);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }

}
