
package beans;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;

public class LineChartBean extends JPanel {
    private DefaultCategoryDataset dataset;
    private JFreeChart chart;
    private ChartPanel chartPanel;

    public LineChartBean() {
        setLayout(new BorderLayout());
        dataset = new DefaultCategoryDataset();
        chart = ChartFactory.createLineChart(
                "Sample Chart", "Category", "Value", dataset);
        chartPanel = new ChartPanel(chart);
        add(chartPanel, BorderLayout.CENTER);
    }

    public void setChartTitle(String title) {
        chart.setTitle(title);
    }

    public void setAxisLabels(String xAxis, String yAxis) {
        chart.getCategoryPlot().getDomainAxis().setLabel(xAxis);
        chart.getCategoryPlot().getRangeAxis().setLabel(yAxis);
    }

    public void addData(String series, String category, Number value) {
        dataset.addValue(value, series, category);
    }

    public void clearData() {
        dataset.clear();
    }

    public JFreeChart getChart() {
        return chart;
    }
}

