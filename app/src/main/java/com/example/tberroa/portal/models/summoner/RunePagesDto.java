package com.example.tberroa.portal.models.summoner;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;

import java.util.Set;

@Table(name = "RunePagesDto")
public class RunePagesDto extends Model {

    public RunePagesDto(){
    }

    @Expose
    @Column(name = "pages")
    public Set<RunePageDto> pages;

    @Expose
    @Column(name = "summoner_id")
    public long summonerId;
}
