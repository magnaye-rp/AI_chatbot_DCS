
package beans;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.beans.*;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

public class singleBarChart extends JPanel implements Serializable {

    private final Map<Color, RevenueSegment> rawSegments = new LinkedHashMap<>();
    private final Map<Color, RevenueSegment> revenueSegments = new LinkedHashMap<>();
    private String tooltipText = "";

    public singleBarChart() {
        setPreferredSize(new Dimension(400, 40));
        setupTooltipListener();
    }

    private void setupTooltipListener() {
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int x = 0;
                int mouseX = e.getX();
                int width = getWidth();

                for (Map.Entry<Color, RevenueSegment> entry : revenueSegments.entrySet()) {
                    int segmentWidth = (int) (width * (entry.getValue().getPercentage() / 100.0));
                    if (mouseX >= x && mouseX < x + segmentWidth) {
                        tooltipText = entry.getValue().getLabel() + ": " + entry.getValue().getPercentage() + "%";
                        setToolTipText(tooltipText);
                        return;
                    }
                    x += segmentWidth;
                }
                setToolTipText(null);
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int width = getWidth();
        int height = getHeight();
        int startX = 0;

        for (Map.Entry<Color, RevenueSegment> entry : revenueSegments.entrySet()) {
            Color color = entry.getKey();
            RevenueSegment segment = entry.getValue();
            int segmentWidth = (int) (width * (segment.getPercentage() / 100.0));

            g.setColor(color);
            g.fillRect(startX, 0, segmentWidth, height);

            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.PLAIN, 12));
            FontMetrics fm = g.getFontMetrics();
            int textWidth = fm.stringWidth(segment.getLabel());
            int textX = startX + (segmentWidth - textWidth) / 2;
            int textY = (height + fm.getAscent()) / 2 - 2;

            if (segmentWidth > textWidth + 10) {
                g.drawString(segment.getLabel(), textX, textY);
            }

            startX += segmentWidth;
        }

        g.setColor(Color.BLACK);
        g.drawRect(0, 0, width - 1, height - 1);
    }

    public void addRevenueSegment(Color color, String label, double value) {
        rawSegments.put(color, new RevenueSegment(label, value));
        computePercentages();
        repaint();
    }

    public void clearRevenueSegments() {
        rawSegments.clear();
        revenueSegments.clear();
        repaint();
    }

    public Map<Color, RevenueSegment> getRevenueSegments() {
        return revenueSegments;
    }

    private void computePercentages() {
        double total = rawSegments.values().stream().mapToDouble(RevenueSegment::getValue).sum();
        revenueSegments.clear();

        for (Map.Entry<Color, RevenueSegment> entry : rawSegments.entrySet()) {
            Color color = entry.getKey();
            RevenueSegment segment = entry.getValue();
            int percentage = total == 0 ? 0 : (int) Math.round((segment.getValue() / total) * 100);
            revenueSegments.put(color, new RevenueSegment(segment.getLabel(), percentage, segment.getValue()));
        }
    }

    public static class RevenueSegment {
        private final String label;
        private final int percentage;
        private final double value;

        public RevenueSegment(String label, double value) {
            this.label = label;
            this.value = value;
            this.percentage = 0;
        }

        public RevenueSegment(String label, int percentage, double value) {
            this.label = label;
            this.percentage = percentage;
            this.value = value;
        }

        public String getLabel() {
            return label;
        }

        public int getPercentage() {
            return percentage;
        }

        public double getValue() {
            return value;
        }
    }
}
