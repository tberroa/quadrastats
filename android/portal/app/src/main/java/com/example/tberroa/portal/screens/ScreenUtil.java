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

    static public String constructIconURL(int iconId) {
        return Params.RURL_DATA_DRAGON + Params.RURL_PROFILE_ICON + iconId + ".png";
    }

    static public int dpToPx(Context context, int dp){
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    static public int getScreenHeight(Context context) {
        return getScreenDimensions(context).y;
    }

    static public int getScreenWidth(Context context) {
        return getScreenDimensions(context).x;
    }

    static public int intToColor(int i){
        switch (i % 8) {
            case 0:
                return R.color.series_blue;
            case 1:
                return R.color.series_green;
            case 2:
                return R.color.series_orange;
            case 3:
                return R.color.series_pink;
            case 4:
                return R.color.series_purple;
            case 5:
                return R.color.series_red;
            case 6:
                return R.color.series_sky;
            case 7:
                return R.color.series_yellow;
            default:
                return R.color.series_blue;
        }
    }

    static public int intToSeriesColor(int i){
        switch (i % 8) {
            case 0:
                return R.xml.line_blue;
            case 1:
                return R.xml.line_green;
            case 2:
                return R.xml.line_orange;
            case 3:
                return R.xml.line_pink;
            case 4:
                return R.xml.line_purple;
            case 5:
                return R.xml.line_red;
            case 6:
                return R.xml.line_sky;
            case 7:
                return R.xml.line_yellow;
            default:
                return R.xml.line_blue;
        }
    }

    static private Point getScreenDimensions(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point screenDimensions = new Point();
        display.getSize(screenDimensions);
        return screenDimensions;
    }
}
