package com.example.tberroa.portal.models.summoner;

// This object contains mastery information.

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;

@Table(name = "MasteryDto")
public class MasteryDto extends Model {

    @Expose
    @Column(name = "mastery_id")    // Mastery ID. For static information correlating to
    public int id;                  // masteries, please refer to the LoL Static Data API.

    @Expose
    @Column(name = "rank")          // Mastery rank (i.e., the number of points put into this mastery).
    public int rank;

}
