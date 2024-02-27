package de.tum.mw.ftm.matsim.project;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PopulationRenumbering {

    public static void main(String[] args) {
        renumberPopulation("src/Project09/Project09/evPopulation10_merged.xml", "src/Project09/Project09/evPopulation10_new.xml");
    }

    public static void renumberPopulation(String inputFile, String outputFile) {
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {

            String line;
            int personCounter = 1;

            while ((line = reader.readLine()) != null) {
                String modifiedLine = renumberPersonId(line, personCounter);
                writer.write(modifiedLine);
                writer.newLine();

                if (line.contains("<person")) {
                    personCounter++;
                }
            }

            System.out.println("Renumbering completed.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String renumberPersonId(String line, int newPersonId) {
        Pattern pattern = Pattern.compile("id=\"(\\d+)\"");
        Matcher matcher = pattern.matcher(line);

        StringBuffer stringBuffer = new StringBuffer();

        while (matcher.find()) {
            matcher.appendReplacement(stringBuffer, "id=\"" + newPersonId + "\"");
        }
        matcher.appendTail(stringBuffer);

        return stringBuffer.toString();
    }
}