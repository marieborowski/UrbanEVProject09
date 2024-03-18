package de.tum.mw.ftm.matsim.project;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class ChargingStationUsageByHour {

    public static void main(String[] args) {
        String inputFile = "src/Project09/Project09/output_1%DC/ITERS/it.4/4.chargingStats.csv";
        String outputFile = "src/Project09/Project09/chargingUsageByHour1%DC.png";

        try {
            CategoryDataset dataset = createDataset(inputFile);
            JFreeChart chart = createChart(dataset);
            saveChartAsPNG(chart, outputFile, 800, 600);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    private static CategoryDataset createDataset(String inputFile) throws IOException, ParseException {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        Map<Integer, Integer> chargingHours = new HashMap<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

        try (BufferedReader br = new BufferedReader(new FileReader(inputFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("chargerId")) {
                    continue; // Skip header line
                }

                String[] values = line.split(";");
                Date startTime = dateFormat.parse(values[4]);
                int hour = startTime.getHours();
                chargingHours.put(hour, chargingHours.getOrDefault(hour, 0) + 1);
            }
        }

        for (Map.Entry<Integer, Integer> entry : chargingHours.entrySet()) {
            dataset.addValue(entry.getValue(), "Charging Activity", String.valueOf(entry.getKey()));
        }

        return dataset;
    }

    private static JFreeChart createChart(CategoryDataset dataset) {
        return ChartFactory.createBarChart(
                "Hourly Charging Distribution",
                "Hour",
                "Charging Events",
                dataset
        );
    }

    private static void saveChartAsPNG(JFreeChart chart, String fileName, int width, int height) throws IOException {
        ChartUtils.saveChartAsPNG(new java.io.File(fileName), chart, width, height);
    }
}