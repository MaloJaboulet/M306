package com.bzz.M306.Data;

public class Data {
    private double relativeEinspeisung;
    private double relativBezug;
    private double zaehlerstandBezug;
    private double zaehlerstandEinspeisung;

    public Data(double standEinspeisung, double standBezug){
        setZaehlerstandBezug(standBezug);
        setZaehlerstandEinspeisung(standEinspeisung);
    }

    public double getRelativeEinspeisung() {
        return relativeEinspeisung;
    }

    public void setRelativeEinspeisung(double relativeEinspeisung) {
        addZaehlerstandEinspeisung(relativeEinspeisung);
        this.relativeEinspeisung = relativeEinspeisung;
    }

    public void addZaehlerstandEinspeisung(double relativeEinspeisung){
         setZaehlerstandEinspeisung(zaehlerstandEinspeisung + relativeEinspeisung);
    }


    public double getRelativBezug() {
        return relativBezug;
    }

    public void setRelativBezug(double relativBezug) {
        addZaehlerstandBezug(relativBezug);
        this.relativBezug = relativBezug;
    }

    public void addZaehlerstandBezug(double relativeBezug){
       setZaehlerstandBezug(zaehlerstandBezug + relativeBezug);
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
