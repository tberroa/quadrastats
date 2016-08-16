package com.example.tberroa.portal.screens.stats.withfriends;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.tberroa.portal.R;
import com.example.tberroa.portal.data.Constants;
import com.example.tberroa.portal.models.stats.MatchStats;
import com.example.tberroa.portal.screens.RoundTransform;
import com.example.tberroa.portal.screens.ScreenUtil;
import com.example.tberroa.portal.screens.StaticRiotData;
import com.example.tberroa.portal.screens.stats.CreateLegendPackage;
import com.example.tberroa.portal.screens.stats.IntValueFormat;
import com.example.tberroa.portal.screens.stats.StatsUtil;
import com.example.tberroa.portal.screens.stats.withfriends.ViewAdapter.WFViewHolder;
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
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ViewAdapter extends RecyclerView.Adapter<WFViewHolder> {

    private final Context context;
    private final Map<String, MatchStats> matchStatsMap;
    private final StaticRiotData staticRiotData;
    private List<String> champIconURLsList;
    private List<List<String>> iconURLsList;

    public ViewAdapter(Context context, Map<String, MatchStats> matchStatsMap, StaticRiotData staticRiotData) {
        this.context = context;
        this.matchStatsMap = matchStatsMap;
        this.staticRiotData = staticRiotData;
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    @Override
    public void onBindViewHolder(WFViewHolder wfViewHolder, int position) {
        // separate the data into a list of names and a list match stats
        List<String> names = new ArrayList<>(matchStatsMap.keySet());
        List<MatchStats> matchStatsList = new ArrayList<>(matchStatsMap.values());

        // set summoner names
        int i = 0;
        for (String name : names) {
            TextView nameView = (TextView) wfViewHolder.summonerLayouts.get(i).findViewById(R.id.summoner_name_view);
            nameView.setText(name);
            i++;
        }

        // set tab buttons
        wfViewHolder.incomeTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ChartsDialog(names, matchStatsList, 0).show();
            }
        });
        wfViewHolder.offenseTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ChartsDialog(names, matchStatsList, 1).show();
            }
        });
        wfViewHolder.utilityTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ChartsDialog(names, matchStatsList, 2).show();
            }
        });
        wfViewHolder.visionTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ChartsDialog(names, matchStatsList, 3).show();
            }
        });

        // display appropriate victory/defeat text
        Boolean winner = matchStatsList.get(0).winner;
        if (winner != null) {
            if (winner) {
                wfViewHolder.victoryView.setVisibility(View.VISIBLE);
            } else {
                wfViewHolder.defeatView.setVisibility(View.VISIBLE);
            }
        }

        // match date
        Date now = new Date();
        long matchDate = matchStatsList.get(0).match_creation;
        long minutesAgo = TimeUnit.MILLISECONDS.toMinutes(now.getTime() - matchDate);
        long hoursAgo = TimeUnit.MILLISECONDS.toHours(now.getTime() - matchDate);
        long daysAgo = TimeUnit.MILLISECONDS.toDays(now.getTime() - matchDate);
        String timeAgo;
        if (daysAgo == 0) {
            if (hoursAgo == 0) {
                if (minutesAgo == 1) {
                    timeAgo = minutesAgo + " " + context.getString(R.string.gwf_minute_ago);
                } else {
                    timeAgo = minutesAgo + " " + context.getString(R.string.gwf_minutes_ago);
                }
            } else {
                if (hoursAgo == 1) {
                    timeAgo = hoursAgo + " " + context.getString(R.string.gwf_hour_ago);
                } else {
                    timeAgo = hoursAgo + " " + context.getString(R.string.gwf_hours_ago);
                }
            }
        } else {
            if (daysAgo == 1) {
                timeAgo = daysAgo + " " + context.getString(R.string.gwf_day_ago);
            } else {
                timeAgo = daysAgo + " " + context.getString(R.string.gwf_days_ago);
            }
        }
        wfViewHolder.matchDateView.setText(timeAgo);


        // team kda
        String teamKills = String.valueOf(matchStatsList.get(0).team_kills);
        String teamDeaths = String.valueOf(matchStatsList.get(0).team_deaths);
        String teamAssists = String.valueOf(matchStatsList.get(0).team_assists);
        String teamKDA = teamKills + "/" + teamDeaths + "/" + teamAssists;
        wfViewHolder.teamKDAView.setText(teamKDA);

        // match duration
        String minutes = String.valueOf((int) Math.floor((matchStatsList.get(0).match_duration) / 60));
        String seconds = String.valueOf(matchStatsList.get(0).match_duration % 60);
        String matchDuration = minutes + "m " + seconds + "s";
        wfViewHolder.matchDurationView.setText(matchDuration);

        // populate the summoner table
        populateSummonerTable(wfViewHolder.summonerLayouts, matchStatsList);
    }

    @Override
    public WFViewHolder onCreateViewHolder(ViewGroup vG, int i) {
        Context context = vG.getContext();
        return new WFViewHolder(LayoutInflater.from(context).inflate(R.layout.view_with_friends, vG, false));
    }

    private void formatBarChart(BarChart barChart) {
        barChart.getXAxis().setDrawLabels(false);
        barChart.getXAxis().setDrawGridLines(false);
        barChart.getXAxis().setDrawAxisLine(false);
        barChart.getAxisLeft().setTextColor(Color.WHITE);
        barChart.getAxisLeft().setDrawAxisLine(false);
        barChart.getAxisRight().setDrawLabels(false);
        barChart.getAxisRight().setDrawGridLines(false);
        barChart.getAxisRight().setDrawAxisLine(false);
        barChart.getLegend().setEnabled(false);
        barChart.setDrawBorders(false);
        barChart.setTouchEnabled(false);
        barChart.setDescription("");
    }

    private void formatBarDataSet(BarDataSet barDataSet) {
        float rawTextSize = context.getResources().getDimension(R.dimen.text_size_small);
        int textSize = (int) (rawTextSize / context.getResources().getDisplayMetrics().density);
        int[] colors = StatsUtil.chartColors();
        barDataSet.setColors(colors, context);
        barDataSet.setValueFormatter(new IntValueFormat());
        barDataSet.setValueTextColor(Color.WHITE);
        barDataSet.setValueTextSize(textSize);
        barDataSet.setHighlightEnabled(false);
    }

    private void formatPieChart(PieChart pieChart) {
        float rawTextSize = context.getResources().getDimension(R.dimen.text_size_huge);
        int textSize = (int) (rawTextSize / context.getResources().getDisplayMetrics().density);
        pieChart.getLegend().setEnabled(false);
        pieChart.setDrawSliceText(false);
        pieChart.setTouchEnabled(false);
        pieChart.setTransparentCircleAlpha(0);
        pieChart.setHoleColor(Color.TRANSPARENT);
        pieChart.setCenterTextColor(ContextCompat.getColor(context, R.color.accent));
        pieChart.setCenterTextSize(textSize);
        pieChart.setHoleRadius(35);
        pieChart.setDescription("");
    }

    private void formatPieDataSet(List<Entry> entries, PieDataSet pieDataSet, boolean notFiveMan) {
        int[] colors = StatsUtil.chartColors();
        if (notFiveMan) {
            colors[entries.size() - 1] = R.color.gray;
        }
        float rawTextSize = context.getResources().getDimension(R.dimen.text_size_large);
        int textSize = (int) (rawTextSize / context.getResources().getDisplayMetrics().density);
        pieDataSet.setColors(colors, context);
        pieDataSet.setValueFormatter(new IntValueFormat());
        pieDataSet.setValueTextColor(Color.WHITE);
        pieDataSet.setValueTextSize(textSize);
        pieDataSet.setSelectionShift(0);
    }

    private void populateSummonerTable(List<LinearLayout> summonerLayouts, List<MatchStats> matchStatsList) {
        // initialize url array lists
        iconURLsList = new ArrayList<>();
        champIconURLsList = new ArrayList<>();

        // iterate over the match data
        int x = 0;
        for (MatchStats matchStats : matchStatsList) {
            // initialize the layout
            LinearLayout summonerLayout = summonerLayouts.get(x);
            switch (x) {
                case 0:
                    summonerLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_overlay_blue));
                    break;
                case 1:
                    summonerLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_overlay_green));
                    break;
                case 2:
                    summonerLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_overlay_orange));
                    break;
                case 3:
                    summonerLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_overlay_pink));
                    break;
                case 4:
                    summonerLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_overlay_purple));
                    break;
                default:
                    summonerLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_overlay_blue));
                    break;
            }

            // turn on the layout
            summonerLayout.setVisibility(View.VISIBLE);

            // gather the champion icon url
            String key = StatsUtil.championKey(matchStats.champion, staticRiotData.championsMap);
            champIconURLsList.add(StatsUtil.championIconURL(staticRiotData.version, key));

            // gather icon urls
            iconURLsList.add(new ArrayList<>());
            iconURLsList.get(x).add(StatsUtil.summonerSpellURL(staticRiotData.version, matchStats.spell1));
            if (matchStats.keystone != null) {
                iconURLsList.get(x).add(StatsUtil.masteryURL(staticRiotData.version, matchStats.keystone));
            } else {
                iconURLsList.get(x).add("");
            }
            if (matchStats.item0 != null) {
                iconURLsList.get(x).add(StatsUtil.itemURL(staticRiotData.version, matchStats.item0));
            } else {
                iconURLsList.get(x).add("");
            }
            if (matchStats.item1 != null) {
                iconURLsList.get(x).add(StatsUtil.itemURL(staticRiotData.version, matchStats.item1));
            } else {
                iconURLsList.get(x).add("");
            }
            if (matchStats.item2 != null) {
                iconURLsList.get(x).add(StatsUtil.itemURL(staticRiotData.version, matchStats.item2));
            } else {
                iconURLsList.get(x).add("");
            }
            iconURLsList.get(x).add(StatsUtil.summonerSpellURL(staticRiotData.version, matchStats.spell2));
            if (matchStats.item6 != null) {
                iconURLsList.get(x).add(StatsUtil.itemURL(staticRiotData.version, matchStats.item6));
            } else {
                iconURLsList.get(x).add("");
            }
            if (matchStats.item3 != null) {
                iconURLsList.get(x).add(StatsUtil.itemURL(staticRiotData.version, matchStats.item3));
            } else {
                iconURLsList.get(x).add("");
            }
            if (matchStats.item4 != null) {
                iconURLsList.get(x).add(StatsUtil.itemURL(staticRiotData.version, matchStats.item4));
            } else {
                iconURLsList.get(x).add("");
            }
            if (matchStats.item5 != null) {
                iconURLsList.get(x).add(StatsUtil.itemURL(staticRiotData.version, matchStats.item5));
            } else {
                iconURLsList.get(x).add("");
            }

            x++;
        }
    }

    public class SummonerGridAdapter extends ArrayAdapter<String> {

        private final Context context;
        private final int side;
        private final List<String> urls;

        SummonerGridAdapter(Context context, int side, List<String> urls) {
            super(context, -1, urls);
            this.context = context;
            this.side = side;
            this.urls = urls;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;

            if (convertView == null) {
                // initialize view holder and layout inflater

                viewHolder = new ViewHolder();
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.element_item_icon, parent, false);

                // initialize views
                viewHolder.icon = (ImageView) convertView.findViewById(R.id.summoner_item_view);

                // set tag
                convertView.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            // resize the image view
            viewHolder.icon.getLayoutParams().width = (int) (side / 2.1);
            viewHolder.icon.getLayoutParams().height = (int) (side / 2.1);
            viewHolder.icon.setLayoutParams(viewHolder.icon.getLayoutParams());

            // set the icon
            if ("".equals(urls.get(position))) {
                Picasso.with(context).load(R.drawable.ic_placeholder).resize((int) (side / 2.1), (int) (side / 2.1))
                        .transform(new RoundTransform()).into(viewHolder.icon);
            } else if (urls.get(position).equals(Constants.UI_NO_ITEM)) {
                Picasso.with(context).load(R.drawable.ic_no_item).resize((int) (side / 2.1), (int) (side / 2.1))
                        .transform(new RoundTransform()).into(viewHolder.icon);
            } else {
                Picasso.with(context).load(urls.get(position)).resize((int) (side / 2.1), (int) (side / 2.1))
                        .placeholder(R.drawable.ic_placeholder).transform(new RoundTransform()).into(viewHolder.icon);
            }

            return convertView;
        }

        class ViewHolder {

            ImageView icon;
        }
    }

    public class WFViewHolder extends RecyclerView.ViewHolder {

        final TextView defeatView;
        final ImageView incomeTab;
        final TextView matchDateView;
        final TextView matchDurationView;
        final ImageView offenseTab;
        final List<LinearLayout> summonerLayouts;
        final TextView teamKDAView;
        final ImageView utilityTab;
        final TextView victoryView;
        final ImageView visionTab;

        WFViewHolder(View itemView) {
            super(itemView);

            // initialize views
            victoryView = (TextView) itemView.findViewById(R.id.victory_view);
            victoryView.setVisibility(View.GONE);
            defeatView = (TextView) itemView.findViewById(R.id.defeat_view);
            defeatView.setVisibility(View.GONE);
            matchDateView = (TextView) itemView.findViewById(R.id.match_date_view);
            teamKDAView = (TextView) itemView.findViewById(R.id.team_kda_view);
            matchDurationView = (TextView) itemView.findViewById(R.id.match_duration_view);

            // store the summoner layouts
            summonerLayouts = new ArrayList<>();
            summonerLayouts.add((LinearLayout) itemView.findViewById(R.id.summoner_1_layout));
            summonerLayouts.add((LinearLayout) itemView.findViewById(R.id.summoner_2_layout));
            summonerLayouts.add((LinearLayout) itemView.findViewById(R.id.summoner_3_layout));
            summonerLayouts.add((LinearLayout) itemView.findViewById(R.id.summoner_4_layout));
            summonerLayouts.add((LinearLayout) itemView.findViewById(R.id.summoner_5_layout));

            // get the champ view dimensions
            ImageView champView = (ImageView) summonerLayouts.get(0).findViewById(R.id.summoner_champ_view);
            champView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    champView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    int height = champView.getHeight();
                    int width = champView.getWidth();
                    int side = Math.min(width, height);

                    // set the champion icon
                    for (int i = 0; i < champIconURLsList.size(); i++) {
                        LinearLayout summonerLayout = summonerLayouts.get(i);
                        ImageView champView = (ImageView) summonerLayout.findViewById(R.id.summoner_champ_view);
                        String url = champIconURLsList.get(i);
                        Picasso.with(context).load(url).resize(side, side)
                                .placeholder(R.drawable.ic_placeholder).transform(new RoundTransform()).into(champView);
                    }
                }
            });

            // get the grid height
            GridView gridView = (GridView) summonerLayouts.get(0).findViewById(R.id.summoner_grid_view);
            gridView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    gridView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    int height = gridView.getHeight();

                    // initialize the grid view
                    for (int i = 0; i < iconURLsList.size(); i++) {
                        LinearLayout summonerLayout = summonerLayouts.get(i);
                        List<String> iconURLs = iconURLsList.get(i);
                        GridView summonerGrid = (GridView) summonerLayout.findViewById(R.id.summoner_grid_view);
                        summonerGrid.setAdapter(new SummonerGridAdapter(context, height, iconURLs));
                    }
                }
            });

            // layouts are invisible by default
            for (LinearLayout layout : summonerLayouts) {
                layout.setVisibility(View.INVISIBLE);
            }

            // initialize the tab buttons
            incomeTab = (ImageView) itemView.findViewById(R.id.income_icon);
            offenseTab = (ImageView) itemView.findViewById(R.id.offense_icon);
            utilityTab = (ImageView) itemView.findViewById(R.id.utility_icon);
            visionTab = (ImageView) itemView.findViewById(R.id.vision_icon);
        }
    }

    private class ChartsDialog extends Dialog {

        final List<MatchStats> matchStatsList;
        final List<String> names;
        final int tab;

        ChartsDialog(List<String> names, List<MatchStats> matchStatsList, int tab) {
            super(context, R.style.AppTheme_Dialog);
            this.matchStatsList = matchStatsList;
            this.names = names;
            this.tab = tab;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.dialog_charts);
            int width = (Constants.UI_DIALOG_WIDTH * ScreenUtil.screenWidth(context)) / 100;
            int height = (Constants.UI_DIALOG_HEIGHT * ScreenUtil.screenHeight(context)) / 100;
            getWindow().setLayout(width, height);

            // if it wasn't a five man queue, include label for non friends in pie charts
            List<String> namesPie = new ArrayList<>(names);
            boolean notFiveMan = false;
            if (names.size() < 5) {
                notFiveMan = true;
                namesPie.add(context.getResources().getString(R.string.gwf_others));
            }

            // initialize legend and recycler view
            RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
            CreateLegendPackage createLegendPackage = new CreateLegendPackage();
            createLegendPackage.context = context;
            createLegendPackage.staticRiotData = staticRiotData;
            createLegendPackage.view = findViewById(R.id.legend_layout);
            createLegendPackage.viewWidth = width;
            switch (tab) {
                case 0:
                    createLegendPackage.names = new LinkedHashSet<>(names);
                    StatsUtil.createLegend(createLegendPackage);
                    IncomeAdapter incomeAdapter = new IncomeAdapter(names, matchStatsList, (75 * height) / 100);
                    recyclerView.setAdapter(incomeAdapter);
                    break;
                case 1:
                    createLegendPackage.names = new LinkedHashSet<>(namesPie);
                    createLegendPackage.notFiveMan = notFiveMan;
                    StatsUtil.createLegend(createLegendPackage);
                    OffenseAdapter offenseAdapter = new OffenseAdapter(names, matchStatsList, (75 * height) / 100);
                    recyclerView.setAdapter(offenseAdapter);
                    break;
                case 2:
                    createLegendPackage.names = new LinkedHashSet<>(namesPie);
                    createLegendPackage.notFiveMan = notFiveMan;
                    StatsUtil.createLegend(createLegendPackage);
                    UtilityAdapter utilityAdapter = new UtilityAdapter(names, matchStatsList, (75 * height) / 100);
                    recyclerView.setAdapter(utilityAdapter);
                    break;
                case 3:
                    createLegendPackage.names = new LinkedHashSet<>(names);
                    StatsUtil.createLegend(createLegendPackage);
                    VisionAdapter visionAdapter = new VisionAdapter(names, matchStatsList, (75 * height) / 100);
                    recyclerView.setAdapter(visionAdapter);
                    break;
            }
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        }
    }

    private class IncomeAdapter extends RecyclerView.Adapter<IncomeAdapter.IncomeViewHolder> {

        final int height;
        final List<MatchStats> matchStatsList;
        final List<String> names;

        IncomeAdapter(List<String> names, List<MatchStats> matchStatsList, int height) {
            this.matchStatsList = matchStatsList;
            this.names = names;
            this.height = height;
        }

        @Override
        public int getItemCount() {
            return 1;
        }

        @Override
        public void onBindViewHolder(IncomeViewHolder viewHolder, int i) {
            // resize layouts
            for (LinearLayout chartLayout : viewHolder.chartLayouts) {
                chartLayout.getLayoutParams().height = height;
                chartLayout.setLayoutParams(chartLayout.getLayoutParams());
            }

            // create the entries
            List<BarEntry> entriesCS = new ArrayList<>();
            List<BarEntry> entriesCS10 = new ArrayList<>();
            List<BarEntry> entriesCSDiff10 = new ArrayList<>();
            List<BarEntry> entriesGold = new ArrayList<>();
            int j = 0;
            for (MatchStats matchStats : matchStatsList) {
                if (matchStats.minions_killed != null) {
                    entriesCS.add(new BarEntry(matchStats.minions_killed, j));
                } else {
                    entriesCS.add(new BarEntry(0, j));
                }
                if (matchStats.cs_at_ten != null) {
                    entriesCS10.add(new BarEntry(matchStats.cs_at_ten, j));
                } else {
                    entriesCS10.add(new BarEntry(0, j));
                }
                if (matchStats.cs_diff_at_ten != null) {
                    entriesCSDiff10.add(new BarEntry(matchStats.cs_diff_at_ten, j));
                } else {
                    entriesCSDiff10.add(new BarEntry(0, j));
                }
                if (matchStats.gold_earned != null) {
                    entriesGold.add(new BarEntry(matchStats.gold_earned, j));
                } else {
                    entriesGold.add(new BarEntry(0, j));
                }
                j++;
            }

            // organize entries
            List<List<BarEntry>> barEntries = new ArrayList<>();
            barEntries.add(entriesCS);
            barEntries.add(entriesCS10);
            barEntries.add(entriesCSDiff10);
            barEntries.add(entriesGold);

            // create the data sets
            List<BarDataSet> barDataSets = new ArrayList<>();
            for (List<BarEntry> entries : barEntries) {
                BarDataSet barDataSet = new BarDataSet(entries, "");
                formatBarDataSet(barDataSet);
                barDataSets.add(barDataSet);
            }

            // populate the charts
            j = 0;
            for (BarChart barChart : viewHolder.barCharts) {
                barChart.setData(new BarData(names, barDataSets.get(j)));
                formatBarChart(barChart);
                j++;
            }
        }

        @Override
        public IncomeViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            Context context = viewGroup.getContext();
            View view = LayoutInflater.from(context).inflate(R.layout.view_wf_income, viewGroup, false);
            return new IncomeViewHolder(view);
        }

        class IncomeViewHolder extends RecyclerView.ViewHolder {

            final List<BarChart> barCharts;
            final List<LinearLayout> chartLayouts;

            IncomeViewHolder(View itemView) {
                super(itemView);
                chartLayouts = new ArrayList<>();
                chartLayouts.add((LinearLayout) itemView.findViewById(R.id.cs_chart_layout));
                chartLayouts.add((LinearLayout) itemView.findViewById(R.id.cs_10_chart_layout));
                chartLayouts.add((LinearLayout) itemView.findViewById(R.id.cs_diff_10_chart_layout));
                chartLayouts.add((LinearLayout) itemView.findViewById(R.id.gold_chart_layout));
                barCharts = new ArrayList<>();
                barCharts.add((BarChart) itemView.findViewById(R.id.cs_chart));
                barCharts.add((BarChart) itemView.findViewById(R.id.cs_10_chart));
                barCharts.add((BarChart) itemView.findViewById(R.id.cs_diff_10_chart));
                barCharts.add((BarChart) itemView.findViewById(R.id.gold_chart));
            }
        }
    }

    private class OffenseAdapter extends RecyclerView.Adapter<OffenseAdapter.OffenseViewHolder> {

        final int height;
        final List<MatchStats> matchStatsList;
        final List<String> names;

        OffenseAdapter(List<String> names, List<MatchStats> matchStatsList, int height) {
            this.matchStatsList = matchStatsList;
            this.names = names;
            this.height = height;
        }

        @Override
        public int getItemCount() {
            return 1;
        }

        @Override
        public void onBindViewHolder(OffenseViewHolder viewHolder, int i) {
            // resize layouts
            for (LinearLayout chartLayout : viewHolder.chartLayouts) {
                chartLayout.getLayoutParams().height = height;
                chartLayout.setLayoutParams(chartLayout.getLayoutParams());
            }

            // if it wasn't a five man queue, include label for non friends in pie charts
            List<String> namesPie = new ArrayList<>(names);
            boolean notFiveMan = false;
            if (names.size() < 5) {
                notFiveMan = true;
                namesPie.add(context.getResources().getString(R.string.gwf_others));
            }

            // create the entries
            List<Entry> entriesKills = new ArrayList<>();
            Long teamKills = matchStatsList.get(0).team_kills;
            Long othersKills = null;
            if (teamKills != null) {
                othersKills = teamKills;
            }
            List<BarEntry> entriesDamage = new ArrayList<>();
            List<BarEntry> entriesSpree = new ArrayList<>();
            List<BarEntry> entriesMultiKill = new ArrayList<>();
            int j = 0;
            for (MatchStats matchStats : matchStatsList) {
                if (matchStats.kills != null) {
                    entriesKills.add(new BarEntry(matchStats.kills, j));
                    if (othersKills != null) {
                        othersKills = othersKills - matchStats.kills;
                    }
                } else {
                    entriesKills.add(new BarEntry(0, j));
                }
                if (matchStats.total_damage_dealt_to_champions != null) {
                    entriesDamage.add(new BarEntry(matchStats.total_damage_dealt_to_champions, j));
                } else {
                    entriesDamage.add(new BarEntry(0, j));
                }
                if (matchStats.largest_killing_spree != null) {
                    entriesSpree.add(new BarEntry(matchStats.largest_killing_spree, j));
                } else {
                    entriesSpree.add(new BarEntry(0, j));
                }
                if (matchStats.largest_multi_kill != null) {
                    entriesMultiKill.add(new BarEntry(matchStats.largest_multi_kill, j));
                } else {
                    entriesMultiKill.add(new BarEntry(0, j));
                }
                j++;
            }
            if (notFiveMan) {
                if (othersKills != null) {
                    entriesKills.add(new Entry(othersKills, j));
                } else {
                    namesPie.remove(namesPie.size() - 1);
                }
            }

            // organize entries
            List<List<Entry>> pieEntries = new ArrayList<>();
            pieEntries.add(entriesKills);
            List<List<BarEntry>> barEntries = new ArrayList<>();
            barEntries.add(entriesDamage);
            barEntries.add(entriesSpree);
            barEntries.add(entriesMultiKill);

            // create the data sets
            List<PieDataSet> pieDataSets = new ArrayList<>();
            for (List<Entry> entries : pieEntries) {
                PieDataSet pieDataSet = new PieDataSet(entries, "");
                if (othersKills != null) {
                    formatPieDataSet(entries, pieDataSet, notFiveMan);
                } else {
                    formatPieDataSet(entries, pieDataSet, false);
                }
                pieDataSets.add(pieDataSet);
            }
            List<BarDataSet> barDataSets = new ArrayList<>();
            for (List<BarEntry> entries : barEntries) {
                BarDataSet barDataSet = new BarDataSet(entries, "");
                formatBarDataSet(barDataSet);
                barDataSets.add(barDataSet);
            }

            // populate the charts
            j = 0;
            for (PieChart pieChart : viewHolder.pieCharts) {
                pieChart.setData(new PieData(namesPie, pieDataSets.get(j)));
                formatPieChart(pieChart);
                switch (j) {
                    case 0:
                        pieChart.setCenterText(context.getResources().getString(R.string.gwf_kills));
                        break;
                }
                j++;
            }
            j = 0;
            for (BarChart barChart : viewHolder.barCharts) {
                barChart.setData(new BarData(names, barDataSets.get(j)));
                formatBarChart(barChart);
                j++;
            }
        }

        @Override
        public OffenseViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            Context context = viewGroup.getContext();
            View view = LayoutInflater.from(context).inflate(R.layout.view_wf_offense, viewGroup, false);
            return new OffenseViewHolder(view);
        }

        class OffenseViewHolder extends RecyclerView.ViewHolder {

            final List<BarChart> barCharts;
            final List<LinearLayout> chartLayouts;
            final List<PieChart> pieCharts;

            OffenseViewHolder(View itemView) {
                super(itemView);
                chartLayouts = new ArrayList<>();
                chartLayouts.add((LinearLayout) itemView.findViewById(R.id.kills_chart_layout));
                chartLayouts.add((LinearLayout) itemView.findViewById(R.id.damage_chart_layout));
                chartLayouts.add((LinearLayout) itemView.findViewById(R.id.spree_chart_layout));
                chartLayouts.add((LinearLayout) itemView.findViewById(R.id.multi_kill_chart_layout));
                pieCharts = new ArrayList<>();
                pieCharts.add((PieChart) itemView.findViewById(R.id.kills_chart));
                barCharts = new ArrayList<>();
                barCharts.add((BarChart) itemView.findViewById(R.id.damage_chart));
                barCharts.add((BarChart) itemView.findViewById(R.id.spree_chart));
                barCharts.add((BarChart) itemView.findViewById(R.id.multi_kill_chart));
            }
        }
    }

    private class UtilityAdapter extends RecyclerView.Adapter<UtilityAdapter.UtilityViewHolder> {

        final int height;
        final List<MatchStats> matchStatsList;
        final List<String> names;

        UtilityAdapter(List<String> names, List<MatchStats> matchStatsList, int height) {
            this.matchStatsList = matchStatsList;
            this.names = names;
            this.height = height;
        }

        @Override
        public int getItemCount() {
            return 1;
        }

        @Override
        public void onBindViewHolder(UtilityViewHolder viewHolder, int i) {
            // resize layouts
            for (LinearLayout chartLayout : viewHolder.chartLayouts) {
                chartLayout.getLayoutParams().height = height;
                chartLayout.setLayoutParams(chartLayout.getLayoutParams());
            }

            // if it wasn't a five man queue, include label for non friends in pie charts
            List<String> namesPie = new ArrayList<>(names);
            boolean notFiveMan = false;
            if (names.size() < 5) {
                notFiveMan = true;
                namesPie.add(context.getResources().getString(R.string.gwf_others));
            }

            // create the entries
            List<Entry> entriesAssists = new ArrayList<>();
            Long teamAssists = matchStatsList.get(0).team_assists;
            Long othersAssists = null;
            if (teamAssists != null) {
                othersAssists = teamAssists;
            }
            List<BarEntry> entriesKDA = new ArrayList<>();
            List<BarEntry> entriesKP = new ArrayList<>();
            int j = 0;
            for (MatchStats matchStats : matchStatsList) {
                if (matchStats.assists != null) {
                    entriesAssists.add(new BarEntry(matchStats.assists, j));
                    if (othersAssists != null) {
                        othersAssists = othersAssists - matchStats.assists;
                    }
                } else {
                    entriesAssists.add(new BarEntry(0, j));
                }
                if (matchStats.kda != null) {
                    entriesKDA.add(new BarEntry(matchStats.kda, j));
                } else {
                    entriesKDA.add(new BarEntry(0, j));
                }
                if (matchStats.kill_participation != null) {
                    entriesKP.add(new BarEntry(matchStats.kill_participation, j));
                } else {
                    entriesKP.add(new BarEntry(0, j));
                }
                j++;
            }
            if (notFiveMan) {
                if (othersAssists != null) {
                    entriesAssists.add(new Entry(othersAssists, j));
                } else {
                    namesPie.remove(namesPie.size() - 1);
                }
            }

            // organize entries
            List<List<Entry>> pieEntries = new ArrayList<>();
            pieEntries.add(entriesAssists);
            List<List<BarEntry>> barEntries = new ArrayList<>();
            barEntries.add(entriesKDA);
            barEntries.add(entriesKP);

            // create the data sets
            List<PieDataSet> pieDataSets = new ArrayList<>();
            for (List<Entry> entries : pieEntries) {
                PieDataSet pieDataSet = new PieDataSet(entries, "");
                if (othersAssists != null) {
                    formatPieDataSet(entries, pieDataSet, notFiveMan);
                } else {
                    formatPieDataSet(entries, pieDataSet, false);
                }
                pieDataSets.add(pieDataSet);
            }
            List<BarDataSet> barDataSets = new ArrayList<>();
            for (List<BarEntry> entries : barEntries) {
                BarDataSet barDataSet = new BarDataSet(entries, "");
                formatBarDataSet(barDataSet);
                barDataSets.add(barDataSet);
            }

            // populate the charts
            j = 0;
            for (PieChart pieChart : viewHolder.pieCharts) {
                pieChart.setData(new PieData(namesPie, pieDataSets.get(j)));
                formatPieChart(pieChart);
                switch (j) {
                    case 0:
                        pieChart.setCenterText(context.getResources().getString(R.string.gwf_assists));
                        break;
                }
                j++;
            }
            j = 0;
            for (BarChart barChart : viewHolder.barCharts) {
                barChart.setData(new BarData(names, barDataSets.get(j)));
                formatBarChart(barChart);
                j++;
            }
        }

        @Override
        public UtilityViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            Context context = viewGroup.getContext();
            View view = LayoutInflater.from(context).inflate(R.layout.view_wf_utility, viewGroup, false);
            return new UtilityViewHolder(view);
        }

        class UtilityViewHolder extends RecyclerView.ViewHolder {

            final List<BarChart> barCharts;
            final List<LinearLayout> chartLayouts;
            final List<PieChart> pieCharts;

            UtilityViewHolder(View itemView) {
                super(itemView);
                chartLayouts = new ArrayList<>();
                chartLayouts.add((LinearLayout) itemView.findViewById(R.id.assists_chart_layout));
                chartLayouts.add((LinearLayout) itemView.findViewById(R.id.kda_chart_layout));
                chartLayouts.add((LinearLayout) itemView.findViewById(R.id.kp_chart_layout));
                pieCharts = new ArrayList<>();
                pieCharts.add((PieChart) itemView.findViewById(R.id.assists_chart));
                barCharts = new ArrayList<>();
                barCharts.add((BarChart) itemView.findViewById(R.id.kda_chart));
                barCharts.add((BarChart) itemView.findViewById(R.id.kp_chart));
            }
        }
    }

    private class VisionAdapter extends RecyclerView.Adapter<VisionAdapter.VisionViewHolder> {

        final int height;
        final List<MatchStats> matchStatsList;
        final List<String> names;

        VisionAdapter(List<String> names, List<MatchStats> matchStatsList, int height) {
            this.matchStatsList = matchStatsList;
            this.names = names;
            this.height = height;
        }

        @Override
        public int getItemCount() {
            return 1;
        }

        @Override
        public void onBindViewHolder(VisionViewHolder viewHolder, int i) {
            // resize layouts
            for (LinearLayout chartLayout : viewHolder.chartLayouts) {
                chartLayout.getLayoutParams().height = height;
                chartLayout.setLayoutParams(chartLayout.getLayoutParams());
            }

            // create the entries
            List<BarEntry> entriesWardsBought = new ArrayList<>();
            List<BarEntry> entriesWardsPlaced = new ArrayList<>();
            List<BarEntry> entriesWardsKilled = new ArrayList<>();
            int j = 0;
            for (MatchStats matchStats : matchStatsList) {
                if (matchStats.vision_wards_bought_in_game != null) {
                    entriesWardsBought.add(new BarEntry(matchStats.vision_wards_bought_in_game, j));
                } else {
                    entriesWardsBought.add(new BarEntry(0, j));
                }
                if (matchStats.wards_placed != null) {
                    entriesWardsPlaced.add(new BarEntry(matchStats.wards_placed, j));
                } else {
                    entriesWardsPlaced.add(new BarEntry(0, j));
                }
                if (matchStats.wards_killed != null) {
                    entriesWardsKilled.add(new BarEntry(matchStats.wards_killed, j));
                } else {
                    entriesWardsKilled.add(new BarEntry(0, j));
                }
                j++;
            }

            // organize entries
            List<List<BarEntry>> barEntries = new ArrayList<>();
            barEntries.add(entriesWardsBought);
            barEntries.add(entriesWardsPlaced);
            barEntries.add(entriesWardsKilled);

            // create the data sets
            List<BarDataSet> barDataSets = new ArrayList<>();
            for (List<BarEntry> entries : barEntries) {
                BarDataSet barDataSet = new BarDataSet(entries, "");
                formatBarDataSet(barDataSet);
                barDataSets.add(barDataSet);
            }

            // populate the charts
            j = 0;
            for (BarChart barChart : viewHolder.barCharts) {
                if (j == 0) { // wards chart
                    ArrayList<IBarDataSet> dataSets = new ArrayList<>();
                    dataSets.add(barDataSets.get(j));
                    dataSets.add(barDataSets.get(j + 1));
                    dataSets.add(barDataSets.get(j + 2));
                    barChart.setData(new BarData(names, dataSets));
                    j = j + 3;
                } else { // regular bar chart
                    barChart.setData(new BarData(names, barDataSets.get(j)));
                    j++;
                }
                formatBarChart(barChart);
            }
        }

        @Override
        public VisionViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            Context context = viewGroup.getContext();
            View view = LayoutInflater.from(context).inflate(R.layout.view_wf_vision, viewGroup, false);
            return new VisionViewHolder(view);
        }

        class VisionViewHolder extends RecyclerView.ViewHolder {

            final List<BarChart> barCharts;
            final List<LinearLayout> chartLayouts;

            VisionViewHolder(View itemView) {
                super(itemView);
                chartLayouts = new ArrayList<>();
                chartLayouts.add((LinearLayout) itemView.findViewById(R.id.wards_chart_layout));
                barCharts = new ArrayList<>();
                barCharts.add((BarChart) itemView.findViewById(R.id.wards_chart));
            }
        }
    }
}
