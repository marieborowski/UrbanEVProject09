package de.tum.mw.ftm.matsim.project;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;


public class HomeCharging {

    public static void main(String[] args) {
        try {

            File xmlFile = new File("src/Project09/Project09/evPopulation_1percent_new.xml");

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            NodeList planList = doc.getElementsByTagName("plan");

            for (int i = 0; i < planList.getLength(); i++) {
                Element planElement = (Element) planList.item(i);

                Element attributesElement = doc.createElement("attributes");

                Element attribute1 = doc.createElement("attribute");
                attribute1.setAttribute("name", "homeChargerPower");
                attribute1.setAttribute("class", "java.lang.String");
                attribute1.setTextContent("11");
                attributesElement.appendChild(attribute1);

                Element attribute2 = doc.createElement("attribute");
                attribute2.setAttribute("name", "rangeAnxietyThreshold");
                attribute2.setAttribute("class", "java.lang.String");
                attribute2.setTextContent("0.2");
                attributesElement.appendChild(attribute2);

                planElement.insertBefore(attributesElement, planElement.getFirstChild());
            }

            saveXML(doc, "src/Project09/Project09/evPopulation1_HomeCharging.xml");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void saveXML(Document doc, String filePath) throws Exception {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();

        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(filePath));
        transformer.transform(source, result);
    }
}
