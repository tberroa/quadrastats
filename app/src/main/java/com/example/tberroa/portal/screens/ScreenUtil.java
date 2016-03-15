package com.example.tberroa.portal.screens;

import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.WindowManager;

import com.example.tberroa.portal.R;
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

    static public String stylizeQueue(Context context, String queue) {
        switch (queue) {
            case Params.DYNAMIC_QUEUE:
                return context.getString(R.string.dynamic_queue);
            case Params.SOLO_QUEUE:
                return context.getString(R.string.solo_queue);
            case Params.TEAM_5:
                return context.getString(R.string.team_5);
            case Params.TEAM_3:
                return context.getString(R.string.team_3);
        }
        return null;
    }

    static public String constructIconURL(int iconId) {
        return Params.DATA_DRAGON_BASE_URL + Params.PROFILE_ICON_URL + iconId + ".png";
    }
}
