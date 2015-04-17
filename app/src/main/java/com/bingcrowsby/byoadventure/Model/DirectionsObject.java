package com.bingcrowsby.byoadventure.Model;

import com.bingcrowsby.byoadventure.GMapV2Direction;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.gson.annotations.Expose;

import java.util.ArrayList;

/**
 * Created by kevinfinn on 1/12/15.
 */
//Information gathered from Google Maps HTTP request for directions
public class DirectionsObject {
    //protected PointList directionPoints = null;//List<LatLng> directionPoints = null;

    public DirectionsObject(){}
    @Expose
    public LatLngBounds bounds;
//    @Expose
//    public LatLng startPosition, endPosition;
    @Expose
    public int distance, duration;
    @Expose
    public ArrayList<String> polyline = null;

//    public PointList getDirectionPoints() throws NullPointerException{
//        if(directionPoints == null) {
//            if (polyline != null) {
//                decodePolyline();
//            }
//            else throw new NullPointerException("Polyline is null");
//        }
//        else
//            throw new NullPointerException("DirectionPoints is null");
//
//        return directionPoints;
//    }

    public static PointList getPointListFromPolyline(ArrayList<String> polyline){
        PointList pointList = new PointList();
        if(polyline != null) {
            for(String poly : polyline) {
                ArrayList<LatLng> arr = GMapV2Direction.decodePoly(poly);
                pointList.addAll(arr);
            }
        }
        return pointList;
    }

//    private void decodePolyline(){
////        directionPoints = new PointList();//directionPoints = new ArrayList<>();
////        directionPoints.add(startPosition);
//        if(polyline != null) {
//            for(String poly : polyline) {
//                ArrayList<LatLng> arr = GMapV2Direction.decodePoly(poly);
//                directionPoints.addAll(arr);
//            }
//        }
////        directionPoints.add(endPosition);
////        startPosition = directionPoints.getStartPoint();
////        endPosition = directionPoints.getEndPoint();
//    }

    public void addPolyline(String poly){
        if(polyline == null)
            polyline = new ArrayList<>();

        polyline.add(poly);
    }
}
