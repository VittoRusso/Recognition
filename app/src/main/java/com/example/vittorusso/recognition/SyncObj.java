package com.example.vittorusso.recognition;

import java.util.ArrayList;

public class SyncObj {
    private ArrayList<Float> valuesX;
    private ArrayList<Float> valuesY;
    private ArrayList<Float> valuesZ;

    public ArrayList<Float> getValuesX() {
        return valuesX;
    }

    public void setValuesX(ArrayList<Float> valuesX) {
        this.valuesX = valuesX;
    }

    public ArrayList<Float> getValuesY() {
        return valuesY;
    }

    public void setValuesY(ArrayList<Float> valuesY) {
        this.valuesY = valuesY;
    }

    public ArrayList<Float> getValuesZ() {
        return valuesZ;
    }

    public void setValuesZ(ArrayList<Float> valuesZ) {
        this.valuesZ = valuesZ;
    }

    public SyncObj(ArrayList<Float> valuesX, ArrayList<Float> valuesY, ArrayList<Float> valuesZ) {
        this.valuesX = valuesX;
        this.valuesY = valuesY;
        this.valuesZ = valuesZ;
    }


}
