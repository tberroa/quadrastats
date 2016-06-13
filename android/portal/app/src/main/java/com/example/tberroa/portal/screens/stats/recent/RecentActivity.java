package com.example.tberroa.portal.screens.stats.recent;

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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.tberroa.portal.R;
import com.example.tberroa.portal.data.LocalDB;
import com.example.tberroa.portal.models.match.ParticipantTimeline;
import com.example.tberroa.portal.models.summoner.Summoner;
import com.example.tberroa.portal.screens.BaseActivity;
import com.example.tberroa.portal.screens.ScreenUtil;
import com.example.tberroa.portal.screens.friends.FriendsActivity;
import com.example.tberroa.portal.data.Params;
import com.example.tberroa.portal.data.UserInfo;
import com.example.tberroa.portal.screens.home.HomeActivity;
import com.example.tberroa.portal.models.stats.MatchStats;
import com.example.tberroa.portal.screens.stats.SelectQueueDialog;
import com.example.tberroa.portal.updater.UpdateJobListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RecentActivity extends BaseActivity {

    private UpdateJobListener updateJobListener;
    private TabLayout tabLayout;
    private GridLayout legendLayout;
    private LinearLayout busyCalculating;
    private Map<String, Bundle> plotData;
    private ArrayList<String> names;

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
                    startActivity(new Intent(RecentActivity.this, HomeActivity.class));
                    finish();
                }
            }
        });

        // set tab layout
        tabLayout = (TabLayout) findViewById(R.id.tab_bar);
        tabLayout.addTab(tabLayout.newTab().setText("Income"));
        tabLayout.addTab(tabLayout.newTab().setText("Offense"));
        tabLayout.addTab(tabLayout.newTab().setText("Utility"));
        tabLayout.addTab(tabLayout.newTab().setText("Vision"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setVisibility(View.GONE);

        // get legend layout
        legendLayout = (GridLayout) findViewById(R.id.legend);
        legendLayout.setVisibility(View.GONE);

        // get message layouts and views
        TextView genMessage = (TextView) findViewById(R.id.gen_message);
        LinearLayout genMessageLayout = (LinearLayout) findViewById(R.id.layout_gen_message);
        genMessageLayout.setVisibility(View.GONE);
        LinearLayout noFriendsLayout = (LinearLayout) findViewById(R.id.layout_no_friends);
        noFriendsLayout.setVisibility(View.GONE);
        LinearLayout busyUpdatingLayout = (LinearLayout) findViewById(R.id.layout_busy_updating);
        busyUpdatingLayout.setVisibility(View.GONE);
        busyCalculating = (LinearLayout) findViewById(R.id.layout_busy_calculating);
        busyCalculating.setVisibility(View.GONE);

        // get users summoner id
        final long userSummonerId = userInfo.getId(this);

        // get friends
        final FriendsList friendsList = new LocalDB().getFriendsList();

        // check conditions
        int condition = RecentUtil.checkConditions(this, userSummonerId, queue, friendsList);

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
                        startActivity(new Intent(RecentActivity.this, FriendsActivity.class));
                        finish();
                    }
                });
                break;

            case 400: // code 400: none of the summoners friends have any matches for this queue
                genMessageLayout.setVisibility(View.VISIBLE);
                genMessage.setText(getString(R.string.no_friend_matches));
                break;

            case 500: // code 500: no issues, conditions are good for showing data
                busyCalculating.setVisibility(View.VISIBLE);

                // process data on separate thread
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // create map of summoner ids
                        Map<String, Long> ids = new LinkedHashMap<>();
                        ids.put(userInfo.getName(RecentActivity.this), userSummonerId);
                        for (Summoner friend : friendsList.getFriends()) {
                           ids.put(friend.name, friend.id);
                        }

                        // get match durations
                        Map<String, long[]> matchDurations;
                        matchDurations = RecentUtil.getMatchDuration(ids, queue);

                        // get participant timeline
                        Map<String, List<ParticipantTimeline>> timeline;
                        timeline = RecentUtil.getTimeline(ids, queue);

                        // get participant stats
                        Map<String, List<MatchStats>> stats;
                        stats = RecentUtil.getStats(ids, queue);

                        // create list of plot titles
                        ArrayList<String> incomePlotTitles = new ArrayList<>();
                        ArrayList<String> offensePlotTitles = new ArrayList<>();
                        ArrayList<String> utilityPlotTitles = new ArrayList<>();
                        ArrayList<String> visionPlotTitles = new ArrayList<>();
                        incomePlotTitles.add("Gold Per Minute");
                        incomePlotTitles.add("CS At 10");
                        incomePlotTitles.add("CS Differential At 10");
                        offensePlotTitles.add("Damage Per Minute");
                        offensePlotTitles.add("Kills");
                        offensePlotTitles.add("Killing Sprees");
                        offensePlotTitles.add("Largest Killing Spree");
                        utilityPlotTitles.add("Assists");
                        utilityPlotTitles.add("Damage Taken Per Death");
                        visionPlotTitles.add("Vision Wards Bought");
                        visionPlotTitles.add("Wards Placed");
                        visionPlotTitles.add("Wards Killed");

                        // get timeline stats
                        List<Map<String, double[]>> gameTimelineStats = new ArrayList<>();
                        gameTimelineStats.add(RecentUtil.goldPerMin(matchDurations, stats));
                        gameTimelineStats.add(RecentUtil.csAtTen(timeline));
                        gameTimelineStats.add(RecentUtil.csDiffAtTen(timeline));

                        // get game stats
                        List<Map<String, long[]>> gameStatsLong = new ArrayList<>();
                        gameStatsLong.add(RecentUtil.dmgPerMin(matchDurations, stats));
                        gameStatsLong.add(RecentUtil.kills(stats));
                        gameStatsLong.add(RecentUtil.killingSprees(stats));
                        gameStatsLong.add(RecentUtil.largestKillingSpree(stats));
                        gameStatsLong.add(RecentUtil.assists(stats));
                        gameStatsLong.add(RecentUtil.damageTakenPerDeath(stats));
                        gameStatsLong.add(RecentUtil.visionWardsBought(stats));
                        gameStatsLong.add(RecentUtil.wardsPlaced(stats));
                        gameStatsLong.add(RecentUtil.wardsKilled(stats));

                        // create number arrays
                        List<Map<String, Number[]>> gameTimelineNumber = new ArrayList<>();
                        for (int i=0; i<gameTimelineStats.size(); i++){
                            gameTimelineNumber.add(RecentUtil.createNumberArrayD(gameTimelineStats.get(i)));
                        }
                        List<Map<String, Number[]>> gameStatsNumber = new ArrayList<>();
                        for (int i=0; i<gameStatsLong.size(); i++){
                            gameStatsNumber.add(RecentUtil.createNumberArrayL(gameStatsLong.get(i)));
                        }

                        // separate by type (0-6 offense, 7-8 utility, 9-11 vision)
                        List<Map<String, Number[]>> offensePlotData = gameStatsNumber.subList(0,4);
                        List<Map<String, Number[]>> utilityPlotData = gameStatsNumber.subList(4,6);
                        List<Map<String, Number[]>> visionPlotData = gameStatsNumber.subList(6,9);

                        // serialize the plot data
                        Gson gson = new Gson();
                        Type plotDataType = new TypeToken<List<Map<String, Number[]>>>(){}.getType();
                        String incomePlotDataJson = gson.toJson(gameTimelineNumber, plotDataType);
                        String offensePlotDataJson = gson.toJson(offensePlotData, plotDataType);
                        String utilityPlotDataJson = gson.toJson(utilityPlotData, plotDataType);
                        String visionPlotDataJson = gson.toJson(visionPlotData, plotDataType);

                        // create list of names
                        names = new ArrayList<>(gameStatsNumber.get(0).keySet());

                        // create tab bundles
                        Bundle incomeTabBundle = new Bundle();
                        incomeTabBundle.putString("plot_data", incomePlotDataJson);
                        incomeTabBundle.putStringArrayList("plot_titles", incomePlotTitles);

                        Bundle offenseTabBundle = new Bundle();
                        offenseTabBundle.putString("plot_data", offensePlotDataJson);
                        offenseTabBundle.putStringArrayList("plot_titles", offensePlotTitles);

                        Bundle utilityTabBundle = new Bundle();
                        utilityTabBundle.putString("plot_data", utilityPlotDataJson);
                        utilityTabBundle.putStringArrayList("plot_titles", utilityPlotTitles);

                        Bundle visionTabBundle = new Bundle();
                        visionTabBundle.putString("plot_data", visionPlotDataJson);
                        visionTabBundle.putStringArrayList("plot_titles", visionPlotTitles);

                        // add bundles to plot data
                        plotData = new LinkedHashMap<>();
                        plotData.put("income", incomeTabBundle);
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
                busyCalculating.setVisibility(View.GONE);
                tabLayout.setVisibility(View.VISIBLE);

                // populate the activity
                createLegend();
                int numberOfTabs = tabLayout.getTabCount();
                FragmentManager fM = getSupportFragmentManager();
                RecentPagerAdapter recentPagerAdapter = new RecentPagerAdapter(fM, numberOfTabs, plotData);
                final ViewPager viewPager = (ViewPager) findViewById(R.id.stats_view_pager);
                viewPager.setAdapter(recentPagerAdapter);
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

    private void createLegend() {
        List<TextView> nameViews = getLegendNames();
        List<ImageView> colorViews = getLegendColors();
        legendLayout.setVisibility(View.VISIBLE);

        // then friends
        int i = 0;
        for (String name : names) {
            nameViews.get(i).setText(name);
            nameViews.get(i).setVisibility(View.VISIBLE);

            switch (i) {
                case 0:
                    colorViews.get(i).setImageResource(R.color.series_blue);
                    break;
                case 1:
                    colorViews.get(i).setImageResource(R.color.series_green);
                    break;
                case 2:
                    colorViews.get(i).setImageResource(R.color.series_orange);
                    break;
                case 3:
                    colorViews.get(i).setImageResource(R.color.series_pink);
                    break;
                case 4:
                    colorViews.get(i).setImageResource(R.color.series_purple);
                    break;
                case 5:
                    colorViews.get(i).setImageResource(R.color.series_red);
                    break;
                case 6:
                    colorViews.get(i).setImageResource(R.color.series_sky);
                    break;
                case 7:
                    colorViews.get(i).setImageResource(R.color.series_yellow);
                    break;
            }
            colorViews.get(i).setVisibility(View.VISIBLE);
            i++;
        }
    }

    private List<TextView> getLegendNames() {
        // initialize list
        List<TextView> nameViews = new ArrayList<>();

        TextView summoner1 = (TextView) findViewById(R.id.summoner_1);
        TextView summoner2 = (TextView) findViewById(R.id.summoner_2);
        TextView summoner3 = (TextView) findViewById(R.id.summoner_3);
        TextView summoner4 = (TextView) findViewById(R.id.summoner_4);
        TextView summoner5 = (TextView) findViewById(R.id.summoner_5);
        TextView summoner6 = (TextView) findViewById(R.id.summoner_6);
        TextView summoner7 = (TextView) findViewById(R.id.summoner_7);
        TextView summoner8 = (TextView) findViewById(R.id.summoner_8);

        summoner1.setVisibility(View.GONE);
        summoner2.setVisibility(View.GONE);
        summoner3.setVisibility(View.GONE);
        summoner4.setVisibility(View.GONE);
        summoner5.setVisibility(View.GONE);
        summoner6.setVisibility(View.GONE);
        summoner7.setVisibility(View.GONE);
        summoner8.setVisibility(View.GONE);

        nameViews.add(summoner1);
        nameViews.add(summoner2);
        nameViews.add(summoner3);
        nameViews.add(summoner4);
        nameViews.add(summoner5);
        nameViews.add(summoner6);
        nameViews.add(summoner7);
        nameViews.add(summoner8);

        return nameViews;
    }

    private List<ImageView> getLegendColors() {
        // initialize list
        List<ImageView> colorViews = new ArrayList<>();

        ImageView color1 = (ImageView) findViewById(R.id.color_1);
        ImageView color2 = (ImageView) findViewById(R.id.color_2);
        ImageView color3 = (ImageView) findViewById(R.id.color_3);
        ImageView color4 = (ImageView) findViewById(R.id.color_4);
        ImageView color5 = (ImageView) findViewById(R.id.color_5);
        ImageView color6 = (ImageView) findViewById(R.id.color_6);
        ImageView color7 = (ImageView) findViewById(R.id.color_7);
        ImageView color8 = (ImageView) findViewById(R.id.color_8);

        color1.setVisibility(View.GONE);
        color2.setVisibility(View.GONE);
        color3.setVisibility(View.GONE);
        color4.setVisibility(View.GONE);
        color5.setVisibility(View.GONE);
        color6.setVisibility(View.GONE);
        color7.setVisibility(View.GONE);
        color8.setVisibility(View.GONE);

        colorViews.add(color1);
        colorViews.add(color2);
        colorViews.add(color3);
        colorViews.add(color4);
        colorViews.add(color5);
        colorViews.add(color6);
        colorViews.add(color7);
        colorViews.add(color8);

        return colorViews;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.recent_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.select_queue:

                SelectQueueDialog selectQueueDialog = new SelectQueueDialog(this);
                selectQueueDialog.setCancelable(true);
                selectQueueDialog.show();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

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
