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
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tberroa.portal.R;
import com.example.tberroa.portal.data.LocalDB;
import com.example.tberroa.portal.data.RiotData;
import com.example.tberroa.portal.data.UserData;
import com.example.tberroa.portal.models.summoner.Summoner;
import com.example.tberroa.portal.screens.account.AccountActivity;
import com.example.tberroa.portal.screens.friends.FriendsActivity;
import com.example.tberroa.portal.screens.stats.recent.RecentActivity;
import com.example.tberroa.portal.screens.stats.season.SeasonActivity;
import com.example.tberroa.portal.screens.stats.withfriends.WFActivity;
import com.squareup.picasso.Picasso;

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
        toggle = new SmoothActionBarDrawerToggle(this, drawer, toolbar);
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

        private static final int CLOSE = R.string.drawer_close;
        private static final int OPEN = R.string.drawer_open;
        private Runnable runnable;

        SmoothActionBarDrawerToggle(Activity activity, DrawerLayout drawer, Toolbar toolbar) {
            super(activity, drawer, toolbar, OPEN, CLOSE);
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

        void runWhenIdle(Runnable runnable) {
            this.runnable = runnable;
        }
    }

    private class ViewInitialization extends AsyncTask<Void, Void, Summoner> {

        @Override
        protected Summoner doInBackground(Void... params) {
            LocalDB localDB = new LocalDB();
            UserData userData = new UserData();
            RiotData riotData = new RiotData();

            staticRiotData = new StaticRiotData();
            staticRiotData.version = riotData.getVersion(BaseActivity.this);
            staticRiotData.championsMap = riotData.getChampionsMap(BaseActivity.this);
            staticRiotData.championsList = riotData.getChampionsList(BaseActivity.this);

            return localDB.summoner(userData.getId(BaseActivity.this));
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
            Picasso.with(BaseActivity.this).load(url).fit()
                    .placeholder(R.drawable.ic_placeholder).transform(new CircleTransform()).into(summonerIcon);
        }
    }
}
