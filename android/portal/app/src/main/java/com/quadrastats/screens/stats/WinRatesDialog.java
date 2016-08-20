package com.quadrastats.screens.stats;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.quadrastats.R;
import com.quadrastats.data.Constants;
import com.quadrastats.models.stats.MatchStats;
import com.quadrastats.screens.ScreenUtil;
import com.quadrastats.screens.StaticRiotData;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class WinRatesDialog extends Dialog {

    private static final String ALL = "ALL";
    private final Context context;
    private final List<MatchStats> matchStatsList;
    private final Map<Long, Map<String, MatchStats>> matchStatsMapMap;
    private final StaticRiotData staticRiotData;
    private final Map<String, Map<String, Map<String, WinRate>>> winRatesBySumChamp;
    private final Map<String, Map<String, WinRate>> winRatesBySumPos;
    private String selectedRole;

    public WinRatesDialog(Context co, List<MatchStats> mL, Map<Long, Map<String, MatchStats>> mM, StaticRiotData sRD) {
        super(co, R.style.AppTheme_Dialog);
        context = co;
        matchStatsList = mL;
        matchStatsMapMap = mM;
        staticRiotData = sRD;
        winRatesBySumPos = new LinkedHashMap<>();
        winRatesBySumChamp = new HashMap<>();
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
        List<String> names = new ArrayList<>(winRatesBySumPos.keySet());
        List<WinRate> winRates = new ArrayList<>();
        for (Map.Entry<String, Map<String, WinRate>> winRatesByPosition : winRatesBySumPos.entrySet()) {
            winRates.add(winRatesByPosition.getValue().get(ALL));
        }

        // populate user win ratio
        TextView userWinRatio = (TextView) findViewById(R.id.user_win_ratio_view);
        userWinRatio.setText(winRates.get(0).ratio());

        // construct adapter package
        selectedRole = ALL;
        WinRatePackage winRatePackage = new WinRatePackage();
        winRatePackage.winRates = winRates;
        winRatePackage.names = names;
        winRatePackage.staticRiotData = staticRiotData;
        winRatePackage.winRatesBySumChamp = winRatesBySumChamp;
        winRatePackage.selectedRole = selectedRole;

        // populate list view
        ListView listView = (ListView) findViewById(R.id.list_view);
        WinRatesAdapter adapter = new WinRatesAdapter(context, winRatePackage);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // cast the view to a linear layout
                LinearLayout layout = (LinearLayout) view;

                // get the number of child views
                int children = layout.getChildCount();

                // toggle champ win ratios
                if (children == 1) {
                    // get the win rate objects map
                    Map<String, Map<String, WinRate>> intermediate = winRatesBySumChamp.get(names.get(i));
                    Map<String, WinRate> winRatesByChamp = intermediate.get(selectedRole);

                    // make sure the map is not null
                    if (winRatesByChamp != null) {
                        adapter.winRates.get(i).expanded = true;

                        // initialize a list to hold the champ views
                        List<ChampView> champLayouts = new ArrayList<>();

                        // iterate over the win rate map
                        LayoutInflater inflater = getLayoutInflater();
                        int side = (20 * ScreenUtil.screenWidth(context)) / 100;
                        Drawable placeholder = ContextCompat.getDrawable(context, R.drawable.ic_placeholder);
                        Bitmap bitmap = ((BitmapDrawable) placeholder).getBitmap();
                        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, side, side, true);
                        Drawable resizedPlaceholder = new BitmapDrawable(context.getResources(), resizedBitmap);
                        for (Map.Entry<String, WinRate> entry : winRatesByChamp.entrySet()) {
                            // initialize views
                            @SuppressLint("InflateParams")
                            View champLayout = inflater.inflate(R.layout.view_win_rates_champ, null);
                            ImageView champIcon = (ImageView) champLayout.findViewById(R.id.champ_icon_view);
                            TextView played = (TextView) champLayout.findViewById(R.id.champ_played_view);
                            TextView won = (TextView) champLayout.findViewById(R.id.champ_wins_view);
                            TextView ratio = (TextView) champLayout.findViewById(R.id.champ_win_ratio_view);

                            // set champion icon
                            String url = StatsUtil.championIconURL(staticRiotData.version, entry.getKey());
                            Picasso.with(context).load(url).resize(side, side)
                                    .placeholder(resizedPlaceholder).into(champIcon);

                            // set text views
                            played.setText(String.valueOf(entry.getValue().played()));
                            won.setText(String.valueOf(entry.getValue().wins()));
                            ratio.setText(entry.getValue().ratio());

                            // add view to list
                            ChampView champView = new ChampView();
                            champView.played = entry.getValue().played();
                            champView.ratio = entry.getValue().ratio();
                            champView.champLayout = champLayout;
                            champLayouts.add(champView);
                        }

                        // sort the champ views by most played
                        Collections.sort(champLayouts, new Comparator<ChampView>() {
                            @Override
                            public int compare(ChampView object1, ChampView object2) {
                                int compareVal = object1.played - object2.played;
                                if (compareVal != 0) {
                                    return compareVal;
                                } else {
                                    return object1.ratio.compareTo(object2.ratio);
                                }
                            }
                        });

                        // insert the champ views into the main layout
                        boolean color = true;
                        for (int j = champLayouts.size() - 1; j >= 0; j--) {
                            View champLayout = champLayouts.get(j).champLayout;
                            if (color) {
                                int bgColor = ContextCompat.getColor(context, R.color.white_transparent);
                                champLayout.setBackgroundColor(bgColor);
                            }
                            layout.addView(champLayout);
                            color = !color;
                        }
                    }
                } else {
                    adapter.winRates.get(i).expanded = false;
                    for (int j = 1; j < children; ) {
                        layout.removeViewAt(j);
                        children = layout.getChildCount();
                    }
                }
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
                    selectedRole = Constants.POS_TOP;
                    topCheck.setVisibility(View.VISIBLE);
                    updateAdapter(adapter, selectedRole);
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
                    selectedRole = Constants.POS_JUNGLE;
                    jungleCheck.setVisibility(View.VISIBLE);
                    updateAdapter(adapter, selectedRole);
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
                    selectedRole = Constants.POS_MID;
                    midCheck.setVisibility(View.VISIBLE);
                    updateAdapter(adapter, selectedRole);
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
                    selectedRole = Constants.POS_BOT;
                    botCheck.setVisibility(View.VISIBLE);
                    updateAdapter(adapter, selectedRole);
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
                    selectedRole = Constants.POS_SUPPORT;
                    supportCheck.setVisibility(View.VISIBLE);
                    updateAdapter(adapter, selectedRole);
                }
            }
        });

        // initialize clear button
        Button clearButton = (Button) findViewById(R.id.clear_button);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedRole = ALL;
                topCheck.setVisibility(View.INVISIBLE);
                jungleCheck.setVisibility(View.INVISIBLE);
                midCheck.setVisibility(View.INVISIBLE);
                botCheck.setVisibility(View.INVISIBLE);
                supportCheck.setVisibility(View.INVISIBLE);
                updateAdapter(adapter, selectedRole);
            }
        });
    }

    private void updateAdapter(WinRatesAdapter adapter, String selectedRole) {
        adapter.selectedRole = selectedRole;
        adapter.winRates.clear();
        for (Map.Entry<String, Map<String, WinRate>> winRatesBySum : winRatesBySumPos.entrySet()) {
            adapter.winRates.add(winRatesBySum.getValue().get(selectedRole));
        }
        for (WinRate winRate : adapter.winRates) {
            if (winRate != null) {
                winRate.expanded = false;
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void updateWinRates(MatchStats matchStats) {
        if (matchStats.winner != null) {
            // get the win rate by position map for the summoner
            Map<String, WinRate> winRatesByPos = winRatesBySumPos.get(matchStats.summoner_name);
            if (winRatesByPos == null) {
                winRatesByPos = new HashMap<>();
            }

            // get the win rate by champion map for the summoner
            Map<String, Map<String, WinRate>> winRatesByChamp = winRatesBySumChamp.get(matchStats.summoner_name);
            if (winRatesByChamp == null) {
                winRatesByChamp = new HashMap<>();
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
                if (role.equals(Constants.POS_BOT) || role.equals(Constants.POS_SUPPORT)) {
                    position = role;
                } else {
                    if ((matchStats.cs_per_min != null) && (matchStats.cs_per_min > 2)) {
                        position = Constants.POS_BOT;
                    } else {
                        position = Constants.POS_SUPPORT;
                    }
                }
            }

            // update the win rate for this summoner in this position
            WinRate winRatePos = winRatesByPos.get(position);
            if (winRatePos == null) {
                winRatePos = new WinRate(matchStats.winner);
            } else {
                winRatePos.update(matchStats.winner);
            }

            // update the win rate for this summoner with this champ
            Map<String, WinRate> intermediate = winRatesByChamp.get(position);
            if (intermediate == null) {
                intermediate = new HashMap<>();
            }
            String champKey = StatsUtil.championKey(matchStats.champion, staticRiotData.championsMap);
            WinRate winRateChamp = intermediate.get(champKey);
            if (winRateChamp == null) {
                winRateChamp = new WinRate(matchStats.winner);
            } else {
                winRateChamp.update(matchStats.winner);
            }
            intermediate.put(champKey, winRateChamp);
            Map<String, WinRate> intermediateAll = winRatesByChamp.get(ALL);
            if (intermediateAll == null) {
                intermediateAll = new HashMap<>();
            }
            WinRate winRateChampAll = intermediateAll.get(champKey);
            if (winRateChampAll == null) {
                winRateChampAll = new WinRate(matchStats.winner);
            } else {
                winRateChampAll.update(matchStats.winner);
            }
            intermediateAll.put(champKey, winRateChampAll);

            // update the overall win rate for this summoner
            WinRate winRateAll = winRatesByPos.get(ALL);
            if (winRateAll == null) {
                winRateAll = new WinRate(matchStats.winner);
            } else {
                winRateAll.update(matchStats.winner);
            }

            // insert the updated win rates into the maps
            winRatesByPos.put(position, winRatePos);
            winRatesByPos.put(ALL, winRateAll);
            winRatesBySumPos.put(matchStats.summoner_name, winRatesByPos);
            winRatesByChamp.put(position, intermediate);
            winRatesByChamp.put(ALL, intermediateAll);
            winRatesBySumChamp.put(matchStats.summoner_name, winRatesByChamp);
        }
    }

    private class ChampView {
        View champLayout;
        int played;
        String ratio;
    }
}