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
import com.github.mikephil.charting.components.LegendEntry;
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
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class itemHistory extends AppCompatActivity {

    private ArrayList<DataLine> curData;
    private SimpleDateFormat localDateFormat = new SimpleDateFormat("HH:mm:ss", Locale.US);
    private LineChart chart;

    private int[] colorsArray;
    private boolean[] legendStatus;
    private String[] labelsArray;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_history);

        getSupportActionBar().setTitle(R.string.graph_title);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);

        colorsArray= new int[]{getResources().getColor(R.color.c0),
                getResources().getColor(R.color.c1),
                getResources().getColor(R.color.c2),
                getResources().getColor(R.color.c3),
                getResources().getColor(R.color.c4),
                getResources().getColor(R.color.c5),
                getResources().getColor(R.color.c6),
                getResources().getColor(R.color.c7),
                getResources().getColor(R.color.c8),
                getResources().getColor(R.color.c9),
                getResources().getColor(R.color.c10)};

        labelsArray = new String[]{"No Activity",
                "Standing Still",
                "Walking",
                "Jogging",
                "Going Up Stairs",
                "Going Down Stairs",
                "Jumping",
                "Laying Down",
                "Laying Upide Down",
                "Squatting",
                "Push Ups"} ;

        legendStatus = new boolean[]{false,
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                false};

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

            String label = curData.get(j).getLabelRF();
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

        List<LegendEntry> entries = new ArrayList<>();
        List<Entry> entriesHR = new ArrayList<>();
        List<Integer> colors = new ArrayList<Integer>();
        for (int i = 0; i < valuesDate.size(); i++) {
            entriesHR.add(new Entry(i,valuesHR.get(i)));
            switch (valuesLabelInt.get(i)){
                case 1:
                    colors.add(getResources().getColor(R.color.c1));
                    if(!legendStatus[1]){
                        LegendEntry entry = new LegendEntry();
                        entry.formColor = colorsArray[1];
                        entry.label = labelsArray[1];
                        entries.add(entry);
                        legendStatus[1]=!legendStatus[1];
                    }
                    break;
                case 2:
                    colors.add(getResources().getColor(R.color.c2));
                    if(!legendStatus[2]){
                        LegendEntry entry = new LegendEntry();
                        entry.formColor = colorsArray[2];
                        entry.label = labelsArray[2];
                        entries.add(entry);
                        legendStatus[2]=!legendStatus[2];
                    }
                    break;
                case 3:
                    colors.add(getResources().getColor(R.color.c3));
                    if(!legendStatus[3]){
                        LegendEntry entry = new LegendEntry();
                        entry.formColor = colorsArray[3];
                        entry.label = labelsArray[3];
                        entries.add(entry);
                        legendStatus[3]=!legendStatus[3];
                    }
                    break;
                case 4:
                    colors.add(getResources().getColor(R.color.c4));
                    if(!legendStatus[4]){
                        LegendEntry entry = new LegendEntry();
                        entry.formColor = colorsArray[4];
                        entry.label = labelsArray[4];
                        entries.add(entry);
                        legendStatus[4]=!legendStatus[4];
                    }
                    break;
                case 5:
                    colors.add(getResources().getColor(R.color.c5));
                    if(!legendStatus[5]){
                        LegendEntry entry = new LegendEntry();
                        entry.formColor = colorsArray[5];
                        entry.label = labelsArray[5];
                        entries.add(entry);
                        legendStatus[5]=!legendStatus[5];
                    }
                    break;
                case 6:
                    colors.add(getResources().getColor(R.color.c6));
                    if(!legendStatus[6]){
                        LegendEntry entry = new LegendEntry();
                        entry.formColor = colorsArray[6];
                        entry.label = labelsArray[6];
                        entries.add(entry);
                        legendStatus[6]=!legendStatus[6];
                    }
                    break;
                case 7:
                    colors.add(getResources().getColor(R.color.c7));
                    if(!legendStatus[7]){
                        LegendEntry entry = new LegendEntry();
                        entry.formColor = colorsArray[7];
                        entry.label = labelsArray[7];
                        entries.add(entry);
                        legendStatus[7]=!legendStatus[7];
                    }
                    break;
                case 8:
                    colors.add(getResources().getColor(R.color.c8));
                    if(!legendStatus[8]){
                        LegendEntry entry = new LegendEntry();
                        entry.formColor = colorsArray[8];
                        entry.label = labelsArray[8];
                        entries.add(entry);
                        legendStatus[8]=!legendStatus[8];
                    }
                    break;
                case 9:
                    colors.add(getResources().getColor(R.color.c9));
                    if(!legendStatus[9]){
                        LegendEntry entry = new LegendEntry();
                        entry.formColor = colorsArray[9];
                        entry.label = labelsArray[9];
                        entries.add(entry);
                        legendStatus[9]=!legendStatus[9];
                    }
                    break;
                case 10:
                    colors.add(getResources().getColor(R.color.c10));
                    if(!legendStatus[10]){
                        LegendEntry entry = new LegendEntry();
                        entry.formColor = colorsArray[10];
                        entry.label = labelsArray[10];
                        entries.add(entry);
                        legendStatus[10]=!legendStatus[10];
                    }
                    break;
                case 0:
                    colors.add(getResources().getColor(R.color.c0));
                    if(!legendStatus[0]){
                        LegendEntry entry = new LegendEntry();
                        entry.formColor = colorsArray[0];
                        entry.label = labelsArray[0];
                        entries.add(entry);
                        legendStatus[0]=!legendStatus[0];
                    }
                    break;
            }


        }
        int[] colorInt = convertIntegers(colors);

        LineDataSet dataSetHR = new LineDataSet(entriesHR,"Heart Rate");
        dataSetHR.setColors(colorInt);
        dataSetHR.setCircleColors(colorInt);


        chart.getXAxis().setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return localDateFormat.format(valuesDate.get((int) value));
            }
        });
        chart.getXAxis().setLabelRotationAngle(45f);
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        chart.setData(new LineData(dataSetHR));
        chart.animateXY(1500,1500);
        chart.getLegend().setCustom(entries);
        chart.getLegend().setWordWrapEnabled(true);
        chart.getDescription().setEnabled(false);
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

    public static int[] convertIntegers(List<Integer> integers)
    {
        int[] ret = new int[integers.size()];
        Iterator<Integer> iterator = integers.iterator();
        for (int i = 0; i < ret.length; i++)
        {
            ret[i] = iterator.next().intValue();
        }
        return ret;
    }
}
