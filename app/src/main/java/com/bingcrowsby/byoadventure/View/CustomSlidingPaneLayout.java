package com.bingcrowsby.byoadventure.View;

import android.content.Context;
import android.support.v4.widget.SlidingPaneLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by kevinfinn on 1/14/15.
 */
public class CustomSlidingPaneLayout extends SlidingPaneLayout {
    boolean slidingEnabled;

    public CustomSlidingPaneLayout(Context context, AttributeSet attr, int numbs){
        super(context,attr,numbs);
    }

    public CustomSlidingPaneLayout(Context context, AttributeSet attr){
        super(context,attr);
    }

    public CustomSlidingPaneLayout(Context context) {
        super(context);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if(isOpen())
            return true;
        else
            return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (!isOpen()) {

            getChildAt(1).dispatchTouchEvent(ev);

            return true;
        }
        else{
            getChildAt(0).dispatchTouchEvent(ev);
            return true;
        }

    }
}
