package com.example.tberroa.portal.screens.stats.withfriends;

import android.content.Context;
import android.graphics.Color;
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
import com.example.tberroa.portal.models.stats.MatchStats;
import com.example.tberroa.portal.screens.ScreenUtil;
import com.example.tberroa.portal.screens.stats.StatsUtil;
import com.github.mikephil.charting.charts.BarChart;
import com.example.tberroa.portal.screens.stats.withfriends.WithFriendsViewAdapter.withFriendsViewHolder;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WithFriendsViewAdapter extends RecyclerView.Adapter<withFriendsViewHolder> {

    private final Context context;
    private final Map<String, MatchStats> matchStatsMap;

    public WithFriendsViewAdapter(Context context, Map<String, MatchStats> matchStatsMap) {
        this.context = context;
        this.matchStatsMap = matchStatsMap;
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    @Override
    public void onBindViewHolder(withFriendsViewHolder withFriendsViewHolder, int position) {
        // separate the data into a list of names and a list match stats
        List<String> names = new ArrayList<>(matchStatsMap.keySet());
        List<MatchStats> matchStatsList = new ArrayList<>(matchStatsMap.values());

        // display appropriate victory/defeat text
        if (matchStatsList.get(0).winner) {
            withFriendsViewHolder.defeatView.setVisibility(View.GONE);
        } else {
            withFriendsViewHolder.victoryView.setVisibility(View.GONE);
        }

        // populate the summoner table
        GridLayout summonerTable = withFriendsViewHolder.summonerTable;
        int cWidth = ScreenUtil.dpToPx(context, 65);
        int cHeight = ScreenUtil.dpToPx(context, 65);
        int width = ScreenUtil.dpToPx(context, 30);
        int height = ScreenUtil.dpToPx(context, 30);
        int padding = ScreenUtil.dpToPx(context, 2);
        for (MatchStats matchStats : matchStatsList) {
            LinearLayout summonerLayout = new LinearLayout(context);
            summonerLayout.setOrientation(LinearLayout.HORIZONTAL);
            summonerLayout.setPadding(padding, padding, padding, padding);
            summonerLayout.setGravity(Gravity.CENTER);

            // champion icon
            ImageView championIconView = new ImageView(context);
            championIconView.setPadding(padding, padding, padding, padding);
            int championIcon = ScreenUtil.championIcon(StatsUtil.championName(matchStats.champion));
            Picasso.with(context).load(championIcon).resize(cWidth, cHeight).into(championIconView);
            summonerLayout.addView(championIconView);

            // summoner spells
            LinearLayout summonerSpellLayout = new LinearLayout(context);
            summonerSpellLayout.setOrientation(LinearLayout.VERTICAL);
            ImageView summonerSpell1View = new ImageView(context);
            ImageView summonerSpell2View = new ImageView(context);
            summonerSpell1View.setPadding(padding, padding, padding, padding);
            summonerSpell2View.setPadding(padding, padding, padding, padding);
            String spell1URL = ScreenUtil.constructSummonerSpellURL(matchStats.spell1);
            String spell2URL = ScreenUtil.constructSummonerSpellURL(matchStats.spell2);
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
            String keystoneURL = ScreenUtil.constructMasteryURL(matchStats.keystone);
            Picasso.with(context).load(keystoneURL).resize(width, height).into(keystoneView);
            keystoneTrinketLayout.addView(keystoneView);
            ImageView trinketView = new ImageView(context);
            trinketView.setPadding(padding, padding, padding, padding);
            String trinketURL = ScreenUtil.constructItemURL(matchStats.item6);
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
                ImageView itemView = new ImageView(context);
                itemView.setPadding(padding, padding, padding, padding);
                Picasso.with(context).load(itemURL).resize(width, height).into(itemView);
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
            barDataSet.setColors(colors, context);
            barDataSet.setValueTextColor(Color.WHITE);
            barDataSet.setValueTextSize(12);
            barDataSets.add(barDataSet);
        }
        List<PieDataSet> pieDataSets = new ArrayList<>();
        for (List<Entry> entries : pieEntries) {
            PieDataSet pieDataSet = new PieDataSet(entries, "");
            pieDataSet.setColors(colors, context);
            pieDataSet.setSelectionShift(0);
            pieDataSets.add(pieDataSet);
        }

        // initialize the chart views
        List<BarChart> barCharts = new ArrayList<>();
        barCharts.add(withFriendsViewHolder.dmgChart);
        List<PieChart> pieCharts = new ArrayList<>();
        pieCharts.add(withFriendsViewHolder.killsChart);

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
        
    }

    @Override
    public withFriendsViewHolder onCreateViewHolder(ViewGroup vG, int i) {
        Context c = vG.getContext();
        return new withFriendsViewHolder(LayoutInflater.from(c).inflate(R.layout.view_with_friends, vG, false));
    }

    public class withFriendsViewHolder extends RecyclerView.ViewHolder {

        final TextView victoryView;
        final TextView defeatView;
        final GridLayout summonerTable;
        final BarChart dmgChart;
        final PieChart killsChart;

        withFriendsViewHolder(View itemView) {
            super(itemView);
            victoryView = (TextView) itemView.findViewById(R.id.victory_view);
            victoryView.setVisibility(View.GONE);
            defeatView = (TextView) itemView.findViewById(R.id.defeat_view);
            defeatView.setVisibility(View.GONE);
            summonerTable = (GridLayout) itemView.findViewById(R.id.summoner_table_layout);
            dmgChart = (BarChart) itemView.findViewById(R.id.dmg_chart);
            killsChart = (PieChart) itemView.findViewById(R.id.kills_chart);
        }
    }

}
