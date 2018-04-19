package com.example.vittorusso.recognition;

import java.util.ArrayList;

public class TransferData {
    private static TransferData ourInstance;
    private ArrayList<DataLine> groupData;

    public ArrayList<DataLine> getGroupData() {
        return groupData;
    }

    public void setGroupData(ArrayList<DataLine> groupData) {
        this.groupData = groupData;
    }

    public static TransferData getInstance() {
        if( ourInstance == null){
            ourInstance = new TransferData();
        }
        return ourInstance;
    }

    private TransferData() {
    }
}
