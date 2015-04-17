package com.bingcrowsby.byoadventure.Model;

import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.util.Log;

import com.bingcrowsby.byoadventure.Controller.CustomGson;
import com.bingcrowsby.byoadventure.LocationFactory;
import com.google.android.gms.maps.model.LatLng;

import org.joda.time.DateTime;
import org.joda.time.Days;

import java.util.Date;

import static com.bingcrowsby.byoadventure.Model.AdventureDbOpenHelper.ROW_DATE_CREATED;
import static com.bingcrowsby.byoadventure.Model.AdventureDbOpenHelper.ROW_DATE_UPDATED;
import static com.bingcrowsby.byoadventure.Model.AdventureDbOpenHelper.ROW_ID;
import static com.bingcrowsby.byoadventure.Model.AdventureDbOpenHelper.ROW_OBJECT;
import static com.bingcrowsby.byoadventure.Model.AdventureDbOpenHelper.ROW_TITLE;

/**
 * Created by kevinfinn on 1/12/15.
 */
public class AdventureObject { //Holds the from the Adventure Database
    public Integer id = null;
    public String madventureTitle;
    public Date mdateCreated, mdateUpdated;
    private LocationFactory locationFactory;

//    @Expose
//    public DirectionsObject mdirections; //info from google maps directions

    public float mdistanceScale;
    private float mcurrentDistance = 0, mtotalDistance = 0;
    private LatLng mcurrentPosition; //for markers

    PointList mpointList;

    OnPositionChangedListener listener;

    public AdventureObject(){  }
    public AdventureObject(String adventureName, PointList pointList, Date dateCreated, LatLng currentPosition) throws Exception {

        mpointList = pointList;
        madventureTitle = adventureName;
        mdateCreated = dateCreated;

        if(mpointList ==null)
            throw new Exception("Pointlist is null");
        if(mpointList.size()<2)
            throw new Exception("Pointlist has size inadequate size.  Must have 2 entries");

        if(currentPosition == null)
            mcurrentPosition = mpointList.getStartPoint();

    }

    public void setCurrentDistance(float currentDistance) { this.mcurrentDistance = currentDistance; }

    public float getTotalDistance() {
        return mtotalDistance;
    }

    public float getCurrentDistance() {
        return mcurrentDistance;
    }

    public void setTotalDistance(float totalDistance) {
        this.mtotalDistance = totalDistance;
    }

    public AdventureMap buildAdventureMap() {
        try {
            AdventureMap map = new AdventureMap.Builder().bounds(AdventureDrawer.getBounds(mpointList)).linePoints(mpointList).currentPosition(mcurrentPosition).setSnippet(getSnippet()).build();
            return map;
        } catch(NullPointerException e){
            return null;
        }
    }

    public void addProgress(float progress){
        mcurrentDistance+=progress;
        if(mcurrentDistance >= mtotalDistance){ //reached the end
            mcurrentDistance = mtotalDistance;
            mcurrentPosition = mpointList.getEndPoint();
        }
        else {
//            new PositionTask().execute(mcurrentDistance);
            if(locationFactory == null)
                locationFactory = new LocationFactory(mpointList);

            mcurrentPosition = locationFactory.addProgress(progress);
            if(mcurrentPosition == null)
                Log.i("AdventureObject", "currentPosition is null");
            else
                notifyPositionChangedListener(mcurrentDistance, mcurrentPosition);
        }
    }

    public void setOnPositionChangedListener(OnPositionChangedListener listener){
        this.listener = listener;
    }

    void notifyPositionChangedListener(float currentDistance, LatLng currentPosition){
        if(listener!=null)
            listener.onPositionChanged(currentDistance, currentPosition);
    }

    public interface OnPositionChangedListener{
        public void onPositionChanged(float newDistance, LatLng newPosition);
    }

    public static int getDaysFromNow(Date date) throws Exception{
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTimeInMillis(getTimeElapsed());

        if(date != null) {
            Days daysbetween = Days.daysBetween(new DateTime(date.getTime()), new DateTime().now());
            return daysbetween.getDays();
        }
        else
            throw new Exception();
    }
    public String getDaysElapsed() {
        String days;
        try {
            days = "Days: " + getDaysFromNow(mdateCreated);
        } catch(Exception e){
            days = "No start date";
        }
        return days;
    }
    public long getDaysSinceUpdateUTC(){
        return System.currentTimeMillis() - mdateUpdated.getTime();
    }
    public String getDistanceString() {
        return ""+mcurrentDistance+"/"+mtotalDistance+" meters";
    }

    public String getSnippet() {
        StringBuilder builder = new StringBuilder();
        try {
            builder.append("\"" + madventureTitle + "\"");
            builder.append("\ncreated:" + mdateCreated.toString());
            builder.append("\nupdated:" + mdateUpdated);
            builder.append("\nscale:" + mdistanceScale);
        } finally {
            return builder.toString();
        }
    }

    /**
     * Created by kevinfinn on 1/12/15.
     */
    public static class Builder extends AdventureObject{

        //    private DirectionsObject directions;
        private String adventureName;
        private float distanceScale;
        private Date startDate, endDate;
        private LatLng currentPosition;
        private PointList mpointlist;

        public Builder(PointList pointList){
            mpointList = pointList;
        }

        public Builder name(String name){
            this.adventureName = name;
            return this;
        }

        public Builder startDate(Date date){
            this.startDate = date;
            return this;
        }

        public Builder currentPosition(LatLng pos){
            this.currentPosition = pos;
            return this;
        }

        public AdventureObject build(){
            try{
                return new AdventureObject(adventureName, mpointList, startDate, currentPosition);
            } catch(Exception e){
                return null;
            }
        }
    }

    public static AdventureObject fromCursor(Cursor cursor){
        //TODO check for valid cursor
        AdventureObject advObject = null;
        try {
            advObject = CustomGson.getGson().fromJson(cursor.getString(cursor.getColumnIndex(ROW_OBJECT)), AdventureObject.class);
            advObject.id = cursor.getInt(cursor.getColumnIndex(ROW_ID));
            advObject.mdateCreated = new Date(cursor.getLong(cursor.getColumnIndex(ROW_DATE_CREATED)));
            advObject.mdateUpdated = new Date(cursor.getLong(cursor.getColumnIndex(ROW_DATE_UPDATED)));
            advObject.madventureTitle = cursor.getString(cursor.getColumnIndex(ROW_TITLE));
        } catch(CursorIndexOutOfBoundsException e){
            e.printStackTrace();
        } catch(NullPointerException e){
            e.printStackTrace();
        }
        return advObject;
    }

}