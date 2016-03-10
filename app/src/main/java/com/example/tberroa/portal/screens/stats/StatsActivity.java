package com.example.tberroa.portal.screens.stats;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
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

import java.util.HashMap;
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

        // get friends
        Set<String> friendNames = new FriendsInfo().getNames(this);

        // check conditions
        int condition = StatUtil.checkConditions(this, summonerId, queue, friendNames);

        switch(condition){
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

                int loadedMatches = 5; // this can be changed by user input in future

                // get friend stats
                Map<String, List<ParticipantStats>> friendStats;
                friendStats = StatUtil.getFriendStats(friendNames, queue, loadedMatches);

                // get summoner stats
                List<ParticipantStats> summonerStats;
                summonerStats = StatUtil.getStats(summonerId, queue, loadedMatches);

                // get wards placed per game
                long[] sWardsPlaced = new long[loadedMatches];
                for (int i = 0; i < loadedMatches; i++) {
                    if (summonerStats.get(i) != null){
                        sWardsPlaced[i] = summonerStats.get(i).wardsPlaced;
                    }
                }

                // get friend wards placed per game
                Map<String, long[]> fWardsPlaced = new HashMap<>();
                for (String name : friendNames) {
                    fWardsPlaced.put(name, new long[loadedMatches]);
                    if (friendStats.get(name).size() > 0) {
                        for (int i = 0; i < loadedMatches; i++) {
                            if (friendStats.get(name).get(i) != null){
                                fWardsPlaced.get(name)[i] = friendStats.get(name).get(i).wardsPlaced;
                            }
                        }
                    }
                }

                // create data array for summoner
                Number[] sNumbers =
                        {sWardsPlaced[0], sWardsPlaced[1], sWardsPlaced[2], sWardsPlaced[3], sWardsPlaced[4]};

                // create data array for friends
                Map<String, Number[]> fNumbers = new HashMap<>();
                for (String name : friendNames) {
                    fNumbers.put(name, new Number[loadedMatches]);
                    for (int i = 0; i < loadedMatches; i++) {
                        fNumbers.get(name)[i] = fWardsPlaced.get(name)[i];
                    }
                }

                // construct XYSeries
                List<XYSeries> series = StatUtil.constructXYSeries(friendNames, sNumbers, fNumbers);

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
