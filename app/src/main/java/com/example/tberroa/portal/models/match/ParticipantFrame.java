package com.example.tberroa.portal.models.match;

// This object contains participant frame information

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;

@SuppressWarnings({"WeakerAccess", "unused"})
@Table(name = "ParticipantFrame")
public class ParticipantFrame  extends Model {

    // parent
    @Expose
    @Column(name = "frame")
    Frame frame;

    @Expose
    @Column(name = "current_gold")
    public int currentGold;

    @Expose
    @Column(name = "dominion_score")
    public int dominionScore;

    @Expose
    @Column(name = "jungle_minions_killed")
    public int jungleMinionsKilled;

    @Expose
    @Column(name = "level")
    public int level;

    @Expose
    @Column(name = "minions_killed")
    public int minionsKilled;

    @Expose
    @Column(name = "participant_id")
    public int participantId;

    @Expose
    @Column(name = "position")
    public Position position;

    @Expose
    @Column(name = "team_score")
    public int teamScore;

    @Expose
    @Column(name = "total_gold")
    public int totalGold;

    @Expose
    @Column(name = "xp")
    public int xp;

    public ParticipantFrame(){
        super();
    }

    public void cascadeSave(){
        save();
        if (position != null){
            position.participantFrame = this;
            position.save();
        }
    }
}
