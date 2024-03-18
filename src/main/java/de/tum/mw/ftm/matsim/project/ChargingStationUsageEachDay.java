package de.tum.mw.ftm.matsim.project;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ChargingStationUsageEachDay {


    public static void main(String[] args) {
        String inputFile = "src/Project09/Project09/output_1%DC/ITERS/it.4/4.chargingStats.csv";
        String outputFile = "src/Project09/Project09/chargingUsageEachDay1%DC.png";

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
        Map<Integer, Map<Integer, Integer>> dailyChargingHours = new HashMap<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

        try (BufferedReader br = new BufferedReader(new FileReader(inputFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("chargerId")) {
                    continue; // Skip header line
                }

                String[] values = line.split(";");
                Date startTime = dateFormat.parse(values[4]);
                int day = Integer.parseInt(values[7]);
                int hour = startTime.getHours();

                dailyChargingHours
                        .computeIfAbsent(day, k -> new HashMap<>())
                        .put(hour, dailyChargingHours.getOrDefault(day, new HashMap<>()).getOrDefault(hour, 0) + 1);
            }
        }

        for (Map.Entry<Integer, Map<Integer, Integer>> dayEntry : dailyChargingHours.entrySet()) {
            for (Map.Entry<Integer, Integer> entry : dayEntry.getValue().entrySet()) {
                dataset.addValue(entry.getValue(), "Day " + dayEntry.getKey(), String.valueOf(entry.getKey()));
            }
        }

        return dataset;
    }

    private static JFreeChart createChart(CategoryDataset dataset) {
        return ChartFactory.createLineChart(
                "Daily Charging Distribution",
                "Hour",
                "Charging Events",
                dataset
        );
    }

    private static void saveChartAsPNG(JFreeChart chart, String fileName, int width, int height) throws IOException {
        ChartUtils.saveChartAsPNG(new java.io.File(fileName), chart, width, height);
    }
}