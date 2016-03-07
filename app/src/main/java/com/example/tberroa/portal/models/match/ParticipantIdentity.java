package com.example.tberroa.portal.models.match;

// This object contains participant identity information

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;

@SuppressWarnings({"WeakerAccess", "unused"})
@Table(name = "ParticipantIdentity")
public class ParticipantIdentity extends Model {

    // parent
    @Expose
    @Column(name = "match_detail")
    MatchDetail matchDetail;

    @Expose
    @Column(name = "participant_id")
    public int participantId;

    @Expose
    @Column(name = "player")
    public Player player;

    public ParticipantIdentity(){
        super();
    }

    public void cascadeSave(){
        save();
        if (player != null){
            player.participantIdentity = this;
            player.save();
        }
    }
}
