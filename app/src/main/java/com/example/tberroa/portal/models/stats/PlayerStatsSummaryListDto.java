package com.example.tberroa.portal.models.stats;

// This object contains a collection of player stats summary information

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"WeakerAccess", "unused", "MismatchedQueryAndUpdateOfCollection"})
@Table(name = "PlayerStatsSummaryListDto")
public class PlayerStatsSummaryListDto extends Model {

    @Expose
    @Column(name = "player_stat_summaries")
    private List<PlayerStatsSummaryDto> playerStatSummaries = new ArrayList<>();

    @Expose
    @Column(name = "summoner_id")
    public long summonerId;

    public PlayerStatsSummaryListDto() {
        super();
    }

    public List<PlayerStatsSummaryDto> getPlayerStatSummaries() {
        return getMany(PlayerStatsSummaryDto.class, "player_stats_summary_list");
    }

    public void cascadeSave() {
        ActiveAndroid.beginTransaction();
        try {
            save();
            if (!playerStatSummaries.isEmpty()) {
                for (PlayerStatsSummaryDto playerStatsSummary : playerStatSummaries) {
                    playerStatsSummary.playerStatsSummaryListDto = this;
                    playerStatsSummary.cascadeSave();
                }
            }
        } finally {
            ActiveAndroid.endTransaction();
        }
    }
}
