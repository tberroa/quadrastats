package com.example.tberroa.portal.screens.authentication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.tberroa.portal.R;
import com.example.tberroa.portal.apimanager.APIUsageInfo;
import com.example.tberroa.portal.data.UserInfo;
import com.example.tberroa.portal.models.summoner.Summoner;
import com.example.tberroa.portal.apimanager.APIMonitorService;
import com.example.tberroa.portal.updater.UpdateJobInfo;
import com.example.tberroa.portal.updater.UpdateService;
import com.example.tberroa.portal.updater.UpdateUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AuthUtil {

    private AuthUtil() {
    }

    public static String validate(Bundle enteredInfo) {
        String validation = "";

        // grab entered information
        String summonerName = enteredInfo.getString("summoner_name", null);
        String password = enteredInfo.getString("password", null);
        String confirmPassword = enteredInfo.getString("confirm_password", null);

        if (summonerName != null) {
            boolean tooShort = summonerName.length() < 3;
            boolean tooLong = summonerName.length() > 16;
            if (!summonerName.matches("[a-zA-Z0-9]+") || tooShort || tooLong) {
                validation = validation.concat("summoner_name");
            }
        }

        if (password != null) {
            if (password.length() < 6 || password.length() > 20) {
                validation = validation.concat("pass_word");
            }
        }

        if (confirmPassword != null) {
            if (!confirmPassword.equals(password)) {
                validation = validation.concat("confirm_password");
            }
        }
        return validation;
    }

    public static void signIn(final Context context, final String summonerName, final String stylizedName,
                              final String region, final boolean inView) {
        UserInfo userInfo = new UserInfo();

        // add summoner to the profile map
        UpdateUtil.addPlayerToProfileMap(context, stylizedName);

        // save summoner name
        userInfo.setBasicName(context, summonerName.toLowerCase());

        // save stylized summoner name
        userInfo.setStylizedName(context, stylizedName);

        // save region
        userInfo.setRegion(context, region);

        // update summoner sign in status
        userInfo.setStatus(context, true);

        // start sign in intent service
        context.startService(new Intent(context, SignInIntentService.class));

        // go to splash page if app is in view
        if (inView) {
            context.startActivity(new Intent(context, SplashActivity.class));

            // apply sign in animation for entering splash page
            ((Activity) context).overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
        }

        ((Activity) context).finish();
    }

    public static void signOut(Context context) {
        // end update service
        context.stopService(new Intent(context, UpdateService.class));

        // end api key service
        context.stopService(new Intent(context, APIMonitorService.class));

        // clear data
        new UserInfo().clear(context);
        new UpdateJobInfo().clear(context);
        new APIUsageInfo().reset(context);

        // go to sign in page
        context.startActivity(new Intent(context, SignInActivity.class));
        ((Activity) context).finish();
    }

    public static Map<String, Summoner> validateName(Context context, String summonerName) {
        List<String> name = new ArrayList<>();
        name.add(summonerName);
        return new RiotAPI(context).getSummonersByName(name);
    }

    public static String decodeRegion(int position) {
        switch (position) {
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
}
