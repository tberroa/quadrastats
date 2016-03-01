package com.example.tberroa.portal.models.match;

// This object contains game event information. Note that not all legal type
// values documented below are valid for all games. Event data evolves over time
// and certain values may be relevant only for older or newer games.

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;

@Table(name = "Event")
public class Event extends Model {

    public Event(){
    }

    @Expose
    @Column(name = "ascended_type")
    public String ascendedType;

    /*

    to be filled another time, doesn't seem like anything I would
    need anytime soon.


     */
}
