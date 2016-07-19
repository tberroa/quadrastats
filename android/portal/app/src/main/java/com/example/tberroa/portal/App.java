package com.example.tberroa.portal;

import com.activeandroid.app.Application;

import org.acra.*;
import org.acra.annotation.*;

@ReportsCrashes(formUri = "https://collector.tracepot.com/dc940cee")
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ACRA.init(this);
    }
}