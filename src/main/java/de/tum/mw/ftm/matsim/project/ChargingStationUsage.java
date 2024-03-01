package de.tum.mw.ftm.matsim.project;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import  java.util.Map.*;

public class ChargingStationUsage {

        public static void main(String[] args) {
            String inputFile = "src/Project09/Project09/output_1/ITERS/it.2/2.chargingStats.csv";

            try {
                // Read the CSV file
                BufferedReader reader = new BufferedReader(new FileReader(inputFile));

                // Initialize a map to store the usage count for each charging station
                Map<String, Integer> chargerUsageCount = new HashMap<>();

                // Read the file line by line
                String line;
                while ((line = reader.readLine()) != null) {
                    // Skip the header line if present
                    if (line.startsWith("chargerId")) {
                        continue;
                    }

                    // Split the line into columns
                    String[] columns = line.split(";");

                    // Extract relevant information
                    String chargerId = columns[0].trim(); // Use column index 0 for "chargerId"

                    // Update the map
                    chargerUsageCount.put(chargerId, chargerUsageCount.getOrDefault(chargerId, 0) + 1);
                }

                // Print the results
                System.out.println("Charging stations most used throughout the week:");
                getTopNChargers(chargerUsageCount, 5).forEach((chargerId, count) ->
                        System.out.println("Charger " + chargerId + ": " + count + " times"));

                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    // Helper method to get the top N entries from a map
    private static Map<String, Integer> getTopNChargers(Map<String, Integer> chargerUsageCount, int n) {
        return chargerUsageCount.entrySet()
                .stream()
                .sorted((e1, e2) -> Integer.compare(e2.getValue(), e1.getValue()))
                .limit(n)
                .collect(java.util.stream.Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, HashMap::new));

    }
}