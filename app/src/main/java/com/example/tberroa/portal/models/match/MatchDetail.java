package com.example.tberroa.portal.models.match;

// This object contains match detail information

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;

import java.util.List;

@Table(name = "MatchDetail")
public class MatchDetail extends Model {

    public MatchDetail(){
    }

    @Expose
    @Column(name = "map_id")
    public int mapId;                   // Match map ID

    @Expose
    @Column(name = "match_creation")
    public long matchCreation;          // Match creation time. Designates when the team select lobby is
                                        // created and/or the match is made through match making,
                                        // not when the game actually starts.

    @Expose
    @Column(name = "match_duration")
    public long matchDuration;          // match duration

    @Expose
    @Column(name = "match_id")
    public long match_id;               // id of the match

    @Expose
    @Column(name = "match_mode")
    public String matchMode;            // Match mode (Legal values: CLASSIC, ODIN, ARAM, TUTORIAL,
                                        // ONEFORALL, ASCENSION, FIRSTBLOOD, KINGPORO)

    @Expose
    @Column(name = "match_type")
    public String matchType;            // Match type (Legal values: CUSTOM_GAME, MATCHED_GAME, TUTORIAL_GAME)

    @Expose
    @Column(name = "match_version")
    public String matchVersion;         // match version

    @Expose
    @Column(name = "participant_identities")
    public List<ParticipantIdentity> participantIdentities;
                                        // participant identity information

    @Expose
    @Column(name = "participants")
    public List<Participant> participants;
                                        // participant information

    @Expose
    @Column(name = "platform_id")
    public String platformId;           // platform id of the match

    @Expose
    @Column(name = "queue_type")        // Match queue type (Legal values: CUSTOM, NORMAL_5x5_BLIND, RANKED_SOLO_5x5,
    public String queueType;            // RANKED_PREMADE_5x5, BOT_5x5, NORMAL_3x3, RANKED_PREMADE_3x3, NORMAL_5x5_DRAFT,
                                        // ODIN_5x5_BLIND, ODIN_5x5_DRAFT, BOT_ODIN_5x5, BOT_5x5_INTRO, BOT_5x5_BEGINNER,
                                        // BOT_5x5_INTERMEDIATE, RANKED_TEAM_3x3, RANKED_TEAM_5x5, BOT_TT_3x3, GROUP_FINDER_5x5,
                                        // ARAM_5x5, ONEFORALL_5x5, FIRSTBLOOD_1x1, FIRSTBLOOD_2x2, SR_6x6, URF_5x5,
                                        // ONEFORALL_MIRRORMODE_5x5, BOT_URF_5x5, NIGHTMARE_BOT_5x5_RANK1, NIGHTMARE_BOT_5x5_RANK2,
                                        // NIGHTMARE_BOT_5x5_RANK5, ASCENSION_5x5, HEXAKILL, BILGEWATER_ARAM_5x5, KING_PORO_5x5,
                                        // COUNTER_PICK, BILGEWATER_5x5, TEAM_BUILDER_DRAFT_UNRANKED_5x5, TEAM_BUILDER_DRAFT_RANKED_5x5)

    @Expose
    @Column(name = "region")
    public String region;               // region where match was played

    @Expose
    @Column(name = "season")            // Season match was played (Legal values: PRESEASON3, SEASON3, PRESEASON2014,
    public String season;               // SEASON2014, PRESEASON2015, SEASON2015, PRESEASON2016, SEASON2016)

    @Expose
    @Column(name = "teams")
    public List<Team> teams;            // team information

    @Expose
    @Column(name = "timeline")
    public Timeline timeline;           // match timeline data (not included by default)

}
