package com.example.tberroa.portal.database;

import android.content.Context;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Select;
import com.example.tberroa.portal.models.match.MatchDetail;
import com.example.tberroa.portal.models.matchlist.MatchList;
import com.example.tberroa.portal.models.summoner.SummonerDto;

public class LocalDB {

    public LocalDB(){
    }

    public SummonerDto getSummoner(long id){
        return new Select()
                .from(SummonerDto.class)
                .where("summoner_id = ?", id)
                .executeSingle();
    }

    public MatchList getMatchList(long summonerId){
        return new Select()
                .from(MatchList.class)
                .where("summoner_id = ?", summonerId)
                .executeSingle();
    }

    public MatchDetail getMatchDetail(long matchId){
        return new Select()
                .from(MatchDetail.class)
                .where("match_id = ?", matchId)
                .executeSingle();
    }

    public void clear(Context context){
        ActiveAndroid.dispose();
        ActiveAndroid.initialize(context);
    }
}


