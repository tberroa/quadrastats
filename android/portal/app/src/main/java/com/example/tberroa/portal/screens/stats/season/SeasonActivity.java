package com.example.tberroa.portal.screens.stats.season;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.support.v7.widget.Toolbar;
import android.view.View.OnClickListener;

import com.example.tberroa.portal.R;
import com.example.tberroa.portal.screens.BaseActivity;
import com.example.tberroa.portal.screens.home.HomeActivity;

public class SeasonActivity extends BaseActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_season);

        // initialize the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.st_activity_title);
        toolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.ic_back_button));
        toolbar.setNavigationOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else {
                    startActivity(new Intent(SeasonActivity.this, HomeActivity.class));
                    finish();
                }
            }
        });
    }
}
