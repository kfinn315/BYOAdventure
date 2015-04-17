package com.bingcrowsby.byoadventure.Activity;

/**
 * Created by kevinfinn on 1/10/15.
 */
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

public class FragmentHelper {

    public static void initFragment( Fragment frag, int container, FragmentManager fm, String tag) {
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(container, frag, tag);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();
    }

    public static void swapFragment( int container1, int container2, FragmentManager fm ) {

        Fragment f1 = fm.findFragmentById(container1);
        Fragment f2 = fm.findFragmentById(container2);

        FragmentTransaction ft = fm.beginTransaction();
        ft.remove(f1);
        ft.remove(f2);
        ft.commit();
        fm.executePendingTransactions();

        ft = fm.beginTransaction();
        ft.add(container1, f2);
        ft.add(container2, f1);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);

        ft.commit();
    }
}
