package com.example.tberroa.portal.screens.stats;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.tberroa.portal.R;
import com.example.tberroa.portal.data.Constants;
import com.example.tberroa.portal.models.stats.MatchStats;
import com.example.tberroa.portal.screens.ScreenUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class WinRatesDialog extends Dialog {

    private static final String ALL = "ALL";
    private final Context context;
    private final List<MatchStats> matchStatsList;
    private final Map<Long, Map<String, MatchStats>> matchStatsMapMap;
    private final List<WinRate> winRates;
    private final Map<String, Map<String, WinRate>> winRatesBySummonerPos;
    private WinRatesAdapter adapter;

    public WinRatesDialog(Context context, List<MatchStats> matchList, Map<Long, Map<String, MatchStats>> matchMap) {
        super(context, R.style.AppTheme_Dialog);
        this.context = context;
        matchStatsList = matchList;
        matchStatsMapMap = matchMap;
        winRatesBySummonerPos = new LinkedHashMap<>();
        winRates = new ArrayList<>();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_win_rates);
        int width = (Constants.UI_DIALOG_WIDTH * ScreenUtil.screenWidth(context)) / 100;
        int height = (Constants.UI_DIALOG_HEIGHT * ScreenUtil.screenHeight(context)) / 100;
        getWindow().setLayout(width, height);

        // get win rates
        if (matchStatsList != null) {
            for (MatchStats matchStats : matchStatsList) {
                updateWinRates(matchStats);
            }
        }
        if (matchStatsMapMap != null) {
            for (Map.Entry<Long, Map<String, MatchStats>> matchMap : matchStatsMapMap.entrySet()) {
                for (Map.Entry<String, MatchStats> match : matchMap.getValue().entrySet()) {
                    updateWinRates(match.getValue());
                }
            }
        }

        // convert map into two lists
        List<String> names = new ArrayList<>(winRatesBySummonerPos.keySet());
        for (Map.Entry<String, Map<String, WinRate>> winRatesByPosition : winRatesBySummonerPos.entrySet()) {
            winRates.add(winRatesByPosition.getValue().get(ALL));
        }

        // populate user win ratio
        TextView userWinRatio = (TextView) findViewById(R.id.user_win_ratio_view);
        userWinRatio.setText(winRates.get(0).ratio());

        // populate list view
        ListView listView = (ListView) findViewById(R.id.list_view);
        adapter = new WinRatesAdapter(context, winRates, names);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int i, long l) {

            }
        });

        // initialize the role checks
        ImageView topCheck = (ImageView) findViewById(R.id.top_check);
        ImageView jungleCheck = (ImageView) findViewById(R.id.jungle_check);
        ImageView midCheck = (ImageView) findViewById(R.id.mid_check);
        ImageView botCheck = (ImageView) findViewById(R.id.bot_check);
        ImageView supportCheck = (ImageView) findViewById(R.id.support_check);
        topCheck.setVisibility(View.INVISIBLE);
        jungleCheck.setVisibility(View.INVISIBLE);
        midCheck.setVisibility(View.INVISIBLE);
        botCheck.setVisibility(View.INVISIBLE);
        supportCheck.setVisibility(View.INVISIBLE);

        // initialize the position images
        ImageView topIcon = (ImageView) findViewById(R.id.top_view);
        ImageView jungleIcon = (ImageView) findViewById(R.id.jungle_view);
        ImageView midIcon = (ImageView) findViewById(R.id.mid_view);
        ImageView botIcon = (ImageView) findViewById(R.id.bot_view);
        ImageView supportIcon = (ImageView) findViewById(R.id.support_view);

        // set listeners to make them behave like radio buttons
        topIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                jungleCheck.setVisibility(View.INVISIBLE);
                midCheck.setVisibility(View.INVISIBLE);
                botCheck.setVisibility(View.INVISIBLE);
                supportCheck.setVisibility(View.INVISIBLE);
                if (topCheck.getVisibility() == View.INVISIBLE) {
                    topCheck.setVisibility(View.VISIBLE);
                    winRates.clear();
                    for (Map.Entry<String, Map<String, WinRate>> winRatesByPos : winRatesBySummonerPos.entrySet()) {
                        winRates.add(winRatesByPos.getValue().get(Constants.POS_TOP));
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });
        jungleIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                topCheck.setVisibility(View.INVISIBLE);
                midCheck.setVisibility(View.INVISIBLE);
                botCheck.setVisibility(View.INVISIBLE);
                supportCheck.setVisibility(View.INVISIBLE);
                if (jungleCheck.getVisibility() == View.INVISIBLE) {
                    jungleCheck.setVisibility(View.VISIBLE);
                    winRates.clear();
                    for (Map.Entry<String, Map<String, WinRate>> winRatesByPos : winRatesBySummonerPos.entrySet()) {
                        winRates.add(winRatesByPos.getValue().get(Constants.POS_JUNGLE));
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });
        midIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                topCheck.setVisibility(View.INVISIBLE);
                jungleCheck.setVisibility(View.INVISIBLE);
                botCheck.setVisibility(View.INVISIBLE);
                supportCheck.setVisibility(View.INVISIBLE);
                if (midCheck.getVisibility() == View.INVISIBLE) {
                    midCheck.setVisibility(View.VISIBLE);
                    winRates.clear();
                    for (Map.Entry<String, Map<String, WinRate>> winRatesByPos : winRatesBySummonerPos.entrySet()) {
                        winRates.add(winRatesByPos.getValue().get(Constants.POS_MID));
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });
        botIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                topCheck.setVisibility(View.INVISIBLE);
                jungleCheck.setVisibility(View.INVISIBLE);
                midCheck.setVisibility(View.INVISIBLE);
                supportCheck.setVisibility(View.INVISIBLE);
                if (botCheck.getVisibility() == View.INVISIBLE) {
                    botCheck.setVisibility(View.VISIBLE);
                    winRates.clear();
                    for (Map.Entry<String, Map<String, WinRate>> winRatesByPos : winRatesBySummonerPos.entrySet()) {
                        winRates.add(winRatesByPos.getValue().get(Constants.POS_BOT));
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });
        supportIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                topCheck.setVisibility(View.INVISIBLE);
                jungleCheck.setVisibility(View.INVISIBLE);
                midCheck.setVisibility(View.INVISIBLE);
                botCheck.setVisibility(View.INVISIBLE);
                if (supportCheck.getVisibility() == View.INVISIBLE) {
                    supportCheck.setVisibility(View.VISIBLE);
                    winRates.clear();
                    for (Map.Entry<String, Map<String, WinRate>> winRatesByPos : winRatesBySummonerPos.entrySet()) {
                        winRates.add(winRatesByPos.getValue().get(Constants.POS_SUPPORT));
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void updateWinRates(MatchStats matchStats) {
        // get the win rate by position map for the summoner
        Map<String, WinRate> winRatesByPosition = winRatesBySummonerPos.get(matchStats.summoner_name);
        if (winRatesByPosition == null) {
            winRatesByPosition = new HashMap<>();
        }

        // determine the position played by the summoner in this match
        String position;
        String lane = matchStats.lane;
        String role = matchStats.role;
        String top = Constants.POS_TOP;
        String jungle = Constants.POS_JUNGLE;
        String mid = Constants.POS_MID;
        if (lane.equals(top) || lane.equals(jungle) || lane.equals(mid)) {
            position = lane;
        } else {
            position = role;
        }

        // update the win rate for this summoner in this position
        WinRate winRate = winRatesByPosition.get(position);
        if (winRate == null) {
            winRate = new WinRate(matchStats.winner);
        } else {
            winRate.update(matchStats.winner);
        }

        // update the overall win rate for this summoner
        WinRate winRateAll = winRatesByPosition.get(ALL);
        if (winRateAll == null) {
            winRateAll = new WinRate(matchStats.winner);
        } else {
            winRateAll.update(matchStats.winner);
        }

        // insert the updated win rates into the maps
        winRatesByPosition.put(position, winRate);
        winRatesByPosition.put(ALL, winRateAll);
        winRatesBySummonerPos.put(matchStats.summoner_name, winRatesByPosition);
    }
}