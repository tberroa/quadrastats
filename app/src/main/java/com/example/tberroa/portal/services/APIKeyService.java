package com.example.tberroa.portal.services;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

import com.example.tberroa.portal.data.APIKeyUsage;

public class APIKeyService extends Service {

    private APIKeyUsage keyUsage = new APIKeyUsage();
    private boolean kill = false;

    @Override
    public void onCreate() {

        final Handler h = new Handler();
        final int delay = 10 * 1000; // 10 seconds

        h.postDelayed(new Runnable() {
            public void run() {
                keyUsage.reset(APIKeyService.this);
                if (!kill) {
                    h.postDelayed(this, delay);
                }
            }
        }, delay);
    }

    @Override
    public void onDestroy() {
        kill = true;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
