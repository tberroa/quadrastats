package com.example.tberroa.portal;

import com.activeandroid.app.Application;
import com.example.tberroa.portal.data.Constants;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;

@ReportsCrashes(formUri = Constants.URL_TRACEPOT)
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ACRA.init(this);
    }
}