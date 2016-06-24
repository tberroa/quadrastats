package com.example.tberroa.portal.screens.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.tberroa.portal.R;
import com.example.tberroa.portal.screens.BaseActivity;
import com.example.tberroa.portal.screens.ScreenUtil;
import com.example.tberroa.portal.screens.friends.FriendsActivity;
import com.example.tberroa.portal.screens.stats.recent.RecentActivity;
import com.example.tberroa.portal.screens.stats.season.SeasonActivity;
import com.example.tberroa.portal.screens.stats.withfriends.WithFriendsActivity;
import com.squareup.picasso.Picasso;

public class HomeActivity extends BaseActivity {

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            moveTaskToBack(true);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // initialize the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);

        // initialize image views
        int width = ScreenUtil.screenWidth(this);
        int height = ScreenUtil.screenHeight(this) / 4;
        ImageView manageFriendsView = (ImageView) findViewById(R.id.manage_friends_image);
        ImageView recentView = (ImageView) findViewById(R.id.recent_image);
        ImageView seasonView = (ImageView) findViewById(R.id.season_image);
        ImageView withFriendsView = (ImageView) findViewById(R.id.with_friends_image);
        Picasso.with(this).load(R.drawable.splash_pool_party)
                .centerCrop().resize(width, height).into(manageFriendsView);
        Picasso.with(this).load(R.drawable.splash_zed)
                .centerCrop().resize(width, height).into(recentView);
        Picasso.with(this).load(R.drawable.splash_shyvana)
                .centerCrop().resize(width, height).into(seasonView);
        Picasso.with(this).load(R.drawable.splash_jarvan)
                .centerCrop().resize(width, height).into(withFriendsView);

        // initialize labels
        String manageFriends = getResources().getString(R.string.manage_friends);
        String recentGames = getResources().getString(R.string.recent_games);
        String seasonTotals = getResources().getString(R.string.season_totals);
        String withFriends = getResources().getString(R.string.with_friends);
        TextView manageFriendsText = (TextView) findViewById(R.id.manage_friends_text);
        TextView recentText = (TextView) findViewById(R.id.recent_text);
        TextView seasonText = (TextView) findViewById(R.id.season_text);
        TextView withFriendsText = (TextView) findViewById(R.id.with_friends_text);
        manageFriendsText.setText(manageFriends);
        recentText.setText(recentGames);
        seasonText.setText(seasonTotals);
        withFriendsText.setText(withFriends);

        // initialize on click listeners
        RelativeLayout manageFriendsLayout = (RelativeLayout) findViewById(R.id.manage_friends_layout);
        RelativeLayout recentLayout = (RelativeLayout) findViewById(R.id.recent_layout);
        RelativeLayout seasonLayout = (RelativeLayout) findViewById(R.id.season_layout);
        RelativeLayout withFriendsLayout = (RelativeLayout) findViewById(R.id.with_friends_layout);
        manageFriendsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, FriendsActivity.class));
            }
        });
        recentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, RecentActivity.class));
            }
        });
        seasonLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, SeasonActivity.class));
            }
        });
        withFriendsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, WithFriendsActivity.class));
            }
        });
    }
}
