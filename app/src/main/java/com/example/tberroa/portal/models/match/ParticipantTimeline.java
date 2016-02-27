package com.example.tberroa.portal.models.match;

// This object contains all timeline information

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;

@Table(name = "ParticipantTimeline")
public class ParticipantTimeline {

    @Expose
    @Column(name = "ancient_golem_assists_per_min_counts")
    public ParticipantTimelineData ancientGolemAssistsPerMinCounts;

    @Expose
    @Column(name = "ancient_golem_kills_per_min_counts")
    public ParticipantTimelineData ancientGolemKillsPerMinCounts;

    @Expose
    @Column(name = "assisted_lane_deaths_per_min_deltas")
    public ParticipantTimelineData assistedLaneDeathsPerMinDeltas;

    @Expose
    @Column(name = "assisted_lane_kills_per_min_deltas")
    public ParticipantTimelineData assistedLaneKillsPerMinDeltas;

    @Expose
    @Column(name = "baron_assists_per_min_counts")
    public ParticipantTimelineData baronAssistsPerMinCounts;

    @Expose
    @Column(name = "baron_kills_per_min_counts")
    public ParticipantTimelineData baronKillsPerMinCounts;

    @Expose
    @Column(name = "creeps_per_min_deltas")
    public ParticipantTimelineData creepsPerMinDeltas;

    @Expose
    @Column(name = "cs_diff_per_min_deltas")
    public ParticipantTimelineData csDiffPerMinDeltas;

    @Expose
    @Column(name = "damage_taken_diff_per_min_deltas")
    public ParticipantTimelineData damageTakenDiffPerMinDeltas;

    @Expose
    @Column(name = "damage_taken_per_min_deltas")
    public ParticipantTimelineData damageTakenPerMinDeltas;

    @Expose
    @Column(name = "dragon_assists_per_min_counts")
    public ParticipantTimelineData dragonAssistsPerMinCounts;

    @Expose
    @Column(name = "dragon_kills_per_min_counts")
    public ParticipantTimelineData dragonKillsPerMinCounts;

    @Expose
    @Column(name = "elder_lizard_kills_per_min_counts")
    public ParticipantTimelineData elderLizardKillsPerMinCounts;

    @Expose
    @Column(name = "gold_per_min_deltas")
    public ParticipantTimelineData goldPerMinDeltas;

    @Expose
    @Column(name = "inhibitor_assists_per_min_counts")
    public ParticipantTimelineData inhibitorAssistsPerMinCounts;

    @Expose
    @Column(name = "inhibitor_kills_per_min_counts")
    public ParticipantTimelineData inhibitorKillsPerMinCounts;

    @Expose
    @Column(name = "lane")          // Participant's lane (Legal values: MID, MIDDLE, TOP, JUNGLE, BOT, BOTTOM)
    public String lane;

    @Expose
    @Column(name = "role")          // Participant's role (Legal values: DUO, NONE, SOLO, DUO_CARRY, DUO_SUPPORT)
    public String role;

    @Expose
    @Column(name = "tower_assists_per_min_counts")
    public ParticipantTimelineData towerAssistsPerMinCounts;

    @Expose
    @Column(name = "tower_kills_per_min_counts")
    public ParticipantTimelineData towerKillsPerMinCounts;

    @Expose
    @Column(name = "vilemaw_assists_per_min_counts")
    public ParticipantTimelineData vilemawAssistsPerMinCounts;

    @Expose
    @Column(name = "vilemaw_kills_per_min_counts")
    public ParticipantTimelineData vilemawKillsPerMinCounts;

    @Expose
    @Column(name = "wards_per_min_deltas")
    public ParticipantTimelineData wardsPerMinDeltas;

    @Expose
    @Column(name = "xp_diff_per_min_deltas")
    public ParticipantTimelineData xpDiffPerMinDeltas;

    @Expose
    @Column(name = "xp_per_min_deltas")
    public ParticipantTimelineData xpPerMinDeltas;

}
