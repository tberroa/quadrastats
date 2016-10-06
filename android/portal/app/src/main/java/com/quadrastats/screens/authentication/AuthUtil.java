package com.quadrastats.screens.authentication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.quadrastats.R;
import com.quadrastats.data.Constants;
import com.quadrastats.data.LocalDB;
import com.quadrastats.data.UserData;
import com.quadrastats.models.ModelUtil;
import com.quadrastats.models.summoner.Summoner;

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

    public static void signIn(Context context, Summoner summoner, boolean inView) {
        new LocalDB().clearDatabase(context);
        new UserData().clear(context);

        // serialize the users summoner object
        String summonerJson = ModelUtil.toJson(summoner, Summoner.class);

        // create splash activity intent
        Intent splashActivity = new Intent(context, SplashActivity.class);
        splashActivity.putExtra("summoner", summonerJson);
        splashActivity.putExtra("in_view", inView);

        // go to splash activity
        context.startActivity(splashActivity);

        // apply animation for entering splash activity
        ((Activity) context).overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);

        // finish current activity
        ((Activity) context).finish();
    }

    public static void signOut(Context context) {
        new UserData().clear(context);
        new LocalDB().clearDatabase(context);

        // go to sign in page
        Intent signOutIntent = new Intent(context, SignInActivity.class);
        signOutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(signOutIntent);
    }
}
