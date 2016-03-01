package com.example.tberroa.portal.models.matchlist;

// This object contains match reference information

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;

@Table(name = "MatchReference")
public class MatchReference extends Model {

    public MatchReference(){
    }

    @Expose
    @Column(name = "champion")
    public long champion;

    @Expose
    @Column(name = "lane")          // Legal values: MID, MIDDLE, TOP, JUNGLE, BOT, BOTTOM
    public String lane;

    @Expose
    @Column(name = "match_id")
    public long matchId;

    @Expose
    @Column(name = "platform_id")
    public String platformId;

    @Expose
    @Column(name = "queue")         // Legal values: TEAM_BUILDER_DRAFT_RANKED_5x5, RANKED_SOLO_5x5,
    public String queue;            // RANKED_TEAM_3x3, RANKED_TEAM_5x5

    @Expose
    @Column(name = "region")
    public String region;

    @Expose
    @Column(name = "role")          // Legal values: DUO, NONE, SOLO, DUO_CARRY, DUO_SUPPORT
    public String role;

    @Expose
    @Column(name = "season")        // Legal values: PRESEASON3, SEASON3, PRESEASON2014, SEASON2014,
    public String season;           // PRESEASON2015, SEASON2015, PRESEASON2016, SEASON2016

    @Expose
    @Column(name = "timestamp")
    public long timestamp;

}
