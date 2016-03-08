package com.example.tberroa.portal.models.match;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;

@SuppressWarnings({"WeakerAccess", "unused", "MismatchedQueryAndUpdateOfCollection"})
@Table(name = "Mastery")
public class Mastery  extends Model {

    // parent
    @Column(name = "participant")
    public Participant participant;

    @Expose
    @Column(name = "mastery_id")
    public long masteryId;

    @Expose
    @Column(name = "rank")
    public long rank;

    public Mastery(){
        super();
    }
}
