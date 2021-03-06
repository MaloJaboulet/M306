package com.bzz.M306.Data;

import com.bzz.M306.Controller.FileHandler;

import java.util.TreeMap;

/**
 * @author Malo Jaboulet
 * @version 1.0
 * @since 05.10.2021
 * <p>
 * Die Model-Klasse der Applikation.
 * Die Klasse beinhaltet alle Daten der Files.
 */
public class EnergyData {
    private TreeMap<Long, Data> sdatData;
    private double eslDataBezug;
    private double eslDataEinspeisung;

    /**
     * Der Konstruktor
     */
    public EnergyData() {
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

}
