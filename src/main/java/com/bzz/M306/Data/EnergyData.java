package com.bzz.M306.Data;

import com.bzz.M306.Controller.FileHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.source.tree.Tree;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.TreeMap;

public class EnergyData {
    private TreeMap<Long, Data> sdatData;
    private double eslDataBezug;
    private double eslDataEinspeisung;
    private TreeMap<Long, csvData> csvDataMap;

    public EnergyData(){
        sdatData = new TreeMap<>();
        sdatData = FileHandler.getFileHandler().getSdatData();

        eslDataEinspeisung = FileHandler.getFileHandler().getZaehlerstandEinspeisung();
        eslDataBezug = FileHandler.getFileHandler().getZaehlerstandBezug();

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

    public static void main(String[] args) throws IOException {
        EnergyData energyData = new EnergyData();
        TreeMap<Long, csvData> csvDataMap = FileHandler.getFileHandler().getCSVData();
        //energyData.writeCSV(csvDataMap);
        //energyData.saveJSON(csvDataMap);

    }
}
