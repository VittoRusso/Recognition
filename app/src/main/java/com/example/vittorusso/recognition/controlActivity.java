package com.example.vittorusso.recognition;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;

public class controlActivity extends AppCompatActivity
        implements FragmentDevice1.OnFragmentInteractionListener,
                   FragmentDevice2.OnFragmentInteractionListener{

    private ArrayList<String> deviceNames = new ArrayList<>();
    private ArrayList<String> deviceAddresses = new ArrayList<>();

    private FragmentDevice1 Frag1;
    private FragmentDevice2 Frag2;
    private String LilyHR = "LilyPad HeartR";
    private String LilyHAR = "OnePlus 5T";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);
        Intent i = getIntent();
        deviceNames = i.getStringArrayListExtra("deviceNames");
        deviceAddresses = i.getStringArrayListExtra("deviceAddresses");

        if (savedInstanceState == null) {
            if(deviceNames.contains(LilyHR)){
                Frag1 = FragmentDevice1.newInstance(deviceNames.get(deviceNames.indexOf(LilyHR)),deviceAddresses.get(deviceNames.indexOf(LilyHR)));
                displayFragmentA();
            }
            if(deviceNames.contains(LilyHAR)){
                Frag2 = FragmentDevice2.newInstance(deviceNames.get(deviceNames.indexOf(LilyHAR)),deviceAddresses.get(deviceNames.indexOf(LilyHAR)));
                displayFragmentB();
            }
        }

    }

    protected void displayFragmentA() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.linearFrag1, Frag1);
        ft.commit();
    }

    protected void displayFragmentB() {
        FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
        ft1.add(R.id.linearFrag2, Frag2);
        ft1.commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri){
        //you can leave it empty
    }


}
