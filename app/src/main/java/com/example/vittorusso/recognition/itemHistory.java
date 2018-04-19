package com.example.vittorusso.recognition;

import android.app.ActionBar;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;

public class itemHistory extends AppCompatActivity {

    private ArrayList<DataLine> curData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_history);

        getSupportActionBar().setTitle(R.string.title_devices);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);

        Intent i = getIntent();

        TransferData transfer = TransferData.getInstance();
        curData = transfer.getGroupData();

        Log.v("TAG","Size Cur: "+curData.size());

    }
}
