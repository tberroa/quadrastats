package com.example.tberroa.portal.screens.stats.withfriends;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.View;

import com.example.tberroa.portal.R;
import com.example.tberroa.portal.data.LocalDB;
import com.example.tberroa.portal.data.Params;
import com.example.tberroa.portal.data.UserInfo;
import com.example.tberroa.portal.models.stats.MatchStats;
import com.example.tberroa.portal.models.summoner.Summoner;
import com.example.tberroa.portal.screens.BaseActivity;
import com.example.tberroa.portal.screens.home.HomeActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class WithFriendsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_with_friends);

        // no animation if starting activity as a reload
        if (getIntent().getAction() != null && getIntent().getAction().equals(Params.RELOAD)) {
            overridePendingTransition(0, 0);
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.with_friends);
        }

        toolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.back_button));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else {
                    startActivity(new Intent(WithFriendsActivity.this, HomeActivity.class));
                    finish();
                }
            }
        });

        // set tab layout to gone while view is initialized
        TabLayout tabLayout = (TabLayout) findViewById(R.id.wf_tab_bar);
        tabLayout.setVisibility(View.GONE);

        new ViewInitialization().execute();
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

    // makes sign in request to backend via http
    private class ViewInitialization extends AsyncTask<Void, Void, Void> {

        Map<Long, Map<String, MatchStats>> matchStatsMapMap;

        @Override
        protected Void doInBackground(Void... params) {
            LocalDB localDB = new LocalDB();

            // get user
            Summoner user = localDB.getSummoner(new UserInfo().getId(WithFriendsActivity.this));

            // get the users match ids
            List<MatchStats> matches = localDB.getMatchStatsList(user.key);
            List<Long> matchIds = new ArrayList<>();
            for (MatchStats match : matches){
                matchIds.add(match.match_id);
            }

            // get the friend keys
            List<String> keys = new ArrayList<>(Arrays.asList((user.key + "," + user.friends).split(",")));

            // use the match ids and friend keys to find games where user played with at least one friend
            matchStatsMapMap = localDB.getMatchesWithFriends(WithFriendsActivity.this, matchIds, keys);

            return null;
        }

        @Override
        protected void onPostExecute(Void param) {
            // initialize the tab layout
            TabLayout tabLayout = (TabLayout) findViewById(R.id.wf_tab_bar);
            List<String> tabs = new ArrayList<>();
            for (int i = 0; i < matchStatsMapMap.size(); i++){
                tabs.add(String.valueOf(i+1));
            }
            for (String tab : tabs){
                tabLayout.addTab(tabLayout.newTab().setText(tab));
            }
            TabLayout.Tab tab = tabLayout.getTabAt(0);
            if (tab != null) {
                tab.select();
            }
            tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
            tabLayout.setVisibility(View.VISIBLE);

            // initialize the view pager
            final ViewPager viewPager = (ViewPager) findViewById(R.id.wf_view_pager);
            int numOfTabs = tabLayout.getTabCount();
            FragmentManager fM = getSupportFragmentManager();
            viewPager.setAdapter(new WithFriendsPagerAdapter(fM, numOfTabs, matchStatsMapMap));

            // sync together the tab layout and view pager
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
        }
    }
}