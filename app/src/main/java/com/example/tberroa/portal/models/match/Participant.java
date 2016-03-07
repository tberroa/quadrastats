package com.example.tberroa.portal.models.match;

// This object contains match participant information

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;

import java.util.List;

@SuppressWarnings({"WeakerAccess", "unused"})
@Table(name = "Participant")
public class Participant extends Model {

    @Expose
    @Column(name = "match_detail")
    public MatchDetail matchDetail;

    @Expose
    @Column(name = "champion_id")
    public int championId;

    @Expose
    @Column(name = "highest_achieved_season_tier")
    public String highestAchievedSeasonTier;

    @Expose
    @Column(name = "masteries")
    public List<Mastery> masteries;

    public List<Mastery> getMasteries(){
        return getMany(Mastery.class, "participant");
    }

    @Expose
    @Column(name = "participant_id")
    public int participantId;

    @Expose
    @Column(name = "runes")
    public List<Rune> runes;

    public List<Rune> getRunes(){
        return getMany(Rune.class, "participant");
    }

    @Expose
    @Column(name = "spell_1_id")
    public int spell1Id;

    @Expose
    @Column(name = "spell_2_id")
    public int spell2Id;

    @Expose
    @Column(name = "stats")
    public ParticipantStats stats;

    @Expose
    @Column(name = "team_id")
    public int teamId;

    @Expose
    @Column(name = "timeline")
    public ParticipantTimeline timeline;

    public Participant(){
        super();
    }

    public void cascadeSave(){
        ActiveAndroid.beginTransaction();
        try{
            save();
            if (masteries != null){
                for (Mastery mastery : masteries){
                    mastery.participant = this;
                    mastery.save();
                }
            }
            if (runes != null){
                for (Rune rune : runes){
                    rune.participant = this;
                    rune.save();
                }
            }
            if (stats != null){
                stats.participant = this;
                stats.save();
            }
            if (timeline != null){
                timeline.participant = this;
                timeline.cascadeSave();
            }
            ActiveAndroid.setTransactionSuccessful();
        } finally {
            ActiveAndroid.endTransaction();
        }
    }
}
