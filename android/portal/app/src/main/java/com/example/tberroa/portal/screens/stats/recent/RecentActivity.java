package com.example.tberroa.portal.screens.stats.recent;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TabLayout.OnTabSelectedListener;
import android.support.design.widget.TabLayout.Tab;
import android.support.design.widget.TabLayout.TabLayoutOnPageChangeListener;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tberroa.portal.R;
import com.example.tberroa.portal.data.LocalDB;
import com.example.tberroa.portal.data.UserInfo;
import com.example.tberroa.portal.models.stats.MatchStats;
import com.example.tberroa.portal.models.summoner.Summoner;
import com.example.tberroa.portal.screens.ScreenUtil;
import com.example.tberroa.portal.screens.stats.BaseStatsActivity;
import com.example.tberroa.portal.screens.stats.StatsUtil;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RecentActivity extends BaseStatsActivity implements RecentAsync {

    public void displayData(List<MatchStats> matchStatsList) {
        populateActivity(matchStatsList, 0, null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent);

        // initialize the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.rg_activity_title);
        toolbar.inflateMenu(R.menu.recent_menu);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.filter:
                        new FilterDialog().show();
                }
                return true;
            }
        });

        // initialize the tab layout
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Income"));
        tabLayout.addTab(tabLayout.newTab().setText("Offense"));
        tabLayout.addTab(tabLayout.newTab().setText("Utility"));
        tabLayout.addTab(tabLayout.newTab().setText("Vision"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setVisibility(View.GONE);

        // initialize legend
        LinearLayout legendLayout = (LinearLayout) findViewById(R.id.legend_layout);
        legendLayout.setVisibility(View.GONE);

        // set swipe refresh layout listeners
        SwipeRefreshLayout dataSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.data_swipe_layout);
        dataSwipeLayout.setOnRefreshListener(this);
        SwipeRefreshLayout messageSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.message_swipe_layout);
        messageSwipeLayout.setOnRefreshListener(this);

        // initialize view
        ViewInitialization viewInitialization = new ViewInitialization();
        viewInitialization.delegateRecent = this;
        viewInitialization.execute(1);
    }

    @Override
    public void onRefresh() {
        RequestMatchStats requestMatchStats = new RequestMatchStats();
        requestMatchStats.delegateRecent = this;
        requestMatchStats.execute(1);
    }

    private void createLegend(Set<String> names, long champion, String position) {
        // set position icon
        ImageView positionIcon = (ImageView) findViewById(R.id.position_view);
        if (position != null) {
            Picasso.with(this).load(ScreenUtil.positionIcon(position)).into(positionIcon);
            positionIcon.setVisibility(View.VISIBLE);
        } else {
            positionIcon.setVisibility(View.GONE);
        }

        // set champion icon
        ImageView championIcon = (ImageView) findViewById(R.id.champ_icon_view);
        if (champion > 0) {
            String name = StatsUtil.championName(champion);
            Picasso.with(this).load(ScreenUtil.championIcon(name)).into(championIcon);
            championIcon.setVisibility(View.VISIBLE);
        } else {
            championIcon.setVisibility(View.GONE);
        }

        // set names
        GridLayout legendNames = (GridLayout) findViewById(R.id.names_layout);
        legendNames.removeAllViews();
        int i = 0;
        for (String name : names) {
            TextView textView = new TextView(this);
            textView.setText(name);
            textView.setTextSize(12);
            textView.setTextColor(ContextCompat.getColor(this, ScreenUtil.intToColor(i)));
            textView.setPadding(ScreenUtil.dpToPx(this, 5), 0, ScreenUtil.dpToPx(this, 5), 0);
            legendNames.addView(textView);
            i++;
        }
    }

    private void populateActivity(List<MatchStats> matchStatsList, int champion, String position) {
        // Before the data can be presented, it needs to be organized. All the data will be put into one map object.
        // The map key is the summoner name and the value is a list of of lists where each list is a list of data
        // points corresponding to one stat chart.
        // Example: Key: Frosiph | Value: list[0] = List<csAtTen>, list[1] = List<csDiffAtTen>, etc.

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setVisibility(View.VISIBLE);

        // create list of chart titles
        ArrayList<String> titles = new ArrayList<>();
        titles.add(getResources().getString(R.string.rg_cs_at_ten));
        titles.add(getResources().getString(R.string.rg_cs_diff_at_ten));
        titles.add(getResources().getString(R.string.rg_cs_per_min));
        titles.add(getResources().getString(R.string.rg_gold_per_min));
        titles.add(getResources().getString(R.string.rg_dmg_per_min));
        titles.add(getResources().getString(R.string.rg_kills));
        titles.add(getResources().getString(R.string.rg_kda));
        titles.add(getResources().getString(R.string.rg_kill_participation));
        titles.add(getResources().getString(R.string.rg_wards_bought));
        titles.add(getResources().getString(R.string.rg_wards_placed));
        titles.add(getResources().getString(R.string.rg_wards_killed));

        // clear set of summoner names
        Set<String> names = new LinkedHashSet<>();

        // clear the map which holds the data for each summoner
        Map<String, List<List<Number>>> aggregateData = new LinkedHashMap<>();

        // populate the map
        for (MatchStats matchStats : matchStatsList) {
            String summoner = matchStats.summoner_name;
            names.add(summoner);

            // initialize the stat lists
            List<List<Number>> summonerData = aggregateData.get(summoner);
            if (summonerData == null) {
                summonerData = new ArrayList<>();
                for (int i = 0; i < titles.size(); i++) {
                    summonerData.add(new ArrayList<>());
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
        legendLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // copy the set of summoner names
                Set<String> selectedNames = new LinkedHashSet<>(names);

                // copy the map of data
                Map<String, List<List<Number>>> selectedData = new LinkedHashMap<>(aggregateData);

                // create a checkbox for each name
                List<CheckBox> checkBoxes = new ArrayList<>();
                for (String name : names) {
                    CheckBox checkBox = new CheckBox(RecentActivity.this);
                    checkBox.setText(name);
                    checkBox.setTextSize(20);
                    checkBoxes.add(checkBox);
                }

                // construct package required by dialog
                GoButtonPackageSSD goButtonPackageSSD = new GoButtonPackageSSD();
                goButtonPackageSSD.selectedNames = selectedNames;
                goButtonPackageSSD.selectedData = selectedData;
                goButtonPackageSSD.checkBoxes = checkBoxes;
                goButtonPackageSSD.titles = titles;
                goButtonPackageSSD.champion = champion;
                goButtonPackageSSD.position = position;

                // display dialog
                new SelectSummonersDialog(RecentActivity.this, goButtonPackageSSD).show();
            }
        });
    }

    private void updateAdapter(ArrayList<String> titles, Map<String, List<List<Number>>> aggregateData) {
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        int numOfTabs = tabLayout.getTabCount();
        FragmentManager fM = getSupportFragmentManager();
        RecentPagerAdapter pagerAdapter = new RecentPagerAdapter(fM, numOfTabs, titles, aggregateData);
        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewPager.setAdapter(pagerAdapter);
        Tab tab = tabLayout.getTabAt(0);
        if (tab != null) {
            tab.select();
        }
        viewPager.addOnPageChangeListener(new TabLayoutOnPageChangeListener(tabLayout) {
            @Override
            public void onPageScrollStateChanged(int state) {
                SwipeRefreshLayout dataSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.data_swipe_layout);
                dataSwipeLayout.setEnabled(state == ViewPager.SCROLL_STATE_IDLE);
            }
        });
        tabLayout.addOnTabSelectedListener(new OnTabSelectedListener() {
            @Override
            public void onTabReselected(Tab tab) {
            }

            @Override
            public void onTabSelected(Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(Tab tab) {
            }
        });
    }

    private class ChampionIcon {

        public final String name;
        public boolean isSelected;
        public ImageView check;

        ChampionIcon(String name) {
            this.name = name;
            isSelected = false;
        }
    }

    private class FilterAdapter extends Adapter<FilterAdapter.ChampionViewHolder> {

        public final List<ChampionIcon> champions;

        public FilterAdapter(List<ChampionIcon> champions) {
            this.champions = champions;
        }

        @Override
        public int getItemCount() {
            return champions.size();
        }

        @Override
        public void onBindViewHolder(ChampionViewHolder clientViewHolder, int i) {
            champions.get(i).check = clientViewHolder.champIconCheck;
            ChampionIcon icon = champions.get(i);
            Picasso.with(RecentActivity.this).load(ScreenUtil.championIcon(icon.name)).into(clientViewHolder.champIcon);

            if (icon.isSelected) {
                clientViewHolder.champIconCheck.setVisibility(View.VISIBLE);
            } else {
                clientViewHolder.champIconCheck.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public ChampionViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            Context c = viewGroup.getContext();
            View v = LayoutInflater.from(c).inflate(R.layout.element_champion_icon, viewGroup, false);
            return new ChampionViewHolder(v);
        }

        public class ChampionViewHolder extends ViewHolder {

            final ImageView champIconCheck;
            final ImageView champIcon;

            ChampionViewHolder(View itemView) {
                super(itemView);
                champIcon = (ImageView) itemView.findViewById(R.id.champ_icon_view);
                champIconCheck = (ImageView) itemView.findViewById(R.id.champ_icon_check);

                champIcon.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for (int i = 0; i < champions.size(); i++) {
                            if (i != getLayoutPosition()) {
                                champions.get(i).isSelected = false;
                                if (champions.get(i).check != null) {
                                    champions.get(i).check.setVisibility(View.INVISIBLE);
                                }
                            }
                        }
                        ChampionIcon icon = champions.get(getLayoutPosition());
                        icon.isSelected = !icon.isSelected;
                        if (icon.isSelected) {
                            champIconCheck.setVisibility(View.VISIBLE);
                        } else {
                            champIconCheck.setVisibility(View.INVISIBLE);
                        }
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
            setContentView(R.layout.dialog_data_filter);
            setCancelable(true);

            // initialize list of champion icons
            List<ChampionIcon> championIcons = new ArrayList<>(131);
            List<String> names = StatsUtil.championNames();
            for (String name : names) {
                championIcons.add(new ChampionIcon(name));
            }

            // initialize recycler view
            int span = ScreenUtil.screenWidth(RecentActivity.this) / ScreenUtil.dpToPx(RecentActivity.this, 75);
            RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
            FilterAdapter adapter = new FilterAdapter(championIcons);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new GridLayoutManager(RecentActivity.this, span));

            // initialize the role checks
            ImageView topCheck = (ImageView) findViewById(R.id.top_check);
            ImageView jungleCheck = (ImageView) findViewById(R.id.jungle_check);
            ImageView midCheck = (ImageView) findViewById(R.id.mid_check);
            ImageView botCheck = (ImageView) findViewById(R.id.bot_check);
            ImageView supportCheck = (ImageView) findViewById(R.id.support_check);
            topCheck.setVisibility(View.INVISIBLE);
            jungleCheck.setVisibility(View.INVISIBLE);
            midCheck.setVisibility(View.INVISIBLE);
            botCheck.setVisibility(View.INVISIBLE);
            supportCheck.setVisibility(View.INVISIBLE);

            // initialize the position images
            ImageView topIcon = (ImageView) findViewById(R.id.top_view);
            ImageView jungleIcon = (ImageView) findViewById(R.id.jungle_view);
            ImageView midIcon = (ImageView) findViewById(R.id.mid_view);
            ImageView botIcon = (ImageView) findViewById(R.id.bot_view);
            ImageView supportIcon = (ImageView) findViewById(R.id.support_view);

            // set listeners to make them behave like radio buttons
            topIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    jungleCheck.setVisibility(View.INVISIBLE);
                    midCheck.setVisibility(View.INVISIBLE);
                    botCheck.setVisibility(View.INVISIBLE);
                    supportCheck.setVisibility(View.INVISIBLE);
                    if (topCheck.getVisibility() == View.INVISIBLE) {
                        topCheck.setVisibility(View.VISIBLE);
                    } else {
                        topCheck.setVisibility(View.INVISIBLE);
                    }
                }
            });
            jungleIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    topCheck.setVisibility(View.INVISIBLE);
                    midCheck.setVisibility(View.INVISIBLE);
                    botCheck.setVisibility(View.INVISIBLE);
                    supportCheck.setVisibility(View.INVISIBLE);
                    if (jungleCheck.getVisibility() == View.INVISIBLE) {
                        jungleCheck.setVisibility(View.VISIBLE);
                    } else {
                        jungleCheck.setVisibility(View.INVISIBLE);
                    }
                }
            });
            midIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    topCheck.setVisibility(View.INVISIBLE);
                    jungleCheck.setVisibility(View.INVISIBLE);
                    botCheck.setVisibility(View.INVISIBLE);
                    supportCheck.setVisibility(View.INVISIBLE);
                    if (midCheck.getVisibility() == View.INVISIBLE) {
                        midCheck.setVisibility(View.VISIBLE);
                    } else {
                        midCheck.setVisibility(View.INVISIBLE);
                    }
                }
            });
            botIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    topCheck.setVisibility(View.INVISIBLE);
                    jungleCheck.setVisibility(View.INVISIBLE);
                    midCheck.setVisibility(View.INVISIBLE);
                    supportCheck.setVisibility(View.INVISIBLE);
                    if (botCheck.getVisibility() == View.INVISIBLE) {
                        botCheck.setVisibility(View.VISIBLE);
                    } else {
                        botCheck.setVisibility(View.INVISIBLE);
                    }
                }
            });
            supportIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    topCheck.setVisibility(View.INVISIBLE);
                    jungleCheck.setVisibility(View.INVISIBLE);
                    midCheck.setVisibility(View.INVISIBLE);
                    botCheck.setVisibility(View.INVISIBLE);
                    if (supportCheck.getVisibility() == View.INVISIBLE) {
                        supportCheck.setVisibility(View.VISIBLE);
                    } else {
                        supportCheck.setVisibility(View.INVISIBLE);
                    }
                }
            });

            // initialize the go button
            Button goButton = (Button) findViewById(R.id.go_button);
            goButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // get champion key
                    int champion = 0;
                    boolean foundSelected = false;
                    for (ChampionIcon icon : adapter.champions) {
                        if (icon.isSelected) {
                            if (!foundSelected) {
                                champion = StatsUtil.championKey(icon.name);
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
                    if (topCheck.getVisibility() == View.VISIBLE) {
                        lane = "TOP";
                        role = null;
                    } else if (jungleCheck.getVisibility() == View.VISIBLE) {
                        lane = "JUNGLE";
                        role = null;
                    } else if (midCheck.getVisibility() == View.VISIBLE) {
                        lane = "MIDDLE";
                        role = null;
                    } else if (botCheck.getVisibility() == View.VISIBLE) {
                        lane = "BOTTOM";
                        role = "DUO_CARRY";
                    } else if (supportCheck.getVisibility() == View.VISIBLE) {
                        lane = "BOTTOM";
                        role = "DUO_SUPPORT";
                    } else {
                        lane = null;
                        role = null;
                    }

                    GoButtonPackageFD goButtonPackage = new GoButtonPackageFD();
                    goButtonPackage.dialog = FilterDialog.this;
                    goButtonPackage.champion = champion;
                    goButtonPackage.lane = lane;
                    goButtonPackage.role = role;

                    new FilterDialogGoButton().execute(goButtonPackage);
                }
            });
        }
    }

    private class FilterDialogGoButton extends AsyncTask<GoButtonPackageFD, Void, List<MatchStats>> {

        int champion;
        FilterDialog dialog;
        String position;

        @Override
        protected List<MatchStats> doInBackground(GoButtonPackageFD... params) {
            LocalDB localDB = new LocalDB();

            // extract objects
            dialog = params[0].dialog;
            champion = params[0].champion;
            String lane = params[0].lane;
            String role = params[0].role;

            if (params[0].lane != null) {
                if ("TOP".equals(lane) || "JUNGLE".equals(lane) || "MIDDLE".equals(lane)) {
                    position = lane;
                } else {
                    position = role;
                }
            } else {
                position = null;
            }

            // get user
            Summoner user = localDB.summoner(new UserInfo().getId(RecentActivity.this));

            // construct list of keys
            List<String> keys = new ArrayList<>(Arrays.asList((user.key + "," + user.friends).split(",")));

            return localDB.matchStatsList(keys, champion, lane, role);
        }

        @Override
        protected void onPostExecute(List<MatchStats> matchStatsList) {
            // display
            if (!matchStatsList.isEmpty()) {
                populateActivity(matchStatsList, champion, position);
                dialog.dismiss();
            } else {
                Toast.makeText(RecentActivity.this, R.string.no_data, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class GoButtonPackageFD {
        public int champion;
        public FilterDialog dialog;
        public String lane;
        public String role;
    }

    private class GoButtonPackageSSD {
        public Set<String> selectedNames;
        public Map<String, List<List<Number>>> selectedData;
        public List<CheckBox> checkBoxes;
        public ArrayList<String> titles;
        public int champion;
        public String position;
    }

    private class SelectSummonersDialog extends Dialog {

        private final Context context;
        private final Set<String> selectedNames;
        private final Map<String, List<List<Number>>> selectedData;
        private final List<CheckBox> checkBoxes;
        private final ArrayList<String> titles;
        private final int champion;
        private final String position;

        public SelectSummonersDialog(Context context, GoButtonPackageSSD goButtonPackageSSD) {
            super(RecentActivity.this, R.style.DialogStyle);
            this.context = context;
            selectedNames = goButtonPackageSSD.selectedNames;
            selectedData = goButtonPackageSSD.selectedData;
            checkBoxes = goButtonPackageSSD.checkBoxes;
            titles = goButtonPackageSSD.titles;
            champion = goButtonPackageSSD.champion;
            position = goButtonPackageSSD.position;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.dialog_select_summoners);
            setCancelable(true);

            // populate the view with the checkboxes
            LinearLayout linearLayout = (LinearLayout) findViewById(R.id.select_summoners_layout);
            for (CheckBox checkBox : checkBoxes) {
                linearLayout.addView(checkBox);
                View divider = new View(context);
                divider.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1));
                divider.setBackgroundColor(ContextCompat.getColor(context, R.color.divider));
                linearLayout.addView(divider);
            }

            // initialize the go button
            Button goButton = (Button) findViewById(R.id.go_button);
            goButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
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
                        dismiss();
                    } else {
                        Toast.makeText(RecentActivity.this, R.string.must_select_one, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
