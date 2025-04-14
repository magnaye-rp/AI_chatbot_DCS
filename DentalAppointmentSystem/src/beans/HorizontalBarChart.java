package beans;

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
import java.util.HashMap;
import java.util.Map;

public class HorizontalBarChart extends JPanel implements Serializable {

    private DefaultCategoryDataset dataset;
    private JFreeChart chart;
    private final Map<String, Paint> weekdayColors = new HashMap<>();

    public HorizontalBarChart() {
        setLayout(new BorderLayout());
        dataset = new DefaultCategoryDataset();

        chart = ChartFactory.createBarChart(
                "Horizontal Bar Chart",
                "Category",
                "Value",
                dataset,
                PlotOrientation.HORIZONTAL,
                true, true, false
        );

        Color backgroundColor = new Color(57, 62, 70);
        Color white = Color.WHITE;

        chart.setBackgroundPaint(backgroundColor);
        setBackground(backgroundColor);

        // Set title color
        chart.getTitle().setPaint(white);

        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(backgroundColor);
        plot.setDomainGridlinePaint(white);
        plot.setRangeGridlinePaint(white);

        // Set axis label and tick colors
        CategoryAxis domainAxis = plot.getDomainAxis();
        ValueAxis rangeAxis = plot.getRangeAxis();
        domainAxis.setLabelPaint(white);
        domainAxis.setTickLabelPaint(white);
        rangeAxis.setLabelPaint(white);
        rangeAxis.setTickLabelPaint(white);

        // Custom renderer for weekday colors
        BarRenderer renderer = new BarRenderer() {
            @Override
            public Paint getItemPaint(int row, int column) {
                String category = (String) dataset.getColumnKey(column);
                return weekdayColors.getOrDefault(category, super.getItemPaint(row, column));
            }
        };
        plot.setRenderer(renderer);

        // Add chart panel
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setBackground(backgroundColor);
        add(chartPanel, BorderLayout.CENTER);

        // Assign custom colors for each weekday
        weekdayColors.put("Monday", new Color(255, 99, 132));
        weekdayColors.put("Tuesday", new Color(54, 162, 235));
        weekdayColors.put("Wednesday", new Color(255, 206, 86));
        weekdayColors.put("Thursday", new Color(75, 192, 192));
        weekdayColors.put("Friday", new Color(153, 102, 255));
        weekdayColors.put("Saturday", new Color(255, 159, 64));
        weekdayColors.put("Sunday", new Color(201, 203, 207));
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
