package com.example.tberroa.portal.activities;

// the signed in summoners profile

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.NavUtils;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tberroa.portal.R;
import com.example.tberroa.portal.data.Params;
import com.example.tberroa.portal.data.SummonerInfo;
import com.example.tberroa.portal.database.LocalDB;
import com.example.tberroa.portal.database.URLConstructor;
import com.example.tberroa.portal.helpers.AuthenticationUtil;
import com.example.tberroa.portal.helpers.CircleTransform;
import com.example.tberroa.portal.models.summoner.SummonerDto;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    final private LocalDB localDB = new LocalDB();
    final private SummonerInfo summonerInfo = new SummonerInfo();

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
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

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        Button signOutButton = (Button) findViewById(R.id.sign_out);
        signOutButton.setOnClickListener(signOutButtonListener);
    }

    private final View.OnClickListener signOutButtonListener = new View.OnClickListener() {
        public void onClick(View v) {
            AuthenticationUtil.signOut(ProfileActivity.this);
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.action_settings:
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
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        }
    }
}
