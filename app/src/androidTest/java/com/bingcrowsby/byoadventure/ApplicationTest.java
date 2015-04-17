package com.bingcrowsby.byoadventure;

import android.app.Application;
import android.test.ApplicationTestCase;

import com.bingcrowsby.byoadventure.Model.AdventureMap;

import org.mockito.Mockito;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
        Mockito.mock(AdventureMap.class);
    }
}