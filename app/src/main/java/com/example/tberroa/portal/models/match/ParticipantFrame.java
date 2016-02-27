package com.example.tberroa.portal.models.match;

// This object contains participant frame information

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;

@Table(name = "ParticipantFrame")
public class ParticipantFrame  extends Model {

    @Expose                                 // Participant's current gold
    @Column(name = "current_gold")
    public int currentGold;

    @Expose
    @Column(name = "dominion_score")        // Dominion score of the participant
    public int dominionScore;

    @Expose
    @Column(name = "jungle_minions_killed") // Number of jungle minions killed by participant
    public int jungleMinionsKilled;

    @Expose
    @Column(name = "level")                 // Participant's current level
    public int level;

    @Expose
    @Column(name = "minions_killed")        // Number of minions killed by participant
    public int minionsKilled;

    @Expose
    @Column(name = "participant_id")        // Participant ID
    public int participantId;

    @Expose
    @Column(name = "position")              // Participant's position
    public Position position;

    @Expose
    @Column(name = "team_score")            // 	Team score of the participant
    public int teamScore;

    @Expose
    @Column(name = "total_gold")            // Participant's total gold
    public int totalGold;

    @Expose
    @Column(name = "xp")                    // Experience earned by participant
    public int xp;
}
