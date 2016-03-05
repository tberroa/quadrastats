package com.example.tberroa.portal.models.match;

// This object contains timeline data

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;

@Table(name = "ParticipantTimelineData")
public class ParticipantTimelineData extends Model {

    @Expose
    @Column(name = "ten_to_twenty")        // Value per minute from 10 min to 20 min
    public double tenToTwenty;

    @Expose
    @Column(name = "thirty_to_end")         // Value per minute from 30 min to the end of the game
    public double thirtyToEnd;

    @Expose
    @Column(name = "twenty_to_thirty")      // Value per minute from 20 min to 30 min
    public double twentyToThirty;

    @Expose
    @Column(name = "zero_to_ten")           // Value per minute from the beginning of the game to 10 min
    public double zeroToTen;

    public ParticipantTimelineData(){
        super();
    }
}
