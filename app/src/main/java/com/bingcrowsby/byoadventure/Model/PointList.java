package com.bingcrowsby.byoadventure.Model;

import android.util.Log;

import com.bingcrowsby.byoadventure.Statics;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;

/**
 * Created by kevinfinn on 4/2/15.
 */
public class PointList extends ArrayList<LatLng>{
    int currentIndex = 0;

    public int getCurrentIndex() {
        return currentIndex;
    }

    private int maxIndex(){
        if(size()==0)
            return 0;
        else
            return size()-1;
    }
    public void setCurrentIndex(int newIndex) {
        Log.i("blah","from "+currentIndex+" to "+newIndex);
        if(newIndex > maxIndex())
            this.currentIndex = maxIndex();
        else if(newIndex < 0)
            this.currentIndex = 0;
        else
            this.currentIndex = newIndex;

        Log.i("blah","= "+currentIndex);
    }

    public LatLng getCurrentPoint(){
        return get(currentIndex);
    }

    public LatLng getNextPoint() throws IndexOutOfBoundsException{
            return get(currentIndex+1);
    }

    public LatLng getStartPoint() throws IndexOutOfBoundsException{
        return get(0);
    }

    public LatLng getEndPoint() throws IndexOutOfBoundsException {
        return get(size()-1);
    }

    public double getDistanceToNextPoint(){
        if(hasNextPoint())
            return Statics.computeLineLength(getCurrentPoint(),getNextPoint());
        else
            return 0;
    }

    public boolean hasNextPoint(){
        Log.i("blah", currentIndex+" +1"+"<" + size() + "?: " + Boolean.toString(currentIndex + 1 < size()));
        return currentIndex+1 < size();
    }

    public boolean moveToNextPoint(){
        if(hasNextPoint()) {
            currentIndex += 1;
            return true;
        }
        else
            return false;

    }
    public boolean moveToPreviousPoint(){
        if(currentIndex==0)
            return false;
        currentIndex--;
        return true;
    }
    public LatLng interpolateFromCurrentPosition(double distanceFromCurrentPos){
        if(!hasNextPoint())
            return null;
        double ratio = distanceFromCurrentPos / getDistanceToNextPoint();
        return SphericalUtil.interpolate(getCurrentPoint(), getNextPoint(), ratio);
    }

    public LatLng remove(int index){
        Log.i("blah","remove "+index+", current "+currentIndex);
        if(index <= currentIndex)
            moveToPreviousPoint();

        Log.i("blah","now "+currentIndex);
        return super.remove(index);
    }
    
    public void add(int index, LatLng latLng){
        Log.i("blah","currentindex "+currentIndex+" and add point at "+index);
        super.add(index,latLng);

        if(currentIndex >= index)
            moveToNextPoint();

        Log.i("blah","currentindex is "+currentIndex);
    }


}
