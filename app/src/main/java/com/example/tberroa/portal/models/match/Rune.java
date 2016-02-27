package com.example.tberroa.portal.models.match;

// This object contains rune information

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;

@Table(name = "Rune")
public class Rune extends Model {

    @Expose
    @Column(name = "rank")
    public long rank;               // rune rank

    @Expose
    @Column(name = "rune_id")
    public long runeId;             // rune id
}
