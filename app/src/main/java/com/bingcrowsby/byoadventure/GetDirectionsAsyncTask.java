package com.bingcrowsby.byoadventure;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.Toast;

import com.bingcrowsby.byoadventure.Activity.MapActivity;
import com.bingcrowsby.byoadventure.Model.DirectionsObject;
import com.google.android.gms.maps.model.LatLng;

import org.w3c.dom.Document;

import java.util.Map;

/**
 * Created by kevinfinn on 1/11/15.
 */
public class GetDirectionsAsyncTask extends AsyncTask<Map<String, String>, Object, DirectionsObject>
{
    public static final String START_LAT = "start_lat";
    public static final String START_LONG = "start_long";
    public static final String END_LAT = "end_lat";
    public static final String END_LONG = "end_long";
    public static final String DIRECTIONS_MODE = "directions_mode";
    private MapActivity activity;
    private Exception exception;
    private ProgressDialog progressDialog;

    public GetDirectionsAsyncTask(MapActivity activity)
    {
        super();
        this.activity = activity;
    }

    public void onPreExecute()
    {
        progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage("Calculating directions");
        progressDialog.show();
    }

    @Override
    public void onPostExecute(DirectionsObject result)
    {
//        Log.i("getDirectionsAsyncTask", "directing from "+result.startPosition+" to "+result.endPosition);
        progressDialog.dismiss();
        if (exception == null)
        {
            activity.handleGetDirectionsResult(result);
//            Log.i("dirsAsyncTask", "handleGetDirectionsResult " + result.size());


        }
        else
        {
            processException();
        }
    }

    @Override
    protected DirectionsObject doInBackground(Map<String, String>... params)
    {
        Map<String, String> paramMap = params[0];
        try
        {
            DirectionsObject dirObj = new DirectionsObject();
            LatLng fromPosition = new LatLng(Double.valueOf(paramMap.get(START_LAT)) , Double.valueOf(paramMap.get(START_LONG)));
            LatLng toPosition = new LatLng(Double.valueOf(paramMap.get(END_LAT)) , Double.valueOf(paramMap.get(END_LONG)));
            GMapV2Direction md = new GMapV2Direction();
            Document doc = md.getDocument(fromPosition, toPosition, paramMap.get(DIRECTIONS_MODE));
            dirObj = md.getDirection(doc);
            dirObj.bounds = md.getBounds(doc);
            int array[] = md.getDistanceAndDuration(doc);
            dirObj.distance = array[0];
            dirObj.duration = array[1];
            return dirObj;
        }
        catch (Exception e)
        {
            exception = e;
            return null;
        }
    }

    private void processException()
    {
        Toast.makeText(activity, activity.getString(R.string.error_when_retrieving_data), Toast.LENGTH_LONG).show();
    }


}
