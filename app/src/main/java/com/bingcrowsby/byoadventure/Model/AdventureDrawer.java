package com.bingcrowsby.byoadventure.Model;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;

import com.bingcrowsby.byoadventure.View.MultilineInfoWindowAdapter;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

/**
 * Created by kevinfinn on 4/5/15.
 */
public class AdventureDrawer {
    public final static int BOUNDS_PADDING = 5;
    public final static int LINE_WIDTH = 3;
    public final static int LINE_COLOR = Color.BLUE;

    public static boolean drawAdventure(final Context context, final GoogleMap googleMap, final AdventureMap adventureMap) throws Exception{
        if(googleMap == null || adventureMap == null || context == null)
            throw new Exception("GoogleMap, AdventureMap or Context are null");
        else {
            PointList pointList = adventureMap.getPointList();
            if (pointList != null && pointList.size() > 0) {
                setInfoWindowAdapter(context, googleMap);
                drawMarkers(googleMap, adventureMap);
                adventureMap.mpolyline = drawPolyline(googleMap, createPolylineOptions(adventureMap.getPointList()));
                updateCamera(googleMap, adventureMap.getBounds());
            }

            return true;
        }
    }

    public static LatLngBounds getBounds(PointList pointList) throws NullPointerException{
        return getBounds(pointList.getStartPoint(),pointList.getEndPoint());
    }
    public static LatLngBounds getBounds(LatLng start, LatLng end){
        return new LatLngBounds.Builder().include(start).include(end).build();
    }
    public static CameraUpdate getCameraUpdate(LatLngBounds bounds, int padding){
        return CameraUpdateFactory.newLatLngBounds(bounds, padding);
    }

    public static void updateCamera(final GoogleMap googleMap, LatLngBounds bounds){
        final CameraUpdate cameraUpdate = getCameraUpdate(bounds, BOUNDS_PADDING);

        try {
            googleMap.animateCamera(cameraUpdate);
        } catch (IllegalStateException e) {
            googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {
                    googleMap.animateCamera(cameraUpdate);
                }
            });
        }
    }

    public static Polyline drawPolyline(GoogleMap googleMap, PolylineOptions options){
        return googleMap.addPolyline(options);
    }

    public static PolylineOptions createPolylineOptions(PointList pointList){
        PolylineOptions mpolylineOptions = new PolylineOptions().width(LINE_WIDTH).color(LINE_COLOR);
        mpolylineOptions.addAll(pointList);

        return mpolylineOptions;
    }

    public static void setInfoWindowAdapter(Context context, GoogleMap googleMap){
        googleMap.setInfoWindowAdapter(new MultilineInfoWindowAdapter(LayoutInflater.from(context)));
    }

    public static void drawMarkers(GoogleMap googleMap, AdventureMap adventureMap){
        googleMap.clear();
        PointList pointList = adventureMap.getPointList();

        MarkerOptions mstartMarkerOptions, mendMarkerOptions, mprogressMarkerOptions;
        mstartMarkerOptions = new MarkerOptions().position(pointList.getStartPoint()).icon(adventureMap.getStartBitmap()).title("START");
        mendMarkerOptions = new MarkerOptions().position(pointList.getEndPoint()).icon(adventureMap.getEndBitmap()).title("STOP");
        mprogressMarkerOptions = new MarkerOptions().position(adventureMap.getProgressPoint()).icon(adventureMap.getProgressBitmap()).title("YOU ARE HERE").snippet(adventureMap.getSnippet());

        adventureMap.setStartMarker(drawMarker(googleMap, mstartMarkerOptions));
        adventureMap.setEndMarker(drawMarker(googleMap, mendMarkerOptions));
        adventureMap.setProgressMarker(drawMarker(googleMap, mprogressMarkerOptions));
    }

    private static Marker drawMarker(GoogleMap googleMap, MarkerOptions markerOptions){
        if (markerOptions.getPosition() != null) {
            return googleMap.addMarker(markerOptions);
        }
        else return null;
    }

}