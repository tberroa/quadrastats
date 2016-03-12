package com.example.tberroa.portal.screens.authentication;

import android.app.IntentService;
import android.content.Intent;

import com.example.tberroa.portal.data.Params;
import com.example.tberroa.portal.data.RiotAPI;
import com.example.tberroa.portal.data.SummonerInfo;
import com.example.tberroa.portal.models.summoner.SummonerDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SignInIntentService extends IntentService {

    public SignInIntentService() {
        super("SignInIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // initialize summoner info
        SummonerInfo summonerInfo = new SummonerInfo();

        // get the basic non stylized name
        String summonerName = summonerInfo.getBasicName(this);

        // query riot api for summoner dto
        List<String> name = new ArrayList<>();
        name.add(summonerName);
        Map<String, SummonerDto> summoners = new RiotAPI(this).getSummonersByName(name);

        // if query was successful, save the summoner's id and profile icon id
        if (summoners != null){
            SummonerDto summoner = summoners.get(summonerName);
            summonerInfo.setId(this, summoner.id);
            summonerInfo.setIconId(this, summoner.profileIconId);
            sendBroadcast(new Intent().setAction(Params.SIGN_IN_SUCCESS));
        }
        else{
            sendBroadcast(new Intent().setAction(Params.SIGN_IN_FAILED));
        }
    }

}
