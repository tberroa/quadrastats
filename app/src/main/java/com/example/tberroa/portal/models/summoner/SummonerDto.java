package com.example.tberroa.portal.models.summoner;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;

@Table(name = "SummonerDto")
public class SummonerDto extends Model {

    @Expose
    @Column(name = "summoner_id")
    public long id;             // Summoner ID

    @Expose
    @Column(name = "name")
    public String name;         // Summoner name

    @Expose
    @Column(name = "profile_icon_id")
    public int profileIconId;   // ID of the summoner icon associated with the summoner.

    @Expose
    @Column(name = "revision_date")
    public long revisionDate;   // Date summoner was last modified specified as epoch milliseconds.
                                // The following events will update this timestamp:
                                // profile icon change, playing the tutorial or advanced tutorial,
                                // finishing a game, summoner name change

    @Expose
    @Column(name = "summoner_level")
    public long summonerLevel;	// summoner level associated with the summoner.

    public SummonerDto(){
        super();
    }

    public SummonerDto(long id, String name, int profileIconId, long revisionDate, long summonerLevel){
        super();
        this.id = id;
        this.name = name;
        this.profileIconId = profileIconId;
        this.revisionDate = revisionDate;
        this.summonerLevel = summonerLevel;
    }
}
