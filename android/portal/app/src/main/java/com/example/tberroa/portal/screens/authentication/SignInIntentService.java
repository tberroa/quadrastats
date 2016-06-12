package com.example.tberroa.portal.screens.authentication;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.example.tberroa.portal.data.LocalDB;
import com.example.tberroa.portal.data.Params;
import com.example.tberroa.portal.data.UserInfo;
import com.example.tberroa.portal.models.ModelUtil;
import com.example.tberroa.portal.models.requests.ReqGetSummoners;
import com.example.tberroa.portal.models.requests.ReqSignIn;
import com.example.tberroa.portal.models.summoner.Summoner;
import com.example.tberroa.portal.network.Http;

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
        Summoner user = new LocalDB().getSummonerById(new UserInfo().getId(this));

        // get summoner objects for the friends
        if (user.friends != null) {
            // turn the comma separated string into a java list
            List<String> keys = new ArrayList<String>(Arrays.asList(user.friends.split(",")));

            // create the request object
            ReqGetSummoners request = new ReqGetSummoners();
            request.region = user.region;
            request.keys = keys;

            // make the request
            String postResponse = null;
            try {
                String url = Params.BURL_SIGN_IN;
                postResponse = new Http().post(url, ModelUtil.toJson(request, ReqGetSummoners.class));
            } catch (java.io.IOException e) {
                Log.e(Params.TAG_EXCEPTIONS, "@SignInActivity: " + e.getMessage());
            }

        }

    }
}
