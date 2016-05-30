package com.example.tberroa.portal.models.summoner;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;

@SuppressWarnings({"WeakerAccess", "unused", "MismatchedQueryAndUpdateOfCollection"})
@Table(name = "SummonerDto")
public class SummonerDto extends Model {

    // parent
    @Column(name = "friend")
    public FriendsList friend;

    @Expose
    @Column(name = "summoner_id")
    public long id;

    @Expose
    @Column(name = "name")
    public String name;

    @Expose
    @Column(name = "profile_icon_id")
    public int profileIconId;

    @Expose
    @Column(name = "revision_date")
    public long revisionDate;

    @Expose
    @Column(name = "summoner_level")
    public long summonerLevel;

    public SummonerDto() {
        super();
    }
}
