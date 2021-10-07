package com.bzz.M306.Data;

/**
 * @author Malo Jaboulet
 * @version 1.0
 * @since 05.10.2021
 * <p>
 * Diese Klasse hat alle Daten der ESL- und SDAT-Files. Es wird all 15 Mituten ein neues Objekt erzeugt.
 * Die Klasse hat den relativen Einschub und Bezug. Auch hat die Klasse den Zählerstand des Bezugs und der Einspeisung.
 */
public class Data {
    private double relativeEinspeisung;
    private double relativBezug;
    private double zaehlerstandBezug;
    private double zaehlerstandEinspeisung;

    /**
     * Konstruktor
     *
     * @param standEinspeisung Zählerstand der Einspeisung
     * @param standBezug       Zählerstand des Bezugs
     */
    public Data(double standEinspeisung, double standBezug) {
        setZaehlerstandBezug(standBezug);
        setZaehlerstandEinspeisung(standEinspeisung);
    }

    /**
     * Erhöt den Zähler um den Bezug des Stroms
     *
     * @param relativeBezug der Wert des Bezugs
     */
    public void addZaehlerstandBezug(double relativeBezug) {
        setZaehlerstandBezug(zaehlerstandBezug + relativeBezug);
    }

    /**
     * Erhöt den Zähler der Einspeisung um die Einspeiung des Stroms
     *
     * @param relativeEinspeisung der Wert der Einspeisung
     */
    public void addZaehlerstandEinspeisung(double relativeEinspeisung) {
        setZaehlerstandEinspeisung(zaehlerstandEinspeisung + relativeEinspeisung);
    }

    public double getRelativeEinspeisung() {
        return relativeEinspeisung;
    }

    public void setRelativeEinspeisung(double relativeEinspeisung) {
        addZaehlerstandEinspeisung(relativeEinspeisung);
        this.relativeEinspeisung = relativeEinspeisung;
    }


    public double getRelativBezug() {
        return relativBezug;
    }

    public void setRelativBezug(double relativBezug) {
        addZaehlerstandBezug(relativBezug);
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
