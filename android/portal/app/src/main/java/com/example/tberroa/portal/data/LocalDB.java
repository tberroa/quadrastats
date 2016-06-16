package com.example.tberroa.portal.data;

import android.content.Context;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Select;
import com.example.tberroa.portal.models.stats.MatchStats;
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

    public Summoner getSummonerByKey(String key) {
        return new Select()
                .from(Summoner.class)
                .where("key = ?", key)
                .executeSingle();
    }

    public List<Summoner> getSummonersByKeys(List<String> keys){
        List<Summoner> summoners = new ArrayList<>();
        ActiveAndroid.beginTransaction();
        try{
            for (String key : keys){
                Summoner summoner = new Select()
                        .from(Summoner.class)
                        .where("key = ?", key)
                        .executeSingle();
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

    public List<MatchStats> getMatchStatsList(List<Long> ids){
        List<MatchStats> matchStatsList = new ArrayList<>();
        ActiveAndroid.beginTransaction();
        try{
            for (Long id : ids){
                List<MatchStats> stats = new Select()
                        .from(MatchStats.class)
                        .where("summoner_id = ?", id)
                        .orderBy("match_creation DESC")
                        .execute();
                if (stats != null){
                    matchStatsList.addAll(stats);
                }
            }
            ActiveAndroid.setTransactionSuccessful();
        }finally {
            ActiveAndroid.endTransaction();
        }
        return matchStatsList;
    }

    public MatchStats getMatchStats(long summoner_id, long match_id){
        return new Select()
                .from(MatchStats.class)
                .where("summoner_id = ?", summoner_id)
                .where("match_id = ?", match_id)
                .executeSingle();
    }

    public void clearDatabase(Context context) {
        ActiveAndroid.dispose();
        context.deleteDatabase("Portal.db");
        ActiveAndroid.initialize(context);
    }
}


