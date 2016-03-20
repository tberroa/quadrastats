package com.example.tberroa.portal.screens;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tberroa.portal.R;
import com.example.tberroa.portal.apimanager.APIMonitorService;
import com.example.tberroa.portal.apimanager.APIUsageInfo;
import com.example.tberroa.portal.screens.profile.ProfileActivity;
import com.example.tberroa.portal.data.Params;
import com.example.tberroa.portal.data.UserInfo;
import com.example.tberroa.portal.screens.friends.FriendsActivity;
import com.example.tberroa.portal.screens.stats.recent.RecentActivity;
import com.example.tberroa.portal.screens.stats.season.SeasonActivity;
import com.example.tberroa.portal.screens.stats.withfriends.WithFriendsActivity;
import com.example.tberroa.portal.updater.UpdateJobInfo;
import com.example.tberroa.portal.updater.UpdateService;
import com.example.tberroa.portal.updater.UpdateUtil;
import com.squareup.picasso.Picasso;

public class BaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    protected Toolbar toolbar;
    private SmoothActionBarDrawerToggle toggle;
    private DrawerLayout drawer;

    @SuppressLint("InflateParams")
    @Override
    public void setContentView(final int layoutResID) {
        // base layout
        drawer = (DrawerLayout) getLayoutInflater().inflate(R.layout.activity_base, null);
        FrameLayout content = (FrameLayout) drawer.findViewById(R.id.activity_content);

        // fill content layout with the provided layout
        getLayoutInflater().inflate(layoutResID, content, true);
        super.setContentView(drawer);

        // initialize toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // initialize drawer
        toggle = new SmoothActionBarDrawerToggle(this, drawer, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        // initialize navigation view
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerLayout = navigationView.getHeaderView(0);

        // get the summoner's profile icon id and stylized name
        UserInfo userInfo = new UserInfo();
        int profileIconId = userInfo.getIconId(this);
        String stylizedName = userInfo.getStylizedName(this);

        // display the stylized summoner name
        TextView summonerNameView = (TextView) headerLayout.findViewById(R.id.summoner_name);
        summonerNameView.setText(stylizedName);

        // display the profile icon
        ImageView summonerIcon = (ImageView) headerLayout.findViewById(R.id.summoner_icon);
        String url = ScreenUtil.constructIconURL(profileIconId);
        Picasso.with(this).load(url).fit().transform(new CircleTransform()).into(summonerIcon);

        // boot up services in the case they were killed unexpectedly
        if (UpdateUtil.serviceNotRunning(this, UpdateService.class)) {
            new UpdateJobInfo().setRunning(this, false);
            startService(new Intent(this, UpdateService.class));
        }
        if (UpdateUtil.serviceNotRunning(this, APIMonitorService.class)) {
            new APIUsageInfo().reset(this);
            startService(new Intent(this, APIMonitorService.class));
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.view_profile:
                toggle.runWhenIdle(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(BaseActivity.this, ProfileActivity.class));
                        finish();
                    }
                });
                drawer.closeDrawers();
                break;
            case R.id.manage_friends:
                toggle.runWhenIdle(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(BaseActivity.this, FriendsActivity.class));
                        finish();
                    }
                });
                drawer.closeDrawers();
                break;
            case R.id.recent_games:
                toggle.runWhenIdle(new Runnable() {
                    @Override
                    public void run() {
                        Intent recentGames = new Intent(BaseActivity.this, RecentActivity.class);
                        recentGames.putExtra("queue", Params.DYNAMIC_QUEUE);
                        startActivity(recentGames);
                        finish();
                    }
                });
                drawer.closeDrawers();
                break;
            case R.id.season_totals:
                toggle.runWhenIdle(new Runnable() {
                    @Override
                    public void run() {
                        Intent seasonTotals = new Intent(BaseActivity.this, SeasonActivity.class);
                        seasonTotals.putExtra("queue", Params.DYNAMIC_QUEUE);
                        startActivity(seasonTotals);
                        finish();
                    }
                });
                drawer.closeDrawers();
                break;
            case R.id.with_friends:
                toggle.runWhenIdle(new Runnable() {
                    @Override
                    public void run() {
                        Intent withFriends = new Intent(BaseActivity.this, WithFriendsActivity.class);
                        withFriends.putExtra("queue", Params.DYNAMIC_QUEUE);
                        startActivity(withFriends);
                        finish();
                    }
                });
                drawer.closeDrawers();
                break;
        }
        return true;
    }

    private class SmoothActionBarDrawerToggle extends ActionBarDrawerToggle {

        private Runnable runnable;

        @SuppressWarnings("SameParameterValue")
        public SmoothActionBarDrawerToggle(Activity a, DrawerLayout d, Toolbar t, int open, int close) {
            super(a, d, t, open, close);
        }

        @Override
        public void onDrawerOpened(View drawerView) {
            super.onDrawerOpened(drawerView);
            invalidateOptionsMenu();
        }

        @Override
        public void onDrawerClosed(View view) {
            super.onDrawerClosed(view);
            invalidateOptionsMenu();
        }

        @Override
        public void onDrawerStateChanged(int newState) {
            super.onDrawerStateChanged(newState);
            if (runnable != null && newState == DrawerLayout.STATE_IDLE) {
                runnable.run();
                runnable = null;
            }
        }

        public void runWhenIdle(Runnable runnable) {
            this.runnable = runnable;
        }
    }
}
