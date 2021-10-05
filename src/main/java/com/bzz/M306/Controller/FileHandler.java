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

public class FileHandler {
    private TreeMap<Long, Data> sdatData;

    private double eslDataBezug;
    private double eslDataEinspeisung;
    private static FileHandler fileHandler;
    private long tempTime;
    private double tempData;


    private FileHandler() {
        sdatData = new TreeMap<>();
        eslDataEinspeisung = 0;
        eslDataBezug = 0;
        tempData = 0;
        tempTime = 0;

        readESL();
        readSDAT();
    }

    public void readSDAT() {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        File[] files = new File("./src/main/java/XMLfiles/SDAT-Files").listFiles();
        DocumentBuilder builder = null;
        TreeMap<Long, Double> tempMap = new TreeMap<>();

        try {
            builder = factory.newDocumentBuilder();

            for (File filname : files) {
                Document document = builder.parse(filname);
                document.getDocumentElement().normalize();
                long milliSceonds = 0;

                NodeList intervalList = document.getElementsByTagName("rsm:Interval");
                Node interval = intervalList.item(0);
                if (interval.getNodeType() == Node.ELEMENT_NODE) {
                    Element intervalElement = (Element) interval;
                    milliSceonds = convertDate(intervalElement);
                }


                NodeList observationList = document.getElementsByTagName("rsm:Observation");
                for (int i = 0; i < observationList.getLength(); i++) {
                    Node observation = observationList.item(i);

                    if (observation.getNodeType() == Node.ELEMENT_NODE) {
                        Element observationElement = (Element) observation;
                        Data data = new Data(getEslDataEinspeisung(),getEslDataBezug());
                        double value = Double.parseDouble(observationElement.getElementsByTagName("rsm:Volume").item(0).getTextContent());

                        milliSceonds = milliSceonds + 900000;

                        NodeList idList = document.getElementsByTagName("rsm:InstanceDocument");
                        Node id = idList.item(0);

                        if (id.getNodeType() == Node.ELEMENT_NODE) {
                            Element idElement = (Element) id;
                            String idS = idElement.getElementsByTagName("rsm:DocumentID").item(0).getTextContent();
                            idS = idS.split("_")[2];
                            if (idS.equals("ID742")) {
                                data.setRelativBezug(value);
                                setEslDataEinspeisung(data.getZaehlerstandBezug());
                            } else {
                                data.setRelativeEinspeisung(value);
                                setEslDataBezug(data.getZaehlerstandBezug());
                            }
                        }
                        sdatData.put(milliSceonds, data);
                    }
                }
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
    }


    public void readESL() {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        File[] files = new File("./src/main/java/XMLfiles/ESL-Files").listFiles();
        DocumentBuilder builder = null;

        try {
            builder = factory.newDocumentBuilder();

            for (File filname : files) {
                Document document = builder.parse(filname);
                document.getDocumentElement().normalize();

                NodeList observationList = document.getElementsByTagName("ValueRow");

                double value1 = 0;
                double value2 = 0;
                for (int i = 0; i < observationList.getLength(); i++) {
                    Node observation = observationList.item(i);

                    if (observation.getNodeType() == Node.ELEMENT_NODE) {
                        Element observationElement = (Element) observation;
                        if (observationElement.getAttribute("obis").equals("1-1:1.8.1") || observationElement.getAttribute("obis").equals("1-1:1.8.2")) {

                            value1= value1 + Double.parseDouble(observationElement.getAttribute("value"));
                        }else if (observationElement.getAttribute("obis").equals("1-1:2.8.1") || observationElement.getAttribute("obis").equals("1-1:2.8.2")){

                            value2 = value2 + Double.parseDouble(observationElement.getAttribute("value"));
                        }
                    }
                }
                eslDataBezug = value1;
                eslDataEinspeisung = value2;
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
    }

    public long convertDate(Element intervalElement) {
        String dateS = intervalElement.getElementsByTagName("rsm:StartDateTime").item(0).getTextContent().split("T")[0];
        String timeS = intervalElement.getElementsByTagName("rsm:StartDateTime").item(0).getTextContent().split("T")[1];
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

    public TreeMap<Long, Data> getSdatData() {
        return sdatData;
    }

    public void setSdatData(TreeMap<Long, Data> sdatData) {
        this.sdatData = sdatData;
    }

    public static void setFileHandler(FileHandler fileHandler) {
        FileHandler.fileHandler = fileHandler;
    }

    public long getTempTime() {
        return tempTime;
    }

    public void setTempTime(long tempTime) {
        this.tempTime = tempTime;
    }

    public double getTempData() {
        return tempData;
    }

    public void setTempData(double tempData) {
        this.tempData = tempData;
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

    public static FileHandler getFileHandler() {
        if (fileHandler == null) fileHandler = new FileHandler();
        return fileHandler;
    }
}
