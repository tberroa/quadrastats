package com.example.tberroa.portal.updater;

import android.app.ActivityManager;
import android.content.Context;

import java.util.Map;

public class UpdateUtil {

    private UpdateUtil() {
    }

    static public void addPlayerToProfileMap(Context context, String stylizedName) {
        // initialize update job info
        UpdateJobInfo updateJobInfo = new UpdateJobInfo();

        // get the current profile map
        Map<String, PlayerUpdateProfile> profileMap = updateJobInfo.getProfiles(context);

        // create a new player update profile with the new players name
        PlayerUpdateProfile newEntry = new PlayerUpdateProfile();
        newEntry.name = stylizedName;

        // add the new player to the profile map
        profileMap.put(stylizedName, newEntry);

        // save the updated profile map
        updateJobInfo.setProfiles(context, profileMap);
    }

    static public boolean isMyServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}
