package org.example;

import javax.swing.*;
import java.awt.*;
import java.util.List;

// JFreeChart Imports
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.CategoryPlot;

public class TeamChartDialog extends JDialog {

    // Gunakan MainApp.TeamStats
    public TeamChartDialog(JFrame parent, String teamName, List<MainApp.TeamStats> stats) {
        super(parent, "Performa Poin: " + teamName, true);
        setSize(800, 600);
        setLocationRelativeTo(parent);

        DefaultCategoryDataset dataset = createDataset(stats);
        JFreeChart chart = createChart(teamName, dataset);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(750, 550));

        add(chartPanel, BorderLayout.CENTER);
        ((JComponent) getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }

    // Gunakan MainApp.TeamStats
    private DefaultCategoryDataset createDataset(List<MainApp.TeamStats> stats) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (MainApp.TeamStats stat : stats) {
            // stat.getPoints() dan stat.getMatchDay() sekarang berfungsi
            dataset.addValue(stat.getPoints(), "Total Poin", "Match " + stat.getMatchDay());
        }
        return dataset;
    }

    private JFreeChart createChart(String teamName, DefaultCategoryDataset dataset) {
        JFreeChart lineChart = ChartFactory.createLineChart(
                "Progres Poin Premier League (Match 13-15) - " + teamName,
                "Match Day",
                "Total Poin",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        // Desain Chart
        CategoryPlot plot = lineChart.getCategoryPlot();
        plot.setBackgroundPaint(new Color(245, 245, 245));
        plot.setRangeGridlinePaint(new Color(200, 200, 200));

        plot.getRenderer().setSeriesPaint(0, new Color(0, 77, 153));
        plot.getRenderer().setSeriesStroke(0, new BasicStroke(3.0f));

        lineChart.getTitle().setFont(new Font("SansSerif", Font.BOLD, 18));

        return lineChart;
    }
}