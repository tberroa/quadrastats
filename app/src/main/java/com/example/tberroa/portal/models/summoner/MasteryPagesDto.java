package com.example.tberroa.portal.models.summoner;

// This object contains masteries information.

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;

import java.util.Set;

@Table(name = "MasteryPagesDto")
public class MasteryPagesDto {

    @Expose
    @Column(name = "pages")                 // Collection of mastery pages associated with the summoner.
    public Set<MasteryPageDto> pages;

    @Expose
    @Column(name = "summoner_id")
    public long summonerId;

}
