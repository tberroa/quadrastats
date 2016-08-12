package com.example.tberroa.portal.screens.stats.season;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tberroa.portal.R;
import com.example.tberroa.portal.models.stats.SeasonStats;
import com.example.tberroa.portal.screens.ScreenUtil;
import com.example.tberroa.portal.screens.stats.IntValueFormat;
import com.example.tberroa.portal.screens.stats.season.SeasonViewAdapter.ChartViewHolder;
import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SeasonViewAdapter extends RecyclerView.Adapter<ChartViewHolder> {

    private final long champion;
    private final Context context;
    private final boolean perGame;
    private final Map<String, Map<Long, SeasonStats>> seasonStatsMapMap;

    public SeasonViewAdapter(Context context, ViewPackage viewPackage) {
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
        for (RadarChart chart : chartViewHolder.charts) {
            chart.getLayoutParams().height = (55 * ScreenUtil.screenHeight(context)) / 100;
            chart.setLayoutParams(chart.getLayoutParams());
        }

        // initialize list to hold labels
        List<String> labels = new ArrayList<>();

        // initialize entries
        List<List<Entry>> entriesList = new ArrayList<>();
        List<Entry> killsEntries = new ArrayList<>();
        List<Entry> deathsEntries = new ArrayList<>();
        List<Entry> assistsEntries = new ArrayList<>();
        List<Entry> kdaEntries = new ArrayList<>();
        List<Entry> doublesEntries = new ArrayList<>();
        List<Entry> triplesEntries = new ArrayList<>();
        List<Entry> quadrasEntries = new ArrayList<>();
        List<Entry> pentasEntries = new ArrayList<>();

        // iterate over the season stats map to create entries and labels
        int j = 0;
        for (Map.Entry<String, Map<Long, SeasonStats>> summonerMap : seasonStatsMapMap.entrySet()) {
            // get stats
            SeasonStats seasonStats = summonerMap.getValue().get(champion);

            // calculate kda
            float kda = (seasonStats.kills + seasonStats.assists) / (float) seasonStats.deaths;

            // populate entries
            if (perGame) {
                killsEntries.add(new Entry(seasonStats.kills / seasonStats.games, j));
                deathsEntries.add(new Entry(seasonStats.deaths / seasonStats.games, j));
                assistsEntries.add(new Entry(seasonStats.assists / seasonStats.games, j));
                kdaEntries.add(new Entry(kda, j));
                doublesEntries.add(new Entry(seasonStats.double_kills / seasonStats.games, j));
                triplesEntries.add(new Entry(seasonStats.triple_kills / seasonStats.games, j));
                quadrasEntries.add(new Entry(seasonStats.quadra_kills / seasonStats.games, j));
                pentasEntries.add(new Entry(seasonStats.penta_kills / seasonStats.games, j));
            } else {
                killsEntries.add(new Entry(seasonStats.kills, j));
                deathsEntries.add(new Entry(seasonStats.deaths, j));
                assistsEntries.add(new Entry(seasonStats.assists, j));
                kdaEntries.add(new Entry(kda, j));
                doublesEntries.add(new Entry(seasonStats.double_kills, j));
                triplesEntries.add(new Entry(seasonStats.triple_kills, j));
                quadrasEntries.add(new Entry(seasonStats.quadra_kills, j));
                pentasEntries.add(new Entry(seasonStats.penta_kills, j));
            }


            // populate labels
            labels.add(String.valueOf(j));

            j++;
        }
        entriesList.add(killsEntries);
        entriesList.add(deathsEntries);
        entriesList.add(assistsEntries);
        entriesList.add(kdaEntries);
        entriesList.add(doublesEntries);
        entriesList.add(triplesEntries);
        entriesList.add(quadrasEntries);
        entriesList.add(pentasEntries);

        // create data sets
        j = 0;
        List<RadarDataSet> dataSets = new ArrayList<>();
        float rawTextSize = context.getResources().getDimension(R.dimen.text_size_small);
        int textSize = (int) (rawTextSize / context.getResources().getDisplayMetrics().density);
        for (List<Entry> entries : entriesList) {
            RadarDataSet dataSet = new RadarDataSet(entries, "");
            dataSet.setDrawFilled(true);
            dataSet.setFillColor(ContextCompat.getColor(context, R.color.accent));
            dataSet.setValueFormatter(new IntValueFormat());
            dataSet.setValueTextColor(Color.WHITE);
            dataSet.setValueTextSize(textSize);
            dataSet.setColor(ContextCompat.getColor(context, R.color.accent));
            dataSets.add(dataSet);

            j++;
        }

        // populate and format charts
        j = 0;
        float rawLabelTextSize = context.getResources().getDimension(R.dimen.text_size_large);
        int labelTextSize = (int) (rawLabelTextSize / context.getResources().getDisplayMetrics().density);
        for (RadarChart chart : chartViewHolder.charts) {
            chart.setData(new RadarData(labels, dataSets.get(j)));
            chart.setWebColor(Color.WHITE);
            chart.setWebColorInner(Color.WHITE);
            chart.getXAxis().setTextColor(Color.WHITE);
            chart.getXAxis().setTextSize(labelTextSize);
            chart.getYAxis().setEnabled(false);
            chart.getLegend().setEnabled(false);
            chart.setTouchEnabled(false);
            chart.setDescription("");

            j++;
        }
    }

    @Override
    public ChartViewHolder onCreateViewHolder(ViewGroup vG, int i) {
        return new ChartViewHolder(LayoutInflater.from(vG.getContext()).inflate(R.layout.view_season, vG, false));
    }

    public class ChartViewHolder extends RecyclerView.ViewHolder {

        final List<RadarChart> charts;

        ChartViewHolder(View itemView) {
            super(itemView);
            // initialize charts
            charts = new ArrayList<>();
            charts.add((RadarChart) itemView.findViewById(R.id.kills_chart));
            charts.add((RadarChart) itemView.findViewById(R.id.deaths_chart));
            charts.add((RadarChart) itemView.findViewById(R.id.assists_chart));
            charts.add((RadarChart) itemView.findViewById(R.id.kda_chart));
            charts.add((RadarChart) itemView.findViewById(R.id.doubles_chart));
            charts.add((RadarChart) itemView.findViewById(R.id.triples_chart));
            charts.add((RadarChart) itemView.findViewById(R.id.quadras_chart));
            charts.add((RadarChart) itemView.findViewById(R.id.pentas_chart));
        }
    }
}
