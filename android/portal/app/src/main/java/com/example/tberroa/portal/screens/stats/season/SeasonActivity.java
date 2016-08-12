package com.example.tberroa.portal.screens.stats.season;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.tberroa.portal.R;
import com.example.tberroa.portal.data.Constants;
import com.example.tberroa.portal.data.LocalDB;
import com.example.tberroa.portal.data.UserData;
import com.example.tberroa.portal.models.datadragon.Champion;
import com.example.tberroa.portal.models.stats.SeasonStats;
import com.example.tberroa.portal.models.summoner.Summoner;
import com.example.tberroa.portal.screens.ScreenUtil;
import com.example.tberroa.portal.screens.home.HomeActivity;
import com.example.tberroa.portal.screens.stats.BaseStatsActivity;
import com.example.tberroa.portal.screens.stats.CreateLegendPackage;
import com.example.tberroa.portal.screens.stats.StatsUtil;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

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
        SeasonViewAdapter adapter = new SeasonViewAdapter(this, viewPackage);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setVisibility(View.VISIBLE);

        // create legend labels
        int i = 0;
        List<String> labels = new ArrayList<>();
        List<String> names = new ArrayList<>(viewPackage.seasonStatsMapMap.keySet());
        for (String name : names) {
            labels.add(i + ". " + name);
            i++;
        }

        // create the legend
        LinearLayout legendLayout = (LinearLayout) findViewById(R.id.legend_layout);
        CreateLegendPackage createLegendPackage = new CreateLegendPackage();
        createLegendPackage.context = this;
        createLegendPackage.championId = viewPackage.champion;
        createLegendPackage.iconSide = legendIconSide;
        createLegendPackage.names = new LinkedHashSet<>(labels);
        createLegendPackage.staticRiotData = staticRiotData;
        createLegendPackage.view = legendLayout;
        createLegendPackage.viewWidth = ScreenUtil.screenWidth(this);
        StatsUtil.createLegend(createLegendPackage);

        // display the legend
        legendLayout.setVisibility(View.VISIBLE);
    }

    private class ChampionIcon {

        public final Champion champion;
        public ImageView check;
        public boolean isSelected;

        ChampionIcon(Champion champion) {
            this.champion = champion;
            isSelected = false;
        }
    }

    private class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.ChampionViewHolder> {

        public final List<ChampionIcon> championIcons;
        private final int side;

        public FilterAdapter(List<ChampionIcon> championIcons, int side) {
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

        public class ChampionViewHolder extends RecyclerView.ViewHolder {

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

        public FilterDialog() {
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

            // use listener to get dialog width and initialize recycler view
            FilterAdapter adapter = new FilterAdapter(championIcons, champIconSide);
            LinearLayout filterLayout = (LinearLayout) findViewById(R.id.filter_layout);
            filterLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    filterLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(new GridLayoutManager(SeasonActivity.this, champIconsPerRow));
                }
            });

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

                    // construct the package to send to the async task
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

        public long championId;
        public FilterDialog dialog;
        public boolean perGame;
    }
}
