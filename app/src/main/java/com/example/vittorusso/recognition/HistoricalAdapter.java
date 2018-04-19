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

}
