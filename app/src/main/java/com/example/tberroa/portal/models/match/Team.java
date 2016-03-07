package com.example.tberroa.portal.models.match;

// This object contains team information

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;

import java.util.List;

@SuppressWarnings({"WeakerAccess", "unused"})
@Table(name = "Team")
public class Team extends Model {

    // parent
    @Expose
    @Column(name = "match_detail")
    MatchDetail matchDetail;

    @Expose
    @Column(name = "bans")
    public List<BannedChampion> bans;

    public List<BannedChampion> getBans(){
        return getMany(BannedChampion.class, "team");
    }

    @Expose
    @Column(name = "baron_kills")
    public int baronKills;

    @Expose
    @Column(name = "dominion_victory_score")
    public long dominionVictoryScore;

    @Expose
    @Column(name = "dragon_kills")
    public int dragonKills;

    @Expose
    @Column(name = "first_baron")
    public boolean firstBaron;

    @Expose
    @Column(name = "first_blood")
    public boolean firstBlood;

    @Expose
    @Column(name = "first_dragon")
    public boolean firstDragon;

    @Expose
    @Column(name = "first_inhibitor")
    public boolean firstInhibitor;

    @Expose
    @Column(name = "first_rift_herald")
    public boolean firstRiftHerald;

    @Expose
    @Column(name = "first_tower")
    public boolean firstTower;

    @Expose
    @Column(name = "inhibitor_kills")
    public int inhibitorKills;

    @Expose
    @Column(name = "rift_herald_kills")
    public int riftHeraldKills;

    @Expose
    @Column(name = "team_id")
    public int teamId;

    @Expose
    @Column(name = "tower_kills")
    public int towerKills;

    @Expose
    @Column(name = "vilemaw_kills")
    public int vilemawKills;

    @Expose
    @Column(name = "winner")
    public boolean winner;

    public Team(){
        super();
    }

    public void cascadeSave(){
        ActiveAndroid.beginTransaction();
        try{
            save();
            if (bans != null){
                for (BannedChampion ban : bans){
                    ban.team = this;
                    ban.save();
                }
            }
            ActiveAndroid.setTransactionSuccessful();
        } finally {
            ActiveAndroid.endTransaction();
        }
    }
}
