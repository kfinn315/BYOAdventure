package com.bingcrowsby.byoadventure.Activity;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.bingcrowsby.byoadventure.R;

/**
 * Created by kevinfinn on 2/18/15.
 */
public class PrefsActivity extends PreferenceActivity {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.preferences);
        }

}
