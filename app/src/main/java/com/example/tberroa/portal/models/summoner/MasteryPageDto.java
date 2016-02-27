package com.example.tberroa.portal.models.summoner;

// This object contains mastery information.

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;

import java.util.List;

@Table(name = "MasteryPageDto")
public class MasteryPageDto extends Model {

    @Expose
    @Column(name = "current")
    public boolean current;     // Indicates if the mastery page is the current mastery page.

    @Expose
    @Column(name = "mastery_page_id")
    public long id;

    @Expose
    @Column(name = "masteries") // Collection of masteries associated with the mastery page.
    public List<MasteryDto> masteries;

    @Expose
    @Column(name = "name")
    public String name;         // Mastery page name.

}
