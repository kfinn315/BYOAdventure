package com.bingcrowsby.byoadventure.Controller;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.bingcrowsby.byoadventure.AdventureData;

/**
 * A custom Loader that loads all of the installed applications.`
 */
public class AdventureLoader extends AsyncTaskLoader<Cursor> {
    static String tag = "AdventureLoader";


    final InterestingConfigChanges mLastConfig = new InterestingConfigChanges();

//    List<AdventureObject> mAdvObjects;
    Cursor madventureCursor = null;
    AdventureData adventureData = null;

    public AdventureLoader(Context context) {
        super(context);
        adventureData = AdventureData.getInstance(context);

        // Retrieve the package manager for later use; note we don't
        // use 'context' directly but instead the save global application
        // context returned by getContext().
        //mPm = getContext().getPackageManager();
    }

    /**
     * This is where the bulk of our work is done.  This function is
     * called in a background thread and should generate a new set of
     * data to be published by the loader.
     */
    @Override
    public Cursor loadInBackground() {
        Log.i(tag,"loadInBackground");
        // Retrieve all known applications.
        adventureData.open();
//        adventureData.loadAdventureInventory();
        return adventureData.getAdventureCursor();
    }

    /**
     * Called when there is new data to deliver to the client.  The
     * super class will take care of delivering it; the implementation
     * here just adds a little more logic.
     */
    @Override
    public void deliverResult(Cursor adventureCursor) {
        Log.i(tag,"deliverResult()");

        if (isReset()) {
            // An async query came in while the loader is stopped.  We
            // don't need the result.
            if (adventureCursor != null) {
                onReleaseResources(adventureCursor);
            }
        }
        Cursor old = madventureCursor;
        madventureCursor = adventureCursor;

        if (isStarted()) {
            // If the Loader is currently started, we can immediately
            // deliver its results.
            super.deliverResult(adventureCursor);
        }

        // At this point we can release the resources associated with
        // 'oldApps' if needed; now that the new result is delivered we
        // know that it is no longer in use.
        if (old != null) {
            onReleaseResources(old);
        }
    }

    /**
     * Handles a request to start the Loader.
     */
    @Override
    protected void onStartLoading() {
        Log.i(tag,"onStartLoading()");

        if (madventureCursor != null) {
            // If we currently have a result available, deliver it
            // immediately.
            deliverResult(madventureCursor);
        }


        // Has something interesting in the configuration changed since we
        // last built the app list?
        boolean configChange = mLastConfig.applyNewConfig(getContext().getResources());

        if (takeContentChanged() || madventureCursor == null || configChange) {
            // If the data has changed since the last time it was loaded
            // or is not currently available, start a load.
            forceLoad();
        }
    }

    @Override
    protected void onForceLoad() {
        super.onForceLoad();
        Log.i("AdventureLoader", "onForceLoad()");
        deliverResult(loadInBackground());
    }

    /**
     * Handles a request to stop the Loader.
     */
    @Override
    protected void onStopLoading() {
        // Attempt to cancel the current load task if possible.
        cancelLoad();
    }

    /**
     * Handles a request to cancel a load.
     */
    @Override
    public void onCanceled(Cursor advCursor) {
        super.onCanceled(advCursor);

        // At this point we can release the resources associated with 'apps'
        // if needed.
        onReleaseResources(advCursor);
    }

    /**
     * Handles a request to completely reset the Loader.
     */
    @Override
    protected void onReset() {
        Log.i(tag,"onReset()");

        super.onReset();

        // Ensure the loader is stopped
        onStopLoading();

        // At this point we can release the resources associated with 'apps'
        // if needed.
        if (madventureCursor != null) {
            onReleaseResources(madventureCursor);
            madventureCursor = null;
        }
    }

    /**
     * Helper function to take care of releasing resources associated
     * with an actively loaded data set.
     */
    protected void onReleaseResources(Cursor advs) {
        // For a simple List<> there is nothing to do.  For something
        // like a Cursor, we would close it here.
//        advs.close();
    }

    /**
     * Helper for determining if the configuration has changed in an interesting
     * way so we need to rebuild the app list.
     */
    public static class InterestingConfigChanges {

        final Configuration mLastConfiguration = new Configuration();
        int mLastDensity;

        boolean applyNewConfig(Resources res) {
            int configChanges = mLastConfiguration.updateFrom(res.getConfiguration());
            boolean densityChanged = mLastDensity != res.getDisplayMetrics().densityDpi;
            if (densityChanged || (configChanges & (ActivityInfo.CONFIG_LOCALE
                    | ActivityInfo.CONFIG_UI_MODE | ActivityInfo.CONFIG_SCREEN_LAYOUT)) != 0) {
                mLastDensity = res.getDisplayMetrics().densityDpi;
                return true;
            }
            return false;
        }
    }
}
