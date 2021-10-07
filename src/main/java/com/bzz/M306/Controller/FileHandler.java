package com.bzz.M306.Controller;

import com.bzz.M306.Data.Data;
import com.bzz.M306.Data.csvData;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
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
    private double zaehlerstandBezug;
    private double zaehlerstandEinspeisung;
    private double anfangbestandBezug;
    private double anfangbestandEinspeiung;
    private static FileHandler fileHandler;


    /**
     * Private Konstruktor, weil die Klasse ein Singolton ist.
     */
    private FileHandler() {
        sdatData = new TreeMap<>();
        zaehlerstandEinspeisung = 0;
        zaehlerstandBezug = 0;

        readESL();
        readSDAT();

    }

    public TreeMap<Long, csvData> getCSVData() {
        TreeMap<Long, csvData> csvDataMap = new TreeMap<>();
        csvData csvData;
        for (Map.Entry<Long, Data> data : sdatData.entrySet()) {
            csvData = new csvData();
            Date date = new Date(data.getKey());
            DateFormat df = new SimpleDateFormat("dd:MM:yy:HH:mm:ss");
            csvData.setDatum(String.valueOf(df.format(date)));
            csvData.setZaehlerstandBezug(data.getValue().getZaehlerstandBezug());
            csvData.setZaehlerstandEinspeisung(data.getValue().getZaehlerstandEinspeisung());
            csvData.setRelativBezug(data.getValue().getRelativBezug());
            csvData.setRelativeEinspeisung(data.getValue().getRelativeEinspeisung());
            csvDataMap.put(data.getKey(), csvData);

        }

        return csvDataMap;
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

                        Data data;
                        //Liest den Verbrauchswert
                        double value = Double.parseDouble(observationElement.getElementsByTagName("rsm:Volume")
                                .item(0).getTextContent());
                        milliSceonds = milliSceonds + 900000; //15 Min

                        if (sdatData.get(milliSceonds) != null) {
                            data = sdatData.get(milliSceonds);
                            data.setZaehlerstandEinspeisung(getZaehlerstandEinspeisung());
                            data.setZaehlerstandBezug(getZaehlerstandBezug());

                        } else {
                            data = new Data(getZaehlerstandEinspeisung(), getZaehlerstandBezug());
                        }
                        //Holt die ID des Files
                        NodeList idList = document.getElementsByTagName("rsm:InstanceDocument");
                        Node id = idList.item(0);

                        if (id.getNodeType() == Node.ELEMENT_NODE) {
                            Element idElement = (Element) id;
                            String idS = idElement.getElementsByTagName("rsm:DocumentID")
                                    .item(0).getTextContent();

                            idS = idS.split("_")[2]; //Nimmt nur die ID

                            //ID742 = Strom Bezug, ID735 = Strom Einspeisung
                            if (idS.equals("ID735")) {
                                data.setRelativeEinspeisung(value);

                                setZaehlerstandEinspeisung(data.getZaehlerstandEinspeisung());


                            } else {
                                data.setRelativBezug(value);

                                setZaehlerstandBezug(data.getZaehlerstandBezug());
                            }
                        }

                        sdatData.put(milliSceonds, data); //Speichert die Daten ab
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
                zaehlerstandBezug = value1;
                anfangbestandBezug = value1;
                zaehlerstandEinspeisung = value2;
                anfangbestandEinspeiung = value2;
            }
        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
        }
    }

    public static void writeCSV(TreeMap<Long, csvData> map) throws IOException {
        try {

            String anfangsBestandBezug = String.valueOf(FileHandler.getFileHandler().getAnfangbestandBezug());
            String anfangsBestandEinspeisung = String.valueOf(FileHandler.getFileHandler().getAnfangbestandEinspeiung());

            FileWriter datalist = new FileWriter("DataList.csv");

            datalist.write("Anfangsbestand Bezug: " + anfangsBestandBezug + "\n\n");
            datalist.write("Anfangsbestand Einspeisung: " + anfangsBestandEinspeisung + "\n\n");
            for (Map.Entry<Long, csvData> entry : map.entrySet()
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

    public static void saveJSON(TreeMap<Long, csvData> map) {
        try {

            URL url = new URL("https://api.npoint.io/0dc854da1619aca3be45");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; utf-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);

            String jsonInputString = new ObjectMapper().writeValueAsString(map.entrySet().toArray());

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
               // os.write(input);  Auskommentiert, weil es zu lange geht.
            }

            /*try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
            }*/

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Daten wurden exportiert");
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
    public double getZaehlerstandBezug() {
        return zaehlerstandBezug;
    }

    /**
     * Setzt den Zählerstand des Bezugs.
     *
     * @param zaehlerstandBezug der Zählerstand
     */
    public void setZaehlerstandBezug(double zaehlerstandBezug) {
        this.zaehlerstandBezug = zaehlerstandBezug;
    }

    /**
     * Holt den Zählerstand der Einspeisung
     *
     * @return der Zählerstand
     */
    public double getZaehlerstandEinspeisung() {
        return zaehlerstandEinspeisung;
    }

    /**
     * Setzt den Zählerstand der Einspeisung
     *
     * @param zaehlerstandEinspeisung der Zählerstand
     */
    public void setZaehlerstandEinspeisung(double zaehlerstandEinspeisung) {
        this.zaehlerstandEinspeisung = zaehlerstandEinspeisung;
    }

    public double getAnfangbestandBezug() {
        return anfangbestandBezug;
    }

    public double getAnfangbestandEinspeiung() {
        return anfangbestandEinspeiung;
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
