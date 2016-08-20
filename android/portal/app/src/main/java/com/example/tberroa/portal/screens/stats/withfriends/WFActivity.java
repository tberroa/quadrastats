package com.example.tberroa.portal.screens.stats.withfriends;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TabLayout.TabLayoutOnPageChangeListener;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.example.tberroa.portal.R;
import com.example.tberroa.portal.models.stats.MatchStats;
import com.example.tberroa.portal.screens.stats.BaseStatsActivity;
import com.example.tberroa.portal.screens.stats.WinRatesDialog;

import java.util.Map;

public class WFActivity extends BaseStatsActivity implements WFAsync {

    public void displayData(Map<Long, Map<String, MatchStats>> matchStatsMapMap) {
        // update the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setOnMenuItemClickListener(new MenuListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                super.onMenuItemClick(item);
                switch (item.getItemId()) {
                    case R.id.win_rates:
                        new WinRatesDialog(WFActivity.this, null, matchStatsMapMap, staticRiotData).show();
                }
                return true;
            }
        });

        // initialize the tab layout
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.removeAllTabs();
        for (int i = 0; i < matchStatsMapMap.size(); i++) {
            tabLayout.addTab(tabLayout.newTab());
        }
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        // initialize the view pager
        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        int numOfTabs = tabLayout.getTabCount();
        FragmentManager fM = getSupportFragmentManager();
        viewPager.setAdapter(new PageAdapter(fM, numOfTabs, matchStatsMapMap, staticRiotData));

        // set page change listener so user won't invoke refresh layout while changing views
        viewPager.addOnPageChangeListener(new TabLayoutOnPageChangeListener(tabLayout) {
            @Override
            public void onPageScrollStateChanged(int state) {
                SwipeRefreshLayout dataSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.data_swipe_layout);
                dataSwipeLayout.setEnabled(state == ViewPager.SCROLL_STATE_IDLE);
            }
        });

        // sync together the tab layout and view pager
        tabLayout.setupWithViewPager(viewPager);

        // set tab labels
        for (int i = 0; i < matchStatsMapMap.size(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab != null) {
                tab.setText(String.valueOf(i));
            }
        }

        tabLayout.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_with_friends);

        // initialize the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.gwf_activity_title);
        toolbar.inflateMenu(R.menu.with_friends_menu);

        // set tab layout to gone while view is initialized
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setVisibility(View.GONE);

        // set swipe refresh layout listeners
        SwipeRefreshLayout dataSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.data_swipe_layout);
        dataSwipeLayout.setOnRefreshListener(this);
        SwipeRefreshLayout messageSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.message_swipe_layout);
        messageSwipeLayout.setOnRefreshListener(this);

        // initialize view
        ViewInitialization viewInitialization = new ViewInitialization();
        viewInitialization.delegateWithFriends = this;
        viewInitialization.execute(3);
    }

    @Override
    public void onRefresh() {
        RequestMatchStats requestMatchStats = new RequestMatchStats();
        requestMatchStats.delegateWithFriends = this;
        requestMatchStats.execute(3);
    }
}