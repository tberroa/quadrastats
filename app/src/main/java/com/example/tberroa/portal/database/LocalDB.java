package com.example.tberroa.portal.database;

import android.content.Context;
import android.util.Log;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Select;
import com.example.tberroa.portal.data.Params;
import com.example.tberroa.portal.models.summoner.SummonerDto;

public class LocalDB {

    public LocalDB(){
    }

    public SummonerDto getSummoner(String name){
        return new Select()
                .from(SummonerDto.class)
                .where("name = ?", name)
                .executeSingle();
    }

    public void clear(Context context){
        Log.d(Params.TAG_DEBUG, "@LocalDB Clear: calling dispose");
        ActiveAndroid.dispose();
        Log.d(Params.TAG_DEBUG, "@LocalDB Clear: calling initialize");
        ActiveAndroid.initialize(context);
        Log.d(Params.TAG_DEBUG, "@LocalDB Clear: line after initialize");
    }
}


