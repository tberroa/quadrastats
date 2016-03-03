package com.example.tberroa.portal.helpers;

import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.WindowManager;

public class ScreenUtil {

    private ScreenUtil(){
    }

    private static Point getScreenDimensions(Context context){
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point screenDimensions = new Point();
        display.getSize(screenDimensions);
        return screenDimensions;
    }

    public static int getScreenWidth(Context context){
        return getScreenDimensions(context).x;
    }

    public static int getScreenHeight(Context context){
        return getScreenDimensions(context).y;
    }

    public static boolean isLandscape(Context context){
        boolean bool = false;
        if (getScreenWidth(context) > getScreenHeight(context)){
            bool = true;
        }
        return bool;
    }
}
