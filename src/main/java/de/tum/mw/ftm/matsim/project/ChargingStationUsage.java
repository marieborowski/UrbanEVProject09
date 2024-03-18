package de.tum.mw.ftm.matsim.project;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class ChargingStationUsage {

        public static void main(String[] args) {
            String inputFile = "src/Project09/Project09/output_1%DC/ITERS/it.4/4.chargingStats.csv";

            try {

                BufferedReader reader = new BufferedReader(new FileReader(inputFile));
                Map<String, Integer> chargerUsageCount = new HashMap<>();

                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("chargerId")) {
                        continue;
                    }

                    String[] columns = line.split(";");
                    String chargerId = columns[0].trim(); // Use column index 0 for "chargerId"

                    chargerUsageCount.put(chargerId, chargerUsageCount.getOrDefault(chargerId, 0) + 1);
                }

                System.out.println("Charging stations most used throughout the week:");
                getTopNChargers(chargerUsageCount, 5).forEach((chargerId, count) ->
                        System.out.println("Charger " + chargerId + ": " + count + " times"));

                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    private static Map<String, Integer> getTopNChargers(Map<String, Integer> chargerUsageCount, int n) {
        return chargerUsageCount.entrySet()
                .stream()
                .sorted((e1, e2) -> Integer.compare(e2.getValue(), e1.getValue()))
                .limit(n)
                .collect(java.util.stream.Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, HashMap::new));

    }
}