package com.bzz.M306.Data;

import com.bzz.M306.Controller.FileHandler;
import java.util.TreeMap;

public class EnergyData {
    private TreeMap<Long, Data> sdatData;
    private double eslDataBezug;
    private double eslDataEinspeisung;

    public EnergyData(){
        sdatData = new TreeMap<>();
        sdatData = FileHandler.getFileHandler().getSdatData();

        eslDataEinspeisung = FileHandler.getFileHandler().getEslDataEinspeisung();
        eslDataBezug = FileHandler.getFileHandler().getEslDataBezug();

    }

    public double getEslDataBezug() {
        return eslDataBezug;
    }

    public void setEslDataBezug(double eslDataBezug) {
        this.eslDataBezug = eslDataBezug;
    }

    public double getEslDataEinspeisung() {
        return eslDataEinspeisung;
    }

    public void setEslDataEinspeisung(double eslDataEinspeisung) {
        this.eslDataEinspeisung = eslDataEinspeisung;
    }

    public TreeMap<Long, Data> getSdatData() {
        return sdatData;
    }

    public void setSdatData(TreeMap<Long, Data> sdatData) {
        this.sdatData = sdatData;
    }

    public static void main(String[] args) {
        EnergyData energyData = new EnergyData();
        energyData.getEslDataEinspeisung();
    }
}
