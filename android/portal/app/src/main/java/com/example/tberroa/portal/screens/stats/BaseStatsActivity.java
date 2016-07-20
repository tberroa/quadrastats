package com.example.tberroa.portal.screens.stats;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.ActiveAndroid;
import com.example.tberroa.portal.R;
import com.example.tberroa.portal.data.LocalDB;
import com.example.tberroa.portal.data.Params;
import com.example.tberroa.portal.data.UserInfo;
import com.example.tberroa.portal.models.ModelUtil;
import com.example.tberroa.portal.models.requests.ReqMatchStats;
import com.example.tberroa.portal.models.stats.MatchStats;
import com.example.tberroa.portal.models.summoner.Summoner;
import com.example.tberroa.portal.network.Http;
import com.example.tberroa.portal.screens.BaseActivity;
import com.example.tberroa.portal.screens.friends.FriendsActivity;
import com.example.tberroa.portal.screens.home.HomeActivity;
import com.example.tberroa.portal.screens.stats.recent.RecentAsync;
import com.example.tberroa.portal.screens.stats.withfriends.WithFriendsAsync;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class BaseStatsActivity extends BaseActivity implements OnRefreshListener {

    private boolean useDataSwipeLayout;

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
    public void onRefresh() {
        SwipeRefreshLayout dataSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.data_swipe_layout);
        SwipeRefreshLayout messageSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.message_swipe_layout);

        if (useDataSwipeLayout) {
            dataSwipeLayout.setRefreshing(true);
        } else {
            messageSwipeLayout.setRefreshing(true);
        }
    }

    @Override
    public void setContentView(int layoutResID) {
        // base layout
        @SuppressLint("InflateParams")
        RelativeLayout baseLayout = (RelativeLayout) getLayoutInflater().inflate(R.layout.activity_base_stats, null);
        FrameLayout statsContentLayout = (FrameLayout) baseLayout.findViewById(R.id.stats_content_layout);

        // fill content layout with the provided layout
        View childStatsView = getLayoutInflater().inflate(layoutResID, null);
        statsContentLayout.addView(childStatsView);

        // set the view
        setContentView(baseLayout);

        // set back button
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.ic_back_button));
        toolbar.setNavigationOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else {
                    startActivity(new Intent(BaseStatsActivity.this, HomeActivity.class));
                    finish();
                }
            }
        });
    }

    public class RequestMatchStats extends AsyncTask<Integer, Void, Boolean[]> {

        public RecentAsync delegateRecent;
        public WithFriendsAsync delegateWithFriends;
        private int activityId;
        private List<MatchStats> matchStatsList;
        private Map<Long, Map<String, MatchStats>> matchStatsMapMap;
        private String postResponse = "";

        @Override
        protected Boolean[] doInBackground(Integer... params) {
            LocalDB localDB = new LocalDB();

            // save activity id
            activityId = params[0];

            // get user
            Summoner user = localDB.summoner(new UserInfo().getId(BaseStatsActivity.this));

            // create the request object
            ReqMatchStats request = new ReqMatchStats();
            request.region = user.region;
            String keys = user.key + "," + user.friends;
            request.keys = new ArrayList<>(Arrays.asList(keys.split(",")));

            // make the request
            try {
                String url = "http://52.90.34.48/stats/match.json";
                postResponse = new Http().post(url, ModelUtil.toJson(request, ReqMatchStats.class));
            } catch (IOException e) {
                Log.e(Params.TAG_EXCEPTIONS, "@RecentActivity: " + e.getMessage());
            }

            // initialize the result array which is returned
            Boolean[] result = {false, false};

            // process the response
            if (postResponse.contains("champ_level")) {
                // successful http request
                result[0] = true;

                // get the match stats objects
                Type type = new TypeToken<List<MatchStats>>() {
                }.getType();
                List<MatchStats> serverMatchStatsList = ModelUtil.fromJsonList(postResponse, type);

                // save any new match stats
                ActiveAndroid.beginTransaction();
                try {
                    for (MatchStats matchStats : serverMatchStatsList) {
                        if (localDB.matchStats(matchStats.summoner_key, matchStats.match_id) == null) {
                            // received new data
                            result[1] = true;
                            matchStats.save();
                        }
                    }
                    ActiveAndroid.setTransactionSuccessful();
                } finally {
                    ActiveAndroid.endTransaction();
                }
            }

            // gather the appropriate data
            List<String> keysList = new ArrayList<>(Arrays.asList(keys.split(",")));
            switch (activityId) {
                case 1: // recent activity
                    matchStatsList = localDB.matchStatsList(keysList, 0, null, null);
                    break;
                case 3: // with friends activity
                    List<MatchStats> matches = localDB.matchStatsList(user.key);
                    List<Long> matchIds = new ArrayList<>();
                    for (MatchStats match : matches) {
                        matchIds.add(match.match_id);
                    }
                    matchStatsMapMap = localDB.matchesWithFriends(matchIds, keysList);
                    break;
            }

            return result;
        }

        @Override
        protected void onPostExecute(Boolean[] result) {
            // clear swipe refresh layouts
            SwipeRefreshLayout dataSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.data_swipe_layout);
            dataSwipeLayout.setRefreshing(false);
            SwipeRefreshLayout messageSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.message_swipe_layout);
            messageSwipeLayout.setRefreshing(false);

            if (result[0]) { // successful http request
                if (result[1]) { // and received new data
                    switch (activityId) {
                        case 1: // recent activity
                            if (!useDataSwipeLayout) {
                                // switch to data swipe layout
                                useDataSwipeLayout(messageSwipeLayout, dataSwipeLayout);
                            }
                            delegateRecent.displayData(matchStatsList);
                            break;
                        case 3: // with friends activity
                            if (!matchStatsMapMap.isEmpty()) {
                                if (!useDataSwipeLayout) {
                                    // switch to data swipe layout
                                    useDataSwipeLayout(messageSwipeLayout, dataSwipeLayout);
                                }
                                delegateWithFriends.displayData(matchStatsMapMap);
                            }
                            break;
                    }
                }
            } else { // display error
                Toast.makeText(BaseStatsActivity.this, postResponse, Toast.LENGTH_SHORT).show();
            }
        }

        private void useDataSwipeLayout(SwipeRefreshLayout messageSwipeLayout, SwipeRefreshLayout dataSwipeLayout) {
            messageSwipeLayout.setEnabled(false);
            ScrollView scrollView = (ScrollView) findViewById(R.id.scroll_view);
            scrollView.setVisibility(View.GONE);
            dataSwipeLayout.setEnabled(true);
            useDataSwipeLayout = true;
        }
    }

    public class ViewInitialization extends AsyncTask<Integer, Void, List<String>> {

        public RecentAsync delegateRecent;
        public WithFriendsAsync delegateWithFriends;
        private int activityId;
        private SwipeRefreshLayout dataSwipeLayout;
        private TextView loadingView;
        private List<MatchStats> matchStatsList;
        private Map<Long, Map<String, MatchStats>> matchStatsMapMap;
        private SwipeRefreshLayout messageSwipeLayout;
        private TextView noDataView;
        private LinearLayout noFriendsLayout;
        private ScrollView scrollView;

        @Override
        protected List<String> doInBackground(Integer... params) {
            LocalDB localDB = new LocalDB();

            // save activity id
            activityId = params[0];

            // get user
            Summoner user = localDB.summoner(new UserInfo().getId(BaseStatsActivity.this));

            // construct list of keys
            List<String> keys = new ArrayList<>(Arrays.asList((user.key + "," + user.friends).split(",")));

            // if user has friends, gather the appropriate data
            if (keys.size() > 1) {
                switch (activityId) {
                    case 1: // recent activity
                        matchStatsList = localDB.matchStatsList(keys, 0, null, null);
                        break;
                    case 3: // with friends activity
                        List<MatchStats> matches = localDB.matchStatsList(user.key);
                        List<Long> matchIds = new ArrayList<>();
                        for (MatchStats match : matches) {
                            matchIds.add(match.match_id);
                        }
                        matchStatsMapMap = localDB.matchesWithFriends(matchIds, keys);
                        break;
                }
            }

            return keys;
        }

        @Override
        protected void onPostExecute(List<String> keys) {
            loadingView.setVisibility(View.GONE);

            // display no friends layout if user has no friends
            if (keys.size() == 1) {
                useDataSwipeLayout = false;
                noFriendsLayout.setVisibility(View.VISIBLE);
                Button goToFriendsActivity = (Button) findViewById(R.id.add_friends_button);
                goToFriendsActivity.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(BaseStatsActivity.this, FriendsActivity.class));
                        finish();
                    }
                });
                return;
            }

            // display no data view if no data was found
            switch (activityId) {
                case 1:
                    if (matchStatsList.isEmpty()) {
                        useDataSwipeLayout = false;
                        messageSwipeLayout.setEnabled(true);
                        noDataView.setVisibility(View.VISIBLE);
                        return;
                    }
                    break;
                case 3:
                    if (matchStatsMapMap.isEmpty()) {
                        useDataSwipeLayout = false;
                        messageSwipeLayout.setEnabled(true);
                        noDataView.setVisibility(View.VISIBLE);
                        return;
                    }
                    break;
            }

            // there is data to display
            scrollView.setVisibility(View.GONE);
            useDataSwipeLayout = true;
            dataSwipeLayout.setEnabled(true);
            switch (activityId) {
                case 1:
                    delegateRecent.displayData(matchStatsList);
                    break;
                case 3:
                    delegateWithFriends.displayData(matchStatsMapMap);
                    break;
            }
        }

        @Override
        protected void onPreExecute() {
            // initialize swipe refresh layouts
            dataSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.data_swipe_layout);
            dataSwipeLayout.setEnabled(false);
            messageSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.message_swipe_layout);
            messageSwipeLayout.setEnabled(false);

            // initialize message layouts and views
            scrollView = (ScrollView) findViewById(R.id.scroll_view);
            scrollView.setVisibility(View.VISIBLE);
            loadingView = (TextView) findViewById(R.id.loading_view);
            loadingView.setVisibility(View.VISIBLE);
            noFriendsLayout = (LinearLayout) findViewById(R.id.no_friends_layout);
            noFriendsLayout.setVisibility(View.GONE);
            noDataView = (TextView) findViewById(R.id.no_data_view);
            noDataView.setVisibility(View.GONE);
        }
    }
}
