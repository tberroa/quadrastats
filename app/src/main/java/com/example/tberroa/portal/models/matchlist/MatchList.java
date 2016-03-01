package com.example.tberroa.portal.models.matchlist;

// This object contains match list information

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;

import java.util.List;

@Table(name = "MatchList")
public class MatchList extends Model {

    public MatchList(){
    }

    @Expose
    @Column(name = "end_index")
    public int endIndex;

    @Expose
    @Column(name = "matches")
    public List<MatchReference> matches;

    @Expose
    @Column(name = "start_index")
    public int startIndex;

    @Expose
    @Column(name = "total_games")
    public int totalGames;


}
