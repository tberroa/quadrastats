package com.example.tberroa.portal.screens.stats.withfriends;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.tberroa.portal.R;
import com.example.tberroa.portal.data.Constants;
import com.example.tberroa.portal.models.stats.MatchStats;
import com.example.tberroa.portal.screens.ScreenUtil;
import com.example.tberroa.portal.screens.StaticRiotData;
import com.example.tberroa.portal.screens.stats.IntValueFormat;
import com.example.tberroa.portal.screens.stats.StatsUtil;
import com.example.tberroa.portal.screens.stats.withfriends.WFViewAdapter.withFriendsViewHolder;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WFViewAdapter extends RecyclerView.Adapter<withFriendsViewHolder> {

    private final Context context;
    private final Map<String, MatchStats> matchStatsMap;
    private final StaticRiotData staticRiotData;

    public WFViewAdapter(Context context, Map<String, MatchStats> matchStatsMap, StaticRiotData staticRiotData) {
        this.context = context;
        this.matchStatsMap = matchStatsMap;
        this.staticRiotData = staticRiotData;
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    @Override
    public void onBindViewHolder(withFriendsViewHolder withFriendsViewHolder, int position) {
        // separate the data into a list of names and a list match stats
        List<String> names = new ArrayList<>(matchStatsMap.keySet());
        List<String> namesPie = new ArrayList<>(matchStatsMap.keySet());
        List<MatchStats> matchStatsList = new ArrayList<>(matchStatsMap.values());

        // if it wasn't a five man queue, include label for non friends in pie charts
        boolean notFiveMan = false;
        if (names.size() < 5) {
            notFiveMan = true;
            namesPie.add(context.getResources().getString(R.string.gwf_others));
        }

        // display appropriate victory/defeat text
        if (matchStatsList.get(0).winner) {
            withFriendsViewHolder.victoryView.setVisibility(View.VISIBLE);
        } else {
            withFriendsViewHolder.defeatView.setVisibility(View.VISIBLE);
        }

        // team kda
        String teamKills = String.valueOf(matchStatsList.get(0).team_kills);
        String teamDeaths = String.valueOf(matchStatsList.get(0).team_deaths);
        String teamAssists = String.valueOf(matchStatsList.get(0).team_assists);
        String teamKDA = teamKills + "/" + teamDeaths + "/" + teamAssists;
        withFriendsViewHolder.teamKDAView.setText(teamKDA);

        // match duration
        String minutes = String.valueOf((int) Math.floor((matchStatsList.get(0).match_duration) / 60));
        String seconds = String.valueOf(matchStatsList.get(0).match_duration % 60);
        String matchDuration = minutes + "m " + seconds + "s";
        withFriendsViewHolder.matchDurationView.setText(matchDuration);

        // populate the summoner table
        populateSummonerTable(withFriendsViewHolder.summonerTable, matchStatsList);

        // create the entries
        List<BarEntry> entriesDmg = new ArrayList<>();
        List<Entry> entriesKills = new ArrayList<>();
        List<Entry> entriesDeaths = new ArrayList<>();
        List<Entry> entriesAssists = new ArrayList<>();
        List<BarEntry> entriesKDA = new ArrayList<>();
        List<BarEntry> entriesCS = new ArrayList<>();
        List<BarEntry> entriesWardsBought = new ArrayList<>();
        List<BarEntry> entriesWardsPlaced = new ArrayList<>();
        List<BarEntry> entriesWardsKilled = new ArrayList<>();
        long othersKills = matchStatsList.get(0).team_kills;
        long othersDeaths = matchStatsList.get(0).team_deaths;
        long othersAssists = matchStatsList.get(0).team_assists;
        int i = 0;
        for (MatchStats matchStats : matchStatsList) {
            entriesDmg.add(new BarEntry(matchStats.total_damage_dealt_to_champions, i));
            entriesKills.add(new Entry(matchStats.kills, i));
            entriesDeaths.add(new Entry(matchStats.deaths, i));
            entriesAssists.add(new Entry(matchStats.assists, i));
            entriesKDA.add(new BarEntry(matchStats.kda, i));
            entriesCS.add(new BarEntry(matchStats.minions_killed, i));
            entriesWardsBought.add(new BarEntry(matchStats.vision_wards_bought_in_game, i));
            entriesWardsPlaced.add(new BarEntry(matchStats.wards_placed, i));
            entriesWardsKilled.add(new BarEntry(matchStats.wards_killed, i));
            othersKills = othersKills - matchStats.kills;
            othersDeaths = othersDeaths - matchStats.deaths;
            othersAssists = othersAssists - matchStats.assists;
            i++;
        }
        if (notFiveMan) {
            entriesKills.add(new Entry(othersKills, i));
            entriesDeaths.add(new Entry(othersDeaths, i));
            entriesAssists.add(new Entry(othersAssists, i));
        }

        // organize entries by chart type
        List<List<BarEntry>> barEntries = new ArrayList<>();
        barEntries.add(entriesDmg);
        barEntries.add(entriesKDA);
        barEntries.add(entriesCS);
        barEntries.add(entriesWardsBought);
        barEntries.add(entriesWardsPlaced);
        barEntries.add(entriesWardsKilled);
        List<List<Entry>> pieEntries = new ArrayList<>();
        pieEntries.add(entriesKills);
        pieEntries.add(entriesDeaths);
        pieEntries.add(entriesAssists);

        // create the data sets
        int[] colors = StatsUtil.chartColors();
        List<BarDataSet> barDataSets = new ArrayList<>();
        for (List<BarEntry> entries : barEntries) {
            BarDataSet barDataSet = new BarDataSet(entries, "");
            barDataSet.setColors(colors, context);
            barDataSet.setValueTextColor(Color.WHITE);
            barDataSet.setValueTextSize(12);
            barDataSet.setValueFormatter(new IntValueFormat());
            barDataSets.add(barDataSet);
        }
        List<PieDataSet> pieDataSets = new ArrayList<>();
        for (List<Entry> entries : pieEntries) {
            PieDataSet pieDataSet = new PieDataSet(entries, "");
            if (notFiveMan) {
                colors[entries.size() - 1] = R.color.gray;
            }
            pieDataSet.setColors(colors, context);
            pieDataSet.setSelectionShift(0);
            pieDataSet.setValueFormatter(new IntValueFormat());
            pieDataSets.add(pieDataSet);
        }

        // initialize the chart views
        List<BarChart> barCharts = new ArrayList<>();
        barCharts.add(withFriendsViewHolder.dmgChart);
        barCharts.add(withFriendsViewHolder.kdaChart);
        barCharts.add(withFriendsViewHolder.csChart);
        barCharts.add(withFriendsViewHolder.wardsChart);
        List<PieChart> pieCharts = new ArrayList<>();
        pieCharts.add(withFriendsViewHolder.killsChart);
        pieCharts.add(withFriendsViewHolder.deathsChart);
        pieCharts.add(withFriendsViewHolder.assistsChart);

        // populate and format the charts
        i = 0;
        for (BarChart barChart : barCharts) {
            if (i == (barCharts.size() - 1)) {
                ArrayList<IBarDataSet> dataSets = new ArrayList<>();
                dataSets.add(barDataSets.get(i));
                dataSets.add(barDataSets.get(i + 1));
                dataSets.add(barDataSets.get(i + 2));
                barChart.setData(new BarData(names, dataSets));
            } else {
                barChart.setData(new BarData(names, barDataSets.get(i)));
            }
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
            if (i == 1) { // kda chart
                barChart.getAxisLeft().setDrawLabels(false);
                barChart.getAxisLeft().setDrawGridLines(false);
                barChart.getAxisLeft().setDrawAxisLine(false);
            }
            i++;
        }
        i = 0;
        for (PieChart pieChart : pieCharts) {
            pieChart.setData(new PieData(namesPie, pieDataSets.get(i)));
            pieChart.setHoleColor(Color.TRANSPARENT);
            pieChart.getLegend().setTextColor(Color.WHITE);
            pieChart.getData().setValueTextSize(15);
            pieChart.setHoleRadius(35);
            pieChart.setDrawSliceText(false);
            pieChart.setTransparentCircleAlpha(0);
            pieChart.getLegend().setEnabled(false);
            pieChart.setTouchEnabled(false);
            pieChart.setDescription("");
            pieChart.setCenterTextSize(13);
            pieChart.setCenterTextColor(Color.WHITE);
            switch (i) {
                case 0:
                    pieChart.setCenterText(context.getResources().getString(R.string.gwf_kills));
                    break;
                case 1:
                    pieChart.setCenterText(context.getResources().getString(R.string.gwf_deaths));
                    break;
                case 2:
                    pieChart.setCenterText(context.getResources().getString(R.string.gwf_assists));
                    break;
            }
            i++;
        }
    }

    @Override
    public withFriendsViewHolder onCreateViewHolder(ViewGroup vG, int i) {
        Context c = vG.getContext();
        return new withFriendsViewHolder(LayoutInflater.from(c).inflate(R.layout.view_with_friends, vG, false));
    }

    private void populateSummonerTable(GridLayout summonerTable, List<MatchStats> matchStatsList) {
        int cWidth = ScreenUtil.dpToPx(context, 65);
        int cHeight = ScreenUtil.dpToPx(context, 65);
        int width = ScreenUtil.dpToPx(context, 30);
        int height = ScreenUtil.dpToPx(context, 30);
        int padding = ScreenUtil.dpToPx(context, 1);
        int x = 0;
        for (MatchStats matchStats : matchStatsList) {
            LinearLayout summonerLayout = new LinearLayout(context);
            summonerLayout.setOrientation(LinearLayout.HORIZONTAL);
            summonerLayout.setPadding(padding, padding, padding, padding);
            summonerLayout.setGravity(Gravity.CENTER);
            summonerLayout.setBackgroundColor(ContextCompat.getColor(context, StatsUtil.intToColor(x)));

            // champion icon
            ImageView championIconView = new ImageView(context);
            championIconView.setPadding(padding, padding, padding, padding);
            String key = StatsUtil.championKey(matchStats.champion, null);
            String url = StatsUtil.championIconURL(staticRiotData.version, key);
            Picasso.with(context).load(url).resize(cWidth, cHeight).into(championIconView);
            summonerLayout.addView(championIconView);

            // summoner spells
            LinearLayout summonerSpellLayout = new LinearLayout(context);
            summonerSpellLayout.setOrientation(LinearLayout.VERTICAL);
            ImageView summonerSpell1View = new ImageView(context);
            ImageView summonerSpell2View = new ImageView(context);
            summonerSpell1View.setPadding(padding, padding, padding, padding);
            summonerSpell2View.setPadding(padding, padding, padding, padding);
            String spell1URL = StatsUtil.summonerSpellURL(staticRiotData.version, matchStats.spell1);
            String spell2URL = StatsUtil.summonerSpellURL(staticRiotData.version, matchStats.spell2);
            Picasso.with(context).load(spell1URL).resize(width, height).into(summonerSpell1View);
            Picasso.with(context).load(spell2URL).resize(width, height).into(summonerSpell2View);
            summonerSpellLayout.addView(summonerSpell1View);
            summonerSpellLayout.addView(summonerSpell2View);
            summonerLayout.addView(summonerSpellLayout);

            // keystone mastery and trinket
            LinearLayout keystoneTrinketLayout = new LinearLayout(context);
            keystoneTrinketLayout.setOrientation(LinearLayout.VERTICAL);
            ImageView keystoneView = new ImageView(context);
            keystoneView.setPadding(padding, padding, padding, padding);
            String keystoneURL = StatsUtil.masteryURL(staticRiotData.version, matchStats.keystone);
            Picasso.with(context).load(keystoneURL).resize(width, height).into(keystoneView);
            keystoneTrinketLayout.addView(keystoneView);
            ImageView trinketView = new ImageView(context);
            trinketView.setPadding(padding, padding, padding, padding);
            String trinketURL = StatsUtil.itemURL(staticRiotData.version, matchStats.item6);
            Picasso.with(context).load(trinketURL).resize(width, height).into(trinketView);
            keystoneTrinketLayout.addView(trinketView);
            summonerLayout.addView(keystoneTrinketLayout);

            // items
            GridLayout itemLayout = new GridLayout(context);
            itemLayout.setColumnCount(3);
            itemLayout.setRowCount(2);
            for (int i = 0; i < 6; i++) {
                String itemURL;
                switch (i) {
                    case 0:
                        itemURL = StatsUtil.itemURL(staticRiotData.version, matchStats.item0);
                        break;
                    case 1:
                        itemURL = StatsUtil.itemURL(staticRiotData.version, matchStats.item1);
                        break;
                    case 2:
                        itemURL = StatsUtil.itemURL(staticRiotData.version, matchStats.item2);
                        break;
                    case 3:
                        itemURL = StatsUtil.itemURL(staticRiotData.version, matchStats.item3);
                        break;
                    case 4:
                        itemURL = StatsUtil.itemURL(staticRiotData.version, matchStats.item4);
                        break;
                    case 5:
                        itemURL = StatsUtil.itemURL(staticRiotData.version, matchStats.item5);
                        break;
                    default:
                        itemURL = StatsUtil.itemURL(staticRiotData.version, matchStats.item0);
                        break;
                }
                ImageView itemView = new ImageView(context);
                itemView.setPadding(padding, padding, padding, padding);
                if (itemURL.equals(Constants.UI_NO_ITEM)) {
                    Picasso.with(context).load(R.drawable.ic_no_item).resize(width, height).into(itemView);
                } else {
                    Picasso.with(context).load(itemURL).resize(width, height).into(itemView);
                }
                itemLayout.addView(itemView);
            }
            summonerLayout.addView(itemLayout);

            // add the summoner to the table
            summonerTable.addView(summonerLayout);
            x++;
        }
    }

    public class withFriendsViewHolder extends RecyclerView.ViewHolder {

        final PieChart assistsChart;
        final BarChart csChart;
        final PieChart deathsChart;
        final TextView defeatView;
        final BarChart dmgChart;
        final BarChart kdaChart;
        final PieChart killsChart;
        final TextView matchDurationView;
        final GridLayout summonerTable;
        final TextView teamKDAView;
        final TextView victoryView;
        final BarChart wardsChart;

        withFriendsViewHolder(View itemView) {
            super(itemView);
            victoryView = (TextView) itemView.findViewById(R.id.victory_view);
            victoryView.setVisibility(View.GONE);
            defeatView = (TextView) itemView.findViewById(R.id.defeat_view);
            defeatView.setVisibility(View.GONE);
            teamKDAView = (TextView) itemView.findViewById(R.id.team_kda_view);
            matchDurationView = (TextView) itemView.findViewById(R.id.match_duration_view);
            summonerTable = (GridLayout) itemView.findViewById(R.id.summoner_table_layout);
            dmgChart = (BarChart) itemView.findViewById(R.id.dmg_chart);
            killsChart = (PieChart) itemView.findViewById(R.id.kills_chart);
            deathsChart = (PieChart) itemView.findViewById(R.id.deaths_chart);
            assistsChart = (PieChart) itemView.findViewById(R.id.assists_chart);
            kdaChart = (BarChart) itemView.findViewById(R.id.kda_chart);
            csChart = (BarChart) itemView.findViewById(R.id.cs_chart);
            wardsChart = (BarChart) itemView.findViewById(R.id.wards_chart);
        }
    }

}
