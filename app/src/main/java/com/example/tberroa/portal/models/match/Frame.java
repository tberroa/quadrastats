package com.example.tberroa.portal.models.match;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;

import java.util.List;
import java.util.Map;

// This object contains game frame information

@Table(name = "Frame")
public class Frame extends Model {

    @Expose
    @Column(name = "events")
    public List<Event> events;          // List of events for this frame.

    @Expose
    @Column(name = "participant_frames")
    public Map<String, ParticipantFrame> participantFrames; // Map of each participant ID to the
                                                            // participant's information for the frame.

    @Expose
    @Column(name = "timestamp")
    public long timestamp;              // Represents how many milliseconds into the game the frame occurred.

    public Frame(){
        super();
    }
}
