package com.example.tberroa.portal.screens.stats.season;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;

import com.example.tberroa.portal.R;
import com.example.tberroa.portal.screens.BaseActivity;
import com.example.tberroa.portal.screens.ScreenUtil;
import com.example.tberroa.portal.screens.home.HomeActivity;
import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;

import java.util.ArrayList;
import java.util.List;

public class SeasonActivity extends BaseActivity {

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_season);

        // initialize the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.st_activity_title);
        toolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.ic_back_button));
        toolbar.setNavigationOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else {
                    startActivity(new Intent(SeasonActivity.this, HomeActivity.class));
                    finish();
                }
            }
        });

        // initialize summoner name labels
        List<String> names = new ArrayList<>();
        names.add("1");
        names.add("2");
        names.add("3");
        names.add("4");
        names.add("5");
        names.add("6");

        // initialize charts
        List<RadarChart> charts = new ArrayList<>();
        RadarChart killsChart = (RadarChart) findViewById(R.id.kills_chart);
        RadarChart deathsChart = (RadarChart) findViewById(R.id.deaths_chart);
        RadarChart assistsChart = (RadarChart) findViewById(R.id.assists_chart);
        RadarChart doublesChart = (RadarChart) findViewById(R.id.doubles_chart);
        RadarChart triplesChart = (RadarChart) findViewById(R.id.triples_chart);
        RadarChart quadrasChart = (RadarChart) findViewById(R.id.quadras_chart);
        RadarChart pentasChart = (RadarChart) findViewById(R.id.pentas_chart);
        charts.add(killsChart);
        charts.add(deathsChart);
        charts.add(assistsChart);
        charts.add(doublesChart);
        charts.add(triplesChart);
        charts.add(quadrasChart);
        charts.add(pentasChart);

        // create entries
        List<Entry> killEntries = new ArrayList<>();
        killEntries.add(new Entry(2423, 0));
        killEntries.add(new Entry(1233, 0));
        killEntries.add(new Entry(3423, 0));
        killEntries.add(new Entry(4233, 0));
        killEntries.add(new Entry(823, 0));
        killEntries.add(new Entry(1753, 0));
        List<Entry> deathEntries = new ArrayList<>();
        deathEntries.add(new Entry(1709, 0));
        deathEntries.add(new Entry(1048, 0));
        deathEntries.add(new Entry(2209, 0));
        deathEntries.add(new Entry(1848, 0));
        deathEntries.add(new Entry(1209, 0));
        deathEntries.add(new Entry(2532, 0));
        List<Entry> assistEntries = new ArrayList<>();
        assistEntries.add(new Entry(4304, 0));
        assistEntries.add(new Entry(2309, 0));
        assistEntries.add(new Entry(6504, 0));
        assistEntries.add(new Entry(3209, 0));
        assistEntries.add(new Entry(1204, 0));
        assistEntries.add(new Entry(1809, 0));
        List<Entry> doubleEntries = new ArrayList<>();
        doubleEntries.add(new Entry(159, 0));
        doubleEntries.add(new Entry(72, 0));
        doubleEntries.add(new Entry(239, 0));
        doubleEntries.add(new Entry(53, 0));
        doubleEntries.add(new Entry(27, 0));
        doubleEntries.add(new Entry(22, 0));
        List<Entry> tripleEntries = new ArrayList<>();
        tripleEntries.add(new Entry(34, 0));
        tripleEntries.add(new Entry(23, 0));
        tripleEntries.add(new Entry(54, 0));
        tripleEntries.add(new Entry(13, 0));
        tripleEntries.add(new Entry(8, 0));
        tripleEntries.add(new Entry(17, 0));
        List<Entry> quadraEntries = new ArrayList<>();
        quadraEntries.add(new Entry(8, 0));
        quadraEntries.add(new Entry(15, 0));
        quadraEntries.add(new Entry(7, 0));
        quadraEntries.add(new Entry(22, 0));
        quadraEntries.add(new Entry(1, 0));
        quadraEntries.add(new Entry(1, 0));
        List<Entry> pentaEntries = new ArrayList<>();
        pentaEntries.add(new Entry(2, 0));
        pentaEntries.add(new Entry(0, 0));
        pentaEntries.add(new Entry(4, 0));
        pentaEntries.add(new Entry(7, 0));
        pentaEntries.add(new Entry(0, 0));
        pentaEntries.add(new Entry(1, 0));

        // create data sets
        RadarDataSet killDataSet = new RadarDataSet(killEntries, "KILLS");
        killDataSet.setDrawFilled(true);
        RadarDataSet deathDataSet = new RadarDataSet(deathEntries, "DEATHS");
        RadarDataSet assistDataSet = new RadarDataSet(assistEntries, "ASSISTS");
        RadarDataSet doubleDataSet = new RadarDataSet(doubleEntries, "DOUBLES");
        RadarDataSet tripleDataSet = new RadarDataSet(tripleEntries, "TRIPLES");
        RadarDataSet quadraDataSet = new RadarDataSet(quadraEntries, "QUADRAS");
        RadarDataSet pentaDataSet = new RadarDataSet(pentaEntries, "PENTAS");

        // populate charts
        killsChart.setData(new RadarData(names, killDataSet));
        deathsChart.setData(new RadarData(names, deathDataSet));
        assistsChart.setData(new RadarData(names, assistDataSet));
        doublesChart.setData(new RadarData(names, doubleDataSet));
        triplesChart.setData(new RadarData(names, tripleDataSet));
        quadrasChart.setData(new RadarData(names, quadraDataSet));
        pentasChart.setData(new RadarData(names, pentaDataSet));

        // format charts
        for (RadarChart chart : charts){
            chart.setWebColor(Color.WHITE);
            chart.setWebColorInner(Color.WHITE);
            chart.getData().setValueTextColor(Color.WHITE);
            chart.getXAxis().setTextColor(Color.WHITE);
            chart.getYAxis().setEnabled(false);
            chart.getLegend().setEnabled(false);
            chart.setTouchEnabled(false);
            chart.setDescription("");
        }
    }
}
