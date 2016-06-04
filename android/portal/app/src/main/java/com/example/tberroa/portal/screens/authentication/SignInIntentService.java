package com.example.tberroa.portal.screens.authentication;

import android.app.IntentService;
import android.content.Intent;

import com.example.tberroa.portal.apimanager.APIMonitorService;
import com.example.tberroa.portal.data.LocalDB;
import com.example.tberroa.portal.data.Params;
import com.example.tberroa.portal.data.UserInfo;
import com.example.tberroa.portal.models.summoner.Summoner;
import com.example.tberroa.portal.updater.UpdateService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SignInIntentService extends IntentService {

    public SignInIntentService() {
        super("SignInIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // clear local database
        new LocalDB().clearDatabase(this);

        // initialize summoner info
        UserInfo userInfo = new UserInfo();

        // get the basic non stylized name
        String summonerName = userInfo.getBasicName(this);

        // query riot api for summoner dto
        List<String> name = new ArrayList<>();
        name.add(summonerName);
        Map<String, Summoner> summoners = new RiotAPI(this).getSummonersByName(name);

        if (summoners != null) {
            // save the summoner's id and profile icon id
            Summoner summoner = summoners.get(summonerName);
            userInfo.setId(this, summoner.id);
            userInfo.setIconId(this, summoner.profileIconId);

            // start api key service
            startService(new Intent(this, APIMonitorService.class));

            // start update service
            startService(new Intent(this, UpdateService.class));

            sendBroadcast(new Intent().setAction(Params.SIGN_IN_SUCCESS));
        } else {
            sendBroadcast(new Intent().setAction(Params.SIGN_IN_FAILED));
        }
    }

}
