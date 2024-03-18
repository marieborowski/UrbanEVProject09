package de.tum.mw.ftm.matsim.project;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class AbsoluteCharging {

    public static void main(String[] args) {
        String inputFile = "src/Project09/Project09/output_1%DC/ITERS/it.4/4.chargingStats.csv";

        try {
            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            Map<String, Integer> vehiclesPerDay = new HashMap<>();

            String line;
            while ((line = reader.readLine()) != null) {

                if (line.startsWith("chargerId")) {
                    continue;
                }

                String[] columns = line.split(";");

                String startDay = columns[7].trim();
                String vehicleId = columns[3].trim();

                vehiclesPerDay.put(startDay, vehiclesPerDay.getOrDefault(startDay, 0) + 1);
            }

            System.out.println("Number of vehicles charging per day:");
            for (Map.Entry<String, Integer> entry : vehiclesPerDay.entrySet()) {
                System.out.println("Day " + entry.getKey() + ": " + entry.getValue() + " vehicles");
            }

            int totalVehiclesForWeek = vehiclesPerDay.values().stream().mapToInt(Integer::intValue).sum();
            System.out.println("\nTotal number of vehicles charging for the week: " + totalVehiclesForWeek);

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}