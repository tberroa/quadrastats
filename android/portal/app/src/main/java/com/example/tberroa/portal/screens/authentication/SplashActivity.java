package com.example.tberroa.portal.screens.authentication;

// Intermediate activity which occurs right after signing in. It's purpose
// is to allow for some initialization processing to occur.

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.example.tberroa.portal.R;
import com.example.tberroa.portal.data.Constants;
import com.example.tberroa.portal.data.LocalDB;
import com.example.tberroa.portal.data.RiotData;
import com.example.tberroa.portal.data.UserInfo;
import com.example.tberroa.portal.models.ModelUtil;
import com.example.tberroa.portal.models.datadragon.Champion;
import com.example.tberroa.portal.models.datadragon.Champions;
import com.example.tberroa.portal.models.requests.ReqGetSummoners;
import com.example.tberroa.portal.models.summoner.Summoner;
import com.example.tberroa.portal.network.Http;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new DataInitialization().execute();
    }

    private class DataInitialization extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            LocalDB localDB = new LocalDB();
            UserInfo userInfo = new UserInfo();
            RiotData riotData = new RiotData();

            // get and save locally each friend summoner object
            Summoner user = localDB.summoner(userInfo.getId(SplashActivity.this));
            if (!"".equals(user.friends)) {
                // turn the comma separated string into a java list
                List<String> keys = new ArrayList<>(Arrays.asList(user.friends.split(",")));

                // create the request object
                ReqGetSummoners request = new ReqGetSummoners();
                request.region = user.region;
                request.keys = keys;

                // make the request
                String postResponse = "";
                try {
                    String url = Constants.URL_GET_SUMMONERS;
                    postResponse = new Http().post(url, ModelUtil.toJson(request, ReqGetSummoners.class));
                } catch (IOException e) {
                    Log.e(Constants.TAG_EXCEPTIONS, "@" + getClass().getSimpleName() + ": " + e.getMessage());
                }

                // save
                if (postResponse.contains(Constants.VALID_GET_SUMMONERS)) {
                    Type type = new TypeToken<List<Summoner>>() {
                    }.getType();
                    List<Summoner> friends = ModelUtil.fromJsonList(postResponse, type);

                    for (Summoner friend : friends) {
                        friend.save();
                    }
                } else {
                    return true;
                }
            }

            // get list of current champions and the most recent data dragon version
            // make the request
            String postResponse = "";
            try {
                String url = Constants.URL_GET_CHAMPIONS;
                postResponse = new Http().get(url);
            } catch (IOException e) {
                Log.e(Constants.TAG_EXCEPTIONS, "@" + getClass().getSimpleName() + ": " + e.getMessage());
            }

            // parse the response
            if (postResponse.contains(Constants.VALID_GET_CHAMPIONS)) {
                Champions champions = ModelUtil.fromJson(postResponse, Champions.class);
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

            return false;
        }

        @Override
        protected void onPostExecute(Boolean error) {
            if (error) {
                String message = getString(R.string.network_error);
                Toast.makeText(SplashActivity.this, message, Toast.LENGTH_SHORT).show();
                startActivity(new Intent(SplashActivity.this, SignInActivity.class));
            }
        }
    }
}
