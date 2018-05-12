package com.example.vittorusso.recognition;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import java.util.ArrayList;

public class HistoricalAdapter extends RecyclerView.Adapter<HistoricalAdapter.MyViewHolder> {

    private ArrayList<ArrayList<DataLine>> DataGroups;
    private RecycleClickListener mListener;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView tvD1, tvD2, tvAc, tvHr;

        public MyViewHolder(View view) {
            super(view);
            tvD1 = view.findViewById(R.id.tvD1);
            tvD2 = view.findViewById(R.id.tvD2);
            tvAc =  view.findViewById(R.id.tvAc);
            tvHr = view.findViewById(R.id.tvHr);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(mListener != null){
                mListener.itemClick(v, getAdapterPosition());
            }
        }
    }

    public void setRecycleClickListener(RecycleClickListener listener){
        mListener = listener;
    }


    public HistoricalAdapter(ArrayList<ArrayList<DataLine>> DataGroups) {
        this.DataGroups = DataGroups;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.historical_recycleview, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        ArrayList<DataLine> curLine = DataGroups.get(position);
        holder.tvD1.setText((curLine.get(0)).getDate().toString());
        holder.tvD2.setText((curLine.get(curLine.size()-1)).getDate().toString());
        holder.tvAc.setText(holder.tvAc.getText().toString()+" "+getCommonActivity(curLine));
        holder.tvHr.setText(holder.tvHr.getText().toString()+" "+getHRAverage(curLine));
    }

    private String getHRAverage(ArrayList<DataLine> curLine) {
        int average = 0;
        for (int i = 0; i < curLine.size(); i++) {
            String check = (curLine.get(i).getValueHR()).replaceAll("\\s","");
            if (check.equals("null")){
                average += 0;
            }else{
                int curHR = Integer.parseInt(check);
                average += curHR;
            }
        }
        average = average/(curLine.size());
        return String.valueOf(average);
    }

    private String getCommonActivity(ArrayList<DataLine> curLine) {
        ArrayList<Integer> activity = new ArrayList<>();
        for (int i = 0; i < curLine.size(); i++) {
            String check = (curLine.get(i).getLabelRF()).replaceAll("\\s","");
            if(check.equals("null")){
                activity.add(0);
            }else{
                activity.add(Integer.parseInt(check));
            }

        }
        Integer[] activityArray = activity.toArray(new Integer[activity.size()]);
        return getTag(Mode(activityArray));
    }

    static int Mode(Integer[] n){
        int t = 0;
        for(int i=0; i<n.length; i++){
            for(int j=1; j<n.length-i; j++){
                if(n[j-1] > n[j]){
                    t = n[j-1];
                    n[j-1] = n[j];
                    n[j] = t;
                }
            }
        }

        int mode = n[0];
        int temp = 1;
        int temp2 = 1;
        for(int i=1;i<n.length;i++){
            if(n[i-1] == n[i]){
                temp++;
            }
            else {
                temp = 1;
            }
            if(temp >= temp2){
                mode = n[i];
                temp2 = temp;
            }
        }
        return mode;
    }

    @Override
    public int getItemCount() {
        return DataGroups.size();
    }

    public void setDataGroups(ArrayList<ArrayList<DataLine>> DataGroups) {
        this.DataGroups = DataGroups;
    }

    public interface RecycleClickListener{
        void itemClick(View view , int position);
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
