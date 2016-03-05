package com.example.tberroa.portal.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.NavigationView;
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
import com.example.tberroa.portal.data.Params;
import com.example.tberroa.portal.data.SummonerInfo;
import com.example.tberroa.portal.database.LocalDB;
import com.example.tberroa.portal.database.URLConstructor;
import com.example.tberroa.portal.helpers.CircleTransform;
import com.example.tberroa.portal.models.summoner.SummonerDto;
import com.squareup.picasso.Picasso;

public class BaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private SmoothActionBarDrawerToggle toggle;
    private DrawerLayout drawer;
    public Toolbar toolbar;

    @SuppressLint("InflateParams")
    @Override
    public void setContentView(final int layoutResID){
        // base layout
        drawer = (DrawerLayout) getLayoutInflater().inflate(R.layout.activity_base, null);
        FrameLayout content = (FrameLayout) drawer.findViewById(R.id.activity_content);

        // fill content layout with the provided layout
        getLayoutInflater().inflate(layoutResID, content, true);
        super.setContentView(drawer);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toggle = new SmoothActionBarDrawerToggle(this, drawer, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerLayout = navigationView.getHeaderView(0);

        String stylizedName = new SummonerInfo().getStylizedName(this);
        Log.d(Params.TAG_DEBUG, "@HomeActivity: stylized name is " + stylizedName);

        TextView summonerNameView = (TextView) headerLayout.findViewById(R.id.summoner_name);
        summonerNameView.setText(stylizedName);

        SummonerDto summoner = new LocalDB().getSummoner(stylizedName);
        if (summoner != null){
            Log.d(Params.TAG_DEBUG, "@HomeActivity: summoner dto is not null");
            ImageView summonerIcon = (ImageView) headerLayout.findViewById(R.id.summoner_icon);
            String url = new URLConstructor().summonerIcon(summoner.profileIconId);
            Picasso.with(this)
                    .load(url)
                    .fit()
                    .transform(new CircleTransform()).into(summonerIcon);
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.view_profile) {
            toggle.runWhenIdle(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(BaseActivity.this, ProfileActivity.class));
                    finish();
                }
            });
            drawer.closeDrawers();
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
