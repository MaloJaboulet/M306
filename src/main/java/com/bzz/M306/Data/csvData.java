package com.bzz.M306.Data;

public class csvData {


    private double relativeEinspeisung;
    private double relativBezug;
    private double zaehlerstandBezug;
    private double zaehlerstandEinspeisung;
    private String datum;

    public csvData(double relativeEinspeisung, double relativBezug, double zaehlerstandBezug, double zaehlerstandEinspeisung, String datum) {
        this.relativeEinspeisung = relativeEinspeisung;
        this.relativBezug = relativBezug;
        this.zaehlerstandBezug = zaehlerstandBezug;
        this.zaehlerstandEinspeisung = zaehlerstandEinspeisung;
        this.datum = datum;
    }


    public csvData() {}

    public String getDatum() {
        return datum;
    }

    public void setDatum(String datum) {

        this.datum = datum;
    }
    public double getRelativeEinspeisung() {
        return relativeEinspeisung;
    }

    public void setRelativeEinspeisung(double relativeEinspeisung) {
        this.relativeEinspeisung = relativeEinspeisung;
    }

    public double getRelativBezug() {
        return relativBezug;
    }

    public void setRelativBezug(double relativBezug) {
        this.relativBezug = relativBezug;
    }

    public double getZaehlerstandBezug() {
        return zaehlerstandBezug;
    }

    public void setZaehlerstandBezug(double zaehlerstandBezug) {
        this.zaehlerstandBezug = zaehlerstandBezug;
    }

    public double getZaehlerstandEinspeisung() {
        return zaehlerstandEinspeisung;
    }

    public void setZaehlerstandEinspeisung(double zaehlerstandEinspeisung) {
        this.zaehlerstandEinspeisung = zaehlerstandEinspeisung;
    }






}
