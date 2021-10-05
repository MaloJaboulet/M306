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


import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.ui.ApplicationFrame;
import org.jfree.chart.ui.RectangleInsets;
import org.jfree.chart.ui.UIUtils;
import org.jfree.data.time.Month;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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

    /**
     * A demonstration application showing how to create a simple time series
     * chart.  This example uses monthly data.
     *
     * @param title the frame title.
     */
    public GUIVerbrauchsdiagramm(String title) {
        super(title);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        ChartPanel chartPanel = (ChartPanel) createDemoPanel();
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
                    createDatasetVerbrauchsdiagramm();
                    chart.setTitle("Stromzählerübersicht");
                }
                if (zaehlerDiagramm.isSelected()) {
                    System.out.println("Zählerdiagramm selected");
                    dataset.removeAllSeries();
                    createDatasetZaehlerdiagramm();
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
            renderer.setDefaultShapesVisible(true);
            renderer.setDefaultShapesFilled(true);
            renderer.setDrawSeriesLineAsPath(true);
        }

        DateAxis axis = (DateAxis) plot.getDomainAxis();
        axis.setDateFormatOverride(new SimpleDateFormat("MMM-yyyy"));

        return chart;

    }

    /**
     * Creates a dataset, consisting of two series of monthly data.
     *
     * @return The dataset.
     */
    private static XYDataset createDatasetVerbrauchsdiagramm() {
        Date date = new Date();
        long test = date.getTime();
        System.out.println(test);
        Calendar cal = Calendar.getInstance();
        int month = cal.get(Calendar.MONTH);

        TimeSeries s1 = new TimeSeries("Kauf von Strom");
        s1.add(new Month(2, 2001), 181.8);
        s1.add(new Month(3, 2001), 167.3);
        s1.add(new Month(4, 2001), 153.8);
        s1.add(new Month(5, 2001), 167.6);
        s1.add(new Month(6, 2001), 158.8);
        s1.add(new Month(7, 2001), 148.3);
        s1.add(new Month(8, 2001), 153.9);
        s1.add(new Month(9, 2001), 142.7);
        s1.add(new Month(10, 2001), 123.2);
        s1.add(new Month(11, 2001), 131.8);
        s1.add(new Month(12, 2001), 139.6);
        s1.add(new Month(1, 2002), 142.9);
        s1.add(new Month(2, 2002), 138.7);
        s1.add(new Month(3, 2002), 137.3);
        s1.add(new Month(4, 2002), 143.9);
        s1.add(new Month(5, 2002), 139.8);
        s1.add(new Month(6, 2002), 137.0);
        s1.add(new Month(month, 2002), 132.8);

        TimeSeries s2 = new TimeSeries("Verkauf von Strom");
        s2.add(new Month(2, 2001), 129.6);
        s2.add(new Month(3, 2001), 123.2);
        s2.add(new Month(4, 2001), 117.2);
        s2.add(new Month(5, 2001), 124.1);
        s2.add(new Month(6, 2001), 122.6);
        s2.add(new Month(7, 2001), 119.2);
        s2.add(new Month(8, 2001), 116.5);
        s2.add(new Month(9, 2001), 112.7);
        s2.add(new Month(10, 2001), 101.5);
        s2.add(new Month(11, 2001), 106.1);
        s2.add(new Month(12, 2001), 110.3);
        s2.add(new Month(1, 2002), 111.7);
        s2.add(new Month(2, 2002), 111.0);
        s2.add(new Month(3, 2002), 109.6);
        s2.add(new Month(4, 2002), 113.2);
        s2.add(new Month(5, 2002), 111.6);
        s2.add(new Month(6, 2002), 108.8);
        s2.add(new Month(7, 2002), 101.6);


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

    private static XYDataset createDatasetZaehlerdiagramm() {

        TimeSeries s3 = new TimeSeries("Zählerdiagramm");
        s3.add(new Month(2, 2001), 150.8);
        s3.add(new Month(3, 2001), 160.3);
        s3.add(new Month(4, 2001), 163.8);
        s3.add(new Month(5, 2001), 167.6);
        s3.add(new Month(6, 2001), 168.8);
        s3.add(new Month(7, 2001), 170.3);
        s3.add(new Month(8, 2001), 173.9);
        s3.add(new Month(9, 2001), 174.7);
        s3.add(new Month(10, 2001), 176.2);
        s3.add(new Month(11, 2001), 180.8);
        s3.add(new Month(12, 2001), 189.6);
        s3.add(new Month(1, 2002), 190.9);
        s3.add(new Month(2, 2002), 191.7);
        s3.add(new Month(3, 2002), 192.3);
        s3.add(new Month(4, 2002), 192.9);
        s3.add(new Month(5, 2002), 193.8);
        s3.add(new Month(6, 2002), 194.0);
        s3.add(new Month(7, 2002), 194.8);

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
    public static JPanel createDemoPanel() {
        chart = createChart(createDatasetVerbrauchsdiagramm());
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

        GUIVerbrauchsdiagramm demo = new GUIVerbrauchsdiagramm(
                "Stromzähler");
        //demo.pack();
        UIUtils.centerFrameOnScreen(demo);
        demo.setVisible(true);

    }

    public JFrame getJFrame() {
        return this;
    }

}
