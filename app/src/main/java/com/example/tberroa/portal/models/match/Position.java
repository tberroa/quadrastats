package com.example.tberroa.portal.models.match;

// This object contains participant frame position information

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;

@SuppressWarnings({"WeakerAccess", "unused"})
@Table(name = "Position")
public class Position extends Model {

    // parent
    @Expose
    @Column(name = "participant_frame")
    ParticipantFrame participantFrame;


    @Expose
    @Column(name = "x")
    public int x;

    @Expose
    @Column(name = "y")
    public int y;

    public Position(){
        super();
    }
}
