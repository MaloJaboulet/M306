package com.bzz.M306.View;

import com.bzz.M306.Controller.FileHandler;
import com.bzz.M306.Data.Data;
import com.bzz.M306.Data.EnergyData;
import com.bzz.M306.Data.csvData;
import org.jfree.chart.*;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.ui.ApplicationFrame;
import org.jfree.chart.ui.RectangleInsets;
import org.jfree.chart.ui.UIUtils;
import org.jfree.data.time.*;
import org.jfree.data.xy.XYDataset;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author Sarah Frischknecht, Malo Jaboulet
 * @version 1.0
 * @since 05.10.2021
 * <p>
 * Das GUI der Applikation. Es werden die Daten der Files in mehrere Graphen umgewandelt.
 * Man kann in die Graphen hinein zoomen und genauere Werte bekommen.
 */
public class GUIVerbrauchsdiagramm extends ApplicationFrame {


    private static TimeSeriesCollection dataset = new TimeSeriesCollection();
    private static JFreeChart chart;


    private JLabel lDay = new JLabel("Tag", SwingConstants.CENTER);
    private JLabel lCurrentDate = new JLabel("01.06.20", SwingConstants.CENTER);
    private JLabel lEmpty = new JLabel("");
    private JLabel lEmpty2 = new JLabel("");

    private JPanel pHeader = new JPanel(new GridLayout(1, 3, 10, 0));
    private JPanel pRadiobuttons = new JPanel(new GridLayout(2, 1));
    private JPanel pSkipDay = new JPanel();
    private JPanel pDate = new JPanel(new GridLayout(3, 1));
    private JPanel pCsv = new JPanel(new GridLayout(3, 3));

    private JButton bExportCSV = new JButton("Export to CSV");
    private JButton bExportJSON = new JButton("Export to JSON");
    private JButton bBackwards = new JButton("<");
    private JButton bForwards = new JButton(">");
    private JButton bBackwardsW = new JButton("<<");
    private JButton bForwardsW = new JButton(">>");
    private JButton bBackwardsM = new JButton("<<<");
    private JButton bForwardsM = new JButton(">>>");
    private ButtonGroup buttonGroup = new ButtonGroup();
    private JRadioButton verbrauchDiagramm = new JRadioButton("Verbrauchsdiagramm");
    private JRadioButton zaehlerDiagramm = new JRadioButton("Zählerstandsdiagramm");
    private JTextField tDate = new JTextField("Datum:");

    private EnergyData energyData;

    /**
     * Der Konstruktor, er zeichnet das ganze GUI.
     *
     * @param title      der Titel der Applikation
     * @param energyData die Daten
     */
    public GUIVerbrauchsdiagramm(String title, EnergyData energyData) {
        super(title);
        this.energyData = energyData;
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        ChartPanel chartPanel = (ChartPanel) createPanel(energyData);

        buttonGroup.add(verbrauchDiagramm);
        buttonGroup.add(zaehlerDiagramm);
        pRadiobuttons.add(verbrauchDiagramm);
        pRadiobuttons.add(zaehlerDiagramm);
        pRadiobuttons.setBorder(new EmptyBorder(0, 20, 0, 0));
        verbrauchDiagramm.setSelected(true);
        tDate.setForeground(Color.gray);

        pSkipDay.setLayout(new GridLayout(1, 7, 5, 0));
        pSkipDay.add(bBackwardsM);
        pSkipDay.add(bBackwardsW);
        pSkipDay.add(bBackwards);
        pSkipDay.add(tDate);
        pSkipDay.add(bForwards);
        pSkipDay.add(bForwardsW);
        pSkipDay.add(bForwardsM);

        pDate.add(lCurrentDate);
        pDate.add(lEmpty2);
        pDate.add(pSkipDay);


        pCsv.add(bExportCSV);
        pCsv.add(lEmpty);
        pCsv.add(bExportJSON);


        pHeader.add(pRadiobuttons);
        pHeader.add(pDate);
        pHeader.add(pCsv);

        this.add(chartPanel, BorderLayout.CENTER);
        this.add(pHeader, BorderLayout.NORTH);

        //Actionlistener
        ActionListener actionListenerDiagramm = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (verbrauchDiagramm.isSelected()) {
                    System.out.println("Verbrauchsdiagramm selected");
                    dataset.removeAllSeries();
                    createDatasetVerbrauchsdiagramm(energyData);
                    chart.setTitle("Stromzählerübersicht");
                }
                if (zaehlerDiagramm.isSelected()) {
                    System.out.println("Zählerdiagramm selected");
                    dataset.removeAllSeries();
                    createDatasetZaehlerdiagramm(energyData);
                    chart.setTitle("Zählerstand");
                }
            }
        };

        bExportCSV.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TreeMap<Long, csvData> map = FileHandler.getFileHandler().getCSVData();
                try {
                    JFileChooser fileChooser = new JFileChooser("./CSV");
                    fileChooser.setDialogTitle("CSV-Datei speichern");
                    fileChooser.setFileFilter(new FileNameExtensionFilter("*.csv","csv"));

                    int userSelection = fileChooser.showSaveDialog(getFrame());

                    if (userSelection == JFileChooser.APPROVE_OPTION) {
                        File fileToSave = fileChooser.getSelectedFile();
                        String path = fileToSave.getPath();
                        if (!path.contains(".csv")){
                            path = path +".csv";
                        }
                        FileHandler.writeCSV(map,path);
                        System.out.println("Save as file: " + fileToSave.getAbsolutePath());
                    }


                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                lEmpty.setText("Daten wurden in ein CSV-File exportiert");
            }
        });

        bExportJSON.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TreeMap<Long, csvData> map = FileHandler.getFileHandler().getCSVData();
                FileHandler.saveJSON(map);

                try {
                    JFileChooser fileChooser = new JFileChooser("./JSON");
                    fileChooser.setDialogTitle("JSON-Datei speichern");
                    fileChooser.setFileFilter(new FileNameExtensionFilter("*.json","json"));

                    int userSelection = fileChooser.showSaveDialog(getFrame());

                    if (userSelection == JFileChooser.APPROVE_OPTION) {
                        File fileToSave = fileChooser.getSelectedFile();
                        String path = fileToSave.getPath();
                        if (!path.contains(".json")){
                            path = path +".json";
                        }
                        FileWriter fileWriter = new FileWriter(path);
                        fileWriter.write(FileHandler.saveJSON(map));
                        fileWriter.flush();
                        fileWriter.close();
                        System.out.println("Save as file: " + fileToSave.getAbsolutePath());
                    }


                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                lEmpty.setText("Daten wurden zum JSON exportiert \n" + "https://api.npoint.io/0dc854da1619aca3be45");
            }
        });

        bForwards.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changeDate(86400000, true);
            }
        });

        bBackwards.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changeDate(86400000, false);
            }
        });
        bBackwardsW.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changeDate(604800000, false);
            }
        });
        bForwardsW.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changeDate(604800000, true);
            }
        });
        bBackwardsM.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changeDate(2629800000L, false);
            }
        });
        bForwardsM.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changeDate(2629800000L, true);
            }
        });

        /*chart.getXYPlot().addChangeListener(new PlotChangeListener() {
            @Override
            public void plotChanged(PlotChangeEvent plotChangeEvent) {
                XYPlot plot = plotChangeEvent.getPlot().getChart().getXYPlot();
                PlotChangeListener plotChangeListener = this;

                plot.removeChangeListener(plotChangeListener);
                long upperBound = (long) plot.getDomainAxis().getUpperBound();
                long lowerBound = (long) plot.getDomainAxis().getLowerBound();

                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(upperBound / 900000 * 900000);
                upperBound = calendar.getTimeInMillis();

                calendar.setTimeInMillis(lowerBound / 900000 * 900000);
                lowerBound = calendar.getTimeInMillis();
                System.out.println(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date(lowerBound)));

                System.out.println(getEnergyData().getSdatData().containsKey(lowerBound));

                double highestValue = 0;
                if (getEnergyData().getSdatData().containsKey(lowerBound)) {
                    for (long i = lowerBound; i < upperBound; i = i + 86400000) {
                        if (getEnergyData().getSdatData().get(i).getRelativBezug() > highestValue) {
                            highestValue = getEnergyData().getSdatData().get(i).getRelativBezug();

                        }
                        if (getEnergyData().getSdatData().get(i).getRelativeEinspeisung() > highestValue) {
                            highestValue = getEnergyData().getSdatData().get(i).getZaehlerstandEinspeisung();
                        }
                    }

                    System.out.println(highestValue);
                    System.out.println(highestValue + 1);

                    plot.getRangeAxis().setLowerBound(0);
                    plot.getRangeAxis().setUpperBound(highestValue + 1);
                }else {
                    plot.getRangeAxis().setLowerBound(0);
                    plot.getRangeAxis().setUpperBound(plot.getRangeAxis().getUpperBound());
                }
                plot.addChangeListener(plotChangeListener);

            }

        });
         */

        tDate.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                checkDate(e);
            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });

        tDate.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (tDate.getText().equals("Datum:")) {
                    tDate.setText("");
                    tDate.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (tDate.getText().isEmpty()) {
                    tDate.setForeground(Color.GRAY);
                    tDate.setText("Datum:");
                }
            }
        });


        verbrauchDiagramm.addActionListener(actionListenerDiagramm);
        zaehlerDiagramm.addActionListener(actionListenerDiagramm);
    }


    /**
     * Erstellt das Diagramm mit den Graphen.
     *
     * @param dataset die Daten
     * @return das Diagramm
     */
    private static JFreeChart createChart(XYDataset dataset) {

        chart = ChartFactory.createTimeSeriesChart(
                "Stromzählerübersicht",      // Titel
                "Datum",             // x-Achse label
                "kWh",              // y-Achse label
                dataset);

        chart.setBackgroundPaint(Color.WHITE);

        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.LIGHT_GRAY);
        plot.setDomainGridlinePaint(Color.WHITE);
        plot.setRangeGridlinePaint(Color.WHITE);
        plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
        plot.setDomainCrosshairVisible(true);
        plot.setRangeCrosshairVisible(true);

        XYItemRenderer r = plot.getRenderer();
        if (r instanceof XYLineAndShapeRenderer) {
            XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) r;
            renderer.setDefaultShapesVisible(false);
            renderer.setDefaultShapesFilled(false);
            renderer.setDrawSeriesLineAsPath(true);
        }

        DateAxis axis = (DateAxis) plot.getDomainAxis();
        axis.setDateFormatOverride(new SimpleDateFormat("dd-MM-yyyy"));

        return chart;

    }

    /**
     * Erstellt die Graphen zu den relativen Bezüge und Einspeisungen.
     *
     * @param energyData die Daten
     * @return die Daten für die Graphen
     */
    private static XYDataset createDatasetVerbrauchsdiagramm(EnergyData energyData) {
        TimeSeries s1 = new TimeSeries("Kauf von Strom");

        TreeMap<Long, Data> mapData = energyData.getSdatData();

        for (Map.Entry<Long, Data> entry : mapData.entrySet()) {
            s1.add(new FixedMillisecond(entry.getKey()), entry.getValue().getRelativBezug());
        }

        TimeSeries s2 = new TimeSeries("Verkauf von Strom");
        for (Map.Entry<Long, Data> entry : mapData.entrySet()) {
            s2.add(new FixedMillisecond(entry.getKey()), entry.getValue().getRelativeEinspeisung());
        }

        dataset.addSeries(s1);
        dataset.addSeries(s2);

        return dataset;

    }

    /**
     * Erstellt die Graphen zu den Zählerständen der Einspeisung und des Bezugs.
     *
     * @param energyData die Daten
     * @return die Daten für die Graphen
     */
    private static XYDataset createDatasetZaehlerdiagramm(EnergyData energyData) {

        TimeSeries s3 = new TimeSeries("Bezug-Zählerdiagramm");
        for (Map.Entry<Long, Data> entry : energyData.getSdatData().entrySet()) {
            s3.add(new FixedMillisecond(entry.getKey()), (int) entry.getValue().getZaehlerstandBezug());
        }

        TimeSeries s4 = new TimeSeries("Einspeisung-Zählerdiagramm");
        for (Map.Entry<Long, Data> entry : energyData.getSdatData().entrySet()) {
            s4.add(new FixedMillisecond(entry.getKey()), (int) entry.getValue().getZaehlerstandEinspeisung());
        }

        dataset.addSeries(s3);
        dataset.addSeries(s4);

        return dataset;

    }


    /**
     * Erstellt ein Panel für das Diagramm.
     *
     * @param energyData die Daten
     * @return ein Panel
     */
    public static JPanel createPanel(EnergyData energyData) {
        chart = createChart(createDatasetVerbrauchsdiagramm(energyData));

        ChartPanel panel = new ChartPanel(chart, false);

        panel.setFillZoomRectangle(true);
        panel.setMouseWheelEnabled(true);
        return panel;
    }

    /**
     * Verändert das Datum, dass sich in der Mitte des Graphen befindet, somit kann pro Tag/Woche/Monat gewechselt werden.
     *
     * @param milliSeconds um wie viel sich der Graph verschieben muss
     * @param hoch         ob das Datum hoch oder runter geht
     */
    public void changeDate(long milliSeconds, boolean hoch) {
        XYPlot plot = chart.getXYPlot();

        long upperBound = (long) plot.getDomainAxis().getUpperBound();
        long lowerBound = (long) plot.getDomainAxis().getLowerBound();
        long center = (upperBound + lowerBound) / 2; //Mitte des Graphen

        long centerday;
        long ersterTag = energyData.getSdatData().firstEntry().getKey() - 86400000;
        long letzterTag = energyData.getSdatData().lastEntry().getKey() + 86400000;

        if (hoch) {
            centerday = (center + milliSeconds);
        } else {
            centerday = (center - milliSeconds);
        }

        DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        lCurrentDate.setText(df.format(new Date(centerday)));

        if ((centerday - 86400000) > ersterTag && (centerday +86400000) < letzterTag ) {
            //Zoomt zu diesem Tag
            plot.getDomainAxis().setUpperBound(centerday + 86400000);
            plot.getDomainAxis().setLowerBound(centerday - 86400000);
        }
    }

    /**
     * Schaut, ob das eingegebene Datum gültig ist und obe es nicht ausserhalb der Daten ist.
     *
     * @param e das KeyEvent
     */
    public void checkDate(KeyEvent e) {
        tDate.setFocusable(true);
        if (e.getKeyChar() == 0x0A) {
            String text = tDate.getText();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");

            try {
                Date date = simpleDateFormat.parse(text);
                long ersterTag = energyData.getSdatData().firstEntry().getKey();
                long letzterTag = energyData.getSdatData().lastEntry().getKey();
                if (date.getTime() > ersterTag && date.getTime() < letzterTag) {
                    //Zoomt zu diesem Tag
                    chart.getXYPlot().getDomainAxis().setUpperBound(date.getTime() + 86400000);
                    chart.getXYPlot().getDomainAxis().setLowerBound(date.getTime() - 86400000);
                    lEmpty2.setText("");
                    DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
                    lCurrentDate.setText(df.format(date));
                } else {
                    lEmpty2.setText("Es wurde eine flasches Datum eingegeben.");
                    lEmpty2.setForeground(Color.red);
                }

            } catch (ParseException parseException) {
                lEmpty2.setText("Es wurde eine flasches Datum eingegeben.");
                lEmpty2.setForeground(Color.red);
            }
        }
    }

    public JTextField gettDate() {
        return tDate;
    }

    public JLabel getlEmpty2() {
        return lEmpty2;
    }

    public JFrame getFrame(){
        return this;
    }

    /**
     * Das Main. Hier wird das GUI gestartet.
     *
     * @param args
     */
    public static void main(String[] args) {
        EnergyData energyData = new EnergyData();
        GUIVerbrauchsdiagramm guiVerbrauchsdiagramm = new GUIVerbrauchsdiagramm(
                "Stromzähler", energyData);

        //guiVerbrauchsdiagramm.pack();
        UIUtils.centerFrameOnScreen(guiVerbrauchsdiagramm);
        guiVerbrauchsdiagramm.setVisible(true);

    }
}
