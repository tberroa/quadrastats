package com.example.tberroa.portal.models.summoner;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;

@SuppressWarnings({"unused", "InstanceVariableNamingConvention"})
@Table(name = "Summoner")
public class Summoner extends Model {

    @Expose
    @Column(name = "division")
    public String division;
    @Expose
    @Column(name = "friends")
    public String friends;
    @Expose
    @Column(name = "key")
    public String key;
    @Expose
    @Column(name = "losses")
    public String losses;
    @Expose
    @Column(name = "name")
    public String name;
    @Expose
    @Column(name = "profile_icon")
    public int profile_icon;
    @Expose
    @Column(name = "region")
    public String region;
    @Expose
    @Column(name = "series")
    public String series;
    @Expose
    @Column(name = "summoner_id")
    public long summoner_id;
    @Expose
    @Column(name = "tier")
    public String tier;
    @Expose
    @Column(name = "wins")
    public String wins;

    @SuppressWarnings("RedundantNoArgConstructor")
    public Summoner() {
    }

    public void addFriend(String friendKey) {
        if ("".equals(friends)) {
            friends = friendKey;
        } else {
            friends += "," + friendKey;
        }
    }
}
