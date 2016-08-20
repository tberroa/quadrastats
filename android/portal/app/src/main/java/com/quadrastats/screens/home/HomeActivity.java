package com.quadrastats.screens.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.quadrastats.R;
import com.quadrastats.screens.BaseActivity;
import com.quadrastats.screens.ScreenUtil;
import com.quadrastats.screens.account.AccountActivity;
import com.quadrastats.screens.friends.FriendsActivity;
import com.quadrastats.screens.stats.recent.RecentActivity;
import com.quadrastats.screens.stats.season.SeasonActivity;
import com.quadrastats.screens.stats.withfriends.WFActivity;
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
        ImageView manageAccountView = (ImageView) findViewById(R.id.manage_account_image);
        ImageView manageFriendsView = (ImageView) findViewById(R.id.manage_friends_image);
        ImageView recentView = (ImageView) findViewById(R.id.recent_image);
        ImageView seasonView = (ImageView) findViewById(R.id.season_image);
        ImageView withFriendsView = (ImageView) findViewById(R.id.with_friends_image);

        // get dimensions for images
        int width = ScreenUtil.screenWidth(this);
        int height = ScreenUtil.screenHeight(this) / 4;

        // resize image views
        manageAccountView.getLayoutParams().width = width;
        manageAccountView.getLayoutParams().height = height;
        manageAccountView.setLayoutParams(manageAccountView.getLayoutParams());
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
        Picasso.with(this).load(R.drawable.splash_blitz).resize(width, height)
                .centerCrop().into(manageAccountView);
        Picasso.with(this).load(R.drawable.splash_pool_party).resize(width, height)
                .centerCrop().into(manageFriendsView);
        Picasso.with(this).load(R.drawable.splash_sivir).resize(width, height)
                .centerCrop().into(recentView);
        Picasso.with(this).load(R.drawable.splash_veigar).resize(width, height)
                .centerCrop().into(seasonView);
        Picasso.with(this).load(R.drawable.splash_skt).resize(width, height)
                .centerCrop().into(withFriendsView);

        // initialize on click listeners
        FrameLayout manageAccountLayout = (FrameLayout) findViewById(R.id.manage_account_layout);
        FrameLayout manageFriendsLayout = (FrameLayout) findViewById(R.id.manage_friends_layout);
        FrameLayout recentLayout = (FrameLayout) findViewById(R.id.recent_layout);
        FrameLayout seasonLayout = (FrameLayout) findViewById(R.id.season_layout);
        FrameLayout withFriendsLayout = (FrameLayout) findViewById(R.id.with_friends_layout);
        manageAccountLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, AccountActivity.class));
                finish();
            }
        });
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
