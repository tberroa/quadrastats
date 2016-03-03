package com.example.tberroa.portal.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

import com.example.tberroa.portal.R;
import com.example.tberroa.portal.data.Params;
import com.example.tberroa.portal.data.SummonerInfo;
import com.example.tberroa.portal.data.UpdateServiceState;
import com.example.tberroa.portal.database.LocalDB;
import com.example.tberroa.portal.database.URLConstructor;
import com.example.tberroa.portal.helpers.CircleTransform;
import com.example.tberroa.portal.helpers.ScreenUtil;
import com.example.tberroa.portal.models.summoner.SummonerDto;
import com.example.tberroa.portal.services.UpdateService;
import com.squareup.picasso.Picasso;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    final private LocalDB localDB = new LocalDB();
    final private SummonerInfo summonerInfo = new SummonerInfo();
    final private UpdateServiceState updateServiceState = new UpdateServiceState();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // check if update service needs to be started up
        int state = updateServiceState.get(this);
        if (state == 0){
            startService(new Intent(this, UpdateService.class));
        }
        Log.d(Params.TAG_DEBUG, "@HomeActivity: updateServiceState is " + Integer.toString(state));

        // ============ general drawer layout & navigation view initializations ===================
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerLayout = navigationView.getHeaderView(0);

        String stylizedName = summonerInfo.getStylizedName(this);
        Log.d(Params.TAG_DEBUG, "@HomeActivity: stylized name is " + stylizedName);

        TextView summonerNameView = (TextView) headerLayout.findViewById(R.id.summoner_name);
        summonerNameView.setText(stylizedName);

        SummonerDto summoner = localDB.getSummoner(stylizedName);
        if (summoner != null){
            Log.d(Params.TAG_DEBUG, "@HomeActivity: summoner dto is not null");
            ImageView summonerIcon = (ImageView) headerLayout.findViewById(R.id.summoner_icon);
            String url = new URLConstructor().summonerIcon(summoner.profileIconId);
            Picasso.with(this)
                    .load(url)
                    .fit()
                    .transform(new CircleTransform()).into(summonerIcon);
        }
        //=========================================================================================

        ImageView dynamicQueue = (ImageView) findViewById(R.id.splash_dynamic_queue);
        ImageView soloQueue = (ImageView) findViewById(R.id.splash_solo_queue);
        ImageView team5 = (ImageView) findViewById(R.id.splash_team_5);
        ImageView team3 = (ImageView) findViewById(R.id.splash_team3);

        int screenWidth = ScreenUtil.getScreenWidth(this);
        int screenHeight = ScreenUtil.getScreenHeight(this);
        int width, height;

        if (!ScreenUtil.isLandscape(this)){
            width = screenWidth;
            height = screenHeight / 4;
        }
        else {
            width = screenWidth / 2;
            height = screenHeight;
        }

        dynamicQueue.getLayoutParams().width = width;
        dynamicQueue.getLayoutParams().height = height;
        soloQueue.getLayoutParams().width = width;
        soloQueue.getLayoutParams().height = height;
        team5.getLayoutParams().width = width;
        team5.getLayoutParams().height = height;
        team3.getLayoutParams().width = width;
        team3.getLayoutParams().height = height;

        dynamicQueue.requestLayout();
        soloQueue.requestLayout();
        team5.requestLayout();
        team3.requestLayout();

        Picasso.with(this).load(R.drawable.splash_akali).centerCrop().fit().into(dynamicQueue);
        Picasso.with(this).load(R.drawable.splash_amumu).centerCrop().fit().into(soloQueue);
        Picasso.with(this).load(R.drawable.splash_shyvana).centerCrop().fit().into(team5);
        Picasso.with(this).load(R.drawable.splash_jarvan).centerCrop().fit().into(team3);
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
            startActivity(new Intent(this, ProfileActivity.class));
            finish();
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

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            moveTaskToBack(true);
        }
    }
}
