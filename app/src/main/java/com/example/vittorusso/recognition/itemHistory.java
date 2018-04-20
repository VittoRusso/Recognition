package com.example.vittorusso.recognition;

import android.app.ActionBar;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class itemHistory extends AppCompatActivity {

    private ArrayList<DataLine> curData;
    private SimpleDateFormat localDateFormat = new SimpleDateFormat("HH:mm:ss", Locale.US);
    private LineChart chart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_history);

        getSupportActionBar().setTitle(R.string.title_devices);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);

        chart = findViewById(R.id.chart);

        Intent i = getIntent();

        TransferData transfer = TransferData.getInstance();
        curData = transfer.getGroupData();

        populateGraph(curData);


    }

    private void populateGraph(ArrayList<DataLine> curData) {
        ArrayList<Integer> valuesHR = new ArrayList<>();
        final ArrayList<Date> valuesDate = new ArrayList<>();
        final ArrayList<String> valuesLabelString = new ArrayList<>();
        ArrayList<Integer> valuesLabelInt = new ArrayList<>();

        for (int j = 0; j < curData.size(); j++) {
            valuesDate.add(curData.get(j).getDate());

            String HR = curData.get(j).getValueHR();
            if(HR.contains(" ")){
                HR.replaceAll("\\s","");
            }
            if(HR.equals("null")){
                valuesHR.add(0);
            }else{
                valuesHR.add(Integer.parseInt(HR));
            }

            String label = curData.get(j).getLabelKNN();
            if(label.contains(" ")){
                label.replaceAll("\\s","");
            }
            if(label.equals("null")){
                valuesLabelString.add(getTag(0));
                valuesLabelInt.add(0);
            }else{
                valuesLabelString.add(getTag(Integer.parseInt(label)));
                valuesLabelInt.add(Integer.parseInt(label));
            }
        }

        List<Entry> entriesHR = new ArrayList<>();
        List<Entry> entriesLabel = new ArrayList<>();
        for (int i = 0; i < valuesDate.size(); i++) {
            entriesHR.add(new Entry(i,valuesHR.get(i)));
            entriesLabel.add(new Entry(i,valuesLabelInt.get(i)));
        }


        LineDataSet dataSetHR = new LineDataSet(entriesHR,"Heart Rate");
        dataSetHR.setColor(getResources().getColor(R.color.red));
        dataSetHR.setCircleColor(getResources().getColor(R.color.red));

        LineDataSet dataSetLabel = new LineDataSet(entriesLabel,"Activities");
        dataSetLabel.setValueFormatter(new IValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                return getTag((int) value);
            }
        });
        dataSetLabel.setColor(getResources().getColor(R.color.blue));
        dataSetLabel.setCircleColor(getResources().getColor(R.color.blue));


        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(dataSetHR);
        dataSets.add(dataSetLabel);
        LineData data = new LineData(dataSets);


        chart.getXAxis().setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return localDateFormat.format(valuesDate.get((int) value));
            }
        });
        chart.getXAxis().setLabelRotationAngle(45f);
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        chart.getAxisRight().setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                if(0<=value && 10>=value){
                    return getTag((int)Math.ceil(value));
                }else{
                    return "";
                }
            }
        });
        chart.setData(data);
        chart.animateXY(1500,1500);
        chart.getLegend().setPosition(Legend.LegendPosition.ABOVE_CHART_CENTER);
        chart.invalidate();

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
}
