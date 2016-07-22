package com.example.tberroa.portal.screens.authentication;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.example.tberroa.portal.data.Constants;
import com.example.tberroa.portal.data.LocalDB;
import com.example.tberroa.portal.data.UserInfo;
import com.example.tberroa.portal.models.ModelUtil;
import com.example.tberroa.portal.models.requests.ReqGetSummoners;
import com.example.tberroa.portal.models.summoner.Summoner;
import com.example.tberroa.portal.network.Http;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SignInIntentService extends IntentService {

    public SignInIntentService() {
        super("SignInIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // get the user's summoner object
        Summoner user = new LocalDB().summoner(new UserInfo().getId(this));

        // get and save locally the summoner objects for the friends
        if (user.friends != null) {
            // turn the comma separated string into a java list
            List<String> keys = new ArrayList<>(Arrays.asList(user.friends.split(",")));

            // create the request object
            ReqGetSummoners request = new ReqGetSummoners();
            request.region = user.region;
            request.keys = keys;

            // make the request
            String postResponse = null;
            try {
                String url = Constants.URL_GET_SUMMONERS;
                postResponse = new Http().post(url, ModelUtil.toJson(request, ReqGetSummoners.class));
            } catch (IOException e) {
                Log.e(Constants.TAG_EXCEPTIONS, "@" + getClass().getSimpleName() + ": " + e.getMessage());
            }

            // save
            if ((postResponse != null) && postResponse.contains(Constants.VALID_GET_SUMMONERS)) {
                Type type = new TypeToken<List<Summoner>>() {
                }.getType();
                List<Summoner> friends = ModelUtil.fromJsonList(postResponse, type);

                for (Summoner friend : friends) {
                    friend.save();
                }
            }
        }
    }
}
