package de.tum.mw.ftm.matsim.project;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;

public class ReducePopulation {

    public static void main(String[] args) {
        try {
            // Input and output file paths
            String inputFile = "src/main/java/Project09/evPopulation5_merged.xml";
            String outputFile = "src/main/java/Project09/evPopulation2.5_merged.xml";

            // Load the XML file
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document document = dBuilder.parse(new File(inputFile));

            // Get the population element
            Element populationElement = (Element) document.getElementsByTagName("population").item(0);

            // Get the person nodes
            NodeList personList = populationElement.getElementsByTagName("person");

            // Calculate the number of persons to keep (2.5% of the total)
            int totalPersons = personList.getLength();
            int personsToKeep = (int) (totalPersons * 0.5);

            // Create a new population element
            Element newPopulationElement = document.createElement("population");

            // Copy the required number of persons to the new population
            for (int i = 0; i < personsToKeep; i++) {
                Node personNode = personList.item(i).cloneNode(true);
                newPopulationElement.appendChild(personNode);
            }

            // Replace the old population with the new one
            document.replaceChild(newPopulationElement, populationElement);

            // Save the modified XML to a new file
            saveToXML(document, outputFile);

            System.out.println("Reduced population file created successfully!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void saveToXML(Document doc, String outputFile) {
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();

            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.STANDALONE, "no");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(outputFile));

            transformer.transform(source, result);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}