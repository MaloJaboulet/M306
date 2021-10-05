package com.bzz.M306.Controller;

import com.bzz.M306.Data.Data;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TreeMap;

/**
 * @author Malo Jaboulet
 * @version 1.0
 * <p>
 * Diese Klasse hat alle Methoden, die mit den SDAT- und ESL-Files zu tun haben.
 * Diese Klasse hat Methoden, die die Files lesen und die Daten dann in eine TreeMap speichern.
 * @since 04.10.2021
 */
public class FileHandler {
    private TreeMap<Long, Data> sdatData;
    private double eslDataBezug;
    private double eslDataEinspeisung;
    private static FileHandler fileHandler;


    /**
     * Private Konstruktor, weil die Klasse ein Singolton ist.
     */
    private FileHandler() {
        sdatData = new TreeMap<>();
        eslDataEinspeisung = 0;
        eslDataBezug = 0;

        readESL();
        readSDAT();
    }

    /**
     * Liest alle SDAT-Files und holt die Daten aus ihnen heraus.
     * <b>Wichtig</b>: Die SDAT-Files müssen unter "src/main/java/XMLfiles/SDAT-Files" gespeichert sein, sonst können
     * die Daten nicht gelesen werden.
     */
    public void readSDAT() {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        //Liest alle Files im Ordner "SDAT-Files"
        File[] files = new File("./src/main/java/XMLfiles/SDAT-Files").listFiles();
        DocumentBuilder builder = null;
        TreeMap<Long, Double> tempMap = new TreeMap<>();

        try {
            builder = factory.newDocumentBuilder();

            for (File filname : files) {
                Document document = builder.parse(filname); //Parsed das XML-File
                document.getDocumentElement().normalize();
                long milliSceonds = 0;

                //Holt die Startzeit
                NodeList intervalList = document.getElementsByTagName("rsm:Interval");
                Node interval = intervalList.item(0);
                if (interval.getNodeType() == Node.ELEMENT_NODE) {
                    Element intervalElement = (Element) interval;
                    milliSceonds = convertDate(intervalElement);    //Convertiert String zu Millisekunden
                }

                //Holt alle Daten mit den Verbrauchswerten
                NodeList observationList = document.getElementsByTagName("rsm:Observation");
                for (int i = 0; i < observationList.getLength(); i++) {
                    Node observation = observationList.item(i);

                    if (observation.getNodeType() == Node.ELEMENT_NODE) {
                        Element observationElement = (Element) observation;

                        Data data = new Data(getEslDataEinspeisung(), getEslDataBezug());
                        //Liest den Verbrauchswert
                        double value = Double.parseDouble(observationElement.getElementsByTagName("rsm:Volume")
                                .item(0).getTextContent());

                        //Wenn Wert nicht 0 geht es weiter
                        if (value != 0) {
                            milliSceonds = milliSceonds + 900000; //15 Min

                            //Holt die ID des Files
                            NodeList idList = document.getElementsByTagName("rsm:InstanceDocument");
                            Node id = idList.item(0);

                            if (id.getNodeType() == Node.ELEMENT_NODE) {
                                Element idElement = (Element) id;
                                String idS = idElement.getElementsByTagName("rsm:DocumentID")
                                        .item(0).getTextContent();

                                idS = idS.split("_")[2]; //Nimmt nur die ID

                                //ID742 = Strom Bezug, ID735 = Strom Einspeisung
                                if (idS.equals("ID742")) {
                                    data.setRelativBezug(value);
                                    setEslDataBezug(data.getZaehlerstandBezug());
                                } else {
                                    data.setRelativeEinspeisung(value);
                                    setEslDataEinspeisung(data.getZaehlerstandBezug());
                                }
                            }
                            sdatData.put(milliSceonds, data); //Speichert die Daten ab
                        }
                    }
                }
            }
        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
        }
    }

    /**
     * Liest alle ESL-Files und holt die Daten aus diesem File heraus.
     * <b>Wichtig</b>: Das ESL-File müss unter "src/main/java/XMLfiles/ESL-Files" gespeichert sein, sonst können
     * die Daten nicht gelesen werden.
     */
    public void readESL() {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        //Liest das File
        File[] files = new File("./src/main/java/XMLfiles/ESL-Files").listFiles();
        DocumentBuilder builder = null;

        try {
            builder = factory.newDocumentBuilder();

            for (File filname : files) {
                Document document = builder.parse(filname);
                document.getDocumentElement().normalize();

                //Holt die Werte
                NodeList observationList = document.getElementsByTagName("ValueRow");

                double value1 = 0;
                double value2 = 0;
                for (int i = 0; i < observationList.getLength(); i++) {
                    Node observation = observationList.item(i);

                    if (observation.getNodeType() == Node.ELEMENT_NODE) {
                        Element observationElement = (Element) observation;
                        //Zählerwert vom Bezug
                        if (observationElement.getAttribute("obis").equals("1-1:1.8.1") ||
                                observationElement.getAttribute("obis").equals("1-1:1.8.2")) {

                            value1 = value1 + Double.parseDouble(observationElement.getAttribute("value"));
                            //Zählerwert von der Einspeisung
                        } else if (observationElement.getAttribute("obis").equals("1-1:2.8.1") ||
                                observationElement.getAttribute("obis").equals("1-1:2.8.2")) {

                            value2 = value2 + Double.parseDouble(observationElement.getAttribute("value"));
                        }
                    }
                }
                eslDataBezug = value1;
                eslDataEinspeisung = value2;
            }
        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
        }
    }

    /**
     * Convertiert den String mit dem Datum und der Uhrzeit in Millisekunden um.
     *
     * @param intervalElement das Element mit dem Datum und der Uhrzeit
     * @return das Datum in Millisekunden
     */
    public long convertDate(Element intervalElement) {
        String startTime = intervalElement.getElementsByTagName("rsm:StartDateTime").item(0).getTextContent();
        String dateS = startTime.split("T")[0]; //Datum
        String timeS = startTime.split("T")[1]; //Uhrzeit
        timeS = timeS.split("Z")[0];

        Date date = null;
        long milliSeconds = 0;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd").parse(dateS);

            Date time = new SimpleDateFormat("HH:mm:ss").parse(timeS);


            milliSeconds = date.getTime() + time.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return milliSeconds;
    }

    /**
     * Holt die TreeMap mit den Daten der SDAT-Files
     *
     * @return die Daten
     */
    public TreeMap<Long, Data> getSdatData() {
        return sdatData;
    }

    /**
     * Setzt die TreeMap mit den Daten
     *
     * @param sdatData die Daten der SDAT-Files
     */
    public void setSdatData(TreeMap<Long, Data> sdatData) {
        this.sdatData = sdatData;
    }

    /**
     * Setzt den FileHandler
     *
     * @param fileHandler der FileHandler
     */
    public static void setFileHandler(FileHandler fileHandler) {
        FileHandler.fileHandler = fileHandler;
    }

    /**
     * Holt die Daten zu dem Zählerstand des Bezugs.
     *
     * @return der Zählerstand
     */
    public double getEslDataBezug() {
        return eslDataBezug;
    }

    /**
     * Setzt den Zählerstand des Bezugs.
     *
     * @param eslDataBezug der Zählerstand
     */
    public void setEslDataBezug(double eslDataBezug) {
        this.eslDataBezug = eslDataBezug;
    }

    /**
     * Holt den Zählerstand der Einspeisung
     *
     * @return der Zählerstand
     */
    public double getEslDataEinspeisung() {
        return eslDataEinspeisung;
    }

    /**
     * Setzt den Zählerstand der Einspeisung
     *
     * @param eslDataEinspeisung der Zählerstand
     */
    public void setEslDataEinspeisung(double eslDataEinspeisung) {
        this.eslDataEinspeisung = eslDataEinspeisung;
    }

    /**
     * Holt den FileHandler
     *
     * @return der FileHandler
     */
    public static FileHandler getFileHandler() {
        if (fileHandler == null) fileHandler = new FileHandler();
        return fileHandler;
    }
}
