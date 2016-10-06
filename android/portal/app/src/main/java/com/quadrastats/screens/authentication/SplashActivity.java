package com.quadrastats.screens.authentication;

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

import com.quadrastats.R;
import com.quadrastats.data.Constants;
import com.quadrastats.data.RiotData;
import com.quadrastats.data.UserData;
import com.quadrastats.models.ModelUtil;
import com.quadrastats.models.datadragon.Champion;
import com.quadrastats.models.datadragon.Champions;
import com.quadrastats.models.summoner.Summoner;
import com.quadrastats.network.Http;
import com.quadrastats.network.HttpResponse;
import com.quadrastats.screens.ScreenUtil;
import com.quadrastats.screens.home.HomeActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SplashActivity extends AppCompatActivity {

    private boolean error;
    private String errorMessage;
    private boolean inView;
    private boolean paused;
    private ProgressBar progressBar;
    private boolean ready;
    private Summoner summoner;

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

        // extract users summoner object
        String summonerJson = getIntent().getExtras().getString("summoner");
        summoner = ModelUtil.fromJson(summonerJson, Summoner.class);

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

    private void next(boolean error) {
        if (error) {
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
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
            HttpResponse postResponse1 = null;
            try {
                String url = Constants.URL_GET_CHAMPIONS;
                postResponse1 = new Http().get(url);
            } catch (IOException e) {
                Log.e(Constants.TAG_EXCEPTIONS, "@" + getClass().getSimpleName() + ": " + e.getMessage());
            }

            // handle the response
            postResponse1 = ScreenUtil.responseHandler(SplashActivity.this, postResponse1);

            // parse the response
            if (postResponse1.valid) {
                Champions champions = ModelUtil.fromJson(postResponse1.body, Champions.class);
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
                errorMessage = postResponse1.error;
                return true;
            }
            publishProgress(1000);

            // save user data
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
