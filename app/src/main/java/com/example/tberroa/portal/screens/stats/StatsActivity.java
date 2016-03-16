package com.example.tberroa.portal.screens.stats;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.example.tberroa.portal.R;
import com.example.tberroa.portal.data.LocalDB;
import com.example.tberroa.portal.models.summoner.FriendsList;
import com.example.tberroa.portal.screens.BaseActivity;
import com.example.tberroa.portal.screens.ScreenUtil;
import com.example.tberroa.portal.screens.friends.FriendsActivity;
import com.example.tberroa.portal.data.Params;
import com.example.tberroa.portal.data.UserInfo;
import com.example.tberroa.portal.screens.home.HomeActivity;
import com.example.tberroa.portal.models.match.ParticipantStats;
import com.example.tberroa.portal.updater.UpdateJobListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class StatsActivity extends BaseActivity {

    private UpdateJobListener updateJobListener;
    private RelativeLayout plotLayout;
    private LinearLayout legendLayout;
    private LinearLayout renderingPlotsLayout;
    private List<TextView> nameViews;
    private List<ImageView> colorViews;
    private Set<String> friendsWithStats;
    private UserInfo userInfo;
    // handler used to populate views once background thread is done processing
    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.arg1 == 75) {
                renderingPlotsLayout.setVisibility(View.GONE);
                plotLayout.setVisibility(View.VISIBLE);
                legendLayout.setVisibility(View.VISIBLE);

                // construct legend, user first then friends
                nameViews.get(0).setText(userInfo.getStylizedName(StatsActivity.this));
                nameViews.get(0).setVisibility(View.VISIBLE);
                colorViews.get(0).setImageResource(R.color.series_blue);
                colorViews.get(0).setVisibility(View.VISIBLE);
                int i = 1;
                for (String name : friendsWithStats) {
                    nameViews.get(i).setText(name);
                    nameViews.get(i).setVisibility(View.VISIBLE);

                    switch (i) {
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
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);
        userInfo = new UserInfo();

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

        // get plot related layouts and views
        plotLayout = (RelativeLayout) findViewById(R.id.plot_layout);
        plotLayout.setVisibility(View.GONE);
        legendLayout = (LinearLayout) findViewById(R.id.legend);
        legendLayout.setVisibility(View.GONE);
        renderingPlotsLayout = (LinearLayout) findViewById(R.id.layout_rendering_plots);
        renderingPlotsLayout.setVisibility(View.GONE);
        nameViews = getLegendNames();
        colorViews = getLegendColors();

        // get message layouts and views
        TextView genMessage = (TextView) findViewById(R.id.gen_message);
        LinearLayout genMessageLayout = (LinearLayout) findViewById(R.id.layout_gen_message);
        genMessageLayout.setVisibility(View.GONE);
        LinearLayout noFriendsLayout = (LinearLayout) findViewById(R.id.layout_no_friends);
        noFriendsLayout.setVisibility(View.GONE);
        LinearLayout busyUpdatingLayout = (LinearLayout) findViewById(R.id.layout_busy_updating);
        busyUpdatingLayout.setVisibility(View.GONE);

        // get summoner id
        final long summonerId = userInfo.getId(this);
        Log.d(Params.TAG_DEBUG, "@StatActivity: summoner id is " + Long.toString(summonerId));

        // get friends
        final FriendsList friendsList = new LocalDB().getFriendsList();

        // check conditions
        int condition = StatUtil.checkConditions(this, summonerId, queue, friendsList);

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
                renderingPlotsLayout.setVisibility(View.VISIBLE);

                // process plot data on separate thread
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        int maxMatches = 10; // this can be changed by user input in future

                        // get summoner stats
                        List<ParticipantStats> summonerStats;
                        summonerStats = StatUtil.getStats(summonerId, queue, maxMatches);

                        // get friend stats
                        Map<String, List<ParticipantStats>> friendStats;
                        friendStats = StatUtil.getFriendStats(friendsList, queue, maxMatches);
                        Type friendStatsType = new TypeToken<Map<String, List<ParticipantStats>>>() {
                        }.getType();
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

                        // create data array for user
                        Number[] sNumbers = new Number[sWardsPlaced.length];
                        Arrays.fill(sNumbers, null);
                        for (int i = 0; i < sWardsPlaced.length; i++) {
                            sNumbers[i] = sWardsPlaced[i];
                        }

                        // create data array for friends
                        Map<String, Number[]> fNumbers = new HashMap<>();
                        for (Map.Entry<String, long[]> friend : fWardsPlaced.entrySet()) {
                            Number[] array = new Number[maxMatches];
                            Arrays.fill(array, null);
                            fNumbers.put(friend.getKey(), array);
                            for (int i = 0; i < friend.getValue().length; i++) {
                                fNumbers.get(friend.getKey())[i] = friend.getValue()[i];
                            }
                        }

                        // create a set of friends who have stats to display
                        friendsWithStats = new HashSet<>();
                        for (Map.Entry<String, Number[]> friend : fNumbers.entrySet()) {
                            friendsWithStats.add(friend.getKey());
                        }

                        // construct XYSeries
                        List<SimpleXYSeries> series = StatUtil.createXYSeries(friendsWithStats, sNumbers, fNumbers);

                        // create plot
                        XYPlot plot = (XYPlot) findViewById(R.id.plot);
                        StatUtil.createPlot(StatsActivity.this, plot, series);

                        // set title
                        TextView plotTitle = (TextView) findViewById(R.id.plot_title);
                        plotTitle.setText(R.string.wards_placed_per_game);

                        Message msg = new Message();
                        msg.arg1 = 75;
                        handler.sendMessage(msg);
                    }
                }).start();
                break;
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
