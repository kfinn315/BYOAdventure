package com.bingcrowsby.byoadventure.Activity;

import android.app.AlertDialog;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.bingcrowsby.byoadventure.AdventureData;
import com.bingcrowsby.byoadventure.Controller.MapConfig;
import com.bingcrowsby.byoadventure.GMapV2Direction;
import com.bingcrowsby.byoadventure.Model.AdventureObject;
import com.bingcrowsby.byoadventure.Model.DirectionsObject;
import com.bingcrowsby.byoadventure.Model.MySharedPrefs;
import com.bingcrowsby.byoadventure.R;
import com.bingcrowsby.byoadventure.Statics;
import com.bingcrowsby.byoadventure.View.CustomSlidingPaneLayout;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import actionbartoggle.ActionBarToggle;

/**
 * Created by kevinfinn on 1/8/15.
 */
public class CreateActivity extends MapActivity {
    static final int RESULT_RELOAD = 5;
    static final String INTENT_INDEX = "index";

    CustomSlidingPaneLayout slidingPaneLayout;
    ActionBarToggle mDrawerToggle;
    FragmentManager fm;
    SupportMapFragment mapFragment;
    CameraPosition cameraPos;
    Marker startMarker, endMarker;
    EditText startEdit, endEdit, timeEdit, distEdit, titleEdit;
    TextView distanceTextView;
    AdventureObject advObject;
    MapConfig mapConfig;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setIcon(getResources().getDrawable(R.drawable.ic_launcher));
        getSupportActionBar().setHomeButtonEnabled(true);

        mapFragment = Statics.getMapFragment();

        fm = getSupportFragmentManager();

        slidingPaneLayout = (CustomSlidingPaneLayout) findViewById(R.id.drawer_layout);

        startEdit = (EditText) findViewById(R.id.editTextStart);
        endEdit = (EditText) findViewById(R.id.editTextEnd);
        timeEdit = (EditText) findViewById(R.id.editTextDuration);
        distEdit = (EditText) findViewById(R.id.editTextDistance);
        titleEdit = (EditText) findViewById(R.id.editTextTitle);

        distanceTextView = (TextView) findViewById(R.id.textViewDistance);
        mDrawerToggle = new ActionBarToggle(this, slidingPaneLayout,
                R.drawable.ic_launcher, R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(getString(R.string.app_name));
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle("drawer title");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        // Set the drawer toggle as the DrawerListener
        slidingPaneLayout.setPanelSlideListener(mDrawerToggle);

    }

    String translateScale(int index) {
        return "1:" + (index + 10) / 10;
    }

    @Override
    public void onResume() {
        super.onResume();

        FragmentHelper.initFragment(mapFragment, R.id.frame, fm, "map");
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        if(latLng == null) return;

//                        Log.i("createActivity", "map clicked at " + latLng);
                        if (startMarker == null) {
                            startMarker = mMap.addMarker(new MarkerOptions().title("START").position(latLng));
                            startEdit.setText(Statics.formatLocation(latLng));
                        } else {
                            if (endMarker == null)
                                endMarker = mMap.addMarker(new MarkerOptions().title("END").position(latLng));
                            else
                                endMarker.setPosition(latLng);

                            endEdit.setText(Statics.formatLocation(latLng));
                        }

                    }
                });
                mapConfig = MySharedPrefs.getMapConfig(CreateActivity.this);
                cameraPos = mapConfig.getCameraPosition();
                if (cameraPos != null)
                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPos));

            }
        });
    }


    @Override
    public void onPause() {
        super.onPause();

        fm = getSupportFragmentManager();

        FragmentTransaction ft = fm.beginTransaction();
        ft.remove(mapFragment);
        ft.commit();

        GoogleMap myMap = mapFragment.getMap();
        if (myMap != null) {
            MySharedPrefs.saveMapConfig(CreateActivity.this, mapConfig);
        }
    }

    @Override
    public void handleGetDirectionsResult(DirectionsObject result) {
        super.handleGetDirectionsResult(result);

        if (mMap != null) {
//            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(result.bounds, 25));

            timeEdit.setText(Statics.formatTime(result.duration));
            distEdit.setText(Statics.formatDistance(result.distance));
            distanceTextView.setText(Statics.formatDistance(result.distance));

            try {
                advObject = new AdventureObject.Builder(DirectionsObject.getPointListFromPolyline(result.polyline)).build();
                advObject.setTotalDistance(result.distance);
                advObject.setCurrentDistance(0);
            } catch (Exception e) {
                new AlertDialog.Builder(this).setTitle("Error").setMessage("AdventureObject Builder() returned error: " + e.getMessage()).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

//        noinspection SimplifiableIfStatement
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }


        if (id == R.id.action_dir) {
            if (startMarker != null && endMarker != null) {
                LatLng start = startMarker.getPosition();
                LatLng end = endMarker.getPosition();
                findDirections(start.latitude, start.longitude, end.latitude, end.longitude, GMapV2Direction.MODE_WALKING);
            }

            return true;
        }
        if (id == R.id.action_save) {
            if (advObject != null) {
                advObject.madventureTitle = titleEdit.getText().toString();
                AdventureData adventureData = AdventureData.getInstance(this);
                adventureData.saveAdventure(advObject);
                int count = adventureData.getAdventureCount();
                MySharedPrefs.saveAdv(this, count-1);
                setResult(RESULT_RELOAD);
            }
            else
                setResult(RESULT_OK);

            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

}