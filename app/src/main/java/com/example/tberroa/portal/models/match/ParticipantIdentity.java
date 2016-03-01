package com.example.tberroa.portal.models.match;

// This object contains participant identity information

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;

@Table(name = "ParticipantIdentity")
public class ParticipantIdentity extends Model {

    public ParticipantIdentity(){
    }

    @Expose
    @Column(name = "participant_id")
    public int participantId;           // participant id

    @Expose
    @Column(name = "player")
    public Player player;               // player information

}
