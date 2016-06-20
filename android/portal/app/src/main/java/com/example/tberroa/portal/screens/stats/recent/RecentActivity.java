package com.example.tberroa.portal.screens.stats.recent;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
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
import com.example.tberroa.portal.screens.ScreenUtil;
import com.example.tberroa.portal.screens.friends.FriendsActivity;
import com.example.tberroa.portal.data.Params;
import com.example.tberroa.portal.data.UserInfo;
import com.example.tberroa.portal.screens.home.HomeActivity;
import com.example.tberroa.portal.models.stats.MatchStats;
import com.example.tberroa.portal.screens.stats.StatsUtil;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RecentActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {

    private boolean emptySwipeFlag;
    private boolean inView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent);

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

        // initialize the tab layout, set to gone
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_bar);
        tabLayout.addTab(tabLayout.newTab().setText("Income"));
        tabLayout.addTab(tabLayout.newTab().setText("Offense"));
        tabLayout.addTab(tabLayout.newTab().setText("Utility"));
        tabLayout.addTab(tabLayout.newTab().setText("Vision"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setVisibility(View.GONE);

        // initialize legend to gone
        LinearLayout legendLayout = (LinearLayout) findViewById(R.id.legend_layout);
        legendLayout.setVisibility(View.GONE);

        new ViewInitialization().execute();
    }

    @Override
    protected void onStart() {
        super.onStart();
        inView = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        inView = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.recent_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.filter:
                new FilterDialog().show();
        }
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
        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        SwipeRefreshLayout emptySwipe = (SwipeRefreshLayout) findViewById(R.id.empty_swipe);

        if (!emptySwipeFlag) {
            swipeRefreshLayout.setRefreshing(true);
        } else {
            emptySwipe.setRefreshing(true);
        }

        SwipeRefreshLayout[] layouts = new SwipeRefreshLayout[2];
        layouts[0] = swipeRefreshLayout;
        layouts[1] = emptySwipe;

        new OnRefresh().execute(layouts);


    }

    private void createLegend(final Set<String> names, int champion, String position) {
        // set position icon
        ImageView positionIcon = (ImageView) findViewById(R.id.pos_icon);
        if (position != null){
            Picasso.with(this).load(ScreenUtil.getPositionIcon(position)).into(positionIcon);
            positionIcon.setVisibility(View.VISIBLE);
        } else{
            positionIcon.setVisibility(View.GONE);
        }

        // set champion icon
        ImageView championIcon = (ImageView) findViewById(R.id.champ_icon);
        if (champion > 0){
            String name = StatsUtil.getChampionName(champion);
            Picasso.with(this).load(ScreenUtil.getChampionIcon(name)).into(championIcon);
            championIcon.setVisibility(View.VISIBLE);
        } else {
            championIcon.setVisibility(View.GONE);
        }

        // set names
        GridLayout legendNames = (GridLayout) findViewById(R.id.legend_names);
        legendNames.removeAllViews();
        int i = 0;
        for (String name : names) {
            TextView textView = new TextView(this);
            textView.setText(name);
            textView.setTextSize(12);
            textView.setTextColor(ContextCompat.getColor(this, R.color.white));
            textView.setPadding(ScreenUtil.dpToPx(this, 5), 0, ScreenUtil.dpToPx(this, 5), 0);
            legendNames.addView(textView);

            ImageView imageView = new ImageView(this);
            imageView.setMinimumWidth(ScreenUtil.dpToPx(this, 10));
            imageView.setMinimumHeight(ScreenUtil.dpToPx(this, 10));
            imageView.setPadding(0, ScreenUtil.dpToPx(this, 5), 0, 0);
            imageView.setImageResource(ScreenUtil.intToColor(i));
            legendNames.addView(imageView);

            i++;
        }
    }

    private void populateActivity(List<MatchStats> matchStatsList, final int champion, final String position) {
        // Before the data can be presented, it needs to be organized. All the data will be put into one map object.
        // The map key is the summoner name and the value is a list of of lists where each list is a list of data
        // points corresponding to one stat chart.
        // Example: Key: Frosiph | Value: list[0] = List<csAtTen>, list[1] = List<csDiffAtTen>, etc.

        TextView noStatsView = (TextView) findViewById(R.id.no_stats);
        noStatsView.setVisibility(View.GONE);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_bar);
        tabLayout.setVisibility(View.VISIBLE);

        // create list of chart titles
        final ArrayList<String> titles = new ArrayList<>();
        titles.add(getResources().getString(R.string.cs_at_ten));
        titles.add(getResources().getString(R.string.cs_diff_at_ten));
        titles.add(getResources().getString(R.string.cs_per_min));
        titles.add(getResources().getString(R.string.gold_per_min));
        titles.add(getResources().getString(R.string.dmg_per_min));
        titles.add(getResources().getString(R.string.kills));
        titles.add(getResources().getString(R.string.kda));
        titles.add(getResources().getString(R.string.kill_participation));
        titles.add(getResources().getString(R.string.vision_wards_bought));
        titles.add(getResources().getString(R.string.wards_placed));
        titles.add(getResources().getString(R.string.wards_killed));

        // clear set of summoner names
        final Set<String> names = new LinkedHashSet<>();

        // clear the map which holds the data for each summoner
        final Map<String, List<List<Number>>> aggregateData = new LinkedHashMap<>();

        // populate the map
        for (MatchStats matchStats : matchStatsList) {
            String summoner = matchStats.summoner_name;
            names.add(summoner);

            // initialize the stat lists
            List<List<Number>> summonerData = aggregateData.get(summoner);
            if (summonerData == null) {
                summonerData = new ArrayList<>();
                for (int i = 0; i < titles.size(); i++) {
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
                summonerData.get(8).add(matchStats.vision_wards_bought_in_game);
            }
            if (matchStats.dmg_per_min != null) {
                summonerData.get(9).add(matchStats.wards_placed);
            }
            if (matchStats.dmg_per_min != null) {
                summonerData.get(10).add(matchStats.wards_killed);
            }

            aggregateData.put(summoner, summonerData);
        }

        // update adapter
        updateAdapter(titles, aggregateData);

        // create the legend
        createLegend(names, champion, position);

        // display the legend
        LinearLayout legendLayout = (LinearLayout) findViewById(R.id.legend_layout);
        legendLayout.setVisibility(View.VISIBLE);
        legendLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // copy the set of summoner names
                final Set<String> selectedNames = new LinkedHashSet<>(names);

                // copy the map of data
                final Map<String, List<List<Number>>> selectedData = new LinkedHashMap<>(aggregateData);

                // create a checkbox for each name
                final List<CheckBox> checkBoxes = new ArrayList<>();
                for (String name : names) {
                    CheckBox checkBox = new CheckBox(RecentActivity.this);
                    checkBox.setText(name);
                    checkBox.setTextSize(20);
                    checkBoxes.add(checkBox);
                }

                // format the checkboxes in a layout
                final LinearLayout linearLayout = new LinearLayout(RecentActivity.this);
                linearLayout.setOrientation(LinearLayout.VERTICAL);
                int padding = ScreenUtil.dpToPx(RecentActivity.this, 5);
                linearLayout.setPadding(0, padding, 0, padding);
                for (CheckBox checkBox : checkBoxes) {
                    linearLayout.addView(checkBox);
                }
                final ScrollView scrollView = new ScrollView(RecentActivity.this);
                scrollView.addView(linearLayout);

                // construct dialog
                ContextThemeWrapper theme = new ContextThemeWrapper(RecentActivity.this, R.style.DialogStyle);
                AlertDialog.Builder builder = new AlertDialog.Builder(theme);
                builder.setView(scrollView);
                builder.setTitle(R.string.summoners_to_chart);
                builder.setCancelable(true);
                builder.setPositiveButton(R.string.done, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // make sure at least one was selected
                        boolean minimumSatisfied = false;
                        for (CheckBox checkBox : checkBoxes) {
                            if (checkBox.isChecked()) {
                                minimumSatisfied = true;
                                break;
                            }
                        }

                        if (minimumSatisfied) {
                            // go through and remove those that were not checked
                            for (CheckBox checkBox : checkBoxes) {
                                if (!checkBox.isChecked()) {
                                    selectedNames.remove(checkBox.getText().toString());
                                    selectedData.remove(checkBox.getText().toString());
                                }
                            }

                            // update the views
                            createLegend(selectedNames, champion, position);
                            updateAdapter(titles, selectedData);
                            dialog.dismiss();
                        } else {
                            Toast.makeText(RecentActivity.this, R.string.must_select_one, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                // display dialog
                builder.create().show();
            }
        });
    }

    private void updateAdapter(ArrayList<String> titles, Map<String, List<List<Number>>> aggregateData) {
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_bar);
        int numOfTabs = tabLayout.getTabCount();
        FragmentManager fM = getSupportFragmentManager();
        RecentPagerAdapter pagerAdapter = new RecentPagerAdapter(fM, numOfTabs, titles, aggregateData);
        final ViewPager viewPager = (ViewPager) findViewById(R.id.stats_view_pager);
        viewPager.setAdapter(pagerAdapter);
        TabLayout.Tab tab = tabLayout.getTabAt(0);
        if (tab != null) {
            tab.select();
        }
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout) {
            @Override
            public void onPageScrollStateChanged(int state) {
                SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
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
    }

    private class ChampionIcon {
        public final String name;
        public boolean isSelected;

        ChampionIcon(String name) {
            this.name = name;
            this.isSelected = false;
        }
    }

    private class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.ChampionViewHolder> {

        public final List<ChampionIcon> champions;

        public FilterAdapter(List<ChampionIcon> champions) {
            this.champions = champions;
        }

        @Override
        public int getItemCount() {
            return champions.size();
        }

        @Override
        public ChampionViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            Context c = viewGroup.getContext();
            View v = LayoutInflater.from(c).inflate(R.layout.element_champion_icon, viewGroup, false);
            return new ChampionViewHolder(v);
        }

        @Override
        public void onBindViewHolder(final ChampionViewHolder clientViewHolder, int i) {
            final ChampionIcon icon = champions.get(i);
            Picasso.with(RecentActivity.this).load(ScreenUtil.getChampionIcon(icon.name)).into(clientViewHolder.image);

            clientViewHolder.checkBox.setOnCheckedChangeListener(null);
            clientViewHolder.checkBox.setChecked(icon.isSelected);

            clientViewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    icon.isSelected = isChecked;
                }
            });
        }

        public class ChampionViewHolder extends RecyclerView.ViewHolder {

            final ImageView image;
            final CheckBox checkBox;

            ChampionViewHolder(final View itemView) {
                super(itemView);
                image = (ImageView) itemView.findViewById(R.id.image);
                checkBox = (CheckBox) itemView.findViewById(R.id.checkbox);

                image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ChampionIcon icon = champions.get(getLayoutPosition());
                        icon.isSelected = !icon.isSelected;
                        checkBox.setChecked(icon.isSelected);
                    }
                });
            }
        }
    }

    private class FilterDialog extends Dialog {

        public FilterDialog() {
            super(RecentActivity.this, R.style.DialogStyle);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.dialog_filter);
            setTitle(R.string.filter_data);
            setCancelable(true);

            // initialize list of champion icons
            List<ChampionIcon> championIcons = new ArrayList<>(131);
            List<String> names = StatsUtil.getChampionNames();
            for (String name : names) {
                championIcons.add(new ChampionIcon(name));
            }

            // initialize recycler view
            int span = ScreenUtil.getScreenWidth(RecentActivity.this) / ScreenUtil.dpToPx(RecentActivity.this, 75);
            RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
            final FilterAdapter adapter = new FilterAdapter(championIcons);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new GridLayoutManager(RecentActivity.this, span));

            // initialize the role check boxes
            final CheckBox topCheckbox = (CheckBox) findViewById(R.id.top_checkbox);
            final CheckBox jungleCheckbox = (CheckBox) findViewById(R.id.jungle_checkbox);
            final CheckBox midCheckbox = (CheckBox) findViewById(R.id.mid_checkbox);
            final CheckBox botCheckbox = (CheckBox) findViewById(R.id.bot_checkbox);
            final CheckBox supportCheckbox = (CheckBox) findViewById(R.id.support_checkbox);

            // set listeners to make them behave like radio buttons
            topCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        jungleCheckbox.setChecked(false);
                        midCheckbox.setChecked(false);
                        botCheckbox.setChecked(false);
                        supportCheckbox.setChecked(false);
                    }
                }
            });
            jungleCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        topCheckbox.setChecked(false);
                        midCheckbox.setChecked(false);
                        botCheckbox.setChecked(false);
                        supportCheckbox.setChecked(false);
                    }
                }
            });
            midCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        topCheckbox.setChecked(false);
                        jungleCheckbox.setChecked(false);
                        botCheckbox.setChecked(false);
                        supportCheckbox.setChecked(false);
                    }
                }
            });
            botCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        topCheckbox.setChecked(false);
                        jungleCheckbox.setChecked(false);
                        midCheckbox.setChecked(false);
                        supportCheckbox.setChecked(false);
                    }
                }
            });
            supportCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        topCheckbox.setChecked(false);
                        jungleCheckbox.setChecked(false);
                        midCheckbox.setChecked(false);
                        botCheckbox.setChecked(false);
                    }
                }
            });

            // initialize the go button
            final Button goButton = (Button) findViewById(R.id.go_button);
            goButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // get champion key
                    int champion = 0;
                    boolean foundSelected = false;
                    for (ChampionIcon icon : adapter.champions) {
                        if (icon.isSelected) {
                            if (!foundSelected) {
                                champion = StatsUtil.getChampionKey(icon.name);
                                foundSelected = true;
                            } else {
                                Toast.makeText(RecentActivity.this, R.string.select_only_one, Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                    }

                    // get position
                    String lane;
                    String role;
                    if (topCheckbox.isChecked()) {
                        lane = "TOP";
                        role = null;
                    } else if (jungleCheckbox.isChecked()) {
                        lane = "JUNGLE";
                        role = null;
                    } else if (midCheckbox.isChecked()) {
                        lane = "MIDDLE";
                        role = null;
                    } else if (botCheckbox.isChecked()) {
                        lane = "BOTTOM";
                        role = "DUO_CARRY";
                    } else if (supportCheckbox.isChecked()) {
                        lane = "BOTTOM";
                        role = "DUO_SUPPORT";
                    } else {
                        lane = null;
                        role = null;
                    }

                    GoButtonPackage goButtonPackage = new GoButtonPackage();
                    goButtonPackage.dialog = FilterDialog.this;
                    goButtonPackage.champion = champion;
                    goButtonPackage.lane = lane;
                    goButtonPackage.role = role;

                    new FilterDialogGoButton().execute(goButtonPackage);
                }
            });
        }
    }

    private class FilterDialogGoButton extends AsyncTask<GoButtonPackage, Void, List<MatchStats>>{

        FilterDialog dialog;
        int champion;
        String position;

        @Override
        protected List<MatchStats> doInBackground(GoButtonPackage... params){
            LocalDB localDB = new LocalDB();

            // extract objects
            dialog = params[0].dialog;
            champion = params[0].champion;
            String lane = params[0].lane;
            String role = params[0].role;

            if (params[0].lane != null){
                if (lane.equals("TOP") || lane.equals("JUNGLE") || lane.equals("MIDDLE")){
                    position = lane;
                } else {
                    position = role;
                }
            } else{
                position = null;
            }

            // get user
            Summoner user = localDB.getSummoner(new UserInfo().getId(RecentActivity.this));

            // construct list of keys
            List<String> keys = new ArrayList<>(Arrays.asList((user.key + "," + user.friends).split(",")));

            return localDB.getMatchStatsList(keys, champion, lane, role);
        }

        @Override
        protected void onPostExecute(List<MatchStats> matchStatsList){
            // display
            if (matchStatsList.size() > 0) {
                populateActivity(matchStatsList, champion, position);
                dialog.dismiss();
            } else {
                Toast.makeText(RecentActivity.this, R.string.no_data, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class GoButtonPackage {
        public FilterDialog dialog;
        public int champion;
        public String lane;
        public String role;
    }

    private class OnRefresh extends AsyncTask<SwipeRefreshLayout, Void, Summoner>{

        SwipeRefreshLayout swipeRefreshLayout;
        SwipeRefreshLayout emptySwipe;

        @Override
        protected Summoner doInBackground(SwipeRefreshLayout... params){
            swipeRefreshLayout = params[0];
            emptySwipe = params[1];

            return new LocalDB().getSummoner(new UserInfo().getId(RecentActivity.this));
        }

        @Override
        protected void onPostExecute(Summoner user){
            // initialize string of keys
            String keys = user.key + ",";

            // get user's friends
            if (!user.friends.equals("")) {
                keys += user.friends;

                // create the request object
                ReqMatchStats request = new ReqMatchStats();
                request.keys = new ArrayList<>(Arrays.asList(keys.split(",")));

                // execute request
                new RequestMatchStats().execute(request);
            } else { // user has no friends
                // clear and disable swipe refresh layouts
                swipeRefreshLayout.setRefreshing(false);
                swipeRefreshLayout.setEnabled(false);
                emptySwipe.setRefreshing(false);
                emptySwipe.setEnabled(false);

                // display message
                LinearLayout noFriendsLayout = (LinearLayout) findViewById(R.id.no_friends_layout);
                noFriendsLayout.setVisibility(View.VISIBLE);
                Button goToFriendsActivity = (Button) findViewById(R.id.go_to_friends_activity);
                goToFriendsActivity.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(RecentActivity.this, FriendsActivity.class));
                        finish();
                    }
                });
            }
        }
    }

    private class RequestMatchStats extends AsyncTask<ReqMatchStats, Void, Boolean[]> {

        List<MatchStats> matchStatsList;
        String postResponse = "";

        @Override
        protected Boolean[] doInBackground(ReqMatchStats... params) {
            // make the request
            try {
                String url = "http://52.90.34.48/stats/match.json";
                postResponse = new Http().post(url, ModelUtil.toJson(params[0], ReqMatchStats.class));
            } catch (java.io.IOException e) {
                Log.e(Params.TAG_EXCEPTIONS, "@RecentActivity: " + e.getMessage());
            }

            // initialize return array
            Boolean result[] = new Boolean[]{false, false};

            // process the response
            if (postResponse.contains("champ_level")) {
                // successful http request
                result[0] = true;

                // get the match stats objects
                Type type = new TypeToken<List<MatchStats>>() {
                }.getType();
                matchStatsList = ModelUtil.fromJsonList(postResponse, type);

                // save the match stats
                ActiveAndroid.beginTransaction();
                try {
                    LocalDB localDB = new LocalDB();
                    for (MatchStats matchStats : matchStatsList) {
                        if (localDB.getMatchStats(matchStats.summoner_key, matchStats.match_id) == null) {
                            // received new data
                            result[1] = true;
                            matchStats.save();
                        }
                    }
                    ActiveAndroid.setTransactionSuccessful();
                } finally {
                    ActiveAndroid.endTransaction();
                }
            }

            return result;
        }

        @Override
        protected void onPostExecute(Boolean[] result) {
            // clear swipe refresh layouts
            SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
            swipeRefreshLayout.setRefreshing(false);
            SwipeRefreshLayout emptySwipe = (SwipeRefreshLayout) findViewById(R.id.empty_swipe);
            emptySwipe.setRefreshing(false);

            if (result[0]) { // successful http request
                if (emptySwipeFlag) {
                    // switch back swipe refresh layouts
                    emptySwipe.setEnabled(false);
                    swipeRefreshLayout.setEnabled(true);
                    emptySwipeFlag = false;

                    // disable scroll view
                    ScrollView scrollView = (ScrollView) findViewById(R.id.scroll_view);
                    scrollView.setVisibility(View.GONE);
                }

                // populate the activity
                if (inView && result[1]) { // and received new data
                    populateActivity(matchStatsList, 0, null);
                }
            } else { // display error
                Toast.makeText(RecentActivity.this, postResponse, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class ViewInitialization extends AsyncTask<Void, Void, List<String>> {

        private SwipeRefreshLayout swipeRefreshLayout;
        private SwipeRefreshLayout emptySwipe;
        private TextView noStatsView;
        private TextView loading;
        private LinearLayout noFriendsLayout;
        private ScrollView scrollView;

        @Override
        protected void onPreExecute(){
            // initialize swipe refresh layouts
            swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
            swipeRefreshLayout.setDistanceToTriggerSync(500);
            swipeRefreshLayout.setOnRefreshListener(RecentActivity.this);
            scrollView = (ScrollView) findViewById(R.id.scroll_view);
            emptySwipe = (SwipeRefreshLayout) findViewById(R.id.empty_swipe);
            emptySwipe.setOnRefreshListener(RecentActivity.this);
            emptySwipe.setEnabled(false);
            emptySwipeFlag = false;

            // initialize message layouts and views
            loading = (TextView) findViewById(R.id.loading);
            loading.setVisibility(View.VISIBLE);
            noStatsView = (TextView) findViewById(R.id.no_stats);
            noStatsView.setVisibility(View.GONE);
            noFriendsLayout = (LinearLayout) findViewById(R.id.no_friends_layout);
            noFriendsLayout.setVisibility(View.GONE);
        }

        List<MatchStats> matchStatsList;

        @Override
        protected List<String> doInBackground(Void... params) {
            LocalDB localDB = new LocalDB();

            // construct key list for user and friends
            Summoner user = localDB.getSummoner(new UserInfo().getId(RecentActivity.this));
            List<String> keys = new ArrayList<>(Arrays.asList((user.key + "," + user.friends).split(",")));

            // if user has friends, get match stats
            if (keys.size() > 1) {
                matchStatsList = localDB.getMatchStatsList(keys, 0, null, null);
            }

            return keys;
        }

        @Override
        protected void onPostExecute(List<String> keys) {
            loading.setVisibility(View.GONE);

            // redirect user if they have no friends
            if (keys.size() == 1) {
                swipeRefreshLayout.setEnabled(false);
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

            // take action based on whether there is data to show or not
            if (!matchStatsList.isEmpty()) {
                scrollView.setVisibility(View.GONE);
                populateActivity(matchStatsList, 0, null);
            } else { // no data to display
                // show message
                noStatsView.setVisibility(View.VISIBLE);

                // switch swipe refresh layouts
                swipeRefreshLayout.setEnabled(false);
                emptySwipe.setEnabled(true);
                emptySwipeFlag = true;
            }
        }
    }
}
