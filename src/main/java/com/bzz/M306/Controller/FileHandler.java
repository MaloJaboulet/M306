package com.bzz.M306.Controller;

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

public class FileHandler {


    public static void main(String[] args) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            File file =
                    new File("./src/main/java/XMLfiles/SDAT-Files/20190313_093127_12X-0000001216-O_E66_12X-LIPPUNEREM-T_ESLEVU121963_-279617263.xml");

            Document document = builder.parse(file);
            document.getDocumentElement().normalize();

            NodeList observationList = document.getElementsByTagName("rsm:Observation");
            for (int i = 0; i < observationList.getLength(); i++) {
                Node observation = observationList.item(i);

                if (observation.getNodeType() == Node.ELEMENT_NODE) {
                    Element observationElement = (Element) observation;
                    int sequence = Integer.parseInt(observationElement.getElementsByTagName("rsm:Sequence").item(0).getTextContent());
                    double value = Double.parseDouble(observationElement.getElementsByTagName("rsm:Volume").item(0).getTextContent());

                    System.out.println("Sequenz : "+ sequence);
                    System.out.println("Value: "+ value);
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
}
