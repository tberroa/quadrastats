package com.example.tberroa.portal.models.match;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;

import java.util.List;
import java.util.Map;

// This object contains game frame information

@SuppressWarnings({"WeakerAccess", "unused"})
@Table(name = "Frame")
public class Frame extends Model {

    // parent
    @Expose
    @Column(name = "timeline")
    Timeline timeline;

    @Expose
    @Column(name = "events")
    public List<Event> events;

    public List<Event> getEvents(){
        return getMany(Event.class, "frame");
    }

    @Expose
    @Column(name = "participant_frames")
    public Map<String, ParticipantFrame> participantFrames;

    @Expose
    @Column(name = "timestamp")
    public long timestamp;

    public Frame(){
        super();
    }

    public void cascadeSave(){
        ActiveAndroid.beginTransaction();
        try{
            save();
            if (events != null){
                for (Event event : events){
                    event.frame = this;
                    event.save();
                }
            }
            if (participantFrames != null){
                for (Map.Entry<String, ParticipantFrame> entry : participantFrames.entrySet()) {
                    entry.getValue().frame = this;
                    entry.getValue().cascadeSave();
                }
            }
            ActiveAndroid.setTransactionSuccessful();
        } finally {
            ActiveAndroid.endTransaction();
        }
    }
}
