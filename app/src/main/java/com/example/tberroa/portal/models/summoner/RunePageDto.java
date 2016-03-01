package com.example.tberroa.portal.models.summoner;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;

import java.util.Set;

@Table(name = "RunePageDto")
public class RunePageDto extends Model {

    public RunePageDto(){
    }

    @Expose
    @Column(name = "current")
    public boolean current;

    @Expose
    @Column(name = "rune_page_id")
    public long id;

    @Expose
    @Column(name = "name")
    public String name;

    @Expose
    @Column(name = "slots")
    public Set<RuneSlotDto> slots;
}
