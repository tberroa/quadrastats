package com.example.tberroa.portal.models.match;

// This object contains match player information

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;

@Table(name = "Player")
public class Player {

    @Expose
    @Column(name = "match_history_uri")
    public String matchHistoryUri;

    @Expose
    @Column(name = "profile_icon")
    public int profileIcon;

    @Expose
    @Column(name = "summoner_id")
    public long summonerId;

    @Expose
    @Column(name = "summoner_name")
    public long summonerName;

}
