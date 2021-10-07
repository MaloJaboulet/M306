package com.bzz.M306.Data;

/**
 * @author Pascal
 * @version 1.0
 * @since 05.10.2021
 * <p>
 * Diese Klasse sammelt alle Daten, die das CSV-File braucht.
 */
public class csvData {

    private double relativeEinspeisung;
    private double relativBezug;
    private double zaehlerstandBezug;
    private double zaehlerstandEinspeisung;
    private String datum;

    /**
     * Der Konstruktor
     */
    public csvData() {
    }

    /**
     * Der Konstruktor
     *
     * @param relativeEinspeisung     die relative Einspeisung
     * @param relativBezug            der relative Bezug
     * @param zaehlerstandBezug       der Zählerstand des Bezugs
     * @param zaehlerstandEinspeisung der Zählerstand der Einspeisung
     * @param datum                   das Datum
     */
    public csvData(double relativeEinspeisung, double relativBezug, double zaehlerstandBezug,
                   double zaehlerstandEinspeisung, String datum) {
        this.relativeEinspeisung = relativeEinspeisung;
        this.relativBezug = relativBezug;
        this.zaehlerstandBezug = zaehlerstandBezug;
        this.zaehlerstandEinspeisung = zaehlerstandEinspeisung;
        this.datum = datum;
    }

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
