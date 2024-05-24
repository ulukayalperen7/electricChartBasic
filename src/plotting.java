import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.DefaultXYDataset;

import javax.swing.*;
import java.awt.*;

public class plotting {
    public static void main(String[] args) {
        double[][] potentials = calculatePotentials(10, 6e-9, 8.99e9, 0.1);
        JFreeChart chart2D = create2DChart(potentials);
        JFreeChart chartVvsX = createVvsXChart(potentials);
        JFreeChart chartVvsDiagonal = createVvsDiagonalChart(potentials);

        ChartPanel chartPanel2D = new ChartPanel(chart2D);
        ChartPanel chartPanelVvsX = new ChartPanel(chartVvsX);
        ChartPanel chartPanelVvsDiagonal = new ChartPanel(chartVvsDiagonal);

        JFrame frame = new JFrame("Electric Potential Distribution");
        frame.setLayout(new GridLayout(1, 3));

        frame.add(chartPanel2D);
        frame.add(chartPanelVvsX);
        frame.add(chartPanelVvsDiagonal);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 400);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static double[][] calculatePotentials(int size, double charge, double k, double cellLength) {
        double[][] potentials = new double[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                double distance = Math.sqrt(i * cellLength * i * cellLength + j * cellLength * j * cellLength);
                if (distance == 0) {
                    potentials[i][j] = Double.POSITIVE_INFINITY;
                } else {
                    potentials[i][j] = (k * charge) / distance;
                }
            }
        }
        return potentials;
    }

    private static JFreeChart create2DChart(double[][] potentials) {
        DefaultXYDataset dataset = new DefaultXYDataset();
        int size = potentials.length;
        double[][] data = new double[2][size * size];
        int index = 0;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                data[0][index] = i * 0.1; // this is x coordinate
                data[1][index] = j * 0.1; // this is y coordinate
                data[1][index] = potentials[i][j]; // potential
                index++;
            }
        }
        dataset.addSeries("Potential", data);
        JFreeChart chart = ChartFactory.createScatterPlot(
                "Electric Potential Distribution",
                "x (m)",
                "y (m)",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false);
        return chart;
    }

    private static JFreeChart createVvsXChart(double[][] potentials) {
        DefaultXYDataset dataset = new DefaultXYDataset();
        int size = potentials.length;
        double[][] data = new double[2][size - 1];
        for (int i = 1; i < size; i++) {
            data[0][i - 1] = i * 0.1;
            data[1][i - 1] = potentials[i][0];
        }
        dataset.addSeries("Potential vs x", data);
        JFreeChart chart = ChartFactory.createXYLineChart(
                "Electric Potential vs x (j=0)",
                "x (m)",
                "Potential (V)",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false);
        return chart;
    }

    private static JFreeChart createVvsDiagonalChart(double[][] potentials) {
        DefaultXYDataset dataset = new DefaultXYDataset();
        int size = potentials.length;
        double[][] data = new double[2][size - 1];
        for (int i = 1; i < size; i++) {
            data[0][i - 1] = i * 0.1 * Math.sqrt(2);
            data[1][i - 1] = potentials[i][i];
        }
        dataset.addSeries("Potential vs r (Diagonal)", data);
        JFreeChart chart = ChartFactory.createXYLineChart(
                "Electric Potential vs r (Diagonal)",
                "r (m)",
                "Potential (V)",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false);
        return chart;
    }
}
