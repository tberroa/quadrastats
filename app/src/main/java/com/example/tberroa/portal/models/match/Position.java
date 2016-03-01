package com.example.tberroa.portal.models.match;

// This object contains participant frame position information

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;

@Table(name = "Position")
public class Position extends Model {

    public Position(){
    }

    @Expose
    @Column(name = "x")
    public int x;

    @Expose
    @Column(name = "y")
    public int y;

}
