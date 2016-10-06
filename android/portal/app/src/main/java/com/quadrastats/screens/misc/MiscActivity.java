package com.quadrastats.screens.misc;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.quadrastats.R;
import com.quadrastats.screens.BaseActivity;
import com.quadrastats.screens.ScreenUtil;
import com.quadrastats.screens.authentication.AuthUtil;
import com.quadrastats.screens.home.HomeActivity;

public class MiscActivity extends BaseActivity {

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
        setContentView(R.layout.activity_misc);

        // initialize the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.misc_activity_title);
        toolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.ic_back_button));
        toolbar.setNavigationOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else {
                    startActivity(new Intent(MiscActivity.this, HomeActivity.class));
                    finish();
                }
            }
        });

        // initialize loading spinner
        int screenWidth = ScreenUtil.screenWidth(this);
        ProgressBar loadingSpinner = (ProgressBar) findViewById(R.id.loading_spinner);
        loadingSpinner.getLayoutParams().width = (25 * screenWidth) / 100;
        loadingSpinner.getLayoutParams().height = (25 * screenWidth) / 100;
        loadingSpinner.setLayoutParams(loadingSpinner.getLayoutParams());
        loadingSpinner.setVisibility(View.GONE);

        // initialize buttons
        ImageView aboutButton = (ImageView) findViewById(R.id.about_button);
        ImageView helpButton = (ImageView) findViewById(R.id.help_button);
        ImageView licenseButton = (ImageView) findViewById(R.id.license_button);
        ImageView signOutButton = (ImageView) findViewById(R.id.sign_out_button);
        aboutButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                new AboutDialog().show();
            }
        });
        helpButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                new HelpDialog().show();
            }
        });
        licenseButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                new LicenseDialog().show();
            }
        });
        signOutButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                AuthUtil.signOut(MiscActivity.this);
            }
        });
    }

    private class AboutDialog extends Dialog {

        AboutDialog() {
            super(MiscActivity.this, R.style.AppTheme_Dialog);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.dialog_about);
            setCancelable(true);
        }
    }

    private class HelpAdapter extends RecyclerView.Adapter<HelpAdapter.HelpViewHolder> {

        @Override
        public int getItemCount() {
            return 1;
        }

        @Override
        public void onBindViewHolder(HelpViewHolder viewHolder, int i) {
        }

        @Override
        public HelpViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            Context context = viewGroup.getContext();
            View v = LayoutInflater.from(context).inflate(R.layout.view_help, viewGroup, false);
            return new HelpViewHolder(v);
        }

        class HelpViewHolder extends RecyclerView.ViewHolder {

            HelpViewHolder(View itemView) {
                super(itemView);
            }
        }
    }

    private class HelpDialog extends Dialog {

        HelpDialog() {
            super(MiscActivity.this, R.style.AppTheme_Dialog);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.dialog_help);
            setCancelable(true);

            // initialize recycler view
            RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
            recyclerView.setAdapter(new HelpAdapter());
            recyclerView.setLayoutManager(new LinearLayoutManager(MiscActivity.this));
        }
    }

    private class LicenseAdapter extends RecyclerView.Adapter<LicenseAdapter.LicenseViewHolder> {

        @Override
        public int getItemCount() {
            return 1;
        }

        @Override
        public void onBindViewHolder(LicenseViewHolder viewHolder, int i) {
        }

        @Override
        public LicenseViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            Context context = viewGroup.getContext();
            View v = LayoutInflater.from(context).inflate(R.layout.view_licenses, viewGroup, false);
            return new LicenseViewHolder(v);
        }

        class LicenseViewHolder extends RecyclerView.ViewHolder {

            LicenseViewHolder(View itemView) {
                super(itemView);
            }
        }
    }

    private class LicenseDialog extends Dialog {

        LicenseDialog() {
            super(MiscActivity.this, R.style.AppTheme_Dialog);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.dialog_licenses);
            setCancelable(true);

            // initialize recycler view
            RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
            recyclerView.setAdapter(new LicenseAdapter());
            recyclerView.setLayoutManager(new LinearLayoutManager(MiscActivity.this));
        }
    }
}
