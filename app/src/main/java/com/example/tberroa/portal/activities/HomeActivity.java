package com.example.tberroa.portal.activities;

import android.os.Bundle;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tberroa.portal.R;
import com.example.tberroa.portal.data.Params;
import com.example.tberroa.portal.data.UserInfo;
import com.example.tberroa.portal.database.RiotAPI;
import com.example.tberroa.portal.database.URLConstructor;
import com.example.tberroa.portal.helpers.CircleTransform;
import com.example.tberroa.portal.models.matchlist.MatchList;
import com.example.tberroa.portal.models.summoner.SummonerDto;
import com.example.tberroa.portal.network.NetworkUtil;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // =================== view initialization ==============================================
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //=======================================================================================
        // set region
        UserInfo userInfo = new UserInfo();
        userInfo.setRegion(this, Params.REGION_NA);

        // initialize riot api
        RiotAPI riotAPI = new RiotAPI(this);

        if (NetworkUtil.isInternetAvailable(this)){
            // get summoners
            List<String> summonerNames = new ArrayList<>();
            summonerNames.add("frosiph");
            summonerNames.add("acruz");
            summonerNames.add("luciaron");
            Map<String, SummonerDto> summoners = riotAPI.getSummoners(summonerNames);

            // get summoner
            SummonerDto summoner = summoners.get("acruz");

            // load summoner icon
            View headerLayout = navigationView.getHeaderView(0);
            ImageView summonerIcon = (ImageView) headerLayout.findViewById(R.id.summoner_icon);
            String url = new URLConstructor().summonerIcon(summoner.profileIconId);
            Picasso.with(this)
                    .load(url)
                    .fit()
                    .transform(new CircleTransform()).into(summonerIcon);

            // load summoner name
            TextView summonerName = (TextView) headerLayout.findViewById(R.id.summoner_name);
            summonerName.setText(summoner.name);

            // get his/her matches
            Map<String, String> parameters = new HashMap<>();
            parameters.put("seasons", Params.SEASON_2016);
            parameters.put("queue", Params.TEAM_BUILDER_DRAFT_RANKED_5);
            MatchList matches = riotAPI.getMatches(summoner.id, parameters);
        }
        else{
            Toast.makeText(this, "internet not available", Toast.LENGTH_SHORT).show();
        }
    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.view_profile) {

        } else if (id == R.id.manage_inner_circle) {

        } else if (id == R.id.dynamic_queue) {

        } else if (id == R.id.solo_queue) {

        } else if (id == R.id.team_5) {

        } else if (id == R.id.team_3) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
