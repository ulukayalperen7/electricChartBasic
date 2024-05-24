import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.DefaultXYDataset;

import javax.swing.*;
import java.awt.*;

public class plotting {
    private static final int SIZE = 10;
    private static final double CELL_LENGTH = 0.1;
    private static final double K = 8.99e9;
    private static final double CHARGE = getLastDigitCharge();

    private static double getLastDigitCharge() {

        String idNumber = "20220808006";
        int lastDigit = Integer.parseInt(idNumber.substring(idNumber.length() - 1));
        return lastDigit * 1e-9;
    }

    public static void main(String[] args) {
        double[][] potentials = calculatePotentials(SIZE, CHARGE, K, CELL_LENGTH);
        JFreeChart chart2D = create2DChart(potentials);
        JFreeChart chartVvsX = createVvsXChart(potentials);
        JFreeChart chartVvsDiagonal = createVvsDiagonalChart(potentials);
        JFreeChart chartEquipotential = createEquipotentialChart(potentials);

        // Create and set up panels
        ChartPanel chartPanel2D = new ChartPanel(chart2D);
        ChartPanel chartPanelVvsX = new ChartPanel(chartVvsX);
        ChartPanel chartPanelVvsDiagonal = new ChartPanel(chartVvsDiagonal);
        ChartPanel chartPanelEquipotential = new ChartPanel(chartEquipotential);

        // Create a JFrame to hold the panels
        JFrame frame = new JFrame("Electric Potential Distribution");
        frame.setLayout(new GridLayout(2, 2));

        // Add panels to the frame
        frame.add(chartPanel2D);
        frame.add(chartPanelVvsX);
        frame.add(chartPanelVvsDiagonal);
        frame.add(chartPanelEquipotential);

        // Set frame properties
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 800);
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
                data[0][index] = i * CELL_LENGTH; // x coordinate
                data[1][index] = j * CELL_LENGTH; // y coordinate
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
            data[0][i - 1] = i * CELL_LENGTH; // x coordinate
            data[1][i - 1] = potentials[i][0]; // potential at (i, 0)
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
            data[0][i - 1] = i * CELL_LENGTH * Math.sqrt(2);
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

    private static JFreeChart createEquipotentialChart(double[][] potentials) {
        DefaultXYDataset dataset = new DefaultXYDataset();
        int size = potentials.length;
        for (double level = 1e9; level <= 5e9; level += 1e9) {
            double[][] data = new double[2][size * size];
            int index = 0;
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    if (Math.abs(potentials[i][j] - level) < 1e8) {
                        data[0][index] = i * CELL_LENGTH;
                        data[1][index] = j * CELL_LENGTH;
                        index++;
                    }
                }
            }
            dataset.addSeries("Equipotential " + level, data);
        }
        JFreeChart chart = ChartFactory.createScatterPlot(
                "Equipotential Lines",
                "x (m)",
                "y (m)",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false);
        return chart;
    }

}
