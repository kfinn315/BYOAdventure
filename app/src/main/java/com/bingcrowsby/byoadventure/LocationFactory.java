package com.bingcrowsby.byoadventure;

import com.bingcrowsby.byoadventure.Model.PointList;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by kevinfinn on 2/12/15.
 */
public class LocationFactory {
    double distanceProgressedOnLine = 0;
    double totalProgress = 0;

    double currentLineLength = 0;
    PointList mdirectionPoints = null;

    public PointList getDirectionPoints(){
        return mdirectionPoints;
    }
    public LocationFactory(PointList directionPoints) {
        distanceProgressedOnLine = 0;

        setPointsList(directionPoints);

    }

    double remainingDistanceOnLine = 0;
    public double getDistanceProgressedOnLine() {
        return distanceProgressedOnLine;
    }

    public double getCurrentLineLength() {
        return currentLineLength;
    }

    public double getRemainingDistanceOnLine() {
        return remainingDistanceOnLine;
    }

    private void updateRemainingDistanceOnLine(){
        remainingDistanceOnLine = currentLineLength-distanceProgressedOnLine;
    }
    private void startLine(){
        currentLineLength = mdirectionPoints.getDistanceToNextPoint();
        distanceProgressedOnLine = 0;
        updateRemainingDistanceOnLine();
    }

    private double moveOnLine(double distance){
        if(distance <= remainingDistanceOnLine) {
            distanceProgressedOnLine += distance;
            distance = 0;
        }
        else { //distance > remainingDistanceOnLine
            distanceProgressedOnLine = currentLineLength;
            distance -= remainingDistanceOnLine;
        }
            updateRemainingDistanceOnLine();
            return distance;
    }
    public LatLng addProgress(double distanceToMove) {
         updateRemainingDistanceOnLine();
        if(mdirectionPoints == null)
            return null;

        if(remainingDistanceOnLine == 0 && !mdirectionPoints.hasNextPoint()){
            return mdirectionPoints.getEndPoint(); //finished
        }

        while(distanceToMove > 0 && mdirectionPoints.hasNextPoint()) {
            distanceToMove = moveOnLine(distanceToMove);
            if(remainingDistanceOnLine == 0) {
                mdirectionPoints.moveToNextPoint();
                startLine();
            }
        }
        return mdirectionPoints.interpolateFromCurrentPosition(distanceProgressedOnLine);
    }

    public void setPointsList(PointList pointsList) {
        mdirectionPoints = pointsList;
        totalProgress = 0;

        if (mdirectionPoints != null) {
            mdirectionPoints.setCurrentIndex(0);
            if (mdirectionPoints.size() >= 2) {
                startLine();
            }
        }
    }

    public boolean hasPointsList() {
        return mdirectionPoints != null;
    }
}
