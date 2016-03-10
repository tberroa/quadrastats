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
import com.example.tberroa.portal.screens.stats.StatsActivity;

public class HomeActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // initialize labels
        String dynamicQueue = getResources().getString(R.string.dynamic_queue);
        String soloQueue = getResources().getString(R.string.solo_queue);
        String team5 = getResources().getString(R.string.team_5);
        String team3 = getResources().getString(R.string.team_3);
        String[] labels = {dynamicQueue, soloQueue, team5, team3};

        // set list view
        ListView listView = (ListView) findViewById(R.id.list_view);
        HomeAdapter homeAdapter = new HomeAdapter(this, labels);
        listView.setAdapter(homeAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,int position, long arg3) {
                Intent intent = new Intent(HomeActivity.this, StatsActivity.class);
                switch (position) {
                    case 0:
                        intent.putExtra("queue", Params.DYNAMIC_QUEUE);
                        break;
                    case 1:
                        intent.putExtra("queue", Params.SOLO_QUEUE);
                        break;
                    case 2:
                        intent.putExtra("queue", Params.TEAM_5);
                        break;
                    case 3:
                        intent.putExtra("queue", Params.TEAM_3);
                        break;

                }
                startActivity(intent);
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
