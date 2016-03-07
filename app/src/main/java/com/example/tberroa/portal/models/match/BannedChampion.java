package com.example.tberroa.portal.models.match;

// This object contains information about banned champions

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;

@SuppressWarnings({"WeakerAccess", "unused"})
@Table(name = "BannedChampion")
public class BannedChampion extends Model {

    // parent
    @Expose
    @Column(name = "team")
    Team team;

    @Expose
    @Column(name = "champion_id")
    public int championId;

    @Expose
    @Column(name = "pick_turn")
    public int pickTurn;

    public BannedChampion(){
        super();
    }
}


