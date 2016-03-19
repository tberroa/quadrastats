package com.example.tberroa.portal.screens.stats;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.tberroa.portal.R;
import com.example.tberroa.portal.data.LocalDB;
import com.example.tberroa.portal.models.summoner.FriendsList;
import com.example.tberroa.portal.models.summoner.SummonerDto;
import com.example.tberroa.portal.screens.BaseActivity;
import com.example.tberroa.portal.screens.ScreenUtil;
import com.example.tberroa.portal.screens.friends.FriendsActivity;
import com.example.tberroa.portal.data.Params;
import com.example.tberroa.portal.data.UserInfo;
import com.example.tberroa.portal.screens.home.HomeActivity;
import com.example.tberroa.portal.models.match.ParticipantStats;
import com.example.tberroa.portal.updater.UpdateJobListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class StatsActivity extends BaseActivity {

    private UpdateJobListener updateJobListener;
    private TabLayout tabLayout;
    private Map<String, Bundle> plotData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);
        final UserInfo userInfo = new UserInfo();

        // get queue
        final String queue = getIntent().getStringExtra("queue");

        // no animation if starting activity as a reload
        if (getIntent().getAction() != null && getIntent().getAction().equals(Params.RELOAD)) {
            overridePendingTransition(0, 0);
        }

        // set toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(ScreenUtil.stylizeQueue(this, queue));
        }

        // set back button
        toolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.back_button));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else {
                    startActivity(new Intent(StatsActivity.this, HomeActivity.class));
                    finish();
                }
            }
        });

        // set tab layout
        tabLayout = (TabLayout) findViewById(R.id.tab_bar);
        tabLayout.addTab(tabLayout.newTab().setText("Gold"));
        tabLayout.addTab(tabLayout.newTab().setText("Offense"));
        tabLayout.addTab(tabLayout.newTab().setText("Utility"));
        tabLayout.addTab(tabLayout.newTab().setText("Vision"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setVisibility(View.GONE);

        // get message layouts and views
        TextView genMessage = (TextView) findViewById(R.id.gen_message);
        LinearLayout genMessageLayout = (LinearLayout) findViewById(R.id.layout_gen_message);
        genMessageLayout.setVisibility(View.GONE);
        LinearLayout noFriendsLayout = (LinearLayout) findViewById(R.id.layout_no_friends);
        noFriendsLayout.setVisibility(View.GONE);
        LinearLayout busyUpdatingLayout = (LinearLayout) findViewById(R.id.layout_busy_updating);
        busyUpdatingLayout.setVisibility(View.GONE);

        // get users summoner id
        final long userSummonerId = userInfo.getId(this);

        // get friends
        final FriendsList friendsList = new LocalDB().getFriendsList();

        // check conditions
        int condition = StatUtil.checkConditions(this, userSummonerId, queue, friendsList);

        switch (condition) {
            case 100: // code 100: summoner has no matches for this queue
                genMessageLayout.setVisibility(View.VISIBLE);
                genMessage.setText(getString(R.string.no_matches));
                break;

            case 200: // code 200: update job is currently running
                busyUpdatingLayout.setVisibility(View.VISIBLE);

                // initialize broadcast receiver to handle completion of update job
                updateJobListener = new UpdateJobListener();
                registerReceiver(updateJobListener, updateJobListener.getFilter());
                break;

            case 300: // code 300: summoner has no friends to compare matches to
                noFriendsLayout.setVisibility(View.VISIBLE);

                // initialize button to allow user navigate to friends activity
                Button goToFriendsActivity = (Button) findViewById(R.id.go_to_friends_activity);
                goToFriendsActivity.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(StatsActivity.this, FriendsActivity.class));
                        finish();
                    }
                });
                break;

            case 400: // code 400: none of the summoners friends have any matches for this queue
                genMessageLayout.setVisibility(View.VISIBLE);
                genMessage.setText(getString(R.string.no_friend_matches));
                break;

            case 500: // code 500: no issues, conditions are good for showing data
                // process data on separate thread
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // create map of summoner ids
                        Map<String, Long> ids = new LinkedHashMap<>();
                        ids.put(userInfo.getStylizedName(StatsActivity.this), userSummonerId);
                        for (SummonerDto friend : friendsList.getFriends()) {
                           ids.put(friend.name, friend.id);
                        }

                        // get participant stats
                        Map<String, List<ParticipantStats>> stats;
                        stats = StatUtil.getStats(ids, queue, Params.MAX_MATCHES);

                        // create list of plot titles
                        ArrayList<String> goldPlotTitles = new ArrayList<>();
                        ArrayList<String> offensePlotTitles = new ArrayList<>();
                        ArrayList<String> utilityPlotTitles = new ArrayList<>();
                        ArrayList<String> visionPlotTitles = new ArrayList<>();
                        offensePlotTitles.add("Total Damage To Champions");
                        offensePlotTitles.add("Kills");
                        offensePlotTitles.add("Killing Sprees");
                        offensePlotTitles.add("Largest Killing Spree");
                        utilityPlotTitles.add("Assists");
                        utilityPlotTitles.add("Damage Taken Per Death");
                        visionPlotTitles.add("Vision Wards Bought");
                        visionPlotTitles.add("Wards Placed");
                        visionPlotTitles.add("Wards Killed");

                        // get game stats
                        List<Map<String, long[]>> gameStatsLong = new ArrayList<>();
                        gameStatsLong.add(StatUtil.totalDamageToChampions(stats));
                        gameStatsLong.add(StatUtil.kills(stats));
                        gameStatsLong.add(StatUtil.killingSprees(stats));
                        gameStatsLong.add(StatUtil.largestKillingSpree(stats));
                        gameStatsLong.add(StatUtil.assists(stats));
                        gameStatsLong.add(StatUtil.damageTakenPerDeath(stats));
                        gameStatsLong.add(StatUtil.visionWardsBought(stats));
                        gameStatsLong.add(StatUtil.wardsPlaced(stats));
                        gameStatsLong.add(StatUtil.wardsKilled(stats));

                        // create number arrays
                        List<Map<String, Number[]>> gameStatsNumber = new ArrayList<>();
                        for (int i=0; i<gameStatsLong.size(); i++){
                            gameStatsNumber.add(StatUtil.createNumberArray(gameStatsLong.get(i)));
                        }

                        // separate by type (0-6 offense, 7-8 utility, 9-11 vision)
                        List<Map<String, Number[]>> goldPlotData = new ArrayList<>();
                        List<Map<String, Number[]>> offensePlotData = gameStatsNumber.subList(0,4);
                        List<Map<String, Number[]>> utilityPlotData = gameStatsNumber.subList(4,6);
                        List<Map<String, Number[]>> visionPlotData = gameStatsNumber.subList(6,9);

                        // serialize the plot data
                        Gson gson = new Gson();
                        Type plotDataType = new TypeToken<List<Map<String, Number[]>>>(){}.getType();
                        String goldPlotDataJson = gson.toJson(goldPlotData, plotDataType);
                        String offensePlotDataJson = gson.toJson(offensePlotData, plotDataType);
                        String utilityPlotDataJson = gson.toJson(utilityPlotData, plotDataType);
                        String visionPlotDataJson = gson.toJson(visionPlotData, plotDataType);

                        // create list of names
                        ArrayList<String> names = new ArrayList<>(gameStatsNumber.get(0).keySet());

                        // create tab bundles
                        Bundle goldTabBundle = new Bundle();
                        goldTabBundle.putString("plot_data", goldPlotDataJson);
                        goldTabBundle.putStringArrayList("plot_titles", goldPlotTitles);
                        goldTabBundle.putStringArrayList("names", names);

                        Bundle offenseTabBundle = new Bundle();
                        offenseTabBundle.putString("plot_data", offensePlotDataJson);
                        offenseTabBundle.putStringArrayList("plot_titles", offensePlotTitles);
                        offenseTabBundle.putStringArrayList("names", names);

                        Bundle utilityTabBundle = new Bundle();
                        utilityTabBundle.putString("plot_data", utilityPlotDataJson);
                        utilityTabBundle.putStringArrayList("plot_titles", utilityPlotTitles);
                        utilityTabBundle.putStringArrayList("names", names);

                        Bundle visionTabBundle = new Bundle();
                        visionTabBundle.putString("plot_data", visionPlotDataJson);
                        visionTabBundle.putStringArrayList("plot_titles", visionPlotTitles);
                        visionTabBundle.putStringArrayList("names", names);

                        // add bundles to plot data
                        plotData = new LinkedHashMap<>();
                        plotData.put("gold", goldTabBundle);
                        plotData.put("offense", offenseTabBundle);
                        plotData.put("utility", utilityTabBundle);
                        plotData.put("vision", visionTabBundle);

                        Message msg = new Message();
                        msg.arg1 = 75;
                        handler.sendMessage(msg);
                    }
                }).start();
                break;
        }
    }

    // handler used to populate views once background thread is done processing
    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.arg1 == 75) {
                tabLayout.setVisibility(View.VISIBLE);

                // populate the activity
                int numberOfTabs = tabLayout.getTabCount();
                FragmentManager fM = getSupportFragmentManager();
                StatsPagerAdapter statsPagerAdapter = new StatsPagerAdapter(fM, numberOfTabs, plotData);
                final ViewPager viewPager = (ViewPager) findViewById(R.id.stats_view_pager);
                viewPager.setAdapter(statsPagerAdapter);
                viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
                tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        viewPager.setCurrentItem(tab.getPosition());
                    }

                    @Override
                    public void onTabUnselected(TabLayout.Tab tab) {
                    }

                    @Override
                    public void onTabReselected(TabLayout.Tab tab) {
                    }
                });
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (updateJobListener != null) {
            unregisterReceiver(updateJobListener);
        }
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
}
