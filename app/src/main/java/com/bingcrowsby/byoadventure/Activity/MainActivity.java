package com.bingcrowsby.byoadventure.Activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bingcrowsby.byoadventure.AdventureData;
import com.bingcrowsby.byoadventure.Controller.AdventureAdapter;
import com.bingcrowsby.byoadventure.Controller.AdventureEventManager;
import com.bingcrowsby.byoadventure.Controller.AdventureLoader;
import com.bingcrowsby.byoadventure.Controller.MapConfig;
import com.bingcrowsby.byoadventure.Controller.UserConfig;
import com.bingcrowsby.byoadventure.Controller.UserStateManager;
import com.bingcrowsby.byoadventure.Model.AdventureDrawer;
import com.bingcrowsby.byoadventure.Model.AdventureObject;
import com.bingcrowsby.byoadventure.Model.MySharedPrefs;
import com.bingcrowsby.byoadventure.R;
import com.bingcrowsby.byoadventure.Statics;
import com.bingcrowsby.byoadventure.View.UserStateAlert;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.jwetherell.pedometer.activity.OnStepListener;
import com.jwetherell.pedometer.activity.PedometerActivity;
import com.jwetherell.pedometer.activity.PedometerManager;


public class MainActivity extends MapActivity implements AdventureObject.OnPositionChangedListener, UserStateManager.HealthChangeListener, UserStateManager.LivesChangeListener {
    static final String tag = "main";
    static int STEP_DISTANCE = 5;

    DrawerLayout drawerLayout;
    FragmentManager fm;
    SupportMapFragment mapFragment;
    CameraPosition cameraPos;
    ListView listView;
    ActionBarDrawerToggle drawerToggle;
    TextView textViewTitle, textViewDistance, textViewDaysElapsed, textViewHealth, textViewLives, textViewPace;
    AlertDialog addProgressDialog;
    View header;
//    Integer selectedIndexList = null;
    PedometerManager pedometerManager = null;
    AdventureAdapter adventureAdapter = null;
    UserStateManager userStateManager = null;
    AdventureEventManager adventureEventManager = null;
    DrawerItemClickListener drawerItemClickListener = null;

    Context mcontext;

    AdventureData adventureData = null;
    AdventureObject adventureObject;

    public AdventureObject getAdventureObject() {
        return adventureObject;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mcontext = this;

        mapFragment = Statics.getMapFragment();
        fm = getSupportFragmentManager();

        if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) == 0) {
            Log.i(tag, "google play services is not available");
        }

        textViewDistance = (TextView) findViewById(R.id.textViewDistance);
        textViewDistance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddProgressDialog();
            }
        });
        textViewTitle = (TextView) findViewById(R.id.textViewTitle);
        textViewTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdvMap.zoomToUser(mMap);
            }
        });
        textViewDaysElapsed = (TextView) findViewById(R.id.textViewDays);
        textViewHealth = (TextView) findViewById(R.id.textViewHealth);
        textViewLives = (TextView) findViewById(R.id.textViewLives);
        textViewPace = (TextView) findViewById(R.id.textViewPace);
        textViewPace.setText(STEP_DISTANCE + "m/step");

        header = findViewById(R.id.header);
        header.setVisibility(View.GONE);

        drawerItemClickListener = new DrawerItemClickListener();
        listView = (ListView) findViewById(R.id.listview);
        listView.setOnItemClickListener(drawerItemClickListener);
        listView.setItemsCanFocus(true);
        listView.setSelector(R.drawable.bg_item);

        addHeaderToListview();

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, true,
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
        drawerLayout.setDrawerListener(drawerToggle);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        getSupportLoaderManager().initLoader(0, null, new AdventureLoaderCallbacks());

        pedometerManager = new PedometerManager(this);
        pedometerManager.setOnStepListener(new OnStepListener() {
            @Override
            public void onStep(int steps) {
                Log.i("pedo", steps + " steps");
//                textViewDistance.setText(steps+" steps");

                if (adventureObject != null)
                    adventureObject.addProgress(STEP_DISTANCE);
            }
        });


        userStateManager = UserStateManager.getInstance(this);
        userStateManager.setHealthChangeListener(this);
        userStateManager.setLivesChangeListener(this);

        adventureEventManager = new AdventureEventManager();
        adventureData = AdventureData.getInstance(this);
    }

    private void addHeaderToListview() {
        View headerView = LayoutInflater.from(this).inflate(R.layout.list_item, null, false);
        headerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawers();
                Intent intent = new Intent(MainActivity.this, CreateActivity.class);
                startActivityForResult(intent, 1);
            }
        });
        TextView textView = (TextView) headerView.findViewById(R.id.text1);
        textView.setText("CREATE NEW ADVENTURE");
        listView.addHeaderView(headerView);
    }

    @Override
    public void onPause() {
        super.onPause();

        Log.i(tag, "pause");

        FragmentTransaction ft = fm.beginTransaction();
        ft.remove(mapFragment);
        ft.commit();

        saveCameraPosition();
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.i(tag, "resume");
    }


    @Override
    public void onStart() {
        super.onStart();
        Log.i(tag, "start");

        pedometerManager.bind();

        loadUserConfig();
        updateUserState();

        FragmentHelper.initFragment(mapFragment, R.id.frame, fm, "map");
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final GoogleMap googleMap) {
                Log.i(tag, "Map is ready");
                mMap = googleMap;

                loadAdventureObjectFromSharedPrefs();

                if (adventureObject != null) {
                    try {
                        AdventureDrawer.drawAdventure(mcontext, googleMap, adventureObject.buildAdventureMap());
                    } catch (Exception e) {
                        new AlertDialog.Builder(MainActivity.this).setTitle("Error").setMessage(e.getMessage());
                    }
                } else {
                    cameraPos = getCameraPosition();
                    if (cameraPos != null) {
                        try {
                            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPos));
                        } catch (IllegalStateException e) {
                            googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                                @Override
                                public void onMapLoaded() {
                                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPos));
                                }
                            });
                        }
                    }
                }
            }
        });
    }
    @Override
    public void onStop() {
        super.onStop();
        Log.i(tag, "stop");

        pedometerManager.unbind();

        MySharedPrefs.saveUserConfig(this, new UserConfig(userStateManager.getHealth(), userStateManager.getLives()));
        MySharedPrefs.saveAdv(this, listView.getCheckedItemPosition());

        header.setVisibility(View.GONE);
    }

    private void loadUserConfig(){
        UserConfig userConfig = MySharedPrefs.getUserConfig(this);
        userStateManager.setUserState(userConfig.health, userConfig.lives);
    }
    private void saveCameraPosition(){
        CameraPosition mMyCam = mapFragment.getMap().getCameraPosition();
        double longitude = mMyCam.target.longitude;
        double latitude = mMyCam.target.latitude;
        float zoom = mMyCam.zoom;
        float bearing = mMyCam.bearing;
        float tilt = mMyCam.tilt;

        MySharedPrefs.saveMapConfig(this, (float) latitude, (float) longitude, zoom, bearing, tilt);
    }

    private CameraPosition getCameraPosition(){
        MapConfig mapConfig = MySharedPrefs.getMapConfig(MainActivity.this);
        try {
            return mapConfig.getCameraPosition();
        } catch(NullPointerException e){
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        if (id == R.id.action_settings) {
            Intent intent = new Intent(MainActivity.this, PrefsActivity.class);
            startActivity(intent);
//            Statics.clearAdventureInventory(this);
//            adventureAdapter.notifyDataSetChanged();
            return true;
        }
        if (id == R.id.action_pedometer) {
            Intent intent = new Intent(MainActivity.this, PedometerActivity.class);
            startActivity(intent);
//            try {
//                if (!pedometerManager.isPedometerRunning())
//                    pedometerManager.start();
//                else
//                    pedometerManager.stop();
//            } catch(RemoteException re){
//                re.printStackTrace();
//            }
            return true;
        }
        if (id == R.id.action_new) {
            Intent intent = new Intent(MainActivity.this, CreateActivity.class);
            startActivityForResult(intent, 1);
            return true;
        }
        if (id == R.id.action_progress) {
            showAddProgressDialog();
            return true;
        }
        if (id == R.id.action_resethealth) {
            userStateManager.resetHealth();
            return true;
        }
        if (id == R.id.action_resetlives) {
            userStateManager.resetLife();
            return true;
        }
        if (id == R.id.action_requestevent) {
            requestEvent();
            return true;
        }
        if (id == R.id.action_advanceDay) {
            userStateManager.advanceDays(1);
        }
        if (id == R.id.action_getResult) {

            ResultDialogFragment dialogFragment = new ResultDialogFragment();
            dialogFragment.show(fm, "result");
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPositionChanged(float newDistance, LatLng newPosition) {
        textViewDistance.setText(newDistance + "/" + adventureObject.getTotalDistance());
        mAdvMap.updateProgressMarker(newPosition);
        Log.i(tag, "update marker to " + newPosition);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//        Log.i(tag, "onActivityResult " + requestCode + " " + resultCode);
        if(resultCode == CreateActivity.RESULT_RELOAD)
            getSupportLoaderManager().getLoader(0).forceLoad();
    }

    private void updateUserState() {
        textViewHealth.setText(userStateManager.getHealth() + " ");
        textViewLives.setText(userStateManager.getLives() + " ");
    }

    public AdventureObject loadAdventure(int index) {
        Toast.makeText(MainActivity.this, "Load Adventure " + (index), Toast.LENGTH_LONG).show();

        adventureObject = adventureAdapter.getItem(index);
        Log.i(tag, "load adventure w/ index "+index);

        if (adventureObject != null) {
            adventureObject.setOnPositionChangedListener(this);
            mAdvMap = adventureObject.buildAdventureMap();
            if (mAdvMap != null)
                try {
                    AdventureDrawer.drawAdventure(this, mMap, mAdvMap);
                    setupAdventureDisplay(adventureObject);
                    setSelectedIndex(index);
                } catch (Exception e) {
                    new AlertDialog.Builder(this).setTitle("Error").setMessage("Unable to draw adventure: " + e.getMessage()).show();

                }
        }
        return adventureObject;
    }

    private void setSelectedIndex(int index){
        listView.setSelection(index);
        listView.setItemChecked(index, true);
    }

    private void setupAdventureDisplay(AdventureObject adventureObject){
        textViewDistance.setText(adventureObject.getCurrentDistance() + "/" + adventureObject.getTotalDistance());
        textViewTitle.setText(adventureObject.madventureTitle);
        textViewDaysElapsed.setText(adventureObject.getDaysElapsed());
        header.setVisibility(View.VISIBLE);
    }

    private void loadAdventureObjectFromSharedPrefs() {
        int advId = MySharedPrefs.getAdv(this);

        if (advId >= 0) {
            loadAdventure(advId);
        }
    }

    //UserStateManager.OnHealthChangedListener
    @Override
    public void onHealthChange(int newHealth) {
        AlertDialog dialog = UserStateAlert.getInstance(mcontext, "Health is " + newHealth);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                //setHealthDecreaseTimer(1);
            }
        });
        dialog.show();
        textViewHealth.setText(userStateManager.getHealth() + " ");

    }

    @Override
    public void onHealthIsZero() {
        UserStateAlert.getInstance(mcontext, "Your health is 0").show();
    }

    //UserStateManager.OnLivesChangedListener
    @Override
    public void onLivesChange(int newLives) {
        UserStateAlert.getInstance(mcontext, "You now have " + newLives + " lives").show();
        textViewLives.setText(userStateManager.getLives() + " ");
    }

    @Override
    public void onLifeIsZero() {
        UserStateAlert.getInstance(mcontext, "YOU ARE COMPLETELY DEAD").show();
    }

    private void requestEvent() {
        Log.i(tag, "requested event");
        AdventureEventManager.EventObject eventObject = adventureEventManager.getEvent(0);
        if (eventObject != null) {
            AdventureEventManager.EventAlert.getInstance(MainActivity.this, eventObject).show();
            userStateManager.decreaseHealth(eventObject.getDamage());
            STEP_DISTANCE = eventObject.getStepDistance();
            STEP_DISTANCE = STEP_DISTANCE * eventObject.getStepDistanceFactor();
            textViewPace.setText(STEP_DISTANCE + "m/step");
        }
    }

    private void showAddProgressDialog() {
        if (addProgressDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            addProgressDialog = builder.setTitle("Add distance to your progress").create();
            View dialogView = getLayoutInflater().inflate(R.layout.inputlayout, null);
            addProgressDialog.setView(dialogView);

            final EditText editText = (EditText) dialogView.findViewById(R.id.editTextProgress);
            addProgressDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    editText.setText("");
                }
            });

            Button button = (Button) dialogView.findViewById(R.id.buttonAdd);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        float distance = Float.valueOf(editText.getText().toString());
                        int selectedIndex = listView.getSelectedItemPosition();
                        AdventureObject advobj = adventureAdapter.getItem(selectedIndex);
                        if (advobj != null) {
                            advobj.addProgress(distance);
                            adventureData.saveAdventure(advobj);
                        }
                        //Marker newMark = mMap.addMarker(new MarkerOptions().position(advobj.getCurrentPosition()).title("Distance:" + distance));
                        Toast.makeText(MainActivity.this, "position updated", Toast.LENGTH_LONG).show();
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                    addProgressDialog.dismiss();
                }
            });
        }
        addProgressDialog.show();
    }

    class AdventureLoaderCallbacks implements LoaderManager.LoaderCallbacks<Cursor> {

        @Override
        public Loader onCreateLoader(int id, Bundle args) {
            Log.i(tag, "Loader created");

            AdventureLoader advLoader = new AdventureLoader(MainActivity.this);
            return advLoader;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
               Log.i(tag, "Loader finished");
            if (adventureAdapter == null) {
                adventureAdapter = new AdventureAdapter(MainActivity.this, cursor);
                listView.setAdapter(adventureAdapter);
            }
            adventureAdapter.changeCursor(cursor);
            listView.forceLayout();
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            Log.i(tag, "Loader reset");
            listView.setAdapter(null);
        }
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> arg0, View view, int position,
                                long arg3) {
            Log.i(tag, "clicked list's " + position + " position");

            drawerLayout.closeDrawers();
            loadAdventure(position-1);
        }
    }
}
