package com.bzz.M306.View;/* =========================
 * GUIVerbrauchsdiagramm.java
 * =========================
 *
 * (C) Copyright 2003-2021, by Object Refinery Limited.
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *   - Neither the name of the Object Refinery Limited nor the
 *     names of its contributors may be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL OBJECT REFINERY LIMITED BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */


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
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

/**
 * An example of a time series chart create using JFreeChart.  For the most
 * part, default settings are used, except that the renderer is modified to
 * show filled shapes (as well as lines) at each data point.
 */
public class GUIVerbrauchsdiagramm extends ApplicationFrame {

    private static final long serialVersionUID = 1L;

    private static TimeSeriesCollection dataset = new TimeSeriesCollection();
    private static JFreeChart chart;


    private JLabel lDay = new JLabel("Tag", SwingConstants.CENTER);
    private JLabel lCurrentDate = new JLabel("automatisch 15.03.21", SwingConstants.CENTER);
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
    private JTextField tDate = new JTextField("Datum eingeben:");

    private EnergyData energyData;

    /**
     * A demonstration application showing how to create a simple time series
     * chart.  This example uses monthly data.
     *
     * @param title the frame title.
     */
    public GUIVerbrauchsdiagramm(String title, EnergyData energyData) {
        super(title);
        this.energyData = energyData;
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        ChartPanel chartPanel = (ChartPanel) createPanel(energyData);

        // chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));

        //bExportCSV.setPreferredSize(new Dimension(1,1));

        buttonGroup.add(verbrauchDiagramm);
        buttonGroup.add(zaehlerDiagramm);
        pRadiobuttons.add(verbrauchDiagramm);
        pRadiobuttons.add(zaehlerDiagramm);
        pRadiobuttons.setBorder(new EmptyBorder(0, 20, 0, 0));
        verbrauchDiagramm.setSelected(true);


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
                EnergyData energyData = new EnergyData();
                TreeMap<Long, csvData> map = FileHandler.getFileHandler().getCSVData();
                try {
                    FileHandler.writeCSV(map);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                lEmpty.setText("Daten wurden in eine csv-File exportiert");
            }
        });

        bExportJSON.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EnergyData energyData = new EnergyData();
                TreeMap<Long, csvData> map = FileHandler.getFileHandler().getCSVData();
                FileHandler.saveJSON(map);
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
                if (e.getKeyCode() == 13){
                    //tDate.getText()
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });

        verbrauchDiagramm.addActionListener(actionListenerDiagramm);
        zaehlerDiagramm.addActionListener(actionListenerDiagramm);
    }


    /**
     * Creates a chart.
     *
     * @param dataset a dataset.
     * @return A chart.
     */
    private static JFreeChart createChart(XYDataset dataset) {

        chart = ChartFactory.createTimeSeriesChart(
                "Stromzählerübersicht",  // title
                "Datum",             // x-axis label
                "kWh",   // y-axis label
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

        System.out.println(axis.getRange());
        return chart;

    }

    /**
     * Creates a dataset, consisting of two series of monthly data.
     *
     * @return The dataset.
     */
    private static XYDataset createDatasetVerbrauchsdiagramm(EnergyData energyData) {
        Date date = new Date();
        long test = date.getTime();
        Calendar cal = Calendar.getInstance();
        int month = cal.get(Calendar.MONTH);

        TimeSeries s1 = new TimeSeries("Kauf von Strom");

        TreeMap<Long, Data> mapData = energyData.getSdatData();

        for (Map.Entry<Long, Data> entry : mapData.entrySet()) {
            s1.add(new FixedMillisecond(entry.getKey()), entry.getValue().getRelativBezug());
        }

        TimeSeries s2 = new TimeSeries("Verkauf von Strom");
        for (Map.Entry<Long, Data> entry : mapData.entrySet()) {
            s2.add(new FixedMillisecond(entry.getKey()), entry.getValue().getRelativeEinspeisung());
        }

        // ******************************************************************
        //  More than 150 demo applications are included with the JFreeChart
        //  Developer Guide...for more information, see:
        //
        //  >   http://www.object-refinery.com/jfreechart/guide.html
        //
        // ******************************************************************

        dataset.addSeries(s1);
        dataset.addSeries(s2);

        return dataset;

    }

    private static XYDataset createDatasetZaehlerdiagramm(EnergyData energyData) {

        TimeSeries s3 = new TimeSeries("Bezug-Zählerdiagramm");
        for (Map.Entry<Long, Data> entry : energyData.getSdatData().entrySet()) {
            s3.add(new FixedMillisecond(entry.getKey()), (int) entry.getValue().getZaehlerstandBezug());
        }

        TimeSeries s4 = new TimeSeries("Einspeisung-Zählerdiagramm");
        for (Map.Entry<Long, Data> entry : energyData.getSdatData().entrySet()) {
            s4.add(new FixedMillisecond(entry.getKey()), (int) entry.getValue().getZaehlerstandEinspeisung());
        }


        // ******************************************************************
        //  More than 150 demo applications are included with the JFreeChart
        //  Developer Guide...for more information, see:
        //
        //  >   http://www.object-refinery.com/jfreechart/guide.html
        //
        // ******************************************************************

        dataset.addSeries(s3);
        dataset.addSeries(s4);


        return dataset;

    }


    /**
     * Creates a panel for the demo (used by SuperDemo.java).
     *
     * @return A panel.
     */
    public static JPanel createPanel(EnergyData energyData) {
        chart = createChart(createDatasetVerbrauchsdiagramm(energyData));

        ChartPanel panel = new ChartPanel(chart, false);
        panel.addChartMouseListener(new ChartMouseListener() {
            @Override
            public void chartMouseClicked(ChartMouseEvent chartMouseEvent) {
                XYPlot plot = chartMouseEvent.getChart().getXYPlot();
                long upperBound = (long) plot.getDomainAxis().getUpperBound();
                long lowerBound = (long) plot.getDomainAxis().getLowerBound();
                //System.out.println("upper: " +upperBound);
                //System.out.println("lower: " +lowerBound);
                long center = (upperBound + lowerBound) / 2;

                System.out.println(center);
                //DateAxis dateAxis =  (DateAxis) plot.getDomainAxis();
                // dateAxis.setRange((centerDay - 86400000),(centerDay+86400000));
                plot.getDomainAxis().setUpperBound(upperBound + 86400000);
                plot.getDomainAxis().setLowerBound(lowerBound + 86400000);


            }

            @Override
            public void chartMouseMoved(ChartMouseEvent chartMouseEvent) {

            }
        });
        panel.setFillZoomRectangle(true);
        panel.setMouseWheelEnabled(true);
        return panel;
    }

    public void changeDate(long milliSeconds, boolean hoch) {
        XYPlot plot = chart.getXYPlot();

        long upperBound = (long) plot.getDomainAxis().getUpperBound();
        long lowerBound = (long) plot.getDomainAxis().getLowerBound();
        long center = (upperBound + lowerBound) / 2;


        long centerday;
        if (hoch) {
            centerday = (center + milliSeconds);
        } else {
            centerday = (center - milliSeconds);
        }

        DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        lCurrentDate.setText(df.format(new Date(centerday)));

        plot.getDomainAxis().setUpperBound(centerday + 86400000);
        plot.getDomainAxis().setLowerBound(centerday - 86400000);
    }

    public EnergyData getEnergyData() {
        return energyData;
    }

    /**
     * Starting point for the demonstration application.
     *
     * @param args ignored.
     */
    public static void main(String[] args) {
        EnergyData energyData = new EnergyData();
        GUIVerbrauchsdiagramm demo = new GUIVerbrauchsdiagramm(
                "Stromzähler", energyData);

        //demo.pack();
        UIUtils.centerFrameOnScreen(demo);
        demo.setVisible(true);

    }

    public JFrame getJFrame() {
        return this;
    }

}
