package com.example.tberroa.portal.screens;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.NavigationView;
import android.support.design.widget.NavigationView.OnNavigationItemSelectedListener;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tberroa.portal.R;
import com.example.tberroa.portal.data.Constants;
import com.example.tberroa.portal.data.LocalDB;
import com.example.tberroa.portal.data.UserInfo;
import com.example.tberroa.portal.models.ModelUtil;
import com.example.tberroa.portal.models.datadragon.Champion;
import com.example.tberroa.portal.models.datadragon.Champions;
import com.example.tberroa.portal.models.summoner.Summoner;
import com.example.tberroa.portal.network.Http;
import com.example.tberroa.portal.screens.account.AccountActivity;
import com.example.tberroa.portal.screens.friends.FriendsActivity;
import com.example.tberroa.portal.screens.stats.recent.RecentActivity;
import com.example.tberroa.portal.screens.stats.season.SeasonActivity;
import com.example.tberroa.portal.screens.stats.withfriends.WFActivity;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class BaseActivity extends AppCompatActivity implements OnNavigationItemSelectedListener {

    protected StaticRiotData staticRiotData;
    private DrawerLayout drawer;
    private SmoothActionBarDrawerToggle toggle;

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        switch (itemId) {
            case R.id.view_profile:
                toggle.runWhenIdle(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(BaseActivity.this, AccountActivity.class));
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
                        startActivity(new Intent(BaseActivity.this, RecentActivity.class));
                        finish();
                    }
                });
                drawer.closeDrawers();
                break;
            case R.id.season_totals:
                toggle.runWhenIdle(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(BaseActivity.this, SeasonActivity.class));
                        finish();
                    }
                });
                drawer.closeDrawers();
                break;
            case R.id.with_friends:
                toggle.runWhenIdle(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(BaseActivity.this, WFActivity.class));
                        finish();
                    }
                });
                drawer.closeDrawers();
                break;
        }
        return true;
    }

    @SuppressLint("InflateParams")
    @Override
    public void setContentView(View view) {
        // base layout
        drawer = (DrawerLayout) getLayoutInflater().inflate(R.layout.activity_base, null);
        FrameLayout content = (FrameLayout) drawer.findViewById(R.id.content_layout);

        // fill content layout with the provided layout
        content.addView(view);

        // set content view
        super.setContentView(drawer);

        // call view initialization method
        viewInitialization();
    }

    @SuppressLint("InflateParams")
    @Override
    public void setContentView(int layoutResID) {
        // base layout
        drawer = (DrawerLayout) getLayoutInflater().inflate(R.layout.activity_base, null);
        FrameLayout content = (FrameLayout) drawer.findViewById(R.id.content_layout);

        // fill content layout with the provided layout
        getLayoutInflater().inflate(layoutResID, content, true);

        // set content view
        super.setContentView(drawer);

        // call view initialization method
        viewInitialization();
    }

    private void viewInitialization() {
        // initialize toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        // initialize drawer
        toggle = new SmoothActionBarDrawerToggle(this, drawer, toolbar, R.string.drawer_open, R.string.drawer_close);
        toggle.setDrawerIndicatorEnabled(false);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // initialize base toolbar menu
        toolbar.inflateMenu(R.menu.base_menu);
        toolbar.setOnMenuItemClickListener(new MenuListener());

        // get the user's profile icon and name
        new ViewInitialization().execute();
    }

    public class MenuListener implements Toolbar.OnMenuItemClickListener {

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.menu:
                    drawer.openDrawer(GravityCompat.START);
            }
            return true;
        }
    }

    private class SmoothActionBarDrawerToggle extends ActionBarDrawerToggle {

        private Runnable runnable;

        @SuppressWarnings("SameParameterValue")
        public SmoothActionBarDrawerToggle(Activity a, DrawerLayout d, Toolbar t, int open, int close) {
            super(a, d, t, open, close);
        }

        @Override
        public void onDrawerClosed(View view) {
            super.onDrawerClosed(view);
            invalidateOptionsMenu();
        }

        @Override
        public void onDrawerOpened(View drawerView) {
            super.onDrawerOpened(drawerView);
            invalidateOptionsMenu();
        }

        @Override
        public void onDrawerStateChanged(int newState) {
            super.onDrawerStateChanged(newState);
            if ((runnable != null) && (newState == DrawerLayout.STATE_IDLE)) {
                runnable.run();
                runnable = null;
            }
        }

        public void runWhenIdle(Runnable runnable) {
            this.runnable = runnable;
        }
    }

    private class ViewInitialization extends AsyncTask<Void, Void, Summoner> {

        @Override
        protected Summoner doInBackground(Void... params) {
            // get the list of champions and data dragon version
            String postResponse = "";
            try {
                String url = Constants.URL_GET_CHAMPIONS;
                postResponse = new Http().get(url);
            } catch (IOException e) {
                Log.e(Constants.TAG_EXCEPTIONS, "@" + getClass().getSimpleName() + ": " + e.getMessage());
            }

            // parse the response
            staticRiotData = new StaticRiotData();
            if (postResponse.contains(Constants.VALID_GET_CHAMPIONS)) {
                Champions champions = ModelUtil.fromJson(postResponse, Champions.class);
                staticRiotData.version = champions.version;
                staticRiotData.championsMap = champions.data;
                staticRiotData.championsList = new ArrayList<>(staticRiotData.championsMap.values());
                Collections.sort(staticRiotData.championsList, new Comparator<Champion>() {
                    @Override
                    public int compare(Champion object1, Champion object2) {
                        return object1.name.compareTo(object2.name);
                    }
                });
            }

            return new LocalDB().summoner(new UserInfo().getId(BaseActivity.this));
        }

        @Override
        protected void onPostExecute(Summoner user) {
            int profileIcon = user.profile_icon;
            String name = user.name;

            // initialize navigation view
            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(BaseActivity.this);
            View headerLayout = navigationView.getHeaderView(0);

            // display the user's name
            TextView summonerNameView = (TextView) headerLayout.findViewById(R.id.user_summoner_name_view);
            summonerNameView.setText(name);

            // display the profile icon
            ImageView summonerIcon = (ImageView) headerLayout.findViewById(R.id.user_profile_icon_view);
            String url = ScreenUtil.profileIconURL(staticRiotData.version, profileIcon);
            Picasso.with(BaseActivity.this).load(url).fit().transform(new CircleTransform()).into(summonerIcon);
        }
    }
}
