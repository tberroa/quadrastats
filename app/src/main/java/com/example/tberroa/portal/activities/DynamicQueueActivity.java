package com.example.tberroa.portal.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.example.tberroa.portal.R;
import com.example.tberroa.portal.data.Friends;
import com.example.tberroa.portal.data.Params;
import com.example.tberroa.portal.data.SummonerInfo;
import com.example.tberroa.portal.data.UpdateJobFlags;
import com.example.tberroa.portal.helpers.StatUtil;
import com.example.tberroa.portal.models.match.ParticipantStats;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DynamicQueueActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dynamic_queue);
        SummonerInfo summonerInfo = new SummonerInfo();
        long summonerId = summonerInfo.getId(this);

        // set toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.dynamic_queue);
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
                    startActivity(new Intent(DynamicQueueActivity.this, HomeActivity.class));
                    finish();
                }
            }
        });

        // get the status of the background update job
        boolean isRunning = new UpdateJobFlags().isRunning(this);

        // only populate activity if the job is not running
        if (!isRunning) {
            // get friends
            Set<String> friendNames = new Friends().getNames(this);
            int numberOfFriends = friendNames.size();

            // only populate activity if the summoner has friends
            if (numberOfFriends > 0) {
                int loadedMatches = 5; // this can be changed by user input in future

                // get friend stats
                Map<String, List<ParticipantStats>> friendStats;
                friendStats = StatUtil.getFriendStats(friendNames, Params.TEAM_BUILDER_DRAFT_RANKED_5, loadedMatches);

                // get summoner stats
                List<ParticipantStats> summonerStats;
                summonerStats = StatUtil.getStats(summonerId, Params.TEAM_BUILDER_DRAFT_RANKED_5, loadedMatches);

                // get wards placed per game
                long[] sWardsPlaced = new long[loadedMatches];
                for (int i = 0; i < loadedMatches; i++) {
                    sWardsPlaced[i] = summonerStats.get(i).wardsPlaced;
                }

                // get friend wards placed per game
                Map<String, long[]> fWardsPlaced = new HashMap<>();
                for (String name : friendNames){
                    fWardsPlaced.put(name, new long[loadedMatches]);
                    if (friendStats.get(name).size() > 0){
                        for (int i=0; i < loadedMatches; i++) {
                            fWardsPlaced.get(name)[i] = friendStats.get(name).get(i).wardsPlaced;
                        }
                    }
                }

                // create data array for summoner
                Number[] sNumbers =
                        {sWardsPlaced[0], sWardsPlaced[1], sWardsPlaced[2], sWardsPlaced[3], sWardsPlaced[4]};

                // create data array for friends
                Map<String, Number[]> fNumbers = new HashMap<>();
                for (String name : friendNames){
                    fNumbers.put(name, new Number[loadedMatches]);
                    for (int i=0; i<loadedMatches; i++){
                        fNumbers.get(name)[i] = fWardsPlaced.get(name)[i];
                    }
                }

                // construct XYSeries
                List<XYSeries> series = StatUtil.constructXYSeries(friendNames, sNumbers, fNumbers);

                // initialize our XYPlot reference:
                XYPlot plot = (XYPlot) findViewById(R.id.plot);

                // create plot
                StatUtil.createPlot(this, plot,series);

                // set title
                TextView plotTitle = (TextView) findViewById(R.id.plot_title);
                plotTitle.setText(R.string.wards_placed_per_game);

            } else {
                Log.d(Params.TAG_DEBUG, "@DynamicQueueActivity: plot not shown, no friends found");
            }
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
