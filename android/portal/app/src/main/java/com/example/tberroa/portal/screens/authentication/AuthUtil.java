package com.example.tberroa.portal.screens.authentication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.example.tberroa.portal.R;
import com.example.tberroa.portal.data.Constants;
import com.example.tberroa.portal.data.LocalDB;
import com.example.tberroa.portal.data.UserInfo;
import com.example.tberroa.portal.models.summoner.Summoner;
import com.example.tberroa.portal.models.summoner.User;

public class AuthUtil {

    private AuthUtil() {
    }

    public static String decodeRegion(int position) {
        switch (position) {
            case 1:
                return Constants.REGION_BR;
            case 2:
                return Constants.REGION_EUNE;
            case 3:
                return Constants.REGION_EUW;
            case 4:
                return Constants.REGION_KR;
            case 5:
                return Constants.REGION_LAN;
            case 6:
                return Constants.REGION_LAS;
            case 7:
                return Constants.REGION_NA;
            case 8:
                return Constants.REGION_OCE;
            case 9:
                return Constants.REGION_RU;
            case 10:
                return Constants.REGION_TR;
            default:
                return "";
        }
    }

    public static void signIn(Context context, Summoner summoner, User user) {
        UserInfo userInfo = new UserInfo();

        // clear local database
        new LocalDB().clearDatabase(context);

        // clear old user info
        userInfo.clear(context);

        // save user info
        userInfo.setEmail(context, user.email);
        userInfo.setId(context, summoner.summoner_id);
        userInfo.setSignInStatus(context, true);

        // save the user's summoner object locally
        summoner.save();

        // apply animation for entering splash activity
        ((Activity) context).overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);

        // go to splash activity
        context.startActivity(new Intent(context, SplashActivity.class));
        ((Activity) context).finish();
    }

    public static void signOut(Context context) {
        // clear user data
        new UserInfo().clear(context);

        // clear local database
        new LocalDB().clearDatabase(context);

        // go to sign in page
        Intent signOutIntent = new Intent(context, SignInActivity.class);
        signOutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(signOutIntent);
    }
}
