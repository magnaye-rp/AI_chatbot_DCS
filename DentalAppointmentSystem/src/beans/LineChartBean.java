package beans;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;
import org.jfree.chart.title.LegendTitle;

public class LineChartBean extends JPanel {
    private DefaultCategoryDataset dataset;
    private JFreeChart chart;
    private ChartPanel chartPanel;
    private LineAndShapeRenderer renderer;

    public LineChartBean() {
        setLayout(new BorderLayout());
        dataset = new DefaultCategoryDataset();
        chart = ChartFactory.createLineChart(
                "Sample Chart", "Category", "Value", dataset);
        chartPanel = new ChartPanel(chart);

        Color backgroundColor = new Color(57, 62, 70);
        Color white = Color.WHITE;

        // Background colors
        chart.setBackgroundPaint(backgroundColor);
        chartPanel.setBackground(backgroundColor);
        setBackground(backgroundColor);

        CategoryPlot plot = chart.getCategoryPlot();
        renderer = new LineAndShapeRenderer();
        plot.setRenderer(renderer);

        plot.setBackgroundPaint(backgroundColor);
        plot.setDomainGridlinePaint(white);
        plot.setRangeGridlinePaint(white);
        
        
        ValueAxis yAxis = plot.getRangeAxis();
        yAxis.setRange(0.0, 10.0); 

        // Axis text colors
        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setLabelPaint(white);
        domainAxis.setTickLabelPaint(white);

        ValueAxis rangeAxis = plot.getRangeAxis();
        rangeAxis.setLabelPaint(white);
        rangeAxis.setTickLabelPaint(white);

        // Title text color
        TextTitle chartTitle = new TextTitle(chart.getTitle().getText(), new Font("SansSerif", Font.BOLD, 18));
chartTitle.setPaint(white);
chart.setTitle(chartTitle);

        LegendTitle legend = chart.getLegend();
        legend.setBackgroundPaint(new Color(57, 62, 70));


        // Legend text color
        if (chart.getLegend() != null) {
            chart.getLegend().setItemPaint(white);
        }

        add(chartPanel, BorderLayout.CENTER);
    }

    public void setChartTitle(String title) {
    TextTitle chartTitle = new TextTitle(title, new Font("SansSerif", Font.BOLD, 18));
    chartTitle.setPaint(Color.WHITE);
    chart.setTitle(chartTitle);
}

    public void setAxisLabels(String xAxis, String yAxis) {
        CategoryPlot plot = chart.getCategoryPlot();
        plot.getDomainAxis().setLabel(xAxis);
        plot.getRangeAxis().setLabel(yAxis);
    }

    public void addData(String series, String category, Number value) {
        dataset.addValue(value, series, category);
    }

    public void clearData() {
        dataset.clear();
    }

    public void setSeriesColor(int seriesIndex, Color color) {
        renderer.setSeriesPaint(seriesIndex, color);
    }

    public JFreeChart getChart() {
        return chart;
    }
}
