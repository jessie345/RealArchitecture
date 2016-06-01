package com.architecture.realarchitecture;

import android.app.Application;

/**
 * Created by liushuo on 16/6/1.
 */
public class MainApp extends Application {

    private static MainApp mAppCotext;

    @Override
    public void onCreate() {
        super.onCreate();
        mAppCotext = this;
    }

    public static Application getAppContext() {
        return mAppCotext;
    }
}
