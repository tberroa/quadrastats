package com.example.tberroa.portal.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.widget.ListView;

import com.example.tberroa.portal.R;
import com.example.tberroa.portal.adapters.HomeAdapter;
import com.example.tberroa.portal.data.Params;
import com.example.tberroa.portal.data.UpdateServiceState;
import com.example.tberroa.portal.services.UpdateService;

public class HomeActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // check if update service needs to be started up
        int state = new UpdateServiceState().get(this);
        if (state == 0){
            startService(new Intent(this, UpdateService.class));
        }
        Log.d(Params.TAG_DEBUG, "@HomeActivity: updateServiceState is " + Integer.toString(state));

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
