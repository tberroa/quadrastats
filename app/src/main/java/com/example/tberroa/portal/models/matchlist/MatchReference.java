package com.example.tberroa.portal.models.matchlist;

// This object contains match reference information

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;

@SuppressWarnings({"WeakerAccess", "unused"})
@Table(name = "MatchReference")
public class MatchReference extends Model {

    // parent
    @Expose
    @Column(name = "match_list")
    public MatchList matchList;

    @Expose
    @Column(name = "champion")
    public long champion;

    @Expose
    @Column(name = "lane")
    public String lane;

    @Expose
    @Column(name = "match_id")
    public long matchId;

    @Expose
    @Column(name = "platform_id")
    public String platformId;

    @Expose
    @Column(name = "queue")
    public String queue;

    @Expose
    @Column(name = "region")
    public String region;

    @Expose
    @Column(name = "role")
    public String role;

    @Expose
    @Column(name = "season")
    public String season;

    @Expose
    @Column(name = "timestamp")
    public long timestamp;

    public MatchReference(){
        super();
    }
}
