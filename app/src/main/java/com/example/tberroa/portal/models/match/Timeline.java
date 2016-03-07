package com.example.tberroa.portal.models.match;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;

import java.util.List;

@SuppressWarnings({"WeakerAccess", "unused"})
@Table(name = "Timeline")
public class Timeline extends Model {

    // parent
    @Expose
    @Column(name = "match_detail")
    MatchDetail matchDetail;

    @Expose
    @Column(name = "frameInterval")
    public long frameInterval;

    @Expose
    @Column(name = "frames")
    public List<Frame> frames;

    public List<Frame> getFrames(){
        return getMany(Frame.class, "timeline");
    }

    public Timeline(){
        super();
    }

    public void cascadeSave(){
        ActiveAndroid.beginTransaction();
        try{
            save();
            if (frames != null){
                for (Frame frame : frames){
                    frame.timeline = this;
                    frame.cascadeSave();
                }
            }
            ActiveAndroid.setTransactionSuccessful();
        } finally {
           ActiveAndroid.endTransaction();
        }
    }
}
