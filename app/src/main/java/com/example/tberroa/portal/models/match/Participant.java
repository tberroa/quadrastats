package com.example.tberroa.portal.models.match;

// This object contains match participant information

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;

import java.util.List;

@Table(name = "Participant")
public class Participant extends Model {

    @Expose
    @Column(name = "champion_id")
    public int championId;                          // champion id

    @Expose
    @Column(name = "highest_achieved_season_tier")  // Highest ranked tier achieved for the previous season, if any,
    public String highestAchievedSeasonTier;        // otherwise null. Used to display border in game loading screen.
                                                    // (Legal values: CHALLENGER, MASTER, DIAMOND, PLATINUM, GOLD, SILVER,
                                                    // BRONZE, UNRANKED)

    @Expose
    @Column(name = "masteries")
    public List<Mastery> masteries;                 // list of mastery information

    @Expose
    @Column(name = "participant_id")
    public int participantId;                       // participant id

    @Expose
    @Column(name = "runes")                         // list of rune information
    public List<Rune> runes;

    @Expose
    @Column(name = "spell_1_id")
    public int spell1Id;                            // first summoner spell id

    @Expose
    @Column(name = "spell_2_id")
    public int spell2Id;                            // second summoner spell id

    @Expose
    @Column(name = "stats")
    public ParticipantStats participantStats;       // participant statistics

    @Expose
    @Column(name = "team_id")
    public int teamId;                              // team id

    @Expose
    @Column(name = "timeline")
    public ParticipantTimeline participantTimeline; // Timeline data. Delta fields refer to values for the specified
                                                    // period (e.g., the gold per minute over the first 10 minutes
                                                    // of the game versus the second 20 minutes of the game. Diffs
                                                    // fields refer to the deltas versus the calculated lane opponent(s).


}
