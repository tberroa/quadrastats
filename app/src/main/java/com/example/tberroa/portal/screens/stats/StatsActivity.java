package com.example.tberroa.portal.screens.stats;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.example.tberroa.portal.R;
import com.example.tberroa.portal.screens.BaseActivity;
import com.example.tberroa.portal.screens.ScreenUtil;
import com.example.tberroa.portal.screens.friends.FriendsActivity;
import com.example.tberroa.portal.screens.friends.FriendsInfo;
import com.example.tberroa.portal.data.Params;
import com.example.tberroa.portal.data.SummonerInfo;
import com.example.tberroa.portal.screens.home.HomeActivity;
import com.example.tberroa.portal.models.match.ParticipantStats;
import com.example.tberroa.portal.updater.UpdateJobListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class StatsActivity extends BaseActivity {

    private UpdateJobListener updateJobListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        // get queue
        String queue = getIntent().getStringExtra("queue");

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

        // get layouts
        LinearLayout genMessageLayout = (LinearLayout) findViewById(R.id.layout_gen_message);
        RelativeLayout plotLayout = (RelativeLayout) findViewById(R.id.plot_layout);
        LinearLayout noFriendsLayout = (LinearLayout) findViewById(R.id.layout_no_friends);
        LinearLayout busyUpdatingLayout = (LinearLayout) findViewById(R.id.layout_busy_updating);
        TextView genMessage = (TextView) findViewById(R.id.gen_message);
        genMessageLayout.setVisibility(View.GONE);
        plotLayout.setVisibility(View.GONE);
        noFriendsLayout.setVisibility(View.GONE);
        busyUpdatingLayout.setVisibility(View.GONE);

        // get summoner id
        long summonerId = new SummonerInfo().getId(this);
        Log.d(Params.TAG_DEBUG, "@StatActivity: summoner id is " + Long.toString(summonerId));

        // get friends
        Set<String> friendNames = new FriendsInfo().getNames(this);

        // check conditions
        int condition = StatUtil.checkConditions(this, summonerId, queue, friendNames);

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
                plotLayout.setVisibility(View.VISIBLE);

                int maxMatches = 10; // this can be changed by user input in future

                // get summoner stats
                List<ParticipantStats> summonerStats;
                summonerStats = StatUtil.getStats(summonerId, queue, maxMatches);

                // get friend stats
                Map<String, List<ParticipantStats>> friendStats;
                friendStats = StatUtil.getFriendStats(friendNames, queue, maxMatches);
                Type friendStatsType = new TypeToken<Map<String, List<ParticipantStats>>>(){}.getType();
                Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
                String friendStatsJson = gson.toJson(friendStats, friendStatsType);
                Log.d(Params.TAG_DEBUG, "@StatActivity: friendStats is " + friendStatsJson);

                // get wards placed per game
                long[] sWardsPlaced = new long[summonerStats.size()];
                for (int i = 0; i < summonerStats.size(); i++) {
                    if (summonerStats.get(i) != null) {
                        sWardsPlaced[i] = summonerStats.get(i).wardsPlaced;
                    }
                }

                // get friend wards placed per game
                Map<String, long[]> fWardsPlaced = new HashMap<>();
                for (Map.Entry<String, List<ParticipantStats>> friend : friendStats.entrySet()) {
                    fWardsPlaced.put(friend.getKey(), new long[friend.getValue().size()]);
                    for (int i = 0; i < friend.getValue().size(); i++) {
                        if (friend.getValue().get(i) != null) {
                            fWardsPlaced.get(friend.getKey())[i] = friend.getValue().get(i).wardsPlaced;
                        }
                    }
                }

                // create data array for summoner
                Number[] sNumbers = new Number[sWardsPlaced.length];
                for (int i = 0; i < sWardsPlaced.length; i++) {
                    sNumbers[i] = sWardsPlaced[i];
                }

                // create data array for friends
                Map<String, Number[]> fNumbers = new HashMap<>();
                for (Map.Entry<String, long[]> friend : fWardsPlaced.entrySet()) {
                    Number[] array = new Number[maxMatches];
                    Arrays.fill(array, 0);
                    fNumbers.put(friend.getKey(), array);
                    for (int i = 0; i < friend.getValue().length; i++) {
                        fNumbers.get(friend.getKey())[i] = friend.getValue()[i];
                    }
                }

                // create a set of friends who have stats to display
                Set<String> friendsWithStats = new HashSet<>();
                for (Map.Entry<String, Number[]> friend : fNumbers.entrySet()){
                    friendsWithStats.add(friend.getKey());
                }

                // construct XYSeries
                List<XYSeries> series = StatUtil.constructXYSeries(friendsWithStats, sNumbers, fNumbers);

                // initialize our XYPlot reference:
                XYPlot plot = (XYPlot) findViewById(R.id.plot);

                // create plot
                StatUtil.createPlot(this, plot, series);

                // set title
                TextView plotTitle = (TextView) findViewById(R.id.plot_title);
                plotTitle.setText(R.string.wards_placed_per_game);
                break;
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
