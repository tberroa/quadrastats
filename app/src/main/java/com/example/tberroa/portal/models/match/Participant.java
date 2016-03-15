package com.example.tberroa.portal.models.match;

// This object contains match participant information

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"WeakerAccess", "unused", "MismatchedQueryAndUpdateOfCollection"})
@Table(name = "Participant")
public class Participant extends Model {

    // parent
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
    private List<Mastery> masteries = new ArrayList<>();

    @Expose
    @Column(name = "participant_id")
    public int participantId;

    @Expose
    @Column(name = "runes")
    private List<Rune> runes = new ArrayList<>();

    @Expose
    @Column(name = "spell_1_id")
    public int spell1Id;

    @Expose
    @Column(name = "spell_2_id")
    public int spell2Id;

    @Expose
    @Column(name = "stats")
    private ParticipantStats stats;

    @Expose
    @Column(name = "team_id")
    public int teamId;

    @Expose
    @Column(name = "timeline")
    private ParticipantTimeline timeline;

    public List<Mastery> getMasteries() {
        return getMany(Mastery.class, "participant");
    }

    public List<Rune> getRunes() {
        return getMany(Rune.class, "participant");
    }

    public ParticipantStats getParticipantStats() {
        return new Select()
                .from(ParticipantStats.class)
                .where("participant = ?", getId())
                .executeSingle();
    }

    public ParticipantTimeline getParticipantTimeline() {
        return new Select()
                .from(ParticipantTimeline.class)
                .where("participant = ?", getId())
                .executeSingle();
    }

    public Participant() {
        super();
    }

    public void cascadeSave() {
        ActiveAndroid.beginTransaction();
        try {
            save();
            if (!masteries.isEmpty()) {
                for (Mastery mastery : masteries) {
                    mastery.participant = this;
                    mastery.save();
                }
            }
            if (!runes.isEmpty()) {
                for (Rune rune : runes) {
                    rune.participant = this;
                    rune.save();
                }
            }
            if (stats != null) {
                stats.participant = this;
                stats.save();
            }
            if (timeline != null) {
                timeline.participant = this;
                timeline.cascadeSave();
            }
            ActiveAndroid.setTransactionSuccessful();
        } finally {
            ActiveAndroid.endTransaction();
        }
    }
}
