package com.example.tberroa.portal.apimanager;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.example.tberroa.portal.data.Params;

public class APIMonitorService extends Service {

    private final APIUsageInfo keyUsage = new APIUsageInfo();
    private boolean kill = false;

    @Override
    public void onCreate() {
        Log.d(Params.TAG_DEBUG, "@APIMonitorService: service has been started");

        final Handler h = new Handler();
        final int delay = 10 * 1000; // 10 seconds

        h.postDelayed(new Runnable() {
            public void run() {
                keyUsage.reset(APIMonitorService.this);
                if (!kill) {
                    h.postDelayed(this, delay);
                }
            }
        }, delay);
    }

    @Override
    public void onDestroy() {
        kill = true;
        keyUsage.reset(this);
        Log.d(Params.TAG_DEBUG, "@APIMonitorService: service has been killed");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
