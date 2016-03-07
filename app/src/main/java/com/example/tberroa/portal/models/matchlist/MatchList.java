package com.example.tberroa.portal.models.matchlist;

// This object contains match list information

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;

import java.util.List;

@SuppressWarnings({"WeakerAccess", "unused"})
@Table(name = "MatchList")
public class MatchList extends Model {

    @Expose
    @Column(name = "summoner_id")
    public long summonerId;

    @Expose
    @Column(name = "end_index")
    public int endIndex;

    @Expose
    @Column(name = "matches")
    public List<MatchReference> matches;

    public List<MatchReference> getMatchReferences(){
        return getMany(MatchReference.class, "match_list");
    }

    @Expose
    @Column(name = "start_index")
    public int startIndex;

    @Expose
    @Column(name = "total_games")
    public int totalGames;

    public MatchList(){
        super();
    }

    public void cascadeSave(){
        ActiveAndroid.beginTransaction();
        try{
            save();
            if (matches != null){
                for (MatchReference match : matches){
                    match.matchList = this;
                    match.save();
                }
            }
            ActiveAndroid.setTransactionSuccessful();
        } finally {
            ActiveAndroid.endTransaction();
        }
    }
}
