package com.example.tberroa.portal.models.summoner;

// This object contains mastery information.

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;

@SuppressWarnings({"WeakerAccess", "unused", "MismatchedQueryAndUpdateOfCollection"})
@Table(name = "MasteryDto")
public class MasteryDto extends Model {

    // parent
    @Column(name = "mastery_page")
    MasteryPageDto masteryPageDto;

    @Expose
    @Column(name = "mastery_id")
    public int id;

    @Expose
    @Column(name = "rank")
    public int rank;

    public MasteryDto(){
        super();
    }
}
