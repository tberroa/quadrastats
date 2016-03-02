package com.example.tberroa.portal.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.WindowManager;

import com.example.tberroa.portal.activities.HomeActivity;
import com.example.tberroa.portal.activities.SignInActivity;
import com.example.tberroa.portal.data.UserInfo;
import com.example.tberroa.portal.database.LocalDB;

public class Utilities {

    private Utilities(){
    }

    public static String validate(Bundle enteredInfo){
        String validation = "";

        // grab entered information
        String summonerName = enteredInfo.getString("summoner_name", null);
        String password = enteredInfo.getString("password", null);
        String confirmPassword = enteredInfo.getString("confirm_password", null);

        if (summonerName != null){
            boolean tooShort = summonerName.length() < 3;
            boolean tooLong = summonerName.length() > 16;
            if (!summonerName.matches("[a-zA-Z0-9]+") || tooShort || tooLong ) {
                validation = validation.concat("summoner_name");
            }
        }

        if (password != null){
            if (password.length() < 6 || password.length() > 20) {
                validation = validation.concat("pass_word");
            }
        }

        if (confirmPassword != null){
            if (!confirmPassword.equals(password)) {
                validation = validation.concat("confirm_password");
            }
        }
        return validation;
    }

    public static void signIn(Context context, String summonerName){
        UserInfo userInfo = new UserInfo();

        // clear shared preferences of old data
        userInfo.clear(context);

        // save summoner name
        userInfo.setSummonerName(context, summonerName);

        // save region (default is na for now, need to update sign in page & backend/altervista)
        userInfo.setRegion(context, "na");

        // update user sign in status
        userInfo.setUserStatus(context, true);

        // go to home page
        context.startActivity(new Intent(context, HomeActivity.class));
        if(context instanceof Activity){
            ((Activity)context).finish();
        }
    }

    public static void signOut(Context context){
        UserInfo userInfo = new UserInfo();

        // clear shared preferences of old data
        userInfo.clear(context);

        // clear database
        new LocalDB().clear(context);

        // go to sign in page
        context.startActivity(new Intent(context, SignInActivity.class));
        if(context instanceof Activity){
            ((Activity)context).finish();
        }
    }

    public static String decodeRegion(int position){
        switch(position){
            case 1:
                return "br";
            case 2:
                return "eune";
            case 3:
                return "euw";
            case 4:
                return "kr";
            case 5:
                return "lan";
            case 6:
                return "las";
            case 7:
                return "na";
            case 8:
                return "oce";
            case 9:
                return "ru";
            case 10:
                return "tr";
            default:
                return "";
        }
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
