package com.example.tberroa.portal.data;

import android.content.Context;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Select;
import com.example.tberroa.portal.models.summoner.Summoner;

import java.util.ArrayList;
import java.util.List;

public class LocalDB {

    public LocalDB() {
    }

    public Summoner getSummonerById(long id) {
        return new Select()
                .from(Summoner.class)
                .where("summoner_id = ?", id)
                .executeSingle();
    }

    public List<Summoner> getSummonersByKeys(List<String> keys){
        List<Summoner> summoners = new ArrayList<>();
        ActiveAndroid.beginTransaction();
        try{
            for (String key : keys){
                Summoner summoner = new Select().from(Summoner.class).where("key = ?", key).executeSingle();
                if (summoner != null){
                    summoners.add(summoner);
                }
            }
            ActiveAndroid.setTransactionSuccessful();
        }finally {
            ActiveAndroid.endTransaction();
        }
        return summoners;
    }

    public void clearDatabase(Context context) {
        ActiveAndroid.dispose();
        context.deleteDatabase("Portal.db");
        ActiveAndroid.initialize(context);
    }
}


