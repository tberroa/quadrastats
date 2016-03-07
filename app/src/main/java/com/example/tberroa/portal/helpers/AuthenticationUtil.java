package com.example.tberroa.portal.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.tberroa.portal.R;
import com.example.tberroa.portal.activities.SignInActivity;
import com.example.tberroa.portal.activities.SplashActivity;
import com.example.tberroa.portal.data.SummonerInfo;
import com.example.tberroa.portal.data.UpdateServiceState;
import com.example.tberroa.portal.database.LocalDB;
import com.example.tberroa.portal.database.RiotAPI;
import com.example.tberroa.portal.models.summoner.SummonerDto;
import com.example.tberroa.portal.services.UpdateService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AuthenticationUtil {

    private AuthenticationUtil(){
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

    public static void signIn(final Context context, String summonerName, String region, boolean inView){
        SummonerInfo summonerInfo = new SummonerInfo();

        // clear shared preferences of old data
        summonerInfo.clear(context);

        // save summoner name
        summonerInfo.setBasicName(context, summonerName.toLowerCase());

        // save region
        summonerInfo.setRegion(context, region);

        // update summoner sign in status
        summonerInfo.setSummonerStatus(context, true);

        // start update service
        context.startService(new Intent(context, UpdateService.class));

        // save summoner id and stylized summoner name
        new Thread(new Runnable() {
            @Override
            public void run() {
                RiotAPI riotAPI = new RiotAPI(context);
                SummonerInfo summonerInfo = new SummonerInfo();
                String basicName = summonerInfo.getBasicName(context);
                List<String> nameList = new ArrayList<>();
                nameList.add(basicName);
                Map<String, SummonerDto> summonerMap = riotAPI.getSummonersByName(nameList);
                if (summonerMap != null){
                    summonerInfo.setId(context, summonerMap.get(basicName).id);
                    summonerInfo.setStylizedName(context, summonerMap.get(basicName).name);
                }
            }
        }).start();

        // go to splash page if app is in view
        if (inView){
            context.startActivity(new Intent(context, SplashActivity.class));
        }

        // destroy activity
        if(context instanceof Activity){
            ((Activity)context).overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
            ((Activity)context).finish();
        }
    }

    public static void signOut(Context context){
        SummonerInfo summonerInfo = new SummonerInfo();

        // clear shared preferences of old data
        summonerInfo.clear(context);

        // clear database
        new LocalDB().clear(context);

        // reset update service state
        new UpdateServiceState().set(context, 0);

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
}
