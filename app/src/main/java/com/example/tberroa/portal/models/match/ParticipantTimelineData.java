package com.example.tberroa.portal.models.match;

// This object contains timeline data

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;

@SuppressWarnings({"WeakerAccess", "unused", "MismatchedQueryAndUpdateOfCollection"})
@Table(name = "ParticipantTimelineData")
public class ParticipantTimelineData extends Model {

    @Expose
    @Column(name = "ten_to_twenty")
    public double tenToTwenty;

    @Expose
    @Column(name = "thirty_to_end")
    public double thirtyToEnd;

    @Expose
    @Column(name = "twenty_to_thirty")
    public double twentyToThirty;

    @Expose
    @Column(name = "zero_to_ten")
    public double zeroToTen;

    public ParticipantTimelineData() {
        super();
    }
}
