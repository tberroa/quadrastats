package com.example.tberroa.portal.screens.stats.withfriends;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tberroa.portal.R;
import com.example.tberroa.portal.models.stats.MatchStats;
import com.example.tberroa.portal.screens.ScreenUtil;
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
            Bundle bundle = this.getArguments();
            String matchStatsMapJson = bundle.getString("match_stats_map");
            Type matchStatsMapType = new TypeToken<Map<String, MatchStats>>() {
            }.getType();
            Map<String, MatchStats> matchStatsMap = new Gson().fromJson(matchStatsMapJson, matchStatsMapType);

            if (matchStatsMap != null) {
                // separate the data into a list of names and a list match stats
                List<String> names = new ArrayList<>(matchStatsMap.keySet());
                List<MatchStats> matchStatsList = new ArrayList<>(matchStatsMap.values());

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
                int[] colors = ScreenUtil.getChartColors();
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
                barCharts.add((BarChart) v.findViewById(R.id.wf_dmg_chart));
                List<PieChart> pieCharts = new ArrayList<>();
                pieCharts.add((PieChart) v.findViewById(R.id.wf_kills_chart));

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

    private void createLegend(final List<String> names, View v) {
        // set unused elements to gone
        ImageView positionIcon = (ImageView) v.findViewById(R.id.pos_icon);
        positionIcon.setVisibility(View.GONE);
        ImageView championIcon = (ImageView) v.findViewById(R.id.champ_icon);
        championIcon.setVisibility(View.GONE);

        // set names
        GridLayout legendNames = (GridLayout) v.findViewById(R.id.legend_names);
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
