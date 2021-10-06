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

    public void writeCSV(TreeMap<Long, csvData> map) throws IOException {
        try {

            String anfangsBestandBezug = String.valueOf( FileHandler.getFileHandler().getAnfangbestandBezug());
            String anfangsBestandEinspeisung = String.valueOf(FileHandler.getFileHandler().getAnfangbestandEinspeiung());

            FileWriter datalist = new FileWriter("DataList.csv");

            datalist.write("Anfangsbestand Bezug: " + anfangsBestandBezug + "\n\n");
            datalist.write("Anfangsbestand Einspeisung: " + anfangsBestandEinspeisung + "\n\n");
            for (Map.Entry<Long, csvData> entry: map.entrySet()
                 ) {
                String datum = entry.getValue().getDatum();
                datalist.write("Datum: " + datum + "\n");
                Double relativeEinspeisung = entry.getValue().getRelativeEinspeisung();
                datalist.write("Relative Einspeisung: " + String.valueOf(relativeEinspeisung) + "\n");
                Double relativBezug = entry.getValue().getRelativBezug();
                datalist.write("Relativer Bezug: " + String.valueOf(relativBezug) + "\n");
                Double zaehlerstandBezug = entry.getValue().getZaehlerstandBezug();
                datalist.write("Zählerstandbezug: " + String.valueOf(zaehlerstandBezug) + "\n");
                Double zaehlerstandEinspeisung = entry.getValue().getZaehlerstandEinspeisung();
                datalist.write("Zählerstandeinspeisung: " + String.valueOf(zaehlerstandEinspeisung) + "\n");
            }
            datalist.close();
            } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void saveJSON(TreeMap<Long, csvData> map) {
        try {

            URL url = new URL("https://api.npoint.io/0dc854da1619aca3be45");
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; utf-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);

            String jsonInputString = new ObjectMapper().writeValueAsString(map.entrySet().toArray());



            try(OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input);
            }

            try(BufferedReader br = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Daten wurden exportiert");
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
