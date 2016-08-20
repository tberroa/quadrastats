package com.quadrastats.screens.stats.season;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;

import com.quadrastats.R;
import com.quadrastats.data.Constants;
import com.quadrastats.data.LocalDB;
import com.quadrastats.data.UserData;
import com.quadrastats.models.datadragon.Champion;
import com.quadrastats.models.stats.SeasonStats;
import com.quadrastats.models.summoner.Summoner;
import com.quadrastats.screens.ScreenUtil;
import com.quadrastats.screens.stats.BaseStatsActivity;
import com.quadrastats.screens.stats.CreateLegendPackage;
import com.quadrastats.screens.stats.StatsUtil;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SeasonActivity extends BaseStatsActivity implements SeasonAsync {

    private int legendIconSide;

    public void displayData(Map<String, Map<Long, SeasonStats>> seasonStatsMapMap) {
        // update the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setOnMenuItemClickListener(new MenuListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                super.onMenuItemClick(item);
                switch (item.getItemId()) {
                    case R.id.filter:
                        new FilterDialog().show();
                        break;
                }
                return true;
            }
        });

        // create view package
        ViewPackage viewPackage = new ViewPackage();
        viewPackage.seasonStatsMapMap = seasonStatsMapMap;
        viewPackage.champion = 0;
        viewPackage.perGame = false;

        populateActivity(viewPackage);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_season);

        // initialize the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.st_activity_title);
        toolbar.inflateMenu(R.menu.season_menu);

        // set swipe refresh layout listeners
        SwipeRefreshLayout dataSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.data_swipe_layout);
        dataSwipeLayout.setOnRefreshListener(this);
        SwipeRefreshLayout messageSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.message_swipe_layout);
        messageSwipeLayout.setOnRefreshListener(this);

        // initialize legend
        LinearLayout legendLayout = (LinearLayout) findViewById(R.id.legend_layout);
        legendLayout.setVisibility(View.GONE);

        // initialize the recycler view to gone
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setVisibility(View.GONE);

        // initialize view
        ViewInitialization viewInitialization = new ViewInitialization();
        viewInitialization.delegateSeason = this;
        viewInitialization.execute(2);
    }

    @Override
    public void onRefresh() {
        RequestSeasonStats requestSeasonStats = new RequestSeasonStats();
        requestSeasonStats.delegateSeason = this;
        requestSeasonStats.execute();
    }

    private void populateActivity(ViewPackage viewPackage) {
        // initialize the recycler view
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        if (viewPackage.seasonStatsMapMap.entrySet().size() > 3) {
            ViewAdapter adapter = new ViewAdapter(this, viewPackage);
            recyclerView.setAdapter(adapter);
        } else {
            ViewAdapterBar adapter = new ViewAdapterBar(this, viewPackage);
            recyclerView.setAdapter(adapter);
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setVisibility(View.VISIBLE);

        // create legend labels
        int i = 0;
        Set<String> labels;
        List<String> names = new ArrayList<>(viewPackage.seasonStatsMapMap.keySet());
        if (viewPackage.seasonStatsMapMap.entrySet().size() > 3) {
            labels = new LinkedHashSet<>();
            for (String name : names) {
                labels.add(i + ". " + name);
                i++;
            }
        } else {
            labels = new LinkedHashSet<>(names);
        }

        // create the legend
        LinearLayout legendLayout = (LinearLayout) findViewById(R.id.legend_layout);
        CreateLegendPackage createLegendPackage = new CreateLegendPackage();
        createLegendPackage.context = this;
        createLegendPackage.championId = viewPackage.champion;
        createLegendPackage.iconSide = legendIconSide;
        createLegendPackage.names = labels;
        createLegendPackage.staticRiotData = staticRiotData;
        createLegendPackage.view = legendLayout;
        createLegendPackage.viewWidth = ScreenUtil.screenWidth(this);
        StatsUtil.createLegend(createLegendPackage);

        // display the legend
        legendLayout.setVisibility(View.VISIBLE);
        legendLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // construct package required by select summoners dialog
                GoButtonPackageSSD goButtonPackageSSD = new GoButtonPackageSSD();
                goButtonPackageSSD.names = new LinkedHashSet<>(names);
                goButtonPackageSSD.data = new LinkedHashMap<>(viewPackage.seasonStatsMapMap);
                goButtonPackageSSD.championId = viewPackage.champion;
                goButtonPackageSSD.perGame = viewPackage.perGame;
                goButtonPackageSSD.recyclerView = recyclerView;

                // display select summoners dialog
                new SelectSummonersDialog(goButtonPackageSSD).show();
            }
        });
    }

    private class ChampionIcon {

        final Champion champion;
        ImageView check;
        boolean isSelected;

        ChampionIcon(Champion champion) {
            this.champion = champion;
            isSelected = false;
        }
    }

    private class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.ChampionViewHolder> {

        final List<ChampionIcon> championIcons;
        private final int side;

        FilterAdapter(List<ChampionIcon> championIcons, int side) {
            this.championIcons = championIcons;
            this.side = side;
        }

        @Override
        public int getItemCount() {
            return championIcons.size();
        }

        @Override
        public void onBindViewHolder(ChampionViewHolder viewHolder, int i) {
            // load champion icon into view
            ChampionIcon icon = championIcons.get(i);
            icon.check = viewHolder.champIconCheck;
            String key = icon.champion.key;
            String url = StatsUtil.championIconURL(staticRiotData.version, key);
            Picasso.with(SeasonActivity.this).load(url).resize(side, side)
                    .placeholder(R.drawable.ic_placeholder).into(viewHolder.champIconView);

            // set check
            if (icon.isSelected) {
                viewHolder.champIconCheck.setVisibility(View.VISIBLE);
            } else {
                viewHolder.champIconCheck.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public ChampionViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            Context context = viewGroup.getContext();
            View view = LayoutInflater.from(context).inflate(R.layout.element_champion_icon, viewGroup, false);
            return new ChampionViewHolder(view);
        }

        class ChampionViewHolder extends RecyclerView.ViewHolder {

            final ImageView champIconCheck;
            final ImageView champIconView;

            ChampionViewHolder(View itemView) {
                super(itemView);

                // initialize views
                champIconView = (ImageView) itemView.findViewById(R.id.champ_icon_view);
                champIconCheck = (ImageView) itemView.findViewById(R.id.champ_icon_check);
                champIconView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for (int i = 0; i < championIcons.size(); i++) {
                            if (i != getLayoutPosition()) {
                                championIcons.get(i).isSelected = false;
                                if (championIcons.get(i).check != null) {
                                    championIcons.get(i).check.setVisibility(View.INVISIBLE);
                                }
                            }
                        }
                        ChampionIcon icon = championIcons.get(getLayoutPosition());
                        icon.isSelected = !icon.isSelected;
                        if (icon.isSelected) {
                            champIconCheck.setVisibility(View.VISIBLE);
                        } else {
                            champIconCheck.setVisibility(View.INVISIBLE);
                        }
                    }
                });

                // resize views according to screen
                champIconView.getLayoutParams().width = side;
                champIconView.getLayoutParams().height = side;
                champIconView.setLayoutParams(champIconView.getLayoutParams());
                champIconCheck.getLayoutParams().width = side;
                champIconCheck.getLayoutParams().height = side;
                champIconCheck.setLayoutParams(champIconCheck.getLayoutParams());
            }
        }
    }

    private class FilterDialog extends Dialog {

        FilterDialog() {
            super(SeasonActivity.this, R.style.AppTheme_Dialog);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.dialog_data_filter_s);
            setCancelable(true);

            // resize dialog
            int dialogWidth = (Constants.UI_DIALOG_WIDTH * ScreenUtil.screenWidth(SeasonActivity.this)) / 100;
            int dialogHeight = (Constants.UI_DIALOG_HEIGHT * ScreenUtil.screenHeight(SeasonActivity.this)) / 100;
            getWindow().setLayout(dialogWidth, dialogHeight);

            // initialize radio buttons
            RadioButton totalsRadio = (RadioButton) findViewById(R.id.totals_radio);
            RadioButton perGameRadio = (RadioButton) findViewById(R.id.per_game_radio);

            // default is totals
            totalsRadio.setChecked(true);

            // initialize champion icon dimensions
            int champIconsPerRow = 4;
            int champIconSide = dialogWidth / champIconsPerRow;

            // set legend icon dimensions based off the champion icon dimensions
            legendIconSide = champIconSide / 2;

            // create the recycler view adapter data set
            List<ChampionIcon> championIcons = new ArrayList<>();
            for (Champion champion : staticRiotData.championsList) {
                championIcons.add(new ChampionIcon(champion));
            }

            // initialize recycler view
            RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
            FilterAdapter adapter = new FilterAdapter(championIcons, champIconSide);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new GridLayoutManager(SeasonActivity.this, champIconsPerRow));

            // initialize the go button
            Button goButton = (Button) findViewById(R.id.go_button);
            goButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // get the selected champion id
                    long championId = 0;
                    boolean foundSelected = false;
                    for (ChampionIcon icon : adapter.championIcons) {
                        if (icon.isSelected) {
                            if (!foundSelected) {
                                championId = icon.champion.id;
                                foundSelected = true;
                            } else {
                                String message = getString(R.string.err_select_only_one);
                                Toast.makeText(SeasonActivity.this, message, Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                    }

                    // construct the package to send to the go button async task
                    GoButtonPackageFD goButtonPackage = new GoButtonPackageFD();
                    goButtonPackage.dialog = FilterDialog.this;
                    goButtonPackage.championId = championId;
                    goButtonPackage.perGame = perGameRadio.isChecked();

                    // execute the go button function
                    new FilterDialogGoButton().execute(goButtonPackage);
                }
            });
        }
    }

    private class FilterDialogGoButton extends AsyncTask<GoButtonPackageFD, Void, ViewPackage> {

        FilterDialog dialog;

        @Override
        protected ViewPackage doInBackground(GoButtonPackageFD... params) {
            LocalDB localDB = new LocalDB();
            UserData userData = new UserData();

            // extract objects
            dialog = params[0].dialog;
            long championId = params[0].championId;
            boolean perGame = params[0].perGame;

            // get user
            Summoner user = localDB.summoner(userData.getId(SeasonActivity.this));

            // construct list of keys
            List<String> keys = new ArrayList<>(Arrays.asList((user.key + "," + user.friends).split(",")));

            // get locally saved data map
            Map<String, Map<Long, SeasonStats>> seasonStatsMapMap = localDB.seasonStatsMap(keys);

            // iterate over map removing summoners without stats for the specified champ
            Iterator<Map.Entry<String, Map<Long, SeasonStats>>> iterator = seasonStatsMapMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, Map<Long, SeasonStats>> entry = iterator.next();
                if (entry.getValue().get(championId) == null) {
                    iterator.remove();
                }
            }

            // make sure map is not empty
            if (!seasonStatsMapMap.isEmpty()) {
                // create view package
                ViewPackage viewPackage = new ViewPackage();
                viewPackage.seasonStatsMapMap = seasonStatsMapMap;
                viewPackage.champion = championId;
                viewPackage.perGame = perGame;

                return viewPackage;
            }

            // map was empty
            return null;
        }

        @Override
        protected void onPostExecute(ViewPackage viewPackage) {
            if (viewPackage != null) {
                populateActivity(viewPackage);
                dialog.dismiss();
            } else {
                Toast.makeText(SeasonActivity.this, R.string.err_no_data, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class GoButtonPackageFD {

        long championId;
        FilterDialog dialog;
        boolean perGame;
    }

    private class GoButtonPackageSSD {

        long championId;
        Map<String, Map<Long, SeasonStats>> data;
        Set<String> names;
        boolean perGame;
        RecyclerView recyclerView;
    }

    private class SSDName {

        final String name;
        boolean isChecked;

        SSDName(String name) {
            this.name = name;
        }
    }

    private class SSDViewAdapter extends RecyclerView.Adapter<SSDViewAdapter.SSDViewHolder> {

        final List<SSDName> names;

        SSDViewAdapter(List<SSDName> names) {
            this.names = names;
        }

        @Override
        public int getItemCount() {
            return names.size();
        }

        @Override
        public void onBindViewHolder(SSDViewHolder viewHolder, int i) {
            viewHolder.checkbox.setText(names.get(i).name);
            viewHolder.checkbox.setChecked(names.get(i).isChecked);
        }

        @Override
        public SSDViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            Context context = viewGroup.getContext();
            View v = LayoutInflater.from(context).inflate(R.layout.view_select_summoners, viewGroup, false);
            return new SSDViewHolder(v);
        }

        class SSDViewHolder extends RecyclerView.ViewHolder {

            final CheckBox checkbox;

            SSDViewHolder(View itemView) {
                super(itemView);
                checkbox = (CheckBox) itemView.findViewById(R.id.summoner_checkbox);
                checkbox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        names.get(getAdapterPosition()).isChecked = !names.get(getAdapterPosition()).isChecked;
                    }
                });
            }
        }
    }

    private class SelectSummonersDialog extends Dialog {

        private final long championId;
        private final Map<String, Map<Long, SeasonStats>> data;
        private final Set<String> names;
        private final boolean perGame;
        private final RecyclerView recyclerView;

        SelectSummonersDialog(GoButtonPackageSSD goButtonPackageSSD) {
            super(SeasonActivity.this, R.style.AppTheme_Dialog);
            names = goButtonPackageSSD.names;
            data = goButtonPackageSSD.data;
            championId = goButtonPackageSSD.championId;
            perGame = goButtonPackageSSD.perGame;
            recyclerView = goButtonPackageSSD.recyclerView;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.dialog_select_summoners);
            setCancelable(true);

            // construct list of SSD Names
            List<SSDName> ssdNames = new ArrayList<>();
            for (String name : names) {
                ssdNames.add(new SSDName(name));
            }

            // initialize recycler view
            RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
            SSDViewAdapter adapter = new SSDViewAdapter(ssdNames);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(SeasonActivity.this));

            // initialize the go button
            Button goButton = (Button) findViewById(R.id.go_button);
            goButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // make sure at least one was selected
                    boolean minimumSatisfied = false;
                    for (SSDName name : ssdNames) {
                        if (name.isChecked) {
                            minimumSatisfied = true;
                            break;
                        }
                    }

                    if (minimumSatisfied) {
                        // remove names that were not checked
                        for (SSDName name : ssdNames) {
                            if (!name.isChecked) {
                                names.remove(name.name);
                                data.remove(name.name);
                            }
                        }

                        // create legend labels
                        int i = 0;
                        Set<String> labels;
                        if (names.size() > 3) {
                            labels = new LinkedHashSet<>();
                            for (String name : names) {
                                labels.add(i + ". " + name);
                                i++;
                            }
                        } else {
                            labels = new LinkedHashSet<>(names);
                        }

                        // update the legend
                        CreateLegendPackage createLegendPackage = new CreateLegendPackage();
                        createLegendPackage.championId = championId;
                        createLegendPackage.context = SeasonActivity.this;
                        createLegendPackage.iconSide = legendIconSide;
                        createLegendPackage.names = labels;
                        createLegendPackage.staticRiotData = staticRiotData;
                        createLegendPackage.view = SeasonActivity.this.findViewById(R.id.legend_layout);
                        createLegendPackage.viewWidth = ScreenUtil.screenWidth(SeasonActivity.this);
                        StatsUtil.createLegend(createLegendPackage);

                        // update the recycler view
                        ViewPackage viewPackage = new ViewPackage();
                        viewPackage.seasonStatsMapMap = data;
                        viewPackage.champion = championId;
                        viewPackage.perGame = perGame;
                        if (data.entrySet().size() > 3) {
                            ViewAdapter adapter = new ViewAdapter(SeasonActivity.this, viewPackage);
                            SelectSummonersDialog.this.recyclerView.setAdapter(adapter);
                        } else {
                            ViewAdapterBar adapter = new ViewAdapterBar(SeasonActivity.this, viewPackage);
                            SelectSummonersDialog.this.recyclerView.setAdapter(adapter);
                        }
                        dismiss();
                    } else {
                        Toast.makeText(SeasonActivity.this, R.string.err_must_select_one, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
