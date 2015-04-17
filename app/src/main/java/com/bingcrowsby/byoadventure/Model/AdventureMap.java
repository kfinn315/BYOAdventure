package com.bingcrowsby.byoadventure.Model;

import android.util.Log;

import com.bingcrowsby.byoadventure.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;

/**
 * Created by kevinfinn on 1/21/15.
 */
public class AdventureMap {
    static final String tag = "AdventureMap";
    protected LatLng mprogressPoint;
    protected LatLngBounds mbounds;
    protected PointList mpointList;
    protected final static Integer runningIconDrawableResource = R.drawable.running_icon_on;
    protected final static Integer endIconDrawableResource = R.drawable.cross_18_2x;

    protected Polyline mpolyline;
    protected Marker mstartMarker, mendMarker, mprogressMarker;
    protected String snippet;

    private static BitmapDescriptor progressDescriptor = null, endDescriptor = null, startDescriptor = null;

    public String getSnippet(){
        return snippet;
    }
    public LatLng getProgressPoint() {
        return mprogressPoint;
    }

    public void setProgressPoint(LatLng mprogressPoint) {
        this.mprogressPoint = mprogressPoint;
    }

    public LatLngBounds getBounds() {
        return mbounds;
    }

    public PointList getPointList() {
        return mpointList;
    }

    public Marker getStartMarker() {
        return mstartMarker;
    }

    public Marker getEndMarker() {
        return mendMarker;
    }

    public Marker getProgressMarker() {
        return mprogressMarker;
    }

    public void setStartMarker(Marker mstartMarker) {
        this.mstartMarker = mstartMarker;
    }

    public void setEndMarker(Marker mendMarker) {
        this.mendMarker = mendMarker;
    }

    public void setProgressMarker(Marker mprogressMarker) {
        this.mprogressMarker = mprogressMarker;
    }

    public void setBounds(LatLngBounds mbounds) {
        this.mbounds = mbounds;
    }

    public Polyline getPolyline() {
        return mpolyline;
    }

    public static BitmapDescriptor getProgressBitmap(){
        if(progressDescriptor == null)
            progressDescriptor = BitmapDescriptorFactory.fromResource(runningIconDrawableResource);

        return progressDescriptor;
    }
    public static BitmapDescriptor getEndBitmap(){
        if(endDescriptor == null)
            endDescriptor = BitmapDescriptorFactory.fromResource(endIconDrawableResource);
        return endDescriptor;
    }

    public static BitmapDescriptor getStartBitmap(){
        if(startDescriptor == null)
            startDescriptor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
        return startDescriptor;
    }

    public void updateProgressMarker(LatLng position) {
        Log.i(tag, "update prog marker");
        if (mprogressMarker != null)
            mprogressMarker.setPosition(position);
    }

    public void zoomToUser(GoogleMap googleMap){
        Log.i(tag,"zoom to user");
        float zoomlvl = 16;
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(mprogressPoint, zoomlvl);
        googleMap.animateCamera(update);
    }

    public static class Builder {
        protected LatLng mprogressPoint;
        protected LatLngBounds mbounds;
        protected PointList mpointList;
        protected Integer mrunningIconDrawableResource = R.drawable.running_icon_on;
        protected String msnippet = null;

        private boolean isAtStartPoint = false;

        public Builder() {
        }

        public AdventureMap build() {
            AdventureMap map = new AdventureMap();
            map.setProgressPoint(mprogressPoint);
            //map.runningIconDrawableResource = mrunningIconDrawableResource;
            map.mpointList = mpointList;
            map.mbounds = mbounds;
            map.snippet = msnippet;
            if (mprogressPoint == null)
                mprogressPoint = mpointList.getStartPoint();
            map.mprogressPoint = mprogressPoint;
            return map;
        }


        public Builder linePoints(PointList pointList) {
            mpointList = pointList;
            if (isAtStartPoint)
                setIsAtStartPoint();

            return this;
        }

        public Builder bounds(LatLngBounds bounds) {
            this.mbounds = bounds;
            return this;
        }

        public Builder currentPosition(LatLng point) {
            this.mprogressPoint = point;
            return this;
        }


        public Builder setIsAtStartPoint() {
            isAtStartPoint = true;

            if (mpointList != null)
                this.currentPosition(mpointList.getStartPoint());

            return this;
        }

        public Builder setCurrentMarkerIcon(int id) {
            this.mrunningIconDrawableResource = id;
            return this;
        }

        public Builder setSnippet(String snippet) {
            this.msnippet = snippet;
            return this;
        }
    }

}
