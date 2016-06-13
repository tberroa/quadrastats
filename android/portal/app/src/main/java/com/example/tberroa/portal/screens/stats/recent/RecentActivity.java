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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

public class RecentActivity extends BaseActivity {

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

        // get message layouts and views
        LinearLayout noFriendsLayout = (LinearLayout) findViewById(R.id.layout_no_friends);
        noFriendsLayout.setVisibility(View.GONE);

        // get user
        LocalDB localDB = new LocalDB();
        Summoner user = localDB.getSummonerById(new UserInfo().getId(this));

        // initialize string of keys
        String keys = user.key + ",";

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

        // get the match stats for user and friends
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

    void updatePlotMap(Map<String, List<Number>> map, String name, Number stat) {
        List<Number> data = map.get(name);
        if (data == null) {
            data = new ArrayList<>();
            data.add(stat);
            map.put(name, data);
        } else {
            data.add(stat);
            map.put(name, data);
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

    class RequestMatchStats extends AsyncTask<String, Void, Void> {

        private String postResponse;

        @Override
        protected Void doInBackground(String... params) {
            // create the request object
            List<String> keys = new ArrayList<>(Arrays.asList(params[0].split(",")));
            ReqMatchStats request = new ReqMatchStats();
            request.keys = keys;

            // debugging
            Log.e(Params.TAG_EXCEPTIONS, "@RecentActivity: request is " + ModelUtil.toJson(request, ReqMatchStats.class));

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
                // After receiving the raw data, this is where the data is organized to be presented using
                // the Android Plot library. Data will be organized by summoner and by plot. One map object
                // corresponds to one plot.

                // declare set of summoner names (used for the legend)
                LinkedHashSet<String> names = new LinkedHashSet<>();

                // get the match stats objects
                Type type =  new TypeToken<List<MatchStats>>() {}.getType();
                List<MatchStats> matchStatsList = ModelUtil.fromJsonList(postResponse, type);

                // declare the map for each plot
                Map<String, List<Number>> csAtTen = new HashMap<>();
                Map<String, List<Number>> csDiffAtTen = new HashMap<>();
                Map<String, List<Number>> csPerMin = new HashMap<>();
                Map<String, List<Number>> goldPerMin = new HashMap<>();

                Map<String, List<Number>> dmgPerMin = new HashMap<>();
                Map<String, List<Number>> kills = new HashMap<>();

                Map<String, List<Number>> kda = new HashMap<>();
                Map<String, List<Number>> killParticipation = new HashMap<>();
                Map<String, List<Number>> ccDealt = new HashMap<>();

                Map<String, List<Number>> visionWardsBought = new HashMap<>();
                Map<String, List<Number>> wardsPlaced = new HashMap<>();
                Map<String, List<Number>> wardsKilled = new HashMap<>();

                // populate the map for each stat
                for (MatchStats matchStats : matchStatsList) {
                    String summoner = matchStats.summoner_name;
                    names.add(summoner);

                    updatePlotMap(csAtTen, summoner, matchStats.cs_at_ten);
                    updatePlotMap(csDiffAtTen, summoner, matchStats.cs_diff_at_ten);
                    updatePlotMap(csPerMin, summoner, matchStats.cs_per_min);
                    updatePlotMap(goldPerMin, summoner, matchStats.gold_per_min);

                    updatePlotMap(dmgPerMin, summoner, matchStats.dmg_per_min);
                    updatePlotMap(kills, summoner, matchStats.kills);

                    updatePlotMap(kda, summoner, matchStats.kda);
                    updatePlotMap(killParticipation, summoner, matchStats.kill_participation);
                    updatePlotMap(ccDealt, summoner, matchStats.total_time_crowd_control_dealt);

                    updatePlotMap(visionWardsBought, summoner, matchStats.vision_wards_bought_in_game);
                    updatePlotMap(wardsPlaced, summoner, matchStats.wards_placed);
                    updatePlotMap(wardsKilled, summoner, matchStats.wards_killed);
                }

                // combine the different maps by tab
                List<Map<String, List<Number>>> incomePlots = new ArrayList<>();
                incomePlots.add(csAtTen);
                incomePlots.add(csDiffAtTen);
                incomePlots.add(csPerMin);
                incomePlots.add(goldPerMin);

                List<Map<String, List<Number>>> offensePlots = new ArrayList<>();
                offensePlots.add(dmgPerMin);
                offensePlots.add(kills);

                List<Map<String, List<Number>>> utilityPlots = new ArrayList<>();
                utilityPlots.add(kda);
                utilityPlots.add(killParticipation);
                utilityPlots.add(ccDealt);

                List<Map<String, List<Number>>> visionPlots = new ArrayList<>();
                visionPlots.add(visionWardsBought);
                visionPlots.add(wardsPlaced);
                visionPlots.add(wardsKilled);

                // create list of plot titles
                ArrayList<String> incomePlotTitles = new ArrayList<>();
                ArrayList<String> offensePlotTitles = new ArrayList<>();
                ArrayList<String> utilityPlotTitles = new ArrayList<>();
                ArrayList<String> visionPlotTitles = new ArrayList<>();

                incomePlotTitles.add("CS At 10");
                incomePlotTitles.add("CS Differential At 10");
                incomePlotTitles.add("CS Per Minute");
                incomePlotTitles.add("Gold Per Minute");

                offensePlotTitles.add("Kills");
                offensePlotTitles.add("Damage Per Minute");

                utilityPlotTitles.add("KDA");
                utilityPlotTitles.add("Kill Participation");
                utilityPlotTitles.add("Duration of CC Dealt");

                visionPlotTitles.add("Vision Wards Bought");
                visionPlotTitles.add("Wards Placed");
                visionPlotTitles.add("Wards Killed");

                // serialize the plot data
                Gson gson = new Gson();
                Type plotType = new TypeToken<List<Map<String, List<Number>>>>() {}.getType();
                String incomePlotsJson = gson.toJson(incomePlots, plotType);
                String offensePlotsJson = gson.toJson(offensePlots, plotType);
                String utilityPlotsJson = gson.toJson(utilityPlots, plotType);
                String visionPlotsJson = gson.toJson(visionPlots, plotType);

                // create tab bundles
                Bundle incomeTabBundle = new Bundle();
                incomeTabBundle.putString("plot_data", incomePlotsJson);
                incomeTabBundle.putStringArrayList("plot_titles", incomePlotTitles);

                Bundle offenseTabBundle = new Bundle();
                offenseTabBundle.putString("plot_data", offensePlotsJson);
                offenseTabBundle.putStringArrayList("plot_titles", offensePlotTitles);

                Bundle utilityTabBundle = new Bundle();
                utilityTabBundle.putString("plot_data", utilityPlotsJson);
                utilityTabBundle.putStringArrayList("plot_titles", utilityPlotTitles);

                Bundle visionTabBundle = new Bundle();
                visionTabBundle.putString("plot_data", visionPlotsJson);
                visionTabBundle.putStringArrayList("plot_titles", visionPlotTitles);

                // add bundles to plot data
                Map<String, Bundle> plotData = new HashMap<>();
                plotData.put("income", incomeTabBundle);
                plotData.put("offense", offenseTabBundle);
                plotData.put("utility", utilityTabBundle);
                plotData.put("vision", visionTabBundle);

                // populate the activity
                createLegend(names);

                TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_bar);
                tabLayout.addTab(tabLayout.newTab().setText("Income"));
                tabLayout.addTab(tabLayout.newTab().setText("Offense"));
                tabLayout.addTab(tabLayout.newTab().setText("Utility"));
                tabLayout.addTab(tabLayout.newTab().setText("Vision"));
                tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

                int numberOfTabs = tabLayout.getTabCount();
                FragmentManager fragManager = getSupportFragmentManager();
                RecentPagerAdapter pagerAdapter = new RecentPagerAdapter(fragManager, numberOfTabs, plotData);
                final ViewPager viewPager = (ViewPager) findViewById(R.id.stats_view_pager);
                viewPager.setAdapter(pagerAdapter);
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
            } else { // display error
                Toast.makeText(RecentActivity.this, postResponse, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
