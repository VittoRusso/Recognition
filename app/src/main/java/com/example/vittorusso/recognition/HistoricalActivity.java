package com.example.vittorusso.recognition;

import android.app.ActionBar;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class HistoricalActivity extends AppCompatActivity {

    private SharedPreferences share;
    private String email;
    private List<DataLine> allData;
    private ArrayList<ArrayList<DataLine>> DataGroup = new ArrayList<ArrayList<DataLine>>();
    private Integer numSession=0;

    private RecyclerView rv;
    private HistoricalAdapter mAdapter;
    private SwipeRefreshLayout swp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historical);

        getSupportActionBar().setTitle(R.string.title_devices);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);

        rv = findViewById(R.id.rv);
        swp = findViewById(R.id.swp);
        mAdapter = new HistoricalAdapter(DataGroup);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        rv.setLayoutManager(mLayoutManager);
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.setAdapter(mAdapter);
        swp.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshlist();
            }
        });


        share = getSharedPreferences(getString(R.string.preferenceKey),MODE_PRIVATE);
        email = share.getString(getString(R.string.emailKey),"");

        new loadUserData().execute(email);

    }

    private void refreshlist() {
        new loadUserData().execute(email);
    }

    private class loadUserData extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... item){
            Log.v("TAG","Im in the async task");
            try{
                final RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                 StringRequest stringRequest = new StringRequest(
                        Request.Method.GET,
                        "http://track-mymovement.tk/getUserData.php?email=%22"+item[0]+"%22",
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.v("TAG","Response");
                                try{
                                    JSONArray jsonArray = new JSONArray(response);
                                    parseJson(jsonArray);
                                    DataGroup = getSessions(allData);
                                    populateRecycleView(DataGroup);
                                    if(swp.isRefreshing()){
                                        swp.setRefreshing(false);
                                    }
                                }catch (JSONException e){
                                    Log.v("TAG",e.toString());
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

    private void populateRecycleView(ArrayList<ArrayList<DataLine>> dataGroup) {

    }

    private ArrayList<ArrayList<DataLine>> getSessions(List<DataLine> allData) {
        ArrayList<ArrayList<DataLine>> sessions = new ArrayList<ArrayList<DataLine>>();
        Date prev = (allData.get(0)).getDate();
        for (int i = 1; i < allData.size(); i++) {
            DataLine cur = allData.get(i);
            Date now = cur.getDate();
            if (now.getTime() - prev.getTime() >= 15*60*1000){
                numSession++;
                Log.v("TAG","ID: "+cur.getId());

            }
            prev = now;
        }
        return sessions;
    }


    private void parseJson(JSONArray jsonArray) {
        allData = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try{
                List<String> elements = Arrays.asList((jsonArray.getString(i).split(",")));
                List<String> data = new ArrayList<>();
                for (int j = 0; j < elements.size(); j++) {
                    data.add((elements.get(j)).substring((elements.get(j)).indexOf(":")+1));
                }
                List<String> data1 = new ArrayList<>();
                DataLine dataLine = new DataLine();
                for (int j = 0; j < data.size(); j++) {
                    String temp = data.get(j);
                    if(temp.contains("\"")) {
                        data1.add(temp.substring(temp.indexOf("\"") + 1, temp.lastIndexOf("\"")));
                    }else {
                        data1.add(temp);
                    }

                    String temp1 = data1.get(j);
                    switch (j){
                        case 0:
                            dataLine.setId(temp1);
                            break;
                        case 1:
                            dataLine.setValueX(temp1);
                            break;
                        case 2:
                            dataLine.setValueY(temp1);
                            break;
                        case 3:
                            dataLine.setValueZ(temp1);
                            break;
                        case 4:
                            dataLine.setValueHR(temp1);
                            break;
                        case 5:
                            dataLine.setLabelRF(temp1);
                            break;
                        case 6:
                            dataLine.setLabelKNN(temp1);
                            break;
                        case 7:
                            dataLine.setLabelNN(temp1);
                            break;
                        case 8:
                            dataLine.setEmail(temp1);
                            break;
                        case 9:
                            DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
                            format.setLenient(false);
                            try {
                                Date date = format.parse(temp1);
                                dataLine.setDate(date);
                            } catch (ParseException e) {
                                Log.v("TAG","Error");
                                e.printStackTrace();
                            }

                            break;
                    }
                    //Log.v("TAG",temp1);
                }

                allData.add(dataLine);
            }catch (JSONException e){
                Log.v("TAG",e.toString());
            }
        }
        Log.v("TAG",allData.size()+"");
    }

    private String getTag(String response) {
        switch (response){
            case "1":
                return "Standing Still";
            case "2":
                return "Walking";
            case "3":
                return "Jogging";
            case "4":
                return "Going Up Stairs";
            case "5":
                return "Going Down Stairs";
            case "6":
                return "Jumping";
            case "7":
                return "Laying Down";
            case "8":
                return "Laying Up";
            case "9":
                return "Squatting";
            case "10":
                return "Push Ups";
            default:
                return "No Activity Found";
        }
    }

}
