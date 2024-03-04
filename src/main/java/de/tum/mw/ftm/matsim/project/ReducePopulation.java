package de.tum.mw.ftm.matsim.project;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileOutputStream;


public class ReducePopulation {

    public static void main(String[] args) {

            String inputFile = "src/Project09/Project09/evPopulation_1percent_new.xml";
            String outputFile = "src/Project09/Project09/evPopulation_0.5percent_new.xml";

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new File(inputFile));

            Element rootElement = document.getDocumentElement();

            NodeList personList = rootElement.getElementsByTagName("person");

            Document reducedDocument = builder.newDocument();
            Element root = reducedDocument.createElement("population");
            reducedDocument.appendChild(root);

            for (int i = 0; i < personList.getLength(); i += 2) {
                Node personNode = personList.item(i);
                if (personNode.getNodeType() == Node.ELEMENT_NODE) {
                    // Import the node into the new document
                    Node importedNode = reducedDocument.importNode(personNode, true);
                    root.appendChild(importedNode);
                }
            }

            writeXML(reducedDocument, outputFile);

            System.out.println("New XML file created: " + outputFile);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void writeXML(Document document, String filePath) {
        try {
            FileOutputStream fos = new FileOutputStream(filePath);
            javax.xml.transform.TransformerFactory.newInstance()
                    .newTransformer().transform(new javax.xml.transform.dom.DOMSource(document),
                            new javax.xml.transform.stream.StreamResult(fos));
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}