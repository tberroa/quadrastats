package com.example.tberroa.portal.models.match;

// This object contains all timeline information

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;

@SuppressWarnings({"WeakerAccess", "unused", "MismatchedQueryAndUpdateOfCollection"})
@Table(name = "ParticipantTimeline")
public class ParticipantTimeline extends Model {

    // parent
    @Column(name = "participant")
    public Participant participant;

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
    @Column(name = "elder_lizard_assists_per_min_counts")
    public ParticipantTimelineData elderLizardAssistsPerMinCounts;

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
    @Column(name = "lane")
    public String lane;

    @Expose
    @Column(name = "role")
    public String role;

    @Expose
    @Column(name = "tower_assists_per_min_counts")
    public ParticipantTimelineData towerAssistsPerMinCounts;

    @Expose
    @Column(name = "tower_kills_per_min_counts")
    public ParticipantTimelineData towerKillsPerMinCounts;

    @Expose
    @Column(name = "tower_kills_per_min_deltas")
    public ParticipantTimelineData towerKillsPerMinDeltas;

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

    public ParticipantTimeline(){
        super();
    }

    public void cascadeSave(){
        ActiveAndroid.beginTransaction();
        try{
            if (ancientGolemAssistsPerMinCounts != null){
                ancientGolemAssistsPerMinCounts.save();
            }
            if (ancientGolemKillsPerMinCounts != null){
                ancientGolemKillsPerMinCounts.save();
            }
            if (assistedLaneDeathsPerMinDeltas != null){
                assistedLaneDeathsPerMinDeltas.save();
            }
            if (assistedLaneKillsPerMinDeltas != null){
                assistedLaneKillsPerMinDeltas.save();
            }
            if (baronAssistsPerMinCounts != null){
                baronAssistsPerMinCounts.save();
            }
            if (baronKillsPerMinCounts != null){
                baronKillsPerMinCounts.save();
            }
            if (creepsPerMinDeltas != null){
                creepsPerMinDeltas.save();
            }
            if (csDiffPerMinDeltas != null){
                csDiffPerMinDeltas.save();
            }
            if (damageTakenDiffPerMinDeltas != null){
                damageTakenDiffPerMinDeltas.save();
            }
            if (damageTakenPerMinDeltas != null){
                damageTakenPerMinDeltas.save();
            }
            if (dragonAssistsPerMinCounts != null){
                dragonAssistsPerMinCounts.save();
            }
            if (dragonKillsPerMinCounts != null){
                dragonKillsPerMinCounts.save();
            }
            if (elderLizardAssistsPerMinCounts != null){
                elderLizardAssistsPerMinCounts.save();
            }
            if (elderLizardKillsPerMinCounts != null){
                elderLizardKillsPerMinCounts.save();
            }
            if (goldPerMinDeltas != null){
                goldPerMinDeltas.save();
            }
            if (inhibitorAssistsPerMinCounts != null){
                inhibitorAssistsPerMinCounts.save();
            }
            if (inhibitorKillsPerMinCounts != null){
                inhibitorKillsPerMinCounts.save();
            }
            if (towerAssistsPerMinCounts != null){
                towerAssistsPerMinCounts.save();
            }
            if (towerKillsPerMinCounts != null){
                towerKillsPerMinCounts.save();
            }
            if (towerKillsPerMinDeltas != null){
                towerKillsPerMinDeltas.save();
            }
            if (vilemawAssistsPerMinCounts != null){
                vilemawAssistsPerMinCounts.save();
            }
            if (vilemawKillsPerMinCounts != null){
                vilemawKillsPerMinCounts.save();
            }
            if (wardsPerMinDeltas != null){
                wardsPerMinDeltas.save();
            }
            if (xpDiffPerMinDeltas != null){
                xpDiffPerMinDeltas.save();
            }
            if (xpPerMinDeltas != null){
                xpPerMinDeltas.save();
            }
            save();
            ActiveAndroid.setTransactionSuccessful();
        }finally {
            ActiveAndroid.endTransaction();
        }
    }
}
