package com.example.tberroa.portal.screens.stats.withfriends;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.tberroa.portal.R;
import com.example.tberroa.portal.models.stats.MatchStats;
import com.example.tberroa.portal.screens.ScreenUtil;
import com.example.tberroa.portal.screens.stats.StatsUtil;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WithFriendsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup group, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_with_friends, group, false);

        if (isAdded()) {
            // grab data passed to fragment
            Bundle bundle = getArguments();
            String matchStatsMapJson = bundle.getString("match_stats_map");
            Type matchStatsMapType = new TypeToken<Map<String, MatchStats>>() {
            }.getType();
            Map<String, MatchStats> matchStatsMap = new Gson().fromJson(matchStatsMapJson, matchStatsMapType);

            if (matchStatsMap != null) {
                // separate the data into a list of names and a list match stats
                List<String> names = new ArrayList<>(matchStatsMap.keySet());
                List<MatchStats> matchStatsList = new ArrayList<>(matchStatsMap.values());

                // display appropriate victory/defeat text
                if (matchStatsList.get(0).winner) {
                    TextView defeatView = (TextView) v.findViewById(R.id.defeat_view);
                    defeatView.setVisibility(View.GONE);
                } else {
                    TextView victoryView = (TextView) v.findViewById(R.id.victory_view);
                    victoryView.setVisibility(View.GONE);
                }

                // populate the summoner table
                GridLayout summonerTable = (GridLayout) v.findViewById(R.id.summoner_table_layout);
                int cWidth = ScreenUtil.dpToPx(getActivity(), 65);
                int cHeight = ScreenUtil.dpToPx(getActivity(), 65);
                int width = ScreenUtil.dpToPx(getActivity(), 30);
                int height = ScreenUtil.dpToPx(getActivity(), 30);
                int padding = ScreenUtil.dpToPx(getActivity(), 2);
                for (MatchStats matchStats : matchStatsList) {
                    LinearLayout summonerLayout = new LinearLayout(getActivity());
                    summonerLayout.setOrientation(LinearLayout.HORIZONTAL);
                    summonerLayout.setPadding(padding, padding, padding, padding);
                    summonerLayout.setGravity(Gravity.CENTER);

                    // champion icon
                    ImageView championIconView = new ImageView(getActivity());
                    championIconView.setPadding(padding, padding, padding, padding);
                    int championIcon = ScreenUtil.championIcon(StatsUtil.championName(matchStats.champion));
                    Picasso.with(getActivity()).load(championIcon).resize(cWidth, cHeight).into(championIconView);
                    summonerLayout.addView(championIconView);

                    // summoner spells
                    LinearLayout summonerSpellLayout = new LinearLayout(getActivity());
                    summonerSpellLayout.setOrientation(LinearLayout.VERTICAL);
                    ImageView summonerSpell1View = new ImageView(getActivity());
                    ImageView summonerSpell2View = new ImageView(getActivity());
                    summonerSpell1View.setPadding(padding, padding, padding, padding);
                    summonerSpell2View.setPadding(padding, padding, padding, padding);
                    String spell1URL = ScreenUtil.constructSummonerSpellURL(matchStats.spell1);
                    String spell2URL = ScreenUtil.constructSummonerSpellURL(matchStats.spell2);
                    Picasso.with(getActivity()).load(spell1URL).resize(width, height).into(summonerSpell1View);
                    Picasso.with(getActivity()).load(spell2URL).resize(width, height).into(summonerSpell2View);
                    summonerSpellLayout.addView(summonerSpell1View);
                    summonerSpellLayout.addView(summonerSpell2View);
                    summonerLayout.addView(summonerSpellLayout);

                    // keystone mastery and trinket
                    LinearLayout keystoneTrinketLayout = new LinearLayout(getActivity());
                    keystoneTrinketLayout.setOrientation(LinearLayout.VERTICAL);
                    ImageView keystoneView = new ImageView(getActivity());
                    keystoneView.setPadding(padding, padding, padding, padding);
                    String keystoneURL = ScreenUtil.constructMasteryURL(matchStats.keystone);
                    Picasso.with(getActivity()).load(keystoneURL).resize(width, height).into(keystoneView);
                    keystoneTrinketLayout.addView(keystoneView);
                    ImageView trinketView = new ImageView(getActivity());
                    trinketView.setPadding(padding, padding, padding, padding);
                    String trinketURL = ScreenUtil.constructItemURL(matchStats.item6);
                    Picasso.with(getActivity()).load(trinketURL).resize(width, height).into(trinketView);
                    keystoneTrinketLayout.addView(trinketView);
                    summonerLayout.addView(keystoneTrinketLayout);

                    // items
                    GridLayout itemLayout = new GridLayout(getActivity());
                    itemLayout.setColumnCount(3);
                    itemLayout.setRowCount(2);
                    for (int i = 0; i < 6; i++) {
                        String itemURL;
                        switch (i) {
                            case 0:
                                itemURL = ScreenUtil.constructItemURL(matchStats.item0);
                                break;
                            case 1:
                                itemURL = ScreenUtil.constructItemURL(matchStats.item1);
                                break;
                            case 2:
                                itemURL = ScreenUtil.constructItemURL(matchStats.item2);
                                break;
                            case 3:
                                itemURL = ScreenUtil.constructItemURL(matchStats.item3);
                                break;
                            case 4:
                                itemURL = ScreenUtil.constructItemURL(matchStats.item4);
                                break;
                            case 5:
                                itemURL = ScreenUtil.constructItemURL(matchStats.item5);
                                break;
                            default:
                                itemURL = ScreenUtil.constructItemURL(matchStats.item0);
                                break;
                        }
                        ImageView itemView = new ImageView(getActivity());
                        itemView.setPadding(padding, padding, padding, padding);
                        Picasso.with(getActivity()).load(itemURL).resize(width, height).into(itemView);
                        itemLayout.addView(itemView);
                    }
                    summonerLayout.addView(itemLayout);

                    // add the summoner to the table
                    summonerTable.addView(summonerLayout);
                }

                // create the entries
                List<BarEntry> entriesDmg = new ArrayList<>();
                List<Entry> entriesKills = new ArrayList<>();
                int i = 0;
                for (MatchStats matchStats : matchStatsList) {
                    entriesDmg.add(new BarEntry(matchStats.total_damage_dealt_to_champions, i));
                    entriesKills.add(new Entry(matchStats.kills, i));
                    i++;
                }

                // organize entries by chart type
                List<List<BarEntry>> barEntries = new ArrayList<>();
                barEntries.add(entriesDmg);
                List<List<Entry>> pieEntries = new ArrayList<>();
                pieEntries.add(entriesKills);

                // create the data sets
                int[] colors = ScreenUtil.chartColors();
                List<BarDataSet> barDataSets = new ArrayList<>();
                for (List<BarEntry> entries : barEntries) {
                    BarDataSet barDataSet = new BarDataSet(entries, "");
                    barDataSet.setColors(colors, getActivity());
                    barDataSet.setValueTextColor(Color.WHITE);
                    barDataSet.setValueTextSize(12);
                    barDataSets.add(barDataSet);
                }
                List<PieDataSet> pieDataSets = new ArrayList<>();
                for (List<Entry> entries : pieEntries) {
                    PieDataSet pieDataSet = new PieDataSet(entries, "");
                    pieDataSet.setColors(colors, getActivity());
                    pieDataSet.setSelectionShift(0);
                    pieDataSets.add(pieDataSet);
                }

                // initialize the chart views
                List<BarChart> barCharts = new ArrayList<>();
                barCharts.add((BarChart) v.findViewById(R.id.dmg_chart));
                List<PieChart> pieCharts = new ArrayList<>();
                pieCharts.add((PieChart) v.findViewById(R.id.kills_chart));

                // populate and format the charts
                i = 0;
                for (BarChart barChart : barCharts) {
                    barChart.setData(new BarData(names, barDataSets.get(i)));
                    barChart.getAxisLeft().setTextColor(Color.WHITE);
                    barChart.setDescription("");
                    barChart.getXAxis().setDrawLabels(false);
                    barChart.getXAxis().setDrawGridLines(false);
                    barChart.getXAxis().setDrawAxisLine(false);
                    barChart.getAxisRight().setDrawLabels(false);
                    barChart.getAxisRight().setDrawGridLines(false);
                    barChart.getAxisRight().setDrawAxisLine(false);
                    barChart.getAxisLeft().setDrawAxisLine(false);
                    barChart.setDrawBorders(false);
                    barChart.getLegend().setEnabled(false);
                    barChart.getData().setHighlightEnabled(false);
                    barChart.setTouchEnabled(false);
                }
                i = 0;
                for (PieChart pieChart : pieCharts) {
                    pieChart.setData(new PieData(names, pieDataSets.get(i)));
                    pieChart.setHoleColor(Color.TRANSPARENT);
                    pieChart.getLegend().setTextColor(Color.WHITE);
                    pieChart.getData().setValueTextSize(15);
                    pieChart.setHoleRadius(35);
                    pieChart.setTransparentCircleAlpha(0);
                    pieChart.getLegend().setEnabled(false);
                    pieChart.setTouchEnabled(false);
                    pieChart.setDescription("");
                    i++;
                }

                // display legend
                createLegend(names, v);
            }
        }
        return v;
    }

    private void createLegend(List<String> names, View v) {
        // set unused elements to gone
        ImageView positionIcon = (ImageView) v.findViewById(R.id.position_view);
        positionIcon.setVisibility(View.GONE);
        ImageView championIcon = (ImageView) v.findViewById(R.id.champ_icon_view);
        championIcon.setVisibility(View.GONE);

        // set names
        GridLayout legendNames = (GridLayout) v.findViewById(R.id.names_layout);
        legendNames.removeAllViews();
        int i = 0;
        for (String name : names) {
            TextView textView = new TextView(getActivity());
            textView.setText(name);
            textView.setTextSize(12);
            textView.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
            textView.setPadding(ScreenUtil.dpToPx(getActivity(), 5), 0, ScreenUtil.dpToPx(getActivity(), 5), 0);
            legendNames.addView(textView);

            ImageView imageView = new ImageView(getActivity());
            imageView.setMinimumWidth(ScreenUtil.dpToPx(getActivity(), 10));
            imageView.setMinimumHeight(ScreenUtil.dpToPx(getActivity(), 10));
            imageView.setPadding(0, ScreenUtil.dpToPx(getActivity(), 5), 0, 0);
            imageView.setImageResource(ScreenUtil.intToColor(i));
            legendNames.addView(imageView);

            i++;
        }
    }
}
