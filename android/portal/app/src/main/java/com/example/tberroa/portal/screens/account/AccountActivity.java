package com.example.tberroa.portal.screens.account;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.example.tberroa.portal.R;
import com.example.tberroa.portal.data.UserInfo;
import com.example.tberroa.portal.screens.authentication.AuthUtil;
import com.example.tberroa.portal.screens.home.HomeActivity;
import com.example.tberroa.portal.screens.BaseActivity;

public class AccountActivity extends BaseActivity {

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
        setContentView(R.layout.activity_account);

        // initialize the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.ma_activity_title);
        toolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.ic_back_button));
        toolbar.setNavigationOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else {
                    startActivity(new Intent(AccountActivity.this, HomeActivity.class));
                    finish();
                }
            }
        });

        // initialize views
        TextView emailView = (TextView) findViewById(R.id.email_view);
        emailView.setText(new UserInfo().getEmail(this));

        // initialize buttons
        Button signOutButton = (Button) findViewById(R.id.sign_out_button);
        signOutButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                AuthUtil.signOut(AccountActivity.this);
            }
        });
    }
}
