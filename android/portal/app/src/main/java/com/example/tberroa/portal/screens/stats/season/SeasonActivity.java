package com.example.tberroa.portal.screens.stats.season;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.example.tberroa.portal.R;
import com.example.tberroa.portal.models.stats.SeasonStats;
import com.example.tberroa.portal.screens.ScreenUtil;
import com.example.tberroa.portal.screens.home.HomeActivity;
import com.example.tberroa.portal.screens.stats.BaseStatsActivity;
import com.example.tberroa.portal.screens.stats.CreateLegendPackage;
import com.example.tberroa.portal.screens.stats.StatsUtil;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

public class SeasonActivity extends BaseStatsActivity implements SeasonAsync {

    public void displayData(Map<String, Map<Long, SeasonStats>> seasonStatsMapMap) {
        // update the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setOnMenuItemClickListener(new MenuListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                super.onMenuItemClick(item);
                return true;
            }
        });

        // create view package
        ViewPackage viewPackage = new ViewPackage();
        viewPackage.seasonStatsMapMap = seasonStatsMapMap;
        viewPackage.champion = 0;
        viewPackage.perGame = false;

        // initialize the recycler view
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        SeasonViewAdapter adapter = new SeasonViewAdapter(this, viewPackage);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setVisibility(View.VISIBLE);

        // create legend labels
        int i = 0;
        List<String> labels = new ArrayList<>();
        List<String> names = new ArrayList<>(seasonStatsMapMap.keySet());
        for (String name : names) {
            labels.add(i + ". " + name);
            i++;
        }

        // create the legend
        LinearLayout legendLayout = (LinearLayout) findViewById(R.id.legend_layout);
        CreateLegendPackage createLegendPackage = new CreateLegendPackage();
        createLegendPackage.context = this;
        createLegendPackage.names = new LinkedHashSet<>(labels);
        createLegendPackage.staticRiotData = staticRiotData;
        createLegendPackage.view = legendLayout;
        createLegendPackage.viewWidth = ScreenUtil.screenWidth(this);
        StatsUtil.createLegend(createLegendPackage);

        // display the legend
        legendLayout.setVisibility(View.VISIBLE);
    }

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

        // set swipe refresh layout listeners
        SwipeRefreshLayout dataSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.data_swipe_layout);
        dataSwipeLayout.setOnRefreshListener(this);
        SwipeRefreshLayout messageSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.message_swipe_layout);
        messageSwipeLayout.setOnRefreshListener(this);

        // initialize legend
        LinearLayout legendLayout = (LinearLayout) findViewById(R.id.legend_layout);
        legendLayout.setVisibility(View.GONE);

        // initialize the recycler view to gone
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setVisibility(View.GONE);

        // initialize view
        ViewInitialization viewInitialization = new ViewInitialization();
        viewInitialization.delegateSeason = this;
        viewInitialization.execute(2);
    }

    @Override
    public void onRefresh() {
        RequestSeasonStats requestSeasonStats = new RequestSeasonStats();
        requestSeasonStats.delegateSeason = this;
        requestSeasonStats.execute();
    }
}
