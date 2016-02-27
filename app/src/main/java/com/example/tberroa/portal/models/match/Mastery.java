package com.example.tberroa.portal.models.match;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;

@Table(name = "Mastery")
public class Mastery  extends Model {

    @Expose
    @Column(name = "mastery_id")        // mastery id
    public long masteryId;

    @Expose
    @Column(name = "rank")              // mastery rank
    public long rank;


}
