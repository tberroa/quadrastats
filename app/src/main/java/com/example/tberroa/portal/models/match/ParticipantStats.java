package com.example.tberroa.portal.models.match;

// This object contains participant statistics information

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;

@Table(name = "ParticipantStats")
public class ParticipantStats extends Model {

    @Expose
    @Column(name = "assists")                   // Number of assists
    public long assists;

    @Expose
    @Column(name = "champ_level")               // Champion level achieved
    public long champLevel;

    @Expose
    @Column(name = "combat_player_score")       // 	If game was a dominion game, player's combat score, otherwise 0
    public long combatPlayerScore;

    @Expose
    @Column(name = "deaths")                    // Number of deaths
    public long deaths;

    @Expose
    @Column(name = "double_kills")              // Number of double kills
    public long doubleKills;

    @Expose
    @Column(name = "first_blood_assist")        // 	Flag indicating if participant got an assist on first blood
    public boolean firstBloodAssist;

    @Expose
    @Column(name = "first_blood_kill")          // 	Flag indicating if participant got first blood
    public boolean firstBloodKill;

    @Expose
    @Column(name = "first_inhibitor_assist")    // 	Flag indicating if participant got an assist on the first inhibitor
    public boolean firstInhibitorAssist;

    @Expose
    @Column(name = "first_inhibitor_kill")      // 	Flag indicating if participant destroyed the first inhibitor
    public boolean firstInhibitorKill;

    @Expose
    @Column(name = "first_tower_assist")        // Flag indicating if participant got an assist on the first tower
    public boolean firstTowerAssist;

    @Expose
    @Column(name = "first_tower_kill")          // Flag indicating if participant destroyed the first tower
    public boolean firstTowerKill;

    @Expose
    @Column(name = "gold_earned")               // Gold earned
    public long goldEarned;

    @Expose
    @Column(name = "gold_spent")                // Gold spent
    public long goldSpent;

    @Expose
    @Column(name = "inhibitor_kills")           // Number of inhibitor kills
    public long inhibitorKills;

    @Expose
    @Column(name = "item0")                     // 	First item ID
    public long item0;

    @Expose
    @Column(name = "item1")                     // 	Second item ID
    public long item1;

    @Expose
    @Column(name = "item2")                     // 	Third item ID
    public long item2;

    @Expose
    @Column(name = "item3")                     // 	Fourth item ID
    public long item3;

    @Expose
    @Column(name = "item4")                     // 	Fifth item ID
    public long item4;

    @Expose
    @Column(name = "item5")                     // Sixth item ID
    public long item5;

    @Expose
    @Column(name = "item6")                     // Seventh item ID
    public long item6;

    @Expose
    @Column(name = "killing_sprees")            // Number of killing sprees
    public long killingSprees;

    @Expose
    @Column(name = "kills")                     // Number of kills
    public long kills;

    @Expose
    @Column(name = "largest_critical_strike")   // Largest critical strike
    public long largestCriticalStrike;

    @Expose
    @Column(name = "largest_killing_spree")     // Largest killing spree
    public long largestKillingSpree;

    @Expose
    @Column(name = "largest_multi_kill")        // Largest multi kill
    public long largestMultiKill;

    @Expose
    @Column(name = "magic_damage_dealt")        // 	Magical damage dealt
    public long magicDamageDealt;

    @Expose
    @Column(name = "magic_damage_dealt_to_champions")
    public long magicDamageDealtToChampions;    // 	Magical damage dealt to champions

    @Expose
    @Column(name = "magic_damage_taken")        // Magic damage taken
    public long magicDamageTaken;

    @Expose
    @Column(name = "minions_killed")            // 	Minions killed
    public long minionsKilled;

    @Expose
    @Column(name = "neutral_minions_killed")    // Neutral minions killed
    public long neutralMinionsKilled;

    @Expose
    @Column(name = "neutral_minions_killed_enemy_jungle")
    public long neutralMinionsKilledEnemyJungle;
                                                // 	Neutral jungle minions killed in the enemy team's jungle

    @Expose
    @Column(name = "neutral_minions_killed_team_jungle")
    public long neutralMinionsKilledTeamJungle;
                                                // Neutral jungle minions killed in your team's jungle

    @Expose
    @Column(name = "node_capture")              // 	If game was a dominion game, number of node captures
    public long nodeCapture;

    @Expose
    @Column(name = "node_capture_assist")       // 	If game was a dominion game, number of node capture assists
    public long nodeCaptureAssist;

    @Expose
    @Column(name = "node_neutralize")           // 	If game was a dominion game, number of node neutralization's
    public long nodeNeutralize;

    @Expose
    @Column(name = "node_neutralize_assist")    // 	If game was a dominion game, number of node neutralization assists
    public long nodeNeutralizeAssist;

    @Expose
    @Column(name = "objective_player_score")    // If game was a dominion game, player's objectives score, otherwise 0
    public long objectivePlayerScore;

    @Expose
    @Column(name = "penta_kills")               // Number of penta kills
    public long pentaKills;

    @Expose
    @Column(name = "physical_damage_dealt")     // Physical damage dealt
    public long physicalDamageDealt;

    @Expose
    @Column(name = "physical_damage_dealt_to_champions")
    public long physicalDamageDealtToChampions; // Physical damage dealt to champions

    @Expose
    @Column(name = "physical_damage_taken")     // Physical damage taken
    public long physicalDamageTaken;

    @Expose
    @Column(name = "quadra_kills")              // Number of quadra kills
    public long quadraKills;

    @Expose
    @Column(name = "sight_wards_bought_in_game")
    public long sightWardsBoughtInGame;         // 	Sight wards purchased

    @Expose
    @Column(name = "team_objective")            // If game was a dominion game, number of completed team objectives (i.e., quests)
    public long teamObjective;

    @Expose
    @Column(name = "total_damage_dealt")        // Total damage dealt
    public long totalDamageDealt;

    @Expose
    @Column(name = "total_damage_dealt_to_champions")
    public long totalDamageDealtToChampions;    // 	Total damage dealt to champions

    @Expose
    @Column(name = "total_damage_taken")        // Total damage taken
    public long totalDamageTaken;

    @Expose
    @Column(name = "total_heal")                // 	Total heal amount
    public long totalHeal;

    @Expose
    @Column(name = "total_player_score")        // 	If game was a dominion game, player's total score, otherwise 0
    public long totalPlayerScore;

    @Expose
    @Column(name = "total_score_rank")          // If game was a dominion game, team rank of the player's total score (e.g., 1-5)
    public long totalScoreRank;

    @Expose
    @Column(name = "total_time_crowd_control_dealt")
    public long totalTimeCrowdControlDealt;     // 	Total dealt crowd control time

    @Expose
    @Column(name = "total_units_healed")
    public long totalUnitsHealed;               // Total units healed

    @Expose
    @Column(name = "tower_kills")
    public long towerKills;                     // 	Number of tower kills

    @Expose
    @Column(name = "triple_kills")              // Number of triple kills
    public long tripleKills;

    @Expose
    @Column(name = "true_damage_dealt")
    public long trueDamageDealt;                // True damage dealt

    @Expose
    @Column(name = "true_damage_dealt_to_champions")
    public long trueDamageDealtToChampions;     // True damage dealt to champions


    @Expose
    @Column(name = "true_damage_taken")
    public long trueDamageTaken;                // True damage taken

    @Expose
    @Column(name = "unreal_kills")
    public long unrealKills;                    // Number of unreal kills

    @Expose
    @Column(name = "vision_wards_bought_in_game")
    public long visionWardsBoughtInGame;        // Vision wards purchased

    @Expose
    @Column(name = "wards_killed")              // Number of wards killed
    public long wardsKilled;

    @Expose
    @Column(name = "wards_placed")              // 	Number of wards placed
    public long wardsPlaced;

    @Expose
    @Column(name = "winner")                    // Flag indicating whether or not the participant won
    public boolean winner;

    public ParticipantStats(){
        super();
    }
}
