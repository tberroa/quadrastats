package com.example.tberroa.portal.models.stats;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;

@SuppressWarnings({"WeakerAccess", "unused", "MismatchedQueryAndUpdateOfCollection"})
@Table(name = "MatchStats")
public class MatchStats extends Model {

    // identity info
    @Expose
    @Column(name = "region")
    public String region;

    @Expose
    @Column(name = "summoner_name")
    public String summoner_name;

    @Expose
    @Column(name = "summoner_id")
    public long summoner_id;

    @Expose
    @Column(name = "match_id")
    public long match_id;

    @Expose
    @Column(name = "match_duration")
    public long match_duration;

    @Expose
    @Column(name = "champion")
    public long champion;

    @Expose
    @Column(name = "lane")
    public String lane;

    @Expose
    @Column(name = "role")
    public String role;

    // raw stats
    @Expose
    @Column(name = "assists")
    public long assists;

    @Expose
    @Column(name = "champ_level")
    public long champ_level;

    @Expose
    @Column(name = "deaths")
    public long deaths;

    @Expose
    @Column(name = "double_kills")
    public long double_kills;

    @Expose
    @Column(name = "first_blood_assist")
    public boolean first_blood_assist;

    @Expose
    @Column(name = "first_blood_kill")
    public boolean first_blood_kill;

    @Expose
    @Column(name = "first_inhibitor_assist")
    public boolean first_inhibitor_assist;

    @Expose
    @Column(name = "first_inhibitor_kill")
    public boolean first_inhibitor_kill;

    @Expose
    @Column(name = "first_tower_assist")
    public boolean first_tower_assist;

    @Expose
    @Column(name = "first_tower_kill")
    public boolean first_tower_kill;

    @Expose
    @Column(name = "gold_earned")
    public long gold_earned;

    @Expose
    @Column(name = "gold_spent")
    public long gold_spent;

    @Expose
    @Column(name = "inhibitor_kills")
    public long inhibitor_kills;

    @Expose
    @Column(name = "item0")
    public long item0;

    @Expose
    @Column(name = "item1")
    public long item1;

    @Expose
    @Column(name = "item2")
    public long item2;

    @Expose
    @Column(name = "item3")
    public long item3;

    @Expose
    @Column(name = "item4")
    public long item4;

    @Expose
    @Column(name = "item5")
    public long item5;

    @Expose
    @Column(name = "item6")
    public long item6;

    @Expose
    @Column(name = "killing_sprees")
    public long killing_sprees;

    @Expose
    @Column(name = "kills")
    public long kills;

    @Expose
    @Column(name = "largest_critical_strike")
    public long largest_critical_strike;

    @Expose
    @Column(name = "largest_killing_spree")
    public long largest_killing_spree;

    @Expose
    @Column(name = "largest_multi_kill")
    public long largest_multi_kill;

    @Expose
    @Column(name = "magic_damage_dealt")
    public long magic_damage_dealt;

    @Expose
    @Column(name = "magic_damage_dealt_to_champions")
    public long magic_damage_dealt_to_champions;

    @Expose
    @Column(name = "magic_damage_taken")
    public long magic_damage_taken;

    @Expose
    @Column(name = "minions_killed")
    public long minions_killed;

    @Expose
    @Column(name = "neutral_minions_killed")
    public long neutral_minions_killed;

    @Expose
    @Column(name = "neutral_minions_killed_enemy_jungle")
    public long neutral_minions_killed_enemy_jungle;

    @Expose
    @Column(name = "neutral_minions_killed_team_jungle")
    public long neutral_minions_killed_team_jungle;

    @Expose
    @Column(name = "penta_kills")
    public long penta_kills;

    @Expose
    @Column(name = "physical_damage_dealt")
    public long physical_damage_dealt;

    @Expose
    @Column(name = "physical_damage_dealt_to_champions")
    public long physical_damage_dealt_to_champions;

    @Expose
    @Column(name = "physical_damage_taken")
    public long physical_damage_taken;

    @Expose
    @Column(name = "quadra_kills")
    public long quadra_kills;

    @Expose
    @Column(name = "sight_wards_bought_in_game")
    public long sight_wards_bought_in_game;

    @Expose
    @Column(name = "total_damage_dealt")
    public long total_damage_dealt;

    @Expose
    @Column(name = "total_damage_dealt_to_champions")
    public long total_damage_dealt_to_champions;

    @Expose
    @Column(name = "total_damage_taken")
    public long total_damage_taken;

    @Expose
    @Column(name = "total_heal")
    public long total_heal;

    @Expose
    @Column(name = "total_time_crowd_control_dealt")
    public long total_time_crowd_control_dealt;

    @Expose
    @Column(name = "total_units_healed")
    public long total_units_healed;

    @Expose
    @Column(name = "tower_kills")
    public long tower_kills;

    @Expose
    @Column(name = "triple_kills")
    public long triple_kills;

    @Expose
    @Column(name = "true_damage_dealt")
    public long true_damage_dealt;

    @Expose
    @Column(name = "true_damage_dealt_to_champions")
    public long true_damage_dealt_to_champions;

    @Expose
    @Column(name = "true_damage_taken")
    public long true_damage_taken;

    @Expose
    @Column(name = "unreal_kills")
    public long unreal_kills;

    @Expose
    @Column(name = "vision_wards_bought_in_game")
    public long vision_wards_bought_in_game;

    @Expose
    @Column(name = "wards_killed")
    public long wards_killed;

    @Expose
    @Column(name = "wards_placed")
    public long wards_placed;

    @Expose
    @Column(name = "winner")
    public boolean winner;

    // calculated stats
    @Expose
    @Column(name = "cs_at_ten")
    public float cs_at_ten;

    @Expose
    @Column(name = "cs_diff_at_ten")
    public float cs_diff_at_ten;

    @Expose
    @Column(name = "cs_per_min")
    public float cs_per_min;

    @Expose
    @Column(name = "dmg_per_min")
    public float dmg_per_min;

    @Expose
    @Column(name = "gold_per_min")
    public float gold_per_min;

    @Expose
    @Column(name = "kda")
    public float kda;

    @Expose
    @Column(name = "kill_participation")
    public float kill_participation;

    public MatchStats() {
        super();
    }
}
