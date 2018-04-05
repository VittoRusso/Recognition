package com.example.vittorusso.recognition;

import android.app.ActionBar;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;

public class controlActivity extends AppCompatActivity {

    private ArrayList<String> deviceNames = new ArrayList<>();
    private ArrayList<String> deviceAddresses = new ArrayList<>();
    private BluetoothAdapter mBluetoothAdapter;

    private FragmentDevice1 Frag1;
    private FragmentDevice2 Frag2;

    private String LilyHR = "LilyPad HeartR";
    private String LilyHAR = "LilyPad HAR";
    private String MacLilyHR = "DA:37:F6:57:FE:83";
    private String MacLilyHAR = "F8:76:6C:D1:B2:1C";

    private updateFragment1 upFrag1;
    private updateFragment2 upFrag2;

    private ArrayList<Float> ValoresX,ValoresY,ValoresZ;
    private ArrayList<Integer> ValoreHR; 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);

        getSupportActionBar().setTitle(R.string.title_devices);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (savedInstanceState == null) {
            Frag1 = FragmentDevice1.newInstance(LilyHAR,MacLilyHAR);
            Frag1.setmListener1(new FragmentDevice1.OnFragmentInteractionListener() {
                @Override
                public void onFragment1Interaction(String data) {
                    incomingHAR();
                }
            });
            displayFragmentA();
            Frag2 = FragmentDevice2.newInstance(LilyHR,MacLilyHR);
            Frag2.setmListener2(new FragmentDevice2.OnFragmentInteractionListener() {
                @Override
                public void onFragment2Interaction(String data) {
                    incomingHeartRate();
                }
            });
            displayFragmentB();
        }

    }

    private void incomingHeartRate() {
    }

    private void incomingHAR() {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menuconnect, menu);
        menu.findItem(R.id.menu_connect).setVisible(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_connect:
                if(Frag1 != null){
                    upFrag1.connectFrag1();
                }
                if(Frag2 != null){
                    upFrag2.connectFrag2();
                }
                break;
        }
        return true;
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

    public interface updateFragment1 {
        void connectFrag1();
    }

    public interface updateFragment2 {
        void connectFrag2();
    }

    public void setUpdateFrag1(updateFragment1 upd){
        this.upFrag1 = upd;
    }

    public void setUpdateFrag2(updateFragment2 upd){
        this.upFrag2 = upd;
    }

}
