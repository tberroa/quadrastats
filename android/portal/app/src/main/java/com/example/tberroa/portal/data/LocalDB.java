package com.example.tberroa.portal.data;

import android.content.Context;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Select;
import com.example.tberroa.portal.models.summoner.Summoner;

public class LocalDB {

    public LocalDB() {
    }

    public Summoner getSummonerByName(String name) {
        return new Select()
                .from(Summoner.class)
                .where("name = ?", name)
                .executeSingle();
    }


    public void clearDatabase(Context context) {
        ActiveAndroid.dispose();
        context.deleteDatabase("Portal.db");
        ActiveAndroid.initialize(context);
    }
}


