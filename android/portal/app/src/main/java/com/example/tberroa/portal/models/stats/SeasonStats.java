package com.example.tberroa.portal.models.stats;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;

@SuppressWarnings({"unused", "InstanceVariableNamingConvention"})
@Table(name = "MatchStats")
public class SeasonStats extends Model {

    @Expose
    @Column(name = "assists")
    public Integer assists;
    @Expose
    @Column(name = "champion")
    public Integer champion;
    @Expose
    @Column(name = "damage_dealt")
    public Integer damage_dealt;
    @Expose
    @Column(name = "damage_taken")
    public Integer damage_taken;
    @Expose
    @Column(name = "deaths")
    public Integer deaths;
    @Expose
    @Column(name = "double_kills")
    public Integer double_kills;
    @Expose
    @Column(name = "games")
    public Integer games;
    @Expose
    @Column(name = "gold_earned")
    public Integer gold_earned;
    @Expose
    @Column(name = "kills")
    public Integer kills;
    @Expose
    @Column(name = "losses")
    public Integer losses;
    @Expose
    @Column(name = "magic_damage_dealt")
    public Integer magic_damage_dealt;
    @Expose
    @Column(name = "max_deaths")
    public Integer max_deaths;
    @Expose
    @Column(name = "max_killing_spree")
    public Integer max_killing_spree;
    @Expose
    @Column(name = "max_kills")
    public Integer max_kills;
    @Expose
    @Column(name = "minion_kills")
    public Integer minion_kills;
    @Expose
    @Column(name = "neutral_minion_kills")
    public Integer neutral_minion_kills;
    @Expose
    @Column(name = "penta_kills")
    public Integer penta_kills;
    @Expose
    @Column(name = "physical_damage_dealt")
    public Integer physical_damage_dealt;
    @Expose
    @Column(name = "quadra_kills")
    public Integer quadra_kills;
    @Expose
    @Column(name = "region")
    public String region;
    @Expose
    @Column(name = "summoner_id")
    public Long summoner_id;
    @Expose
    @Column(name = "summoner_key")
    public String summoner_key;
    @Expose
    @Column(name = "summoner_name")
    public String summoner_name;
    @Expose
    @Column(name = "triple_kills")
    public Integer triple_kills;
    @Expose
    @Column(name = "wins")
    public Integer wins;
}
