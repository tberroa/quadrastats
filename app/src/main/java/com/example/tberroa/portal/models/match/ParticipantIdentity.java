package com.example.tberroa.portal.models.match;

// This object contains participant identity information

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.google.gson.annotations.Expose;

@SuppressWarnings({"WeakerAccess", "unused", "MismatchedQueryAndUpdateOfCollection"})
@Table(name = "ParticipantIdentity")
public class ParticipantIdentity extends Model {

    // parent
    @Column(name = "match_detail")
    public MatchDetail matchDetail;

    @Expose
    @Column(name = "participant_id")
    public int participantId;

    @Expose
    @Column(name = "player")
    private Player player;

    public ParticipantIdentity(){
        super();
    }

    public Player getPlayer(){
        return new Select()
                .from(Player.class)
                .where("participant_identity = ?", getId())
                .executeSingle();
    }

    public void cascadeSave(){
        save();
        if (player != null){
            player.participantIdentity = this;
            player.save();
        }
    }
}
