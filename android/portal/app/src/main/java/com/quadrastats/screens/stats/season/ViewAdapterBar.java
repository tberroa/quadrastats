package com.quadrastats.screens.stats.season;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.quadrastats.R;
import com.quadrastats.models.stats.SeasonStats;
import com.quadrastats.screens.ScreenUtil;
import com.quadrastats.screens.stats.IntValueFormat;
import com.quadrastats.screens.stats.StatsUtil;
import com.quadrastats.screens.stats.season.ViewAdapterBar.ChartViewHolder;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ViewAdapterBar extends RecyclerView.Adapter<ChartViewHolder> {

    private final long champion;
    private final Context context;
    private final boolean perGame;
    private final Map<String, Map<Long, SeasonStats>> seasonStatsMapMap;

    public ViewAdapterBar(Context context, ViewPackage viewPackage) {
        this.context = context;
        seasonStatsMapMap = viewPackage.seasonStatsMapMap;
        champion = viewPackage.champion;
        perGame = viewPackage.perGame;
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    @Override
    public void onBindViewHolder(ChartViewHolder chartViewHolder, int i) {
        // resize charts
        for (BarChart chart : chartViewHolder.charts) {
            chart.getLayoutParams().height = (55 * ScreenUtil.screenHeight(context)) / 100;
            chart.setLayoutParams(chart.getLayoutParams());
        }

        // modify chart titles if necessary
        if (perGame) {
            for (TextView titleView : chartViewHolder.titleViews) {
                String currentTitle = titleView.getText().toString();
                String newTitle = currentTitle + " " + context.getString(R.string.st_per_game);
                titleView.setText(newTitle);
            }
        }

        // initialize list to hold labels
        List<String> labels = new ArrayList<>();

        // initialize entries
        List<List<BarEntry>> entriesList = new ArrayList<>();
        List<BarEntry> gamesEntries = new ArrayList<>();
        List<BarEntry> killsEntries = new ArrayList<>();
        List<BarEntry> deathsEntries = new ArrayList<>();
        List<BarEntry> assistsEntries = new ArrayList<>();
        List<BarEntry> kdaEntries = new ArrayList<>();
        List<BarEntry> dmgEntries = new ArrayList<>();
        List<BarEntry> goldEntries = new ArrayList<>();
        List<BarEntry> csEntries = new ArrayList<>();
        List<BarEntry> maxKillsEntries = new ArrayList<>();
        List<BarEntry> maxDeathsEntries = new ArrayList<>();
        List<BarEntry> doublesEntries = new ArrayList<>();
        List<BarEntry> triplesEntries = new ArrayList<>();
        List<BarEntry> quadrasEntries = new ArrayList<>();
        List<BarEntry> pentasEntries = new ArrayList<>();

        // iterate over the season stats map to create entries and labels
        int j = 0;
        for (Map.Entry<String, Map<Long, SeasonStats>> summonerMap : seasonStatsMapMap.entrySet()) {
            // get stats
            SeasonStats seasonStats = summonerMap.getValue().get(champion);

            // calculate kda
            Float kda = null;
            if ((seasonStats.kills != null) && (seasonStats.deaths != null) && (seasonStats.assists != null)) {
                if (seasonStats.deaths != 0) {
                    kda = (seasonStats.kills + seasonStats.assists) / (float) seasonStats.deaths;
                } else {
                    kda = (float) (seasonStats.kills + seasonStats.assists);
                }
            }

            // calculate cs
            Integer cs = null;
            if ((seasonStats.minion_kills != null) && (seasonStats.neutral_minion_kills != null)) {
                cs = seasonStats.minion_kills + seasonStats.neutral_minion_kills;
            }

            // populate non per game entries
            if (seasonStats.games != null) {
                gamesEntries.add(new BarEntry(seasonStats.games, j));
            }
            if (kda != null) {
                kdaEntries.add(new BarEntry(kda, j));
            }
            if (seasonStats.max_kills != null) {
                maxKillsEntries.add(new BarEntry(seasonStats.max_kills, j));
            }
            if (seasonStats.max_deaths != null) {
                maxDeathsEntries.add(new BarEntry(seasonStats.max_deaths, j));
            }
            if (seasonStats.double_kills != null) {
                doublesEntries.add(new BarEntry(seasonStats.double_kills, j));
            }
            if (seasonStats.triple_kills != null) {
                triplesEntries.add(new BarEntry(seasonStats.triple_kills, j));
            }
            if (seasonStats.quadra_kills != null) {
                quadrasEntries.add(new BarEntry(seasonStats.quadra_kills, j));
            }
            if (seasonStats.penta_kills != null) {
                pentasEntries.add(new BarEntry(seasonStats.penta_kills, j));
            }

            // populate per game entries
            if (perGame) {
                if ((seasonStats.kills != null) && (seasonStats.games != null)) {
                    killsEntries.add(new BarEntry(seasonStats.kills / seasonStats.games, j));
                }
                if ((seasonStats.deaths != null) && (seasonStats.games != null)) {
                    deathsEntries.add(new BarEntry(seasonStats.deaths / seasonStats.games, j));
                }
                if ((seasonStats.assists != null) && (seasonStats.games != null)) {
                    assistsEntries.add(new BarEntry(seasonStats.assists / seasonStats.games, j));
                }
                if ((seasonStats.damage_dealt != null) && (seasonStats.games != null)) {
                    dmgEntries.add(new BarEntry(seasonStats.damage_dealt / seasonStats.games, j));
                }
                if ((seasonStats.gold_earned != null) && (seasonStats.games != null)) {
                    goldEntries.add(new BarEntry(seasonStats.gold_earned / seasonStats.games, j));
                }
                if ((cs != null) && (seasonStats.games != null)) {
                    csEntries.add(new BarEntry(cs / seasonStats.games, j));
                }
            } else {
                if (seasonStats.kills != null) {
                    killsEntries.add(new BarEntry(seasonStats.kills, j));
                }
                if (seasonStats.deaths != null) {
                    deathsEntries.add(new BarEntry(seasonStats.deaths, j));
                }
                if (seasonStats.assists != null) {
                    assistsEntries.add(new BarEntry(seasonStats.assists, j));
                }
                if (seasonStats.damage_dealt != null) {
                    dmgEntries.add(new BarEntry(seasonStats.damage_dealt, j));
                }
                if (seasonStats.gold_earned != null) {
                    goldEntries.add(new BarEntry(seasonStats.gold_earned, j));
                }
                if (cs != null) {
                    csEntries.add(new BarEntry(cs, j));
                }
            }

            // populate labels
            labels.add(String.valueOf(j));

            j++;
        }
        entriesList.add(gamesEntries);
        entriesList.add(killsEntries);
        entriesList.add(deathsEntries);
        entriesList.add(assistsEntries);
        entriesList.add(kdaEntries);
        entriesList.add(dmgEntries);
        entriesList.add(goldEntries);
        entriesList.add(csEntries);
        entriesList.add(maxKillsEntries);
        entriesList.add(maxDeathsEntries);
        entriesList.add(doublesEntries);
        entriesList.add(triplesEntries);
        entriesList.add(quadrasEntries);
        entriesList.add(pentasEntries);

        // create data sets
        j = 0;
        List<BarDataSet> dataSets = new ArrayList<>();
        float rawTextSize = context.getResources().getDimension(R.dimen.text_size_small);
        int textSize = (int) (rawTextSize / context.getResources().getDisplayMetrics().density);
        for (List<BarEntry> entries : entriesList) {
            BarDataSet dataSet = new BarDataSet(entries, "");
            int[] colors = StatsUtil.chartColors();
            dataSet.setColors(colors, context);
            dataSet.setValueFormatter(new IntValueFormat());
            dataSet.setValueTextColor(Color.WHITE);
            dataSet.setValueTextSize(textSize);
            dataSet.setHighlightEnabled(false);
            dataSets.add(dataSet);
            j++;
        }

        // populate and format charts
        j = 0;
        for (BarChart chart : chartViewHolder.charts) {
            if (dataSets.get(j).getEntryCount() == 0) {
                chartViewHolder.chartLayouts.get(j).setVisibility(View.GONE);
            } else {
                chart.setData(new BarData(labels, dataSets.get(j)));
                chart.getXAxis().setDrawLabels(false);
                chart.getXAxis().setDrawGridLines(false);
                chart.getXAxis().setDrawAxisLine(false);
                chart.getAxisLeft().setTextColor(Color.WHITE);
                chart.getAxisLeft().setDrawAxisLine(false);
                chart.getAxisRight().setDrawLabels(false);
                chart.getAxisRight().setDrawGridLines(false);
                chart.getAxisRight().setDrawAxisLine(false);
                chart.getLegend().setEnabled(false);
                chart.setDrawBorders(false);
                chart.setTouchEnabled(false);
                chart.setDescription("");
            }

            j++;
        }
    }

    @Override
    public ChartViewHolder onCreateViewHolder(ViewGroup vG, int i) {
        return new ChartViewHolder(LayoutInflater.from(vG.getContext()).inflate(R.layout.view_season_bar, vG, false));
    }

    class ChartViewHolder extends RecyclerView.ViewHolder {

        final List<View> chartLayouts;
        final List<BarChart> charts;
        final List<TextView> titleViews;

        ChartViewHolder(View itemView) {
            super(itemView);
            // initialize chart layouts
            chartLayouts = new ArrayList<>();
            chartLayouts.add(itemView.findViewById(R.id.games_chart_layout));
            chartLayouts.add(itemView.findViewById(R.id.kills_chart_layout));
            chartLayouts.add(itemView.findViewById(R.id.deaths_chart_layout));
            chartLayouts.add(itemView.findViewById(R.id.assists_chart_layout));
            chartLayouts.add(itemView.findViewById(R.id.kda_chart_layout));
            chartLayouts.add(itemView.findViewById(R.id.damage_chart_layout));
            chartLayouts.add(itemView.findViewById(R.id.gold_chart_layout));
            chartLayouts.add(itemView.findViewById(R.id.cs_chart_layout));
            chartLayouts.add(itemView.findViewById(R.id.max_kills_chart_layout));
            chartLayouts.add(itemView.findViewById(R.id.max_deaths_chart_layout));
            chartLayouts.add(itemView.findViewById(R.id.doubles_chart_layout));
            chartLayouts.add(itemView.findViewById(R.id.triples_chart_layout));
            chartLayouts.add(itemView.findViewById(R.id.quadras_chart_layout));
            chartLayouts.add(itemView.findViewById(R.id.pentas_chart_layout));

            // initialize charts
            charts = new ArrayList<>();
            charts.add((BarChart) itemView.findViewById(R.id.games_chart));
            charts.add((BarChart) itemView.findViewById(R.id.kills_chart));
            charts.add((BarChart) itemView.findViewById(R.id.deaths_chart));
            charts.add((BarChart) itemView.findViewById(R.id.assists_chart));
            charts.add((BarChart) itemView.findViewById(R.id.kda_chart));
            charts.add((BarChart) itemView.findViewById(R.id.damage_chart));
            charts.add((BarChart) itemView.findViewById(R.id.gold_chart));
            charts.add((BarChart) itemView.findViewById(R.id.cs_chart));
            charts.add((BarChart) itemView.findViewById(R.id.max_kills_chart));
            charts.add((BarChart) itemView.findViewById(R.id.max_deaths_chart));
            charts.add((BarChart) itemView.findViewById(R.id.doubles_chart));
            charts.add((BarChart) itemView.findViewById(R.id.triples_chart));
            charts.add((BarChart) itemView.findViewById(R.id.quadras_chart));
            charts.add((BarChart) itemView.findViewById(R.id.pentas_chart));

            // initialize titles
            titleViews = new ArrayList<>();
            titleViews.add((TextView) itemView.findViewById(R.id.kills_chart_title));
            titleViews.add((TextView) itemView.findViewById(R.id.deaths_chart_title));
            titleViews.add((TextView) itemView.findViewById(R.id.assists_chart_title));
            titleViews.add((TextView) itemView.findViewById(R.id.damage_chart_title));
            titleViews.add((TextView) itemView.findViewById(R.id.gold_chart_title));
            titleViews.add((TextView) itemView.findViewById(R.id.cs_chart_title));
        }
    }
}
