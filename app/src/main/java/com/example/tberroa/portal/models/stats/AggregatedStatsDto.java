package com.example.tberroa.portal.models.stats;

// This object contains aggregated stat information.
// I'm purposely ignoring Dominion only stats

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;

@Table(name = "AggregatedStatsDto")
public class AggregatedStatsDto extends Model {

    @Expose
    @Column(name = "bot_games_played")
    public int botGamesPlayed;

    @Expose
    @Column(name = "killing_spree")
    public int killingSpree;

    @Expose
    @Column(name = "max_champions_killed")
    public int maxChampionsKilled;

    @Expose
    @Column(name = "max_largest_critical_strike")
    public int maxLargestCriticalStrike;

    @Expose
    @Column(name = "max_largest_killing_spree")
    public int maxLargestKillingSpree;

    @Expose
    @Column(name = "max_num_deaths")            // Only returned for ranked statistics.
    public int maxNumDeaths;

    @Expose
    @Column(name = "max_time_played")
    public int maxTimePlayed;

    @Expose
    @Column(name = "max_time_spent_living")
    public int maxTimeSpentLiving;

    @Expose
    @Column(name = "most_champion_kills_per_session")
    public int mostChampionKillsPerSession;

    @Expose
    @Column(name = "most_spells_cast")
    public int mostSpellsCast;

    @Expose
    @Column(name = "normal_games_played")
    public int normalGamesPlayed;

    @Expose
    @Column(name = "ranked_premade_games_played")
    public int rankedPremadeGamesPlayed;

    @Expose
    @Column(name = "ranked_solo_games_played")
    public int rankedSoloGamesPlayed;

    @Expose
    @Column(name = "total_assists")
    public int totalAssists;

    @Expose
    @Column(name = "total_champion_kills")
    public int totalChampionKills;

    @Expose
    @Column(name = "total_damage_dealt")
    public int totalDamageDealt;

    @Expose
    @Column(name = "total_damage_taken")
    public int totalDamageTaken;

    @Expose
    @Column(name = "total_deaths_per_session")  // Only returned for ranked statistics.
    public int totalDeathsPerSession;

    @Expose
    @Column(name = "total_double_kills")
    public int totalDoubleKills;

    @Expose
    @Column(name = "total_first_blood")
    public int totalFirstBlood;

    @Expose
    @Column(name = "total_gold_earned")
    public int total_gold_earned;

    @Expose
    @Column(name = "total_heal")
    public int totalHeal;

    @Expose
    @Column(name = "total_magic_damage_dealt")
    public int totalMagicDamageDealt;

    @Expose
    @Column(name = "total_minion_kills")
    public int totalMinionKills;

    @Expose
    @Column(name = "total_neutral_minions_killed")
    public int totalNeutralMinionsKilled;

    @Expose
    @Column(name = "total_penta_kills")
    public int totalPentaKills;

    @Expose
    @Column(name = "total_physical_damage_dealt")
    public int totalPhysicalDamageDealt;

    @Expose
    @Column(name = "total_quadra_kills")
    public int totalQuadraKills;

    @Expose
    @Column(name = "total_sessions_lost")
    public int totalSessionsLost;

    @Expose
    @Column(name = "total_sessions_played")
    public int totalSessionsPlayed;

    @Expose
    @Column(name = "total_sessions_won")
    public int totalSessionsWon;

    @Expose
    @Column(name = "total_triple_kills")
    public int totalTripleKills;

    @Expose
    @Column(name = "total_turrets_killed")
    public int totalTurretsKilled;

    @Expose
    @Column(name = "total_unreal_kills")
    public int totalUnrealKills;

    public AggregatedStatsDto(){
        super();
    }
}
