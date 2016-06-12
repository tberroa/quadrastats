package com.example.tberroa.portal.models.summoner;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;

@SuppressWarnings({"WeakerAccess", "unused", "MismatchedQueryAndUpdateOfCollection"})
@Table(name = "Summoner")
public class Summoner extends Model {

    @Expose
    @Column(name = "region")
    public String region;

    @Expose
    @Column(name = "key")
    public String key;

    @Expose
    @Column(name = "name")
    public String name;

    @Expose
    @Column(name = "summoner_id")
    public long summonerId;

    @Expose
    @Column(name = "profile_icon")
    public int profileIcon;

    @Expose
    @Column(name = "friends")
    public String friends;

    public Summoner() {
        super();
    }
}
