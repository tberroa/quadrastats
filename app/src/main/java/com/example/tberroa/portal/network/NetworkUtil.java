package com.example.tberroa.portal.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.example.tberroa.portal.data.Params;

public class NetworkUtil {

    private NetworkUtil() {
    }

    public static boolean isInternetAvailable(Context context) {
        boolean isInternetAvailable = false;
        try {
            ConnectivityManager connectivityManager =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null && (networkInfo.isConnected())) {
                isInternetAvailable = true;
            }
        } catch (Exception e) {
            Log.e(Params.TAG_EXCEPTIONS, "@isNetAvailable: " + e.getMessage());
        }
        return isInternetAvailable;
    }
}
