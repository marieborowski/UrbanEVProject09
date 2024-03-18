package de.tum.mw.ftm.matsim.project;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.index.SpatialIndex;
import org.locationtech.jts.index.strtree.STRtree;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;

public class ChargingStationMatcher {

    private static final double MATCH_RADIUS = 1000.0;

    public static void main(String[] args) {
        SpatialIndex spatialIndex = buildSpatialIndex();

        List<ChargingStation> chargingStations = loadChargingStations("src/main/java/Project09/ChargingStations.xml");

        matchChargingStations(chargingStations, spatialIndex);

        writeMatchedChargingStationsToXML(chargingStations);
    }

    private static SpatialIndex buildSpatialIndex() {
        SpatialIndex spatialIndex = new STRtree();

        try (FileReader fileReader = new FileReader("src/main/java/Project09/network_neu2.xml")) {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(new InputSource(fileReader));

            NodeList nodeNodes = doc.getElementsByTagName("node");
            NodeList linkNodes = doc.getElementsByTagName("link");

            Map<String, Point> nodeCoordinates = new HashMap<>();

            for (int i = 0; i < nodeNodes.getLength(); i++) {
                Element nodeElement = (Element) nodeNodes.item(i);
                String nodeId = nodeElement.getAttribute("id");
                double x = Double.parseDouble(nodeElement.getAttribute("x"));
                double y = Double.parseDouble(nodeElement.getAttribute("y"));

                nodeCoordinates.put(nodeId, createPoint(x, y));
            }

            for (int i = 0; i < linkNodes.getLength(); i++) {
                Element linkElement = (Element) linkNodes.item(i);
                String linkId = linkElement.getAttribute("id");
                String fromNodeId = linkElement.getAttribute("from");
                String toNodeId = linkElement.getAttribute("to");

                Point fromNode = nodeCoordinates.get(fromNodeId);
                Point toNode = nodeCoordinates.get(toNodeId);

                double x1 = fromNode.getX();
                double y1 = fromNode.getY();
                double x2 = toNode.getX();
                double y2 = toNode.getY();

                spatialIndex.insert(new Envelope(x1, x2, y1, y2), new Link(x1, y1, x2, y2, linkId));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return spatialIndex;
    }

    private static STRtree buildSpatialIndex(List<Link> roadLinks) {
        STRtree spatialIndex = new STRtree();

        for (Link roadLink : roadLinks) {
            Envelope envelope = new Envelope(roadLink.getX1(), roadLink.getX2(), roadLink.getY1(), roadLink.getY2());
            spatialIndex.insert(envelope, roadLink);
        }

        spatialIndex.build();
        return spatialIndex;
    }

    private static List<ChargingStation> loadChargingStations(String filePath) {
        List<ChargingStation> chargingStations = new ArrayList<>();

        try {
            File xmlFile = new File(filePath);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);

            doc.getDocumentElement().normalize();

            NodeList chargerNodes = doc.getElementsByTagName("charger");

            for (int i = 0; i < chargerNodes.getLength(); i++) {
                Element chargerElement = (Element) chargerNodes.item(i);
                String id = chargerElement.getAttribute("id");
                double x = Double.parseDouble(chargerElement.getAttribute("x").replace(',', '.'));
                double y = Double.parseDouble(chargerElement.getAttribute("y").replace(',', '.'));
                double plugPower = Double.parseDouble(chargerElement.getAttribute("plug_power").replace(',', '.'));
                int plugCount = Integer.parseInt(chargerElement.getAttribute("plug_count"));

                ChargingStation chargingStation = new ChargingStation(id, x, y, plugPower, plugCount);
                chargingStations.add(chargingStation);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return chargingStations;
    }

    private static void matchChargingStations(List<ChargingStation> chargingStations, SpatialIndex spatialIndex) {
        for (ChargingStation chargingStation : chargingStations) {
            Point stationPoint = createPoint(chargingStation.getX(), chargingStation.getY());
            Envelope searchEnvelope = new Envelope(stationPoint.getCoordinate());
            searchEnvelope.expandBy(MATCH_RADIUS);

            List<?> candidateLinks = spatialIndex.query(searchEnvelope);

            Link nearestLink = findNearestLink(stationPoint, candidateLinks);
            chargingStation.setMatchedLink(nearestLink);

            if (nearestLink != null) {
                Coordinate nearestPoint = nearestPointOnLine(nearestLink.getX1(), nearestLink.getY1(),
                        nearestLink.getX2(), nearestLink.getY2(),
                        stationPoint.getX(), stationPoint.getY());
                chargingStation.setNearestPointX(nearestPoint.x);
                chargingStation.setNearestPointY(nearestPoint.y);
            }
        }
    }

    private static Link findNearestLink(Point point, List<?> candidateLinks) {
        Link nearestLink = null;
        double minDistance = Double.MAX_VALUE;

        for (Object candidate : candidateLinks) {
            if (candidate instanceof Link) {
                Link candidateLink = (Link) candidate;
                double distance1 = point.distance(createPoint(candidateLink.getX1(), candidateLink.getY1()));
                double distance2 = point.distance(createPoint(candidateLink.getX2(), candidateLink.getY2()));

                double distance = Math.min(distance1, distance2);

                if (distance < minDistance) {
                    minDistance = distance;
                    nearestLink = candidateLink;
                }
            }
        }

        return nearestLink;
    }

    private static Coordinate nearestPointOnLine(double x1, double y1, double x2, double y2, double x, double y) {
        double dx = x2 - x1;
        double dy = y2 - y1;

        double u = ((x - x1) * dx + (y - y1) * dy) / (dx * dx + dy * dy);

        double nearestX, nearestY;

        if (u < 0) {
            nearestX = x1;
            nearestY = y1;
        } else if (u > 1) {
            nearestX = x2;
            nearestY = y2;
        } else {
            nearestX = x1 + u * dx;
            nearestY = y1 + u * dy;
        }

        return new Coordinate(nearestX, nearestY);
    }

    private static Point createPoint(double x, double y) {
        GeometryFactory geometryFactory = new GeometryFactory();
        Coordinate coordinate = new Coordinate(x, y);
        return geometryFactory.createPoint(coordinate);
    }

    private static void writeMatchedChargingStationsToXML(List<ChargingStation> chargingStations) {
        try {
            XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
            XMLStreamWriter writer = outputFactory.createXMLStreamWriter(new FileWriter("src/main/java/Project09/MatchedChargingStations.xml"));

            writer.writeStartDocument();
            writer.writeStartElement("matched_chargers");

            for (ChargingStation chargingStation : chargingStations) {
                if (chargingStation.getMatchedLink() != null) {
                    writer.writeCharacters("\n  ");
                    writer.writeStartElement("charger");
                    writer.writeAttribute("id", chargingStation.getId());
                    writer.writeAttribute("x", formatCoordinate(chargingStation.getNearestPointX())); // Use nearest point coordinates
                    writer.writeAttribute("y", formatCoordinate(chargingStation.getNearestPointY()));
                    writer.writeAttribute("plug_power", String.valueOf((int) chargingStation.getPlugPower()));
                    writer.writeAttribute("plug_count", String.valueOf(chargingStation.getPlugCount()));


                    writer.writeEndElement();
                }
            }

            writer.writeCharacters("\n");
            writer.writeEndElement();
            writer.writeEndDocument();

            writer.close();

            System.out.println("Matched charging stations written to XML.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String formatCoordinate(double coordinate) {
        DecimalFormat df = new DecimalFormat("0.000000", new DecimalFormatSymbols(Locale.US));
        return df.format(coordinate);
    }
}

class ChargingStation {
    private String id;
    private double x;
    private double y;
    private Link matchedLink;
    private double nearestPointX;
    private double nearestPointY;
    private double plugPower;
    private int plugCount;

    public ChargingStation(String id, double x, double y, double plugPower, int plugCount) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.plugPower = plugPower;
        this.plugCount = plugCount;
    }

    public String getId() {
        return id;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setMatchedLink(Link matchedLink) {
        this.matchedLink = matchedLink;
    }

    public Link getMatchedLink() {
        return matchedLink;
    }

    public double getNearestPointX() {
        return nearestPointX;
    }

    public double getNearestPointY() {
        return nearestPointY;
    }

    public void setNearestPointX(double nearestPointX) {
        this.nearestPointX = nearestPointX;
    }

    public void setNearestPointY(double nearestPointY) {
        this.nearestPointY = nearestPointY;
    }

    public double getPlugPower() {
        return plugPower;
    }

    public int getPlugCount() {
        return plugCount;
    }
}

class Link {
    private double x1;
    private double y1;
    private double x2;
    private double y2;
    private String linkId;

    public Link(double x1, double y1, double x2, double y2, String linkId) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.linkId = linkId;
    }

    public String getLinkId() {
        return linkId;
    }

    public double getX1() {
        return x1;
    }

    public double getY1() {
        return y1;
    }

    public double getX2() {
        return x2;
    }

    public double getY2() {
        return y2;
    }
}
