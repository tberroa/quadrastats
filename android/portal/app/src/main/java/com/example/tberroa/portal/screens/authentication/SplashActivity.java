package com.example.tberroa.portal.screens.authentication;

// Intermediate activity which occurs right after signing in. It's purpose
// is to allow for some initialization processing to occur.

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.animation.DecelerateInterpolator;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.activeandroid.ActiveAndroid;
import com.example.tberroa.portal.R;
import com.example.tberroa.portal.data.Constants;
import com.example.tberroa.portal.data.RiotData;
import com.example.tberroa.portal.data.UserData;
import com.example.tberroa.portal.models.ModelUtil;
import com.example.tberroa.portal.models.datadragon.Champion;
import com.example.tberroa.portal.models.datadragon.Champions;
import com.example.tberroa.portal.models.requests.ReqGetSummoners;
import com.example.tberroa.portal.models.requests.ReqMatchStats;
import com.example.tberroa.portal.models.stats.MatchStats;
import com.example.tberroa.portal.models.summoner.Summoner;
import com.example.tberroa.portal.models.summoner.User;
import com.example.tberroa.portal.network.Http;
import com.example.tberroa.portal.screens.home.HomeActivity;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SplashActivity extends AppCompatActivity {

    private boolean error;
    private boolean inView;
    private boolean paused;
    private ProgressBar progressBar;
    private boolean ready;
    private Summoner summoner;
    private User user;

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // move to background if app is not in view
        if (!getIntent().getBooleanExtra("in_view", true)) {
            moveTaskToBack(true);
        }

        // extract summoner and user objects
        String summonerJson = getIntent().getExtras().getString("summoner");
        String userJson = getIntent().getExtras().getString("user");
        summoner = ModelUtil.fromJson(summonerJson, Summoner.class);
        user = ModelUtil.fromJson(userJson, User.class);

        // initialize progress bar
        progressBar = (ProgressBar) findViewById(R.id.splash_progress_bar);
        progressBar.setMax(1000);

        new DataInitialization().execute();
    }

    @Override
    protected void onPause() {
        super.onPause();
        paused = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        inView = true;
        paused = false;
        if (ready) {
            next(error);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        inView = false;
    }

    protected void next(boolean error) {
        if (error) {
            String message = getString(R.string.network_error);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, SignInActivity.class));
        } else {
            Intent homeActivity = new Intent(this, HomeActivity.class);
            homeActivity.putExtra("in_view", inView);
            startActivity(homeActivity);
        }
        finish();
    }

    private class DataInitialization extends AsyncTask<Void, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            RiotData riotData = new RiotData();
            UserData userData = new UserData();

            // 1. get list of current champions and the most recent data dragon version
            // make the request
            String postResponse1 = "";
            try {
                String url = Constants.URL_GET_CHAMPIONS;
                postResponse1 = new Http().get(url);
            } catch (IOException e) {
                Log.e(Constants.TAG_EXCEPTIONS, "@" + getClass().getSimpleName() + ": " + e.getMessage());
            }

            // parse the response
            if (postResponse1.contains(Constants.VALID_GET_CHAMPIONS)) {
                Champions champions = ModelUtil.fromJson(postResponse1, Champions.class);
                riotData.setVersion(SplashActivity.this, champions.version);
                riotData.setChampionsMap(SplashActivity.this, champions.data);
                List<Champion> championsList = new ArrayList<>(champions.data.values());
                Collections.sort(championsList, new Comparator<Champion>() {
                    @Override
                    public int compare(Champion object1, Champion object2) {
                        return object1.name.compareTo(object2.name);
                    }
                });
                riotData.setChampionsList(SplashActivity.this, championsList);
            } else {
                return true;
            }
            publishProgress(333);

            // 2. get and save locally each friend summoner object
            if (!"".equals(summoner.friends)) {
                // create the request object
                ReqGetSummoners request = new ReqGetSummoners();
                request.region = summoner.region;
                request.keys = new ArrayList<>(Arrays.asList(summoner.friends.split(",")));

                // make the request
                String postResponse2 = "";
                try {
                    String url = Constants.URL_GET_SUMMONERS;
                    postResponse2 = new Http().post(url, ModelUtil.toJson(request, ReqGetSummoners.class));
                } catch (IOException e) {
                    Log.e(Constants.TAG_EXCEPTIONS, "@" + getClass().getSimpleName() + ": " + e.getMessage());
                }

                // save
                if (postResponse2.contains(Constants.VALID_GET_SUMMONERS)) {
                    Type type = new TypeToken<List<Summoner>>() {
                    }.getType();
                    List<Summoner> friends = ModelUtil.fromJsonList(postResponse2, type);
                    ActiveAndroid.beginTransaction();
                    try {
                        for (Summoner friend : friends) {
                            friend.save();
                        }
                        ActiveAndroid.setTransactionSuccessful();
                    } finally {
                        ActiveAndroid.endTransaction();
                    }
                } else {
                    return true;
                }
            }
            publishProgress(666);

            // 3. get match stats for user and friends
            if (!"".equals(summoner.friends)) {
                // create the request object
                ReqMatchStats request = new ReqMatchStats();
                request.region = summoner.region;
                String keys = summoner.key + "," + summoner.friends;
                request.keys = new ArrayList<>(Arrays.asList(keys.split(",")));

                // make the request
                String postResponse3 = "";
                try {
                    String url = Constants.URL_GET_MATCH_STATS;
                    postResponse3 = new Http().post(url, ModelUtil.toJson(request, ReqMatchStats.class));
                } catch (IOException e) {
                    Log.e(Constants.TAG_EXCEPTIONS, "@" + getClass().getSimpleName() + ": " + e.getMessage());
                }

                // save
                if (postResponse3.contains(Constants.VALID_GET_MATCH_STATS)) {
                    Type type = new TypeToken<List<MatchStats>>() {
                    }.getType();
                    List<MatchStats> matchStatsList = ModelUtil.fromJsonList(postResponse3, type);
                    ActiveAndroid.beginTransaction();
                    try {
                        for (MatchStats matchStats : matchStatsList) {
                            matchStats.save();
                        }
                        ActiveAndroid.setTransactionSuccessful();
                    } finally {
                        ActiveAndroid.endTransaction();
                    }
                }
            }
            publishProgress(1000);

            // save user data
            userData.setEmail(SplashActivity.this, user.email);
            userData.setId(SplashActivity.this, summoner.summoner_id);
            userData.setSignInStatus(SplashActivity.this, true);

            // save the user's summoner object locally
            summoner.save();

            return false;
        }

        @Override
        protected void onPostExecute(Boolean error) {
            if (!paused) {
                next(error);
            } else {
                ready = true;
                SplashActivity.this.error = error;
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            int current = progressBar.getProgress();
            ObjectAnimator animation = ObjectAnimator.ofInt(progressBar, "progress", current, values[0]);
            animation.setDuration(500);
            animation.setInterpolator(new DecelerateInterpolator());
            animation.start();
        }
    }
}
