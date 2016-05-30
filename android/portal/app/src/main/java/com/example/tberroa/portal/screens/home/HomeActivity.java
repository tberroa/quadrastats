package com.example.tberroa.portal.screens.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.tberroa.portal.R;
import com.example.tberroa.portal.data.Params;
import com.example.tberroa.portal.screens.BaseActivity;
import com.example.tberroa.portal.screens.friends.FriendsActivity;
import com.example.tberroa.portal.screens.stats.recent.RecentActivity;
import com.example.tberroa.portal.screens.stats.season.SeasonActivity;
import com.example.tberroa.portal.screens.stats.withfriends.WithFriendsActivity;

public class HomeActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // initialize labels
        String manageFriends = getResources().getString(R.string.manage_friends);
        String recentGames = getResources().getString(R.string.recent_games);
        String seasonTotals = getResources().getString(R.string.season_totals);
        String withFriends = getResources().getString(R.string.with_friends);
        String[] labels = {manageFriends, recentGames, seasonTotals, withFriends};

        // set list view
        ListView listView = (ListView) findViewById(R.id.list_view);
        HomeAdapter homeAdapter = new HomeAdapter(this, labels);
        listView.setAdapter(homeAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                switch (position) {
                    case 0:
                        Intent manageFriends = new Intent(HomeActivity.this, FriendsActivity.class);
                        startActivity(manageFriends);
                        break;
                    case 1:
                        Intent recentGames = new Intent(HomeActivity.this, RecentActivity.class);
                        recentGames.putExtra("queue", Params.DYNAMIC_QUEUE);
                        startActivity(recentGames);
                        break;
                    case 2:
                        Intent seasonTotals = new Intent(HomeActivity.this, SeasonActivity.class);
                        seasonTotals.putExtra("queue", Params.DYNAMIC_QUEUE);
                        startActivity(seasonTotals);
                        break;
                    case 3:
                        Intent withFriends = new Intent(HomeActivity.this,WithFriendsActivity.class);
                        withFriends.putExtra("queue", Params.DYNAMIC_QUEUE);
                        startActivity(withFriends);
                        break;
                }
            }
        });
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
