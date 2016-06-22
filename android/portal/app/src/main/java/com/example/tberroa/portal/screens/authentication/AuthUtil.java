package com.example.tberroa.portal.screens.authentication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.example.tberroa.portal.R;
import com.example.tberroa.portal.data.LocalDB;
import com.example.tberroa.portal.data.UserInfo;
import com.example.tberroa.portal.models.summoner.Summoner;

public class AuthUtil {

    private AuthUtil() {
    }

    public static void signIn(final Context context, final Summoner summoner, final boolean inView) {
        UserInfo userInfo = new UserInfo();

        // clear local database
        new LocalDB().clearDatabase(context);

        // clear old user info
        userInfo.clear(context);

        // save user info
        userInfo.setName(context, summoner.name);
        userInfo.setId(context, summoner.summoner_id);
        userInfo.setSignInStatus(context, true);

        // save the user's summoner object locally
        summoner.save();

        // start sign in intent service
        context.startService(new Intent(context, SignInIntentService.class));

        // go to splash page if app is in view
        if (inView) {
            context.startActivity(new Intent(context, SplashActivity.class));

            // apply animation for entering splash page
            ((Activity) context).overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
        }

        ((Activity) context).finish();
    }

    public static void signOut(Context context) {
        // clear user data
        new UserInfo().clear(context);

        // clear local database
        new LocalDB().clearDatabase(context);

        // go to sign in page
        context.startActivity(new Intent(context, SignInActivity.class));
        ((Activity) context).finish();
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
