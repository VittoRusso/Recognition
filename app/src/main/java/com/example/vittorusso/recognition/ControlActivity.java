package com.example.vittorusso.recognition;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.gigamole.library.PulseView;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;

public class ControlActivity extends AppCompatActivity {

    private ArrayList<String> deviceNames = new ArrayList<>();
    private ArrayList<String> deviceAddresses = new ArrayList<>();
    private ArrayList<String> heartRateArray = new ArrayList<>();
    private Integer curHR = 1;

    private ArrayList<Float> ValuesX = new ArrayList<>();
    private ArrayList<Float> ValuesY = new ArrayList<>();
    private ArrayList<Float> ValuesZ = new ArrayList<>();

    private String LilyHR = "LilyPad HeartR";
    private String LilyHAR = "LilyPad HAR";
    private String MacLilyHR = "DA:37:F6:57:FE:83";
    private String MacLilyHAR = "F8:76:6C:D1:B2:1C";
    private String email;

    private Float minX = 0f;
    private Float maxX = 0f;
    private Float minY = 0f;
    private Float maxY = 0f;
    private Float minZ = 0f;
    private Float maxZ = 0f;
    private Float numsensor0;
    private Float numsensor1;
    private Float numsensor2;

    private boolean status = false;
    private boolean isHR = false;
    private boolean isHAR = false;

    private updateFragment1 upFrag1;
    private updateFragment2 upFrag2;

    private FragmentDevice1 Frag1;
    private FragmentDevice2 Frag2;

    private PulseView pv;
    private TextView tvHR;
    private TextView tvRec;
    private Button btnGo;
    private LinearLayout ly;

    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;

    private StringBuilder recDataString = new StringBuilder();

    private AlertDialog dialogStart;

    private BufferedOutputStream out;

    private boolean pvState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);

        getSupportActionBar().setTitle(R.string.title_devices);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);

        if (savedInstanceState == null) {
            Frag1 = FragmentDevice1.newInstance(LilyHAR,MacLilyHAR);
            Frag1.setmListener1(new FragmentDevice1.OnFragmentInteractionListener() {
                @Override
                public void onFragment1Interaction(String data) {
                    incomingHAR(data);
                }
            });
            displayFragmentA();
            Frag2 = FragmentDevice2.newInstance(LilyHR,MacLilyHR);
            Frag2.setmListener2(new FragmentDevice2.OnFragmentInteractionListener() {
                @Override
                public void onFragment2Interaction(String data) {
                    incomingHeartRate(data);
                }
            });
            displayFragmentB();
        }


        pv = findViewById(R.id.pv);
        tvHR = findViewById(R.id.tvHR);
        tvHR.setVisibility(View.INVISIBLE);
        btnGo = findViewById(R.id.btnRec);
        ly = findViewById(R.id.layoutRec);
        btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(true) {
                    //isHAR && isHR
                    status = true;
                    ly.removeView(btnGo);
                    tvRec = new TextView(getApplicationContext());
                    tvRec.setText(getString(R.string.activity));
                    tvRec.setTextColor(getResources().getColor(R.color.colorAccent));
                    tvRec.setTextSize(18);
                    tvRec.setGravity(Gravity.CENTER);
                    tvRec.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.BOTTOM));
                    ly.addView(tvRec);
                    Timer timer = new Timer();
                    timer.schedule(new runScriptTimer(), 2000, 2000);
                }else{
                    dialogStart.show();
                }
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.alertTitle);
        builder.setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        dialogStart = builder.create();

        sharedPref = getSharedPreferences(getString(R.string.preferenceKey),MODE_PRIVATE);

        if (sharedPref.getFloat("minX", -999) == -999) {
            Intent calibrate = new Intent(this, CalibrationActivity.class);
            //startActivityForResult(calibrate,2);
        } else {
            minX = sharedPref.getFloat("minX", -999);
            maxX = sharedPref.getFloat("maxX", -999);
            minY = sharedPref.getFloat("minY", -999);
            maxY = sharedPref.getFloat("maxY", -999);
            minZ = sharedPref.getFloat("minZ", -999);
            maxZ = sharedPref.getFloat("maxZ", -999);
        }

        email = sharedPref.getString(getString(R.string.emailKey),null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menuconnect, menu);
        menu.findItem(R.id.menu_connect).setVisible(true);
        menu.findItem(R.id.menu_calibration).setVisible(true);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
            case R.id.menu_calibration:
                Intent calibrate = new Intent(this, CalibrationActivity.class);
                startActivityForResult(calibrate, 2);
                break;
        }
        return true;
    }

    private void incomingHeartRate(String data) {
        isHR = true;
        String array1[]= data.split("#");
        if(array1[1].contains("1")){
            if (!pvState){
                pv.startPulse();
                pvState = !pvState;
            }
            tvHR.setVisibility(View.VISIBLE);
            tvHR.setText(array1[0]);
            curHR = Integer.parseInt(array1[0]);
        }else{
            if (pvState){
                pv.finishPulse();
                pvState = !pvState;
            }
            tvHR.setVisibility(View.INVISIBLE);
        }
    }

    private void incomingHAR(String data) {
        isHAR = true;
        recDataString.append(data);
        int endOfLineIndex = recDataString.indexOf("~");
        if (endOfLineIndex > 0) {
            String dataInPrint = recDataString.substring(0, endOfLineIndex);
            if (recDataString.charAt(0) == '#') {
                StringTokenizer tokens = new StringTokenizer(dataInPrint, "+");
                String sensor0 = tokens.nextToken().replace("#", "");
                String sensor1 = tokens.nextToken();
                String sensor2 = tokens.nextToken();

                numsensor0 = standerdize(Float.parseFloat(sensor0), minX, maxX);  // Pin X
                numsensor1 = standerdize(Float.parseFloat(sensor1), minY, maxY);  // Pin Y
                numsensor2 = standerdize(Float.parseFloat(sensor2), minZ, maxZ);  // Pin Z

                ValuesX.add(numsensor0);
                ValuesY.add(numsensor1);
                ValuesZ.add(numsensor2);

                if(ValuesX.size() == 20){
                    if(status){
                        new SendHttp().execute(createQuery(ValuesX,ValuesY,ValuesZ));
                    }
                    ValuesX.clear();
                    ValuesY.clear();
                    ValuesZ.clear();
                }

            }
            recDataString.delete(0, recDataString.length());
        }
    }



    private String createQuery(ArrayList<Float> valuesX, ArrayList<Float> valuesY, ArrayList<Float> valuesZ) {
        StringBuilder query = new StringBuilder();
        StringBuilder arrayX = new StringBuilder();
        StringBuilder arrayY = new StringBuilder();
        StringBuilder arrayZ = new StringBuilder();
        arrayX.append("[");
        arrayY.append("[");
        arrayZ.append("[");
        for (int i = 0; i < valuesX.size() ; i++) {
            arrayX.append(valuesX.get(i));
            arrayY.append(valuesY.get(i));
            arrayZ.append(valuesZ.get(i));

            arrayX.append(",");
            arrayY.append(",");
            arrayZ.append(",");
        }
        arrayX.setLength(arrayX.length()-1);
        arrayY.setLength(arrayY.length()-1);
        arrayZ.setLength(arrayZ.length()-1);
        arrayX.append("]");
        arrayY.append("]");
        arrayZ.append("]");

        query.append(getString(R.string.root));
        query.append("?value_x=");
        query.append(arrayX.toString());
        query.append("&value_y=");
        query.append(arrayY.toString());
        query.append("&value_z=");
        query.append(arrayZ.toString());
        query.append("&hr=");
        query.append(curHR);
        query.append("&idPersonal=%22");
        query.append(email);
        query.append("%22");

        return query.toString();
    }

     class SendHttp extends AsyncTask <String,Void,Void> {
        @Override
        public Void doInBackground(String...params){
            try{
                final RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                StringRequest stringRequest = new StringRequest(
                        Request.Method.GET,
                        params[0],
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.v("TAG","Error");
                            }
                        }
                );
                requestQueue.add(stringRequest);
                Log.v("TAG",params[0]);
            }catch (Exception e){
                Log.v("TAG",e.getMessage());
            }
            return null;
        }
    }

    private class runScript extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... item){
            try{
                final RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                StringRequest stringRequest = new StringRequest(
                        Request.Method.GET,
                        getString(R.string.runScript),
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                if(!response.isEmpty() || response.equals("")){
                                    String[] labels = response.split(",");
                                    String show = getString(R.string.activity)+"\n RF: "+getTag(Integer.parseInt(labels[0]))+"\n"+" KNN: "+getTag(Integer.parseInt(labels[1]))+"\n"+" NN: "+getTag(Integer.parseInt(labels[2]));
                                    tvRec.setText(show);
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.v("TAG","Error");
                            }
                        }
                );
                requestQueue.add(stringRequest);
            }catch (Exception e){
                Log.v("TAG",e.getMessage());
            }
            return null;
        }
    }

    class runScriptTimer extends TimerTask {
        public void run() {
            new runScript().execute();
        }
    }

    private String getTag(Integer response) {
        switch (response){
            case 1:
                return "Standing Still";
            case 2:
                return "Walking";
            case 3:
                return "Jogging";
            case 4:
                return "Going Up Stairs";
            case 5:
                return "Going Down Stairs";
            case 6:
                return "Jumping";
            case 7:
                return "Laying Down";
            case 8:
                return "Laying Up";
            case 9:
                return "Squatting";
            case 10:
                return "Push Ups";
            default:
                return "No Activity Found";
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 2) {
            if (resultCode == RESULT_OK) {
                minX = data.getFloatExtra("minX",0f);
                maxX = data.getFloatExtra("maxX",0f);
                minY = data.getFloatExtra("minY",0f);
                maxY = data.getFloatExtra("maxY",0f);
                minZ = data.getFloatExtra("minZ",0f);
                maxZ = data.getFloatExtra("maxZ",0f);
                editor = sharedPref.edit();
                editor.putFloat("minX",minX);
                editor.putFloat("maxX",maxX);
                editor.putFloat("minY",minY);
                editor.putFloat("maxY",maxY);
                editor.putFloat("minZ",minZ);
                editor.putFloat("maxZ",maxZ);
                editor.apply();
            }
        }
    }

    public float standerdize(float x, float in_min, float in_max) {
        float out_min = -1;
        float out_max = 1;
        return ((x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min);
    }

    public interface updateFragment1 {
        void connectFrag1();
        void disconnectFrag1();
    }

    public interface updateFragment2 {
        void connectFrag2();
        void disconnectFrag2();
    }

    public void setUpdateFrag1(updateFragment1 upd){
        this.upFrag1 = upd;
    }

    public void setUpdateFrag2(updateFragment2 upd){
        this.upFrag2 = upd;
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


}
