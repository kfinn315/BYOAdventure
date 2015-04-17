package com.bingcrowsby.byoadventure;

import android.app.Application;

import com.bingcrowsby.byoadventure.Controller.UserStateManager;
import com.bingcrowsby.byoadventure.Model.MySharedPrefs;

/**
 * Created by kevinfinn on 1/28/15.
 */
public class ByoaApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
//        AdventureData.getInstance(getApplicationContext());
        initUserState();

    }

    private void initUserState(){
        long dateLastopen = MySharedPrefs.getLastOpened(getApplicationContext());
        long dayInterval = Statics.getTimeIntervalInDays(dateLastopen, System.currentTimeMillis());

        UserStateManager.getInstance(this).advanceDays(dayInterval);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
//        AdventureData.getInstance(getApplicationContext()).close();
        MySharedPrefs.saveLastOpened(getApplicationContext(),System.currentTimeMillis());
    }
}
