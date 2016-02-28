package com.example.tberroa.portal.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.tberroa.portal.activities.HomeActivity;
import com.example.tberroa.portal.data.UserInfo;

public class Utilities {

    private Utilities(){
    }

    public static String validate(Bundle enteredInfo){
        String validation = "";

        // grab entered information
        String summonerName = enteredInfo.getString("summoner_name", null);
        String password = enteredInfo.getString("password", null);
        String confirmPassword = enteredInfo.getString("confirm_password", null);
        String email = enteredInfo.getString("email", null);

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

        if (email != null){
            if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                validation = validation.concat("email");
            }
        }
        return validation;
    }

    public static void SignIn(Context context, String summonerName){
        UserInfo userInfo = new UserInfo();

        // clear shared preferences of old data
        userInfo.clear(context);

        // save summoner name
        userInfo.setSummonerName(context, summonerName);

        // update user sign in status
        userInfo.setUserStatus(context, true);

        // go to home page
        context.startActivity(new Intent(context, HomeActivity.class));
        if(context instanceof Activity){
            ((Activity)context).finish();
        }
    }



}
