package com.example.tberroa.portal.models.match;

// This object contains information about banned champions

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;

@Table(name = "BannedChampion")
public class BannedChampion extends Model {

    public BannedChampion(){
    }

    @Expose
    @Column(name = "champion_id")       // Banned champion ID
    public int championId;

    @Expose
    @Column(name = "pick_turn")         // Turn during which the champion was banned
    public int pickTurn;
}


