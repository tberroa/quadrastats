package com.example.tberroa.portal.screens.stats;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.example.tberroa.portal.R;
import com.example.tberroa.portal.data.Constants;
import com.example.tberroa.portal.models.stats.MatchStats;
import com.example.tberroa.portal.screens.ScreenUtil;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class WinRatesDialog extends Dialog {

    private final Context context;
    private final List<MatchStats> matchStatsList;
    private final Map<Long, Map<String, MatchStats>> matchStatsMapMap;

    public WinRatesDialog(Context context, List<MatchStats> matchList, Map<Long, Map<String, MatchStats>> matchMap) {
        super(context, R.style.AppTheme_Dialog);
        this.context = context;
        matchStatsList = matchList;
        matchStatsMapMap = matchMap;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_win_rates);
        int width = (Constants.UI_DIALOG_WIDTH * ScreenUtil.screenWidth(context)) / 100;
        int height = (Constants.UI_DIALOG_HEIGHT * ScreenUtil.screenHeight(context)) / 100;
        getWindow().setLayout(width, height);

        // get win rates
        Map<String, WinRate> winRatesByFriend = new LinkedHashMap<>();
        if (matchStatsList != null) {
            for (MatchStats matchStats : matchStatsList) {
                WinRate winRate = winRatesByFriend.get(matchStats.summoner_name);
                if (winRate == null) {
                    winRate = new WinRate(matchStats.winner);
                } else {
                    winRate.update(matchStats.winner);
                }
                winRatesByFriend.put(matchStats.summoner_name, winRate);
            }
        }
        if (matchStatsMapMap != null) {
            for (Map.Entry<Long, Map<String, MatchStats>> matchMap : matchStatsMapMap.entrySet()) {
                for (Map.Entry<String, MatchStats> match : matchMap.getValue().entrySet()) {
                    WinRate winRate = winRatesByFriend.get(match.getKey());
                    if (winRate == null) {
                        winRate = new WinRate(match.getValue().winner);
                    } else {
                        winRate.update(match.getValue().winner);
                    }
                    winRatesByFriend.put(match.getKey(), winRate);
                }
            }
        }

        // convert map into two lists
        List<String> names = new ArrayList<>(winRatesByFriend.keySet());
        List<WinRate> winRates = new ArrayList<>(winRatesByFriend.values());

        // populate user win ratio
        TextView userWinRatio = (TextView) findViewById(R.id.user_win_ratio_view);
        userWinRatio.setText(winRates.get(0).ratio());

        // populate list view
        ListView listView = (ListView) findViewById(R.id.list_view);
        WinRatesAdapter adapter = new WinRatesAdapter(context, winRates, names);
        listView.setAdapter(adapter);
    }
}