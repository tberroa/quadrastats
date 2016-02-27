package com.example.tberroa.portal.models.match;

// This object contains team information

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;

import java.util.List;

@Table(name = "Team")
public class Team extends Model {

    @Expose
    @Column(name = "bans")
    public List<BannedChampion> bans;      // If game was draft mode, contains banned champion data, otherwise null

    @Expose
    @Column(name = "baron_kills")
    public int baronKills;                  // number of times the team killed baron

    @Expose
    @Column(name = "dominion_victory_score")
    public long dominionVictoryScore;       // If game was a dominion game, specifies the points the team had at
                                            // game end, otherwise null

    @Expose
    @Column(name = "dragon_kills")
    public int dragonKills;                 // number of times the team killed dragon

    @Expose
    @Column(name = "first_baron")
    public boolean firstBaron;              // Flag indicating whether or not the team got the first baron kill

    @Expose
    @Column(name = "first_blood")           // Flag indicating whether or not the team got first blood
    public boolean firstBlood;

    @Expose
    @Column(name = "first_dragon")          // Flag indicating whether or not the team got the first dragon kill
    public boolean firstDragon;

    @Expose
    @Column(name = "first_inhibitor")       // Flag indicating whether or not the team destroyed the first inhibitor
    public boolean firstInhibitor;

    @Expose
    @Column(name = "first_rift_herald")
    public boolean firstRiftHerald;         // Flag indicating whether or not the team got the first rift herald kill

    @Expose
    @Column(name = "first_tower")
    public boolean firstTower;              // 	Flag indicating whether or not the team destroyed the first tower

    @Expose
    @Column(name = "inhibitor_kills")
    public int inhibitorKills;              // Number of inhibitors the team destroyed

    @Expose
    @Column(name = "rift_herald_kills")
    public int riftHeraldKills;             // Number of times the team killed rift herald

    @Expose
    @Column(name = "team_id")
    public int teamId;                      // Team ID

    @Expose
    @Column(name = "tower_kills")
    public int towerKills;                  // Number of towers the team destroyed

    @Expose
    @Column(name = "vilemaw_kills")
    public int vilemawKills;                // Number of times the team killed vilemaw

    @Expose
    @Column(name = "winner")
    public boolean winner;                  // 	Flag indicating whether or not the team won





}
