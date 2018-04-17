package com.example.vittorusso.recognition;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class HistoricalAdapter extends RecyclerView.Adapter<HistoricalAdapter.MyViewHolder> {

    private ArrayList<ArrayList<DataLine>> DataGroups;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvD1, tvD2, tvAc, tvHr;

        public MyViewHolder(View view) {
            super(view);
            tvD1 = view.findViewById(R.id.tvD1);
            tvD2 = view.findViewById(R.id.tvD2);
            tvAc =  view.findViewById(R.id.tvAc);
            tvHr = view.findViewById(R.id.tvHr);
        }
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
    public void onBindViewHolder(MyViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return DataGroups.size();
    }
}
