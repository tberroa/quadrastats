package com.example.tberroa.portal.models.stats;

import android.support.annotation.Nullable;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;

@SuppressWarnings({"unused", "InstanceVariableNamingConvention"})
@Table(name = "SeasonStats")
public class SeasonStats extends Model {

    @Nullable
    @Expose
    @Column(name = "assists")
    public Integer assists;
    @Expose
    @Column(name = "champion")
    public int champion;
    @Nullable
    @Expose
    @Column(name = "damage_dealt")
    public Integer damage_dealt;
    @Nullable
    @Expose
    @Column(name = "damage_taken")
    public Integer damage_taken;
    @Nullable
    @Expose
    @Column(name = "deaths")
    public Integer deaths;
    @Nullable
    @Expose
    @Column(name = "double_kills")
    public Integer double_kills;
    @Nullable
    @Expose
    @Column(name = "games")
    public Integer games;
    @Nullable
    @Expose
    @Column(name = "gold_earned")
    public Integer gold_earned;
    @Nullable
    @Expose
    @Column(name = "kills")
    public Integer kills;
    @Nullable
    @Expose
    @Column(name = "losses")
    public Integer losses;
    @Nullable
    @Expose
    @Column(name = "magic_damage_dealt")
    public Integer magic_damage_dealt;
    @Nullable
    @Expose
    @Column(name = "max_deaths")
    public Integer max_deaths;
    @Nullable
    @Expose
    @Column(name = "max_killing_spree")
    public Integer max_killing_spree;
    @Nullable
    @Expose
    @Column(name = "max_kills")
    public Integer max_kills;
    @Nullable
    @Expose
    @Column(name = "minion_kills")
    public Integer minion_kills;
    @Nullable
    @Expose
    @Column(name = "neutral_minion_kills")
    public Integer neutral_minion_kills;
    @Nullable
    @Expose
    @Column(name = "penta_kills")
    public Integer penta_kills;
    @Nullable
    @Expose
    @Column(name = "physical_damage_dealt")
    public Integer physical_damage_dealt;
    @Nullable
    @Expose
    @Column(name = "quadra_kills")
    public Integer quadra_kills;
    @Expose
    @Column(name = "region")
    public String region;
    @Expose
    @Column(name = "summoner_id")
    public long summoner_id;
    @Expose
    @Column(name = "summoner_key")
    public String summoner_key;
    @Expose
    @Column(name = "summoner_name")
    public String summoner_name;
    @Nullable
    @Expose
    @Column(name = "triple_kills")
    public Integer triple_kills;
    @Nullable
    @Expose
    @Column(name = "wins")
    public Integer wins;
}
