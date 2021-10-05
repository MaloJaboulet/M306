package View;/* =========================
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


import com.bzz.M306.Data.Data;
import com.bzz.M306.Data.EnergyData;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.event.PlotChangeEvent;
import org.jfree.chart.event.PlotChangeListener;
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

    private JPanel pHeader = new JPanel(new GridLayout(1, 3));
    private JPanel pRadiobuttons = new JPanel(new GridLayout(2, 1));
    private JPanel pSkipDay = new JPanel();
    private JPanel pDate = new JPanel(new GridLayout(2, 1));
    private JPanel pCsv = new JPanel(new GridLayout(3, 3));

    private JButton bExportCSV = new JButton("Export to CSV");
    private JButton bBackwards = new JButton("<");
    private JButton bForwards = new JButton(">");
    private ButtonGroup buttonGroup = new ButtonGroup();
    private JRadioButton verbrauchDiagramm = new JRadioButton("Verbrauchsdiagramm");
    private JRadioButton zaehlerDiagramm = new JRadioButton("Zählerstandsdiagramm");

    private EnergyData energyData;
    private long centerDay;

    /**
     * A demonstration application showing how to create a simple time series
     * chart.  This example uses monthly data.
     *
     * @param title the frame title.
     */
    public GUIVerbrauchsdiagramm(String title, EnergyData energyData) {
        super(title);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        ChartPanel chartPanel = (ChartPanel) createPanel(energyData);
        this.energyData = energyData;
        this.centerDay = 0;
        // chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));

        //bExportCSV.setPreferredSize(new Dimension(1,1));

        buttonGroup.add(verbrauchDiagramm);
        buttonGroup.add(zaehlerDiagramm);
        pRadiobuttons.add(verbrauchDiagramm);
        pRadiobuttons.add(zaehlerDiagramm);
        pRadiobuttons.setBorder(new EmptyBorder(0, 20, 0, 0));
        verbrauchDiagramm.setSelected(true);

        pSkipDay.add(bBackwards, BorderLayout.WEST);
        pSkipDay.add(lDay, BorderLayout.CENTER);
        pSkipDay.add(bForwards, BorderLayout.EAST);

        pDate.add(lCurrentDate);
        pDate.add(pSkipDay);

        pCsv.add(lEmpty);
        pCsv.add(bExportCSV);


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

        ActionListener actionListenerCSV = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //export data to csv-file
            }
        };

        chart.getXYPlot().addChangeListener(new PlotChangeListener() {
            @Override
            public void plotChanged(PlotChangeEvent plotChangeEvent) {
               long upperBound = (long)plotChangeEvent.getPlot().getChart().getXYPlot().getDomainAxis().getUpperBound();
               long lowerBound = (long)plotChangeEvent.getPlot().getChart().getXYPlot().getDomainAxis().getLowerBound();
                System.out.println("upper: " +upperBound);
                System.out.println("lower: " +lowerBound);
               long center = (upperBound + lowerBound)/2;
               centerDay = center;
                System.out.println(centerDay);
               // chart.getPlot().getChart().getXYPlot().getDomainAxis().
            }
        });


        verbrauchDiagramm.addActionListener(actionListenerDiagramm);
        zaehlerDiagramm.addActionListener(actionListenerDiagramm);
        bExportCSV.addActionListener(actionListenerCSV);
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
        System.out.println(test);
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

        TimeSeries s3 = new TimeSeries("Zählerdiagramm");
        for (Map.Entry<Long, Data> entry : energyData.getSdatData().entrySet()) {
            s3.add(new FixedMillisecond(entry.getKey()), entry.getValue().getZaehlerstandBezug());
        }



        // ******************************************************************
        //  More than 150 demo applications are included with the JFreeChart
        //  Developer Guide...for more information, see:
        //
        //  >   http://www.object-refinery.com/jfreechart/guide.html
        //
        // ******************************************************************

        dataset.addSeries(s3);

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
        panel.setFillZoomRectangle(true);
        panel.setMouseWheelEnabled(true);
        return panel;
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
