package com.example.tberroa.portal.screens.stats.recent;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.support.v7.view.ContextThemeWrapper;
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
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
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
        toolbar.setTitle(R.string.recent_games);
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
        onResume();
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

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
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
        legendLayout.setOnClickListener(new OnClickListener() {
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
                LinearLayout linearLayout = new LinearLayout(RecentActivity.this);
                linearLayout.setOrientation(LinearLayout.VERTICAL);
                int padding = ScreenUtil.dpToPx(RecentActivity.this, 5);
                linearLayout.setPadding(0, padding, 0, padding);
                for (CheckBox checkBox : checkBoxes) {
                    linearLayout.addView(checkBox);
                }
                ScrollView scrollView = new ScrollView(RecentActivity.this);
                scrollView.addView(linearLayout);

                // construct dialog
                ContextThemeWrapper theme = new ContextThemeWrapper(RecentActivity.this, R.style.DialogStyle);
                Builder builder = new Builder(theme);
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
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        int numOfTabs = tabLayout.getTabCount();
        FragmentManager fM = getSupportFragmentManager();
        RecentPagerAdapter pagerAdapter = new RecentPagerAdapter(fM, numOfTabs, titles, aggregateData);
        final ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
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
        tabLayout.setOnTabSelectedListener(new OnTabSelectedListener() {
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
            final ChampionIcon icon = champions.get(i);
            Picasso.with(RecentActivity.this).load(ScreenUtil.championIcon(icon.name)).into(clientViewHolder.champIcon);

            clientViewHolder.champIconCheckbox.setOnCheckedChangeListener(null);
            clientViewHolder.champIconCheckbox.setChecked(icon.isSelected);

            clientViewHolder.champIconCheckbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    icon.isSelected = isChecked;
                }
            });
        }

        @Override
        public ChampionViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            Context c = viewGroup.getContext();
            View v = LayoutInflater.from(c).inflate(R.layout.element_champion_icon, viewGroup, false);
            return new ChampionViewHolder(v);
        }

        public class ChampionViewHolder extends ViewHolder {

            final CheckBox champIconCheckbox;
            final ImageView champIcon;

            ChampionViewHolder(View itemView) {
                super(itemView);
                champIcon = (ImageView) itemView.findViewById(R.id.champ_icon_view);
                champIconCheckbox = (CheckBox) itemView.findViewById(R.id.champ_icon_checkbox);

                champIcon.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ChampionIcon icon = champions.get(getLayoutPosition());
                        icon.isSelected = !icon.isSelected;
                        champIconCheckbox.setChecked(icon.isSelected);
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
            List<String> names = StatsUtil.championNames();
            for (String name : names) {
                championIcons.add(new ChampionIcon(name));
            }

            // initialize recycler view
            int span = ScreenUtil.screenWidth(RecentActivity.this) / ScreenUtil.dpToPx(RecentActivity.this, 75);
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
            topCheckbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
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
            jungleCheckbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
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
            midCheckbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
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
            botCheckbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
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
            supportCheckbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
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

    private class FilterDialogGoButton extends AsyncTask<GoButtonPackage, Void, List<MatchStats>> {

        int champion;
        FilterDialog dialog;
        String position;

        @Override
        protected List<MatchStats> doInBackground(GoButtonPackage... params) {
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

    private class GoButtonPackage {
        public int champion;
        public FilterDialog dialog;
        public String lane;
        public String role;
    }
}
