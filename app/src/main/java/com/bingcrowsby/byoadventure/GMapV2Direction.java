package com.bingcrowsby.byoadventure;

import android.util.Log;

import com.bingcrowsby.byoadventure.Model.DirectionsObject;
import com.bingcrowsby.byoadventure.Model.Line;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by kevinfinn on 1/11/15.
 */
public class GMapV2Direction {
    public final static String MODE_DRIVING = "driving";
    public final static String MODE_WALKING = "walking";

    public GMapV2Direction() { }

    public Document getDocument(LatLng start, LatLng end, String mode) {
        String url = "http://maps.googleapis.com/maps/api/directions/xml?"
                + "origin=" + start.latitude + "," + start.longitude
                + "&destination=" + end.latitude + "," + end.longitude
                + "&sensor=false&units=metric&mode=" + mode;

        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpContext localContext = new BasicHttpContext();
            HttpPost httpPost = new HttpPost(url);
            HttpResponse response = httpClient.execute(httpPost, localContext);
            InputStream in = response.getEntity().getContent();
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = builder.parse(in);
            return doc;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getDurationText (Document doc) {
        NodeList nl1 = doc.getElementsByTagName("duration");
        Node node1 = nl1.item(0);
        NodeList nl2 = node1.getChildNodes();
        Node node2 = nl2.item(getNodeIndex(nl2, "text"));
        Log.i("DurationText", node2.getTextContent());
        return node2.getTextContent();
    }

    public int getDurationValue (Document doc) {
        NodeList nl1 = doc.getElementsByTagName("duration");
        Node node1 = nl1.item(0);
        NodeList nl2 = node1.getChildNodes();
        Node node2 = nl2.item(getNodeIndex(nl2, "value"));
        Log.i("DurationValue", node2.getTextContent());
        return Integer.parseInt(node2.getTextContent());
    }

    public String getDistanceText (Document doc) {
        NodeList nl1 = doc.getElementsByTagName("distance");
        Node node1 = nl1.item(0);
        NodeList nl2 = node1.getChildNodes();
        Node node2 = nl2.item(getNodeIndex(nl2, "text"));
        Log.i("DistanceText", node2.getTextContent());
        return node2.getTextContent();
    }

    public int getDistanceValue (Document doc) {
        NodeList nl1 = doc.getElementsByTagName("distance");
        Node node1 = nl1.item(0);
        NodeList nl2 = node1.getChildNodes();
        Node node2 = nl2.item(getNodeIndex(nl2, "value"));
        Log.i("DistanceValue", node2.getTextContent());
        return Integer.parseInt(node2.getTextContent());
    }

    public String getStartAddress (Document doc) {
        NodeList nl1 = doc.getElementsByTagName("start_address");
        Node node1 = nl1.item(0);
        Log.i("StartAddress", node1.getTextContent());
        return node1.getTextContent();
    }

    public String getEndAddress (Document doc) {
        NodeList nl1 = doc.getElementsByTagName("end_address");
        Node node1 = nl1.item(0);
        Log.i("StartAddress", node1.getTextContent());
        return node1.getTextContent();
    }

    public String getCopyRights (Document doc) {
        NodeList nl1 = doc.getElementsByTagName("copyrights");
        Node node1 = nl1.item(0);
        Log.i("CopyRights", node1.getTextContent());
        return node1.getTextContent();
    }

    public int[] getDistanceAndDuration(Document doc){
        int distance = 0;
        int duration = 0;
        NodeList nl1 = doc.getElementsByTagName("leg");
        if(nl1.getLength() > 0){
            for(int i = 0; i < nl1.getLength(); i++){
                Node node1 = nl1.item(i);
                NodeList nl2 = node1.getChildNodes();
                Node distNode = nl2.item(getNodeIndex(nl2, "distance"));
                Node durNode = nl2.item(getNodeIndex(nl2, "duration"));
                NodeList nlDist = distNode.getChildNodes();
                NodeList nlDur = durNode.getChildNodes();
                distance += Integer.valueOf(nlDist.item(getNodeIndex(nlDist,"value")).getTextContent());
                duration += Integer.valueOf(nlDur.item(getNodeIndex(nlDur,"value")).getTextContent());
            }
        }
        return new int[]{distance,duration};
    }

    public LatLngBounds getBounds(Document doc) {
        Node bounds = doc.getElementsByTagName("bounds").item(0);
        NodeList nlBounds = bounds.getChildNodes();
        NodeList swNode =nlBounds.item(getNodeIndex(nlBounds,"southwest")).getChildNodes();
        NodeList neNode = nlBounds.item(getNodeIndex(nlBounds,"northeast")).getChildNodes();

        double lat = Double.valueOf(swNode.item(getNodeIndex(swNode,"lat")).getTextContent());
        double lon = Double.valueOf(swNode.item(getNodeIndex(swNode,"lng")).getTextContent());

        LatLng sw = new LatLng(lat, lon);
        LatLng ne = new LatLng(Double.valueOf(neNode.item(getNodeIndex(neNode,"lat")).getTextContent()), Double.valueOf(neNode.item(getNodeIndex(neNode,"lng")).getTextContent()));

        return new LatLngBounds(sw, ne);
    }

    public DirectionsObject getDirection(Document doc) {
        NodeList nl1, nl2, nl3;
//        ArrayList listGeopoints = new ArrayList();
        Map<Integer,Line> dirLines = new HashMap<>();
        ArrayList<Line> lineList = new ArrayList<>();
        DirectionsObject directionsObject = new DirectionsObject();

        nl1 = doc.getElementsByTagName("step");
        if (nl1.getLength() > 0) {
            for (int i = 0; i < nl1.getLength(); i++) {
                Line line = new Line();
                Node node1 = nl1.item(i);
                nl2 = node1.getChildNodes();

                Node locationNode = nl2.item(getNodeIndex(nl2, "start_location"));
                nl3 = locationNode.getChildNodes();
                Node latNode = nl3.item(getNodeIndex(nl3, "lat"));
                double lat = Double.parseDouble(latNode.getTextContent());
                Node lngNode = nl3.item(getNodeIndex(nl3, "lng"));
                double lng = Double.parseDouble(lngNode.getTextContent());
                line.startPoint = new LatLng(lat, lng);

                locationNode = nl2.item(getNodeIndex(nl2, "polyline"));
                nl3 = locationNode.getChildNodes();
                latNode = nl3.item(getNodeIndex(nl3, "points"));
                String poly = latNode.getTextContent();
                directionsObject.addPolyline(poly);

                locationNode = nl2.item(getNodeIndex(nl2, "end_location"));
                nl3 = locationNode.getChildNodes();
                latNode = nl3.item(getNodeIndex(nl3, "lat"));
                lat = Double.parseDouble(latNode.getTextContent());
                lngNode = nl3.item(getNodeIndex(nl3, "lng"));
                lng = Double.parseDouble(lngNode.getTextContent());
                line.endPoint = new LatLng(lat,lng);
//                listGeopoints.add(line.endPosition);

                locationNode = nl2.item(getNodeIndex(nl2, "distance"));
                nl3 = locationNode.getChildNodes();
                Node distNode = nl3.item(getNodeIndex(nl3, "value"));
                line.distance = Float.valueOf(distNode.getTextContent());

                dirLines.put(i,line);
            }
        }

//        directionsObject.startPosition = directionsObject.polyline.get(0).toString();
//        directionsObject.endPosition = directionsObject.polyline.get(directionsObject.polyline.size()-1);
//        directionsObject.directionLines = dirLines;
//        directionsObject.mdirectionPoints = listGeopoints;
        return directionsObject;
    }

    private int getNodeIndex(NodeList nl, String nodename) {
        for(int i = 0 ; i < nl.getLength() ; i++) {
            if(nl.item(i).getNodeName().equals(nodename))
                return i;
        }
        return -1;
    }

    public static ArrayList<LatLng> decodePoly(String encoded) {
        ArrayList<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;
        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;                 shift += 5;             } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;
            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;                 shift += 5;             } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng position = new LatLng((double) lat / 1E5, (double) lng / 1E5);
            poly.add(position);
        }
        return poly;
    }
}