package com.example.tberroa.portal.data;

import android.content.Context;

import com.activeandroid.ActiveAndroid;

public class DataUtil {

    private DataUtil(){
    }

    static public String summonerIcon(int iconId){
        return Params.DATA_DRAGON_BASE_URL + "6.4.1/img/profileicon/"+iconId+".png";
    }

    static public void clearDatabase(Context context){
        ActiveAndroid.dispose();
        context.deleteDatabase("Portal.db");
        ActiveAndroid.initialize(context);
    }
}
