package de.tum.mw.ftm.matsim.project;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

public class AbsoluteCharging {

    public static void main(String[] args) {
        String inputFile = "src/Project09/Project09/output_1/ITERS/it.2/2.chargingStats.csv";

        try {
            // Read the CSV file
            BufferedReader reader = new BufferedReader(new FileReader(inputFile));

            // Initialize a map to store the number of vehicles charging each day
            Map<String, Integer> vehiclesPerDay = new HashMap<>();

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
                String startDay = columns[7].trim(); // Use column index 7 for "startDay"
                String vehicleId = columns[3].trim(); // Use column index 3 for "vehicleId"

                // Update the map
                vehiclesPerDay.put(startDay, vehiclesPerDay.getOrDefault(startDay, 0) + 1);
            }

            // Print the results
            System.out.println("Number of vehicles charging per day:");
            for (Map.Entry<String, Integer> entry : vehiclesPerDay.entrySet()) {
                System.out.println("Day " + entry.getKey() + ": " + entry.getValue() + " vehicles");
            }

            // Calculate and print the total count for the week
            int totalVehiclesForWeek = vehiclesPerDay.values().stream().mapToInt(Integer::intValue).sum();
            System.out.println("\nTotal number of vehicles charging for the week: " + totalVehiclesForWeek);

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}