package com.bingcrowsby.byoadventure.Controller;

import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by kevinfinn on 2/25/15.
 */
public class MapConfig
{
    public float mzoom, mbearing, mtilt;
    public LatLng mtarget;

    public MapConfig(LatLng target, float zoom, float bearing, float tilt) {
        mtarget = target;
        mzoom = zoom;
        mbearing = bearing;
        mtilt = tilt;
    }

    public CameraPosition getCameraPosition() throws NullPointerException{
        return new CameraPosition.Builder().bearing(mbearing).target(mtarget).tilt(mtilt).zoom(mzoom).build();
    }


}
