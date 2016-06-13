package com.example.tberroa.portal.screens;

import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.WindowManager;

import com.example.tberroa.portal.data.Params;

public class ScreenUtil {

    private ScreenUtil() {
    }

    static private Point getScreenDimensions(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point screenDimensions = new Point();
        display.getSize(screenDimensions);
        return screenDimensions;
    }

    static public int getScreenWidth(Context context) {
        return getScreenDimensions(context).x;
    }

    static public int getScreenHeight(Context context) {
        return getScreenDimensions(context).y;
    }

    static public String constructIconURL(int iconId) {
        return Params.RURL_DATA_DRAGON + Params.RURL_PROFILE_ICON + iconId + ".png";
    }
}
