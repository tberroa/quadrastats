package com.example.tberroa.portal.models.stats;

import android.support.annotation.Nullable;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;

@SuppressWarnings({"unused", "InstanceVariableNamingConvention"})
@Table(name = "MatchStats")
public class MatchStats extends Model {

    @Nullable
    @Expose
    @Column(name = "assists")
    public Long assists;
    @Nullable
    @Expose
    @Column(name = "champ_level")
    public Long champ_level;
    @Expose
    @Column(name = "champion")
    public long champion;
    @Nullable
    @Expose
    @Column(name = "cs_at_ten")
    public Float cs_at_ten;
    @Nullable
    @Expose
    @Column(name = "cs_diff_at_ten")
    public Float cs_diff_at_ten;
    @Nullable
    @Expose
    @Column(name = "cs_per_min")
    public Float cs_per_min;
    @Nullable
    @Expose
    @Column(name = "deaths")
    public Long deaths;
    @Nullable
    @Expose
    @Column(name = "dmg_per_min")
    public Float dmg_per_min;
    @Nullable
    @Expose
    @Column(name = "double_kills")
    public Long double_kills;
    @Nullable
    @Expose
    @Column(name = "first_blood_assist")
    public Boolean first_blood_assist;
    @Nullable
    @Expose
    @Column(name = "first_blood_kill")
    public Boolean first_blood_kill;
    @Nullable
    @Expose
    @Column(name = "first_inhibitor_assist")
    public Boolean first_inhibitor_assist;
    @Nullable
    @Expose
    @Column(name = "first_inhibitor_kill")
    public Boolean first_inhibitor_kill;
    @Nullable
    @Expose
    @Column(name = "first_tower_assist")
    public Boolean first_tower_assist;
    @Nullable
    @Expose
    @Column(name = "first_tower_kill")
    public Boolean first_tower_kill;
    @Nullable
    @Expose
    @Column(name = "gold_earned")
    public Long gold_earned;
    @Nullable
    @Expose
    @Column(name = "gold_per_min")
    public Float gold_per_min;
    @Nullable
    @Expose
    @Column(name = "gold_spent")
    public Long gold_spent;
    @Nullable
    @Expose
    @Column(name = "inhibitor_kills")
    public Long inhibitor_kills;
    @Nullable
    @Expose
    @Column(name = "item0")
    public Long item0;
    @Nullable
    @Expose
    @Column(name = "item1")
    public Long item1;
    @Nullable
    @Expose
    @Column(name = "item2")
    public Long item2;
    @Nullable
    @Expose
    @Column(name = "item3")
    public Long item3;
    @Nullable
    @Expose
    @Column(name = "item4")
    public Long item4;
    @Nullable
    @Expose
    @Column(name = "item5")
    public Long item5;
    @Nullable
    @Expose
    @Column(name = "item6")
    public Long item6;
    @Nullable
    @Expose
    @Column(name = "kda")
    public Float kda;
    @Nullable
    @Expose
    @Column(name = "keystone")
    public Long keystone;
    @Nullable
    @Expose
    @Column(name = "kill_participation")
    public Float kill_participation;
    @Nullable
    @Expose
    @Column(name = "killing_sprees")
    public Long killing_sprees;
    @Nullable
    @Expose
    @Column(name = "kills")
    public Long kills;
    @Expose
    @Column(name = "lane")
    public String lane;
    @Nullable
    @Expose
    @Column(name = "largest_critical_strike")
    public Long largest_critical_strike;
    @Nullable
    @Expose
    @Column(name = "largest_killing_spree")
    public Long largest_killing_spree;
    @Nullable
    @Expose
    @Column(name = "largest_multi_kill")
    public Long largest_multi_kill;
    @Nullable
    @Expose
    @Column(name = "magic_damage_dealt")
    public Long magic_damage_dealt;
    @Nullable
    @Expose
    @Column(name = "magic_damage_dealt_to_champions")
    public Long magic_damage_dealt_to_champions;
    @Nullable
    @Expose
    @Column(name = "magic_damage_taken")
    public Long magic_damage_taken;
    @Expose
    @Column(name = "match_creation")
    public long match_creation;
    @Expose
    @Column(name = "match_duration")
    public long match_duration;
    @Expose
    @Column(name = "match_id")
    public long match_id;
    @Nullable
    @Expose
    @Column(name = "minions_killed")
    public Long minions_killed;
    @Nullable
    @Expose
    @Column(name = "neutral_minions_killed")
    public Long neutral_minions_killed;
    @Nullable
    @Expose
    @Column(name = "neutral_minions_killed_enemy_jungle")
    public Long neutral_minions_killed_enemy_jungle;
    @Nullable
    @Expose
    @Column(name = "neutral_minions_killed_team_jungle")
    public Long neutral_minions_killed_team_jungle;
    @Nullable
    @Expose
    @Column(name = "penta_kills")
    public Long penta_kills;
    @Nullable
    @Expose
    @Column(name = "physical_damage_dealt")
    public Long physical_damage_dealt;
    @Nullable
    @Expose
    @Column(name = "physical_damage_dealt_to_champions")
    public Long physical_damage_dealt_to_champions;
    @Nullable
    @Expose
    @Column(name = "physical_damage_taken")
    public Long physical_damage_taken;
    @Nullable
    @Expose
    @Column(name = "quadra_kills")
    public Long quadra_kills;
    @Expose
    @Column(name = "region")
    public String region;
    @Expose
    @Column(name = "role")
    public String role;
    @Nullable
    @Expose
    @Column(name = "sight_wards_bought_in_game")
    public Long sight_wards_bought_in_game;
    @Expose
    @Column(name = "spell1")
    public int spell1;
    @Expose
    @Column(name = "spell2")
    public int spell2;
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
    @Column(name = "team_assists")
    public Long team_assists;
    @Nullable
    @Expose
    @Column(name = "team_deaths")
    public Long team_deaths;
    @Nullable
    @Expose
    @Column(name = "team_kills")
    public Long team_kills;
    @Nullable
    @Expose
    @Column(name = "total_damage_dealt")
    public Long total_damage_dealt;
    @Nullable
    @Expose
    @Column(name = "total_damage_dealt_to_champions")
    public Long total_damage_dealt_to_champions;
    @Nullable
    @Expose
    @Column(name = "total_damage_taken")
    public Long total_damage_taken;
    @Nullable
    @Expose
    @Column(name = "total_heal")
    public Long total_heal;
    @Nullable
    @Expose
    @Column(name = "total_time_crowd_control_dealt")
    public Long total_time_crowd_control_dealt;
    @Nullable
    @Expose
    @Column(name = "total_units_healed")
    public Long total_units_healed;
    @Nullable
    @Expose
    @Column(name = "tower_kills")
    public Long tower_kills;
    @Nullable
    @Expose
    @Column(name = "triple_kills")
    public Long triple_kills;
    @Nullable
    @Expose
    @Column(name = "true_damage_dealt")
    public Long true_damage_dealt;
    @Nullable
    @Expose
    @Column(name = "true_damage_dealt_to_champions")
    public Long true_damage_dealt_to_champions;
    @Nullable
    @Expose
    @Column(name = "true_damage_taken")
    public Long true_damage_taken;
    @Nullable
    @Expose
    @Column(name = "unreal_kills")
    public Long unreal_kills;
    @Nullable
    @Expose
    @Column(name = "vision_wards_bought_in_game")
    public Long vision_wards_bought_in_game;
    @Nullable
    @Expose
    @Column(name = "wards_killed")
    public Long wards_killed;
    @Nullable
    @Expose
    @Column(name = "wards_placed")
    public Long wards_placed;
    @Nullable
    @Expose
    @Column(name = "winner")
    public Boolean winner;

    @SuppressWarnings("RedundantNoArgConstructor")
    public MatchStats() {
    }
}
