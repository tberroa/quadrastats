package com.example.tberroa.portal.models.match;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;

import java.util.List;

@Table(name = "Timeline")
public class Timeline extends Model {

    @Expose
    @Column(name = "frameInterval")
    public long frameInterval;      // time between each returned frame in milliseconds

    @Expose
    @Column(name = "frames")
    public List<Frame> frames;       // list of timeline frames for the game

}
