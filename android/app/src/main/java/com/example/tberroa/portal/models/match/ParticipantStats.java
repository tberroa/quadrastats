package com.example.tberroa.portal.models.match;

// This object contains participant statistics information

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;

@SuppressWarnings({"WeakerAccess", "unused", "MismatchedQueryAndUpdateOfCollection"})
@Table(name = "ParticipantStats")
public class ParticipantStats extends Model {

    // parent
    @Column(name = "participant")
    public Participant participant;

    @Expose
    @Column(name = "assists")
    public long assists;

    @Expose
    @Column(name = "champ_level")
    public long champLevel;

    @Expose
    @Column(name = "combat_player_score")
    public long combatPlayerScore;

    @Expose
    @Column(name = "deaths")
    public long deaths;

    @Expose
    @Column(name = "double_kills")
    public long doubleKills;

    @Expose
    @Column(name = "first_blood_assist")
    public boolean firstBloodAssist;

    @Expose
    @Column(name = "first_blood_kill")
    public boolean firstBloodKill;

    @Expose
    @Column(name = "first_inhibitor_assist")
    public boolean firstInhibitorAssist;

    @Expose
    @Column(name = "first_inhibitor_kill")
    public boolean firstInhibitorKill;

    @Expose
    @Column(name = "first_tower_assist")
    public boolean firstTowerAssist;

    @Expose
    @Column(name = "first_tower_kill")
    public boolean firstTowerKill;

    @Expose
    @Column(name = "gold_earned")
    public long goldEarned;

    @Expose
    @Column(name = "gold_spent")
    public long goldSpent;

    @Expose
    @Column(name = "inhibitor_kills")
    public long inhibitorKills;

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
    public long killingSprees;

    @Expose
    @Column(name = "kills")
    public long kills;

    @Expose
    @Column(name = "largest_critical_strike")
    public long largestCriticalStrike;

    @Expose
    @Column(name = "largest_killing_spree")
    public long largestKillingSpree;

    @Expose
    @Column(name = "largest_multi_kill")
    public long largestMultiKill;

    @Expose
    @Column(name = "magic_damage_dealt")
    public long magicDamageDealt;

    @Expose
    @Column(name = "magic_damage_dealt_to_champions")
    public long magicDamageDealtToChampions;

    @Expose
    @Column(name = "magic_damage_taken")
    public long magicDamageTaken;

    @Expose
    @Column(name = "minions_killed")
    public long minionsKilled;

    @Expose
    @Column(name = "neutral_minions_killed")
    public long neutralMinionsKilled;

    @Expose
    @Column(name = "neutral_minions_killed_enemy_jungle")
    public long neutralMinionsKilledEnemyJungle;

    @Expose
    @Column(name = "neutral_minions_killed_team_jungle")
    public long neutralMinionsKilledTeamJungle;

    @Expose
    @Column(name = "node_capture")
    public long nodeCapture;

    @Expose
    @Column(name = "node_capture_assist")
    public long nodeCaptureAssist;

    @Expose
    @Column(name = "node_neutralize")
    public long nodeNeutralize;

    @Expose
    @Column(name = "node_neutralize_assist")
    public long nodeNeutralizeAssist;

    @Expose
    @Column(name = "objective_player_score")
    public long objectivePlayerScore;

    @Expose
    @Column(name = "penta_kills")
    public long pentaKills;

    @Expose
    @Column(name = "physical_damage_dealt")
    public long physicalDamageDealt;

    @Expose
    @Column(name = "physical_damage_dealt_to_champions")
    public long physicalDamageDealtToChampions;

    @Expose
    @Column(name = "physical_damage_taken")
    public long physicalDamageTaken;

    @Expose
    @Column(name = "quadra_kills")
    public long quadraKills;

    @Expose
    @Column(name = "sight_wards_bought_in_game")
    public long sightWardsBoughtInGame;

    @Expose
    @Column(name = "team_objective")
    public long teamObjective;

    @Expose
    @Column(name = "total_damage_dealt")
    public long totalDamageDealt;

    @Expose
    @Column(name = "total_damage_dealt_to_champions")
    public long totalDamageDealtToChampions;

    @Expose
    @Column(name = "total_damage_taken")
    public long totalDamageTaken;

    @Expose
    @Column(name = "total_heal")
    public long totalHeal;

    @Expose
    @Column(name = "total_player_score")
    public long totalPlayerScore;

    @Expose
    @Column(name = "total_score_rank")
    public long totalScoreRank;

    @Expose
    @Column(name = "total_time_crowd_control_dealt")
    public long totalTimeCrowdControlDealt;

    @Expose
    @Column(name = "total_units_healed")
    public long totalUnitsHealed;

    @Expose
    @Column(name = "tower_kills")
    public long towerKills;

    @Expose
    @Column(name = "triple_kills")
    public long tripleKills;

    @Expose
    @Column(name = "true_damage_dealt")
    public long trueDamageDealt;

    @Expose
    @Column(name = "true_damage_dealt_to_champions")
    public long trueDamageDealtToChampions;

    @Expose
    @Column(name = "true_damage_taken")
    public long trueDamageTaken;

    @Expose
    @Column(name = "unreal_kills")
    public long unrealKills;

    @Expose
    @Column(name = "vision_wards_bought_in_game")
    public long visionWardsBoughtInGame;

    @Expose
    @Column(name = "wards_killed")
    public long wardsKilled;

    @Expose
    @Column(name = "wards_placed")
    public long wardsPlaced;

    @Expose
    @Column(name = "winner")
    public boolean winner;

    public ParticipantStats() {
        super();
    }
}
