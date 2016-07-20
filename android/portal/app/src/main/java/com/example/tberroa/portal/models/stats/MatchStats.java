package com.example.tberroa.portal.models.stats;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;

@SuppressWarnings("unused")
@Table(name = "MatchStats")
public class MatchStats extends Model {

    // identity info
    @Expose
    @Column(name = "region")
    public String region;
    @Expose
    @Column(name = "summoner_key")
    public String summoner_key;
    @Expose
    @Column(name = "summoner_name")
    public String summoner_name;
    @Expose
    @Column(name = "summoner_id")
    public Long summoner_id;
    @Expose
    @Column(name = "match_id")
    public Long match_id;
    @Expose
    @Column(name = "match_creation")
    public Long match_creation;
    @Expose
    @Column(name = "match_duration")
    public Long match_duration;
    @Expose
    @Column(name = "champion")
    public Long champion;
    @Expose
    @Column(name = "lane")
    public String lane;
    @Expose
    @Column(name = "role")
    public String role;
    @Expose
    @Column(name = "spell1")
    public int spell1;
    @Expose
    @Column(name = "spell2")
    public int spell2;
    @Expose
    @Column(name = "keystone")
    public Long keystone;

    // raw stats
    @Expose
    @Column(name = "assists")
    public Long assists;
    @Expose
    @Column(name = "champ_level")
    public Long champ_level;
    @Expose
    @Column(name = "deaths")
    public Long deaths;
    @Expose
    @Column(name = "double_kills")
    public Long double_kills;
    @Expose
    @Column(name = "first_blood_assist")
    public Boolean first_blood_assist;
    @Expose
    @Column(name = "first_blood_kill")
    public Boolean first_blood_kill;
    @Expose
    @Column(name = "first_inhibitor_assist")
    public Boolean first_inhibitor_assist;
    @Expose
    @Column(name = "first_inhibitor_kill")
    public Boolean first_inhibitor_kill;
    @Expose
    @Column(name = "first_tower_assist")
    public Boolean first_tower_assist;
    @Expose
    @Column(name = "first_tower_kill")
    public Boolean first_tower_kill;
    @Expose
    @Column(name = "gold_earned")
    public Long gold_earned;
    @Expose
    @Column(name = "gold_spent")
    public Long gold_spent;
    @Expose
    @Column(name = "inhibitor_kills")
    public Long inhibitor_kills;
    @Expose
    @Column(name = "item0")
    public Long item0;
    @Expose
    @Column(name = "item1")
    public Long item1;
    @Expose
    @Column(name = "item2")
    public Long item2;
    @Expose
    @Column(name = "item3")
    public Long item3;
    @Expose
    @Column(name = "item4")
    public Long item4;
    @Expose
    @Column(name = "item5")
    public Long item5;
    @Expose
    @Column(name = "item6")
    public Long item6;
    @Expose
    @Column(name = "killing_sprees")
    public Long killing_sprees;
    @Expose
    @Column(name = "kills")
    public Long kills;
    @Expose
    @Column(name = "largest_critical_strike")
    public Long largest_critical_strike;
    @Expose
    @Column(name = "largest_killing_spree")
    public Long largest_killing_spree;
    @Expose
    @Column(name = "largest_multi_kill")
    public Long largest_multi_kill;
    @Expose
    @Column(name = "magic_damage_dealt")
    public Long magic_damage_dealt;
    @Expose
    @Column(name = "magic_damage_dealt_to_champions")
    public Long magic_damage_dealt_to_champions;
    @Expose
    @Column(name = "magic_damage_taken")
    public Long magic_damage_taken;
    @Expose
    @Column(name = "minions_killed")
    public Long minions_killed;
    @Expose
    @Column(name = "neutral_minions_killed")
    public Long neutral_minions_killed;
    @Expose
    @Column(name = "neutral_minions_killed_enemy_jungle")
    public Long neutral_minions_killed_enemy_jungle;
    @Expose
    @Column(name = "neutral_minions_killed_team_jungle")
    public Long neutral_minions_killed_team_jungle;
    @Expose
    @Column(name = "penta_kills")
    public Long penta_kills;
    @Expose
    @Column(name = "physical_damage_dealt")
    public Long physical_damage_dealt;
    @Expose
    @Column(name = "physical_damage_dealt_to_champions")
    public Long physical_damage_dealt_to_champions;
    @Expose
    @Column(name = "physical_damage_taken")
    public Long physical_damage_taken;
    @Expose
    @Column(name = "quadra_kills")
    public Long quadra_kills;
    @Expose
    @Column(name = "sight_wards_bought_in_game")
    public Long sight_wards_bought_in_game;
    @Expose
    @Column(name = "total_damage_dealt")
    public Long total_damage_dealt;
    @Expose
    @Column(name = "total_damage_dealt_to_champions")
    public Long total_damage_dealt_to_champions;
    @Expose
    @Column(name = "total_damage_taken")
    public Long total_damage_taken;
    @Expose
    @Column(name = "total_heal")
    public Long total_heal;
    @Expose
    @Column(name = "total_time_crowd_control_dealt")
    public Long total_time_crowd_control_dealt;
    @Expose
    @Column(name = "total_units_healed")
    public Long total_units_healed;
    @Expose
    @Column(name = "tower_kills")
    public Long tower_kills;
    @Expose
    @Column(name = "triple_kills")
    public Long triple_kills;
    @Expose
    @Column(name = "true_damage_dealt")
    public Long true_damage_dealt;
    @Expose
    @Column(name = "true_damage_dealt_to_champions")
    public Long true_damage_dealt_to_champions;
    @Expose
    @Column(name = "true_damage_taken")
    public Long true_damage_taken;
    @Expose
    @Column(name = "unreal_kills")
    public Long unreal_kills;
    @Expose
    @Column(name = "vision_wards_bought_in_game")
    public Long vision_wards_bought_in_game;
    @Expose
    @Column(name = "wards_killed")
    public Long wards_killed;
    @Expose
    @Column(name = "wards_placed")
    public Long wards_placed;
    @Expose
    @Column(name = "winner")
    public Boolean winner;

    // calculated stats
    @Expose
    @Column(name = "cs_at_ten")
    public Float cs_at_ten;
    @Expose
    @Column(name = "cs_diff_at_ten")
    public Float cs_diff_at_ten;
    @Expose
    @Column(name = "cs_per_min")
    public Float cs_per_min;
    @Expose
    @Column(name = "dmg_per_min")
    public Float dmg_per_min;
    @Expose
    @Column(name = "gold_per_min")
    public Float gold_per_min;
    @Expose
    @Column(name = "kda")
    public Float kda;
    @Expose
    @Column(name = "kill_participation")
    public Float kill_participation;
    @Expose
    @Column(name = "team_kills")
    public Long team_kills;
    @Expose
    @Column(name = "team_deaths")
    public Long team_deaths;
    @Expose
    @Column(name = "team_assists")
    public Long team_assists;

    @SuppressWarnings("RedundantNoArgConstructor")
    public MatchStats() {
    }
}
