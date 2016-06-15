package com.example.tberroa.portal.screens.stats.recent;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.ActiveAndroid;
import com.example.tberroa.portal.R;
import com.example.tberroa.portal.data.LocalDB;
import com.example.tberroa.portal.models.ModelUtil;
import com.example.tberroa.portal.models.requests.ReqMatchStats;
import com.example.tberroa.portal.models.summoner.Summoner;
import com.example.tberroa.portal.network.Http;
import com.example.tberroa.portal.screens.BaseActivity;
import com.example.tberroa.portal.screens.friends.FriendsActivity;
import com.example.tberroa.portal.data.Params;
import com.example.tberroa.portal.data.UserInfo;
import com.example.tberroa.portal.screens.home.HomeActivity;
import com.example.tberroa.portal.models.stats.MatchStats;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

public class RecentActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {

    final private Map<String, List<List<Number>>> aggregateData = new HashMap<>();
    final private ArrayList<String> plotTitles = new ArrayList<>();
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private LinearLayout genMessageLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String keys;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        // no animation if starting activity as a reload
        if (getIntent().getAction() != null && getIntent().getAction().equals(Params.RELOAD)) {
            overridePendingTransition(0, 0);
        }

        // set toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.recent_games);
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

        // initialize swipe refresh layout
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);

        // get message layouts and views
        TextView genMessage = (TextView) findViewById(R.id.gen_message);
        genMessageLayout = (LinearLayout) findViewById(R.id.layout_gen_message);
        genMessageLayout.setVisibility(View.GONE);
        LinearLayout noFriendsLayout = (LinearLayout) findViewById(R.id.layout_no_friends);
        noFriendsLayout.setVisibility(View.GONE);

        // initialize the tab layout
        tabLayout = (TabLayout) findViewById(R.id.tab_bar);
        tabLayout.addTab(tabLayout.newTab().setText("Income"));
        tabLayout.addTab(tabLayout.newTab().setText("Offense"));
        tabLayout.addTab(tabLayout.newTab().setText("Utility"));
        tabLayout.addTab(tabLayout.newTab().setText("Vision"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setVisibility(View.GONE);

        // create list of plot titles
        plotTitles.add("CS At 10");
        plotTitles.add("CS Differential At 10");
        plotTitles.add("CS Per Minute");
        plotTitles.add("Gold Per Minute");
        plotTitles.add("Damage Per Minute");
        plotTitles.add("Kills");
        plotTitles.add("KDA");
        plotTitles.add("Kill Participation");
        plotTitles.add("Duration of CC Dealt");
        plotTitles.add("Vision Wards Bought");
        plotTitles.add("Wards Placed");
        plotTitles.add("Wards Killed");

        // set up view pager and fragments
        int numOfTabs = tabLayout.getTabCount();
        FragmentManager fM = getSupportFragmentManager();
        RecentPagerAdapter pagerAdapter = new RecentPagerAdapter(fM, numOfTabs, plotTitles, aggregateData);
        viewPager = (ViewPager) findViewById(R.id.stats_view_pager);
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout){
            @Override
            public void onPageScrollStateChanged( int state ) {
                swipeRefreshLayout.setEnabled(state == ViewPager.SCROLL_STATE_IDLE);
            }
        });
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

        // get user
        LocalDB localDB = new LocalDB();
        Summoner user = localDB.getSummonerById(new UserInfo().getId(this));

        // initialize string of keys
        keys = user.key + ",";

        // get user's friends
        if (!user.friends.equals("")) {
            keys += user.friends;
        } else { // user has no friends
            noFriendsLayout.setVisibility(View.VISIBLE);
            Button goToFriendsActivity = (Button) findViewById(R.id.go_to_friends_activity);
            goToFriendsActivity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(RecentActivity.this, FriendsActivity.class));
                    finish();
                }
            });
            return;
        }

        // load match stats from local database
        List<String> keysList = new ArrayList<>(Arrays.asList(keys.split(",")));
        List<Summoner> summoners = new LocalDB().getSummonersByKeys(keysList);
        List<Long> ids = new ArrayList<>();
        for (Summoner summoner : summoners){
            ids.add(summoner.summoner_id);
        }
        List<MatchStats> matchStatsList = new LocalDB().getMatchStats(ids);

        // populate the activity
        if (!matchStatsList.isEmpty()){
            populateActivity(matchStatsList);
        } else{
            // show message
            genMessage.setText(R.string.no_stats);
            genMessageLayout.setVisibility(View.VISIBLE);
        }
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
        return true;
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
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        new RequestMatchStats().execute(keys);
    }

    private void createLegend(LinkedHashSet<String> names) {
        List<TextView> nameViews = getLegendNames();
        List<ImageView> colorViews = getLegendColors();

        int i = 0;
        for (String name : names) {
            nameViews.get(i).setText(name);
            nameViews.get(i).setVisibility(View.VISIBLE);

            switch (i % 8) {
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

        GridLayout legendLayout = (GridLayout) findViewById(R.id.legend);
        legendLayout.setVisibility(View.VISIBLE);
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

    private void populateActivity (List<MatchStats> matchStatsList) {
        // Before the data can be presented using the Android Plot library, it needs to be organized.
        // All the data will be put into one map object. The map key is the summoner name and the value
        // is a list of of lists where each list is a list of data points corresponding to one stat plot.
        // Example: Key: Frosiph | Value: list[0] = csAtTen, list[1] = csDiffAtTen, etc.

        genMessageLayout.setVisibility(View.GONE);
        tabLayout.setVisibility(View.VISIBLE);

        // total number of plots, used for initialization later
        int totalPlots = 12;

        // declare set of summoner names (used for the legend)
        LinkedHashSet<String> names = new LinkedHashSet<>();

        // clear the map which holds the data for each summoner
        aggregateData.clear();

        // populate the map
        for (MatchStats matchStats : matchStatsList) {
            String summoner = matchStats.summoner_name;
            names.add(summoner);

            List<List<Number>> summonerData = aggregateData.get(summoner);
            if (summonerData == null) {
                summonerData = new ArrayList<>();
                for (int i = 0; i < totalPlots; i++){
                    summonerData.add(new ArrayList<Number>());
                }
            }

            if (matchStats.cs_at_ten != null) {
                summonerData.get(0).add(matchStats.cs_at_ten);
            }
            if (matchStats.cs_diff_at_ten != null) {
                summonerData.get(1).add(matchStats.cs_diff_at_ten);
            }
            if (matchStats.cs_per_min != null) {
                summonerData.get(2).add(matchStats.cs_per_min);
            }
            if (matchStats.gold_per_min != null) {
                summonerData.get(3).add(matchStats.gold_per_min);
            }
            if (matchStats.dmg_per_min != null) {
                summonerData.get(4).add(matchStats.dmg_per_min);
            }
            if (matchStats.dmg_per_min != null) {
                summonerData.get(5).add(matchStats.kills);
            }
            if (matchStats.dmg_per_min != null) {
                summonerData.get(6).add(matchStats.kda);
            }
            if (matchStats.dmg_per_min != null) {
                summonerData.get(7).add(matchStats.kill_participation);
            }
            if (matchStats.dmg_per_min != null) {
                summonerData.get(8).add(matchStats.total_time_crowd_control_dealt);
            }
            if (matchStats.dmg_per_min != null) {
                summonerData.get(9).add(matchStats.vision_wards_bought_in_game);
            }
            if (matchStats.dmg_per_min != null) {
                summonerData.get(10).add(matchStats.wards_placed);
            }
            if (matchStats.dmg_per_min != null) {
                summonerData.get(11).add(matchStats.wards_killed);
            }

            aggregateData.put(summoner, summonerData);
        }

        // update adapter
        int numOfTabs = tabLayout.getTabCount();
        FragmentManager fM = getSupportFragmentManager();
        RecentPagerAdapter pagerAdapter = new RecentPagerAdapter(fM, numOfTabs, plotTitles, aggregateData);
        viewPager.setAdapter(pagerAdapter);

        // create the legend
        createLegend(names);
    }

    class RequestMatchStats extends AsyncTask<String, Void, Void> {

        private String postResponse;

        @Override
        protected Void doInBackground(String... params) {
            // create the request object
            List<String> keys = new ArrayList<>(Arrays.asList(params[0].split(",")));
            ReqMatchStats request = new ReqMatchStats();
            request.keys = keys;

            // make the request
            try {
                String url = Params.BURL_MATCH_STATS;
                postResponse = new Http().post(url, ModelUtil.toJson(request, ReqMatchStats.class));
            } catch (java.io.IOException e) {
                Log.e(Params.TAG_EXCEPTIONS, "@RecentActivity: " + e.getMessage());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void param) {
            if (postResponse.contains("champ_level")) {
                // get the match stats objects
                Type type =  new TypeToken<List<MatchStats>>() {}.getType();
                List<MatchStats> matchStatsList = ModelUtil.fromJsonList(postResponse, type);

                // save the match stats
                ActiveAndroid.beginTransaction();
                try{
                    for (MatchStats matchStats : matchStatsList){
                        matchStats.save();
                    }
                    ActiveAndroid.setTransactionSuccessful();
                }finally {
                    ActiveAndroid.endTransaction();
                }

                // populate the activity
                populateActivity(matchStatsList);
            } else { // display error
                Toast.makeText(RecentActivity.this, postResponse, Toast.LENGTH_SHORT).show();
            }
            swipeRefreshLayout.setRefreshing(false);
        }
    }
}
