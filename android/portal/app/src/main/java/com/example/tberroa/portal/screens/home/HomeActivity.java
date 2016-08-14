package com.example.tberroa.portal.screens.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tberroa.portal.R;
import com.example.tberroa.portal.screens.BaseActivity;
import com.example.tberroa.portal.screens.ScreenUtil;
import com.example.tberroa.portal.screens.friends.FriendsActivity;
import com.example.tberroa.portal.screens.stats.recent.RecentActivity;
import com.example.tberroa.portal.screens.stats.season.SeasonActivity;
import com.example.tberroa.portal.screens.stats.withfriends.WFActivity;
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

        // move to background if app is not in view
        if (!getIntent().getBooleanExtra("in_view", true)) {
            moveTaskToBack(true);
        }

        // initialize the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);

        // initialize image views
        ImageView manageFriendsView = (ImageView) findViewById(R.id.manage_friends_image);
        ImageView recentView = (ImageView) findViewById(R.id.recent_image);
        ImageView seasonView = (ImageView) findViewById(R.id.season_image);
        ImageView withFriendsView = (ImageView) findViewById(R.id.with_friends_image);

        // get dimensions for images
        int width = ScreenUtil.screenWidth(this);
        int height = ScreenUtil.screenHeight(this) / 4;

        // resize image views
        manageFriendsView.getLayoutParams().width = width;
        manageFriendsView.getLayoutParams().height = height;
        manageFriendsView.setLayoutParams(manageFriendsView.getLayoutParams());
        recentView.getLayoutParams().width = width;
        recentView.getLayoutParams().height = height;
        recentView.setLayoutParams(recentView.getLayoutParams());
        seasonView.getLayoutParams().width = width;
        seasonView.getLayoutParams().height = height;
        seasonView.setLayoutParams(seasonView.getLayoutParams());
        withFriendsView.getLayoutParams().width = width;
        withFriendsView.getLayoutParams().height = height;
        withFriendsView.setLayoutParams(withFriendsView.getLayoutParams());

        // load images into image views
        Picasso.with(this).load(R.drawable.splash_pool_party).resize(width, height)
                .centerCrop().into(manageFriendsView);
        Picasso.with(this).load(R.drawable.splash_zed).resize(width, height)
                .centerCrop().into(recentView);
        Picasso.with(this).load(R.drawable.splash_shyvana).resize(width, height)
                .centerCrop().into(seasonView);
        Picasso.with(this).load(R.drawable.splash_jarvan).resize(width, height)
                .centerCrop().into(withFriendsView);

        // initialize labels
        String manageFriends = getResources().getString(R.string.mf_activity_title);
        String recentGames = getResources().getString(R.string.rg_activity_title);
        String seasonTotals = getResources().getString(R.string.st_activity_title);
        String withFriends = getResources().getString(R.string.gwf_activity_title);
        TextView manageFriendsText = (TextView) findViewById(R.id.manage_friends_text);
        TextView recentText = (TextView) findViewById(R.id.recent_text);
        TextView seasonText = (TextView) findViewById(R.id.season_text);
        TextView withFriendsText = (TextView) findViewById(R.id.with_friends_text);
        manageFriendsText.setText(manageFriends);
        recentText.setText(recentGames);
        seasonText.setText(seasonTotals);
        withFriendsText.setText(withFriends);

        // initialize on click listeners
        FrameLayout manageFriendsLayout = (FrameLayout) findViewById(R.id.manage_friends_layout);
        FrameLayout recentLayout = (FrameLayout) findViewById(R.id.recent_layout);
        FrameLayout seasonLayout = (FrameLayout) findViewById(R.id.season_layout);
        FrameLayout withFriendsLayout = (FrameLayout) findViewById(R.id.with_friends_layout);
        manageFriendsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, FriendsActivity.class));
                finish();
            }
        });
        recentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, RecentActivity.class));
                finish();
            }
        });
        seasonLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, SeasonActivity.class));
                finish();
            }
        });
        withFriendsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, WFActivity.class));
                finish();
            }
        });
    }
}
