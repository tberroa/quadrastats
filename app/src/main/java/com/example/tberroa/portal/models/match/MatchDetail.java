package com.example.tberroa.portal.models.match;

// This object contains match detail information

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"WeakerAccess", "unused", "MismatchedQueryAndUpdateOfCollection"})
@Table(name = "MatchDetail")
public class MatchDetail extends Model {

    @Expose
    @Column(name = "map_id")
    public int mapId;

    @Expose
    @Column(name = "match_creation")
    public long matchCreation;

    @Expose
    @Column(name = "match_duration")
    public long matchDuration;

    @Expose
    @Column(name = "match_id")
    public long matchId;

    @Expose
    @Column(name = "match_mode")
    public String matchMode;

    @Expose
    @Column(name = "match_type")
    public String matchType;

    @Expose
    @Column(name = "match_version")
    public String matchVersion;

    @Expose
    @Column(name = "participant_identities")
    private List<ParticipantIdentity> participantIdentities = new ArrayList<>();

    @Expose
    @Column(name = "participants")
    private List<Participant> participants = new ArrayList<>();

    @Expose
    @Column(name = "platform_id")
    public String platformId;

    @Expose
    @Column(name = "queue_type")
    public String queueType;

    @Expose
    @Column(name = "region")
    public String region;

    @Expose
    @Column(name = "season")
    public String season;

    @Expose
    @Column(name = "teams")
    private List<Team> teams = new ArrayList<>();

    public MatchDetail() {
        super();
    }

    public List<ParticipantIdentity> getParticipantIdentities() {
        return getMany(ParticipantIdentity.class, "match_detail");
    }

    public List<Participant> getParticipants() {
        return getMany(Participant.class, "match_detail");
    }

    public List<Team> getTeams() {
        return getMany(Team.class, "match_detail");
    }

    public void cascadeSave() {
        ActiveAndroid.beginTransaction();
        try {
            save();
            if (!participantIdentities.isEmpty()) {
                for (ParticipantIdentity identity : participantIdentities) {
                    identity.matchDetail = this;
                    identity.cascadeSave();
                }
            }
            if (!participants.isEmpty()) {
                for (Participant participant : participants) {
                    participant.matchDetail = this;
                    participant.cascadeSave();
                }
            }
            if (!teams.isEmpty()) {
                for (Team team : teams) {
                    team.matchDetail = this;
                    team.save();
                }
            }
            ActiveAndroid.setTransactionSuccessful();
        } finally {
            ActiveAndroid.endTransaction();
        }
    }
}
