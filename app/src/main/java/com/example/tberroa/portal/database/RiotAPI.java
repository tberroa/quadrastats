package com.example.tberroa.portal.database;

import android.content.Context;
import android.util.Log;

import com.example.tberroa.portal.data.Params;
import com.example.tberroa.portal.data.UserInfo;
import com.example.tberroa.portal.models.summoner.SummonerDto;
import com.example.tberroa.portal.network.Http;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;


public class RiotAPI {

    private String region;

    public RiotAPI(Context context){
        region = new UserInfo().getRegion(context);
    }

    public SummonerDto getSummoner(String summonerName){

        // construct url
        String url = Params.RIOT_BASE_URL + "/api/lol/"+region+"/v1.4/summoner/by-name/"+summonerName+Params.API_KEY;
        Log.d("test1", "summoner get url: "+url);

        // get summoner in json format
        String summonerJson = "";
        try{
            summonerJson = new Http().get(url);
        }catch(Exception e){
            // do stuff
        }

        // deserialize and return summoner
        Type singleSummoner = new TypeToken<SummonerDto>(){}.getType();
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        return gson.fromJson(summonerJson, singleSummoner);
    }
}
