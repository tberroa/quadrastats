package com.example.tberroa.portal.activities;

// the signed in summoners profile

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.Button;

import com.example.tberroa.portal.R;
import com.example.tberroa.portal.helpers.AuthenticationUtil;

public class ProfileActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Button signOutButton = (Button) findViewById(R.id.sign_out);
        signOutButton.setOnClickListener(signOutButtonListener);
    }

    private final View.OnClickListener signOutButtonListener = new View.OnClickListener() {
        public void onClick(View v) {
            AuthenticationUtil.signOut(ProfileActivity.this);
        }
    };

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
