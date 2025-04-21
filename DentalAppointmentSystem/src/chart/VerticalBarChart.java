package chart;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;
import java.io.Serializable;
import org.jfree.chart.axis.CategoryLabelPositions;

public class VerticalBarChart extends JPanel implements Serializable {

    private DefaultCategoryDataset dataset;
    private JFreeChart chart;

    public VerticalBarChart() {
        setLayout(new BorderLayout());
        dataset = new DefaultCategoryDataset();

        chart = ChartFactory.createBarChart(
                "Monthly Revenue Per Service",
                "Service",
                "Revenue (PHP)",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false
        );

        Color backgroundColor = new Color(57, 62, 70);
        Color white = Color.WHITE;

        chart.setBackgroundPaint(backgroundColor);
        setBackground(backgroundColor);

        chart.getTitle().setPaint(white);

        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(backgroundColor);
        plot.setDomainGridlinePaint(white);
        plot.setRangeGridlinePaint(white);

        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setLabelPaint(white);
        domainAxis.setTickLabelPaint(white);
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);

        ValueAxis rangeAxis = plot.getRangeAxis();
        rangeAxis.setLabelPaint(white);
        rangeAxis.setTickLabelPaint(white);

        BarRenderer renderer = new BarRenderer();
        plot.setRenderer(renderer);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setBackground(backgroundColor);
        add(chartPanel, BorderLayout.CENTER);
    }

    public void addValue(Number value, String rowKey, String columnKey) {
        dataset.setValue(value, rowKey, columnKey);
    }

    public void clear() {
        dataset.clear();
    }

    public void setChartTitle(String title) {
        chart.setTitle(title);
    }

    public void setAxisLabels(String categoryLabel, String valueLabel) {
        chart.getCategoryPlot().getDomainAxis().setLabel(categoryLabel);
        chart.getCategoryPlot().getRangeAxis().setLabel(valueLabel);
    }
}
