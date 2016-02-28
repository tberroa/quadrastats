package com.example.tberroa.portal.database;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.tberroa.portal.data.Params;
import com.example.tberroa.portal.data.UserInfo;
import com.example.tberroa.portal.helpers.ModelSerializer;
import com.example.tberroa.portal.models.matchlist.MatchList;
import com.example.tberroa.portal.models.summoner.SummonerDto;
import com.example.tberroa.portal.network.Http;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class RiotAPI {

    private final String region;
    private String url;

    public RiotAPI(Context context){
        region = new UserInfo().getRegion(context);
        Log.d(Params.TAG_DEBUG, "region: " + region);
    }

    // get summoner objects mapped by standardized summoner name for a given list of summoner names
    public Map<String, SummonerDto> getSummoners(List<String> summonerNames){
        // construct names field
        String names = "";
        for (int i=0; i < summonerNames.size(); i++){
            if (i == 0){
                names = summonerNames.get(i);
            }
            else{
                names = names + ","+summonerNames.get(i);
            }
        }

        // construct url
        url = Params.RIOT_BASE_URL + "/api/lol/"+region+"/v1.4/summoner/by-name/" +
                names +
                "?api_key=" + Params.API_KEY;
        Log.d(Params.TAG_DEBUG, "get summoner url: " + url);

        // get summoner map
        String summonerMap = "";
        try{
            summonerMap =  new AttemptPost().execute().get();
        }catch (java.lang.InterruptedException | java.util.concurrent.ExecutionException e){
            Log.e(Params.TAG_EXCEPTIONS, e.getMessage() );
        }
        Log.d(Params.TAG_DEBUG, "http get output: " + summonerMap);

        // return summoner map
        Type typeMap = new TypeToken<Map<String, SummonerDto>>(){}.getType();
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        return gson.fromJson(summonerMap, typeMap);
    }

    public MatchList getMatches(long id, Map<String, String> parameters){
        String queue, season, beginTime, endTime, beginIndex, endIndex;

        // grab parameters
        queue = (parameters.get("queue") != null) ? parameters.get("queue") : "";
        season = (parameters.get("season") != null) ? parameters.get("season") : "";
        beginTime = (parameters.get("begin_time") != null) ? parameters.get("begin_time") : "";
        endTime = (parameters.get("end_time") != null) ? parameters.get("end_time") : "";
        beginIndex = (parameters.get("begin_index") != null) ? parameters.get("begin_index") : "";
        endIndex = (parameters.get("end_index") != null) ? parameters.get("end_index") : "";

        // construct url
        url = Params.RIOT_BASE_URL+ "/api/lol/"+region+"/v2.2/matchlist/by-summoner/" +
                id +
                "?rankedQueues=" + queue +
                "&seasons=" + season +
                "&beginTime=" + beginTime +
                "&endTime=" + endTime +
                "&beginIndex=" + beginIndex +
                "&endIndex=" + endIndex +
                "&api_key=" + Params.API_KEY;
        Log.d(Params.TAG_DEBUG, "get matches url: " + url);

        // get matches
        String matches = "";
        try{
            matches =  new AttemptPost().execute().get();
        }catch (java.lang.InterruptedException | java.util.concurrent.ExecutionException e){
            Log.e(Params.TAG_EXCEPTIONS, e.getMessage() );
        }
        Log.d(Params.TAG_DEBUG, "http get output: " + matches);

        //return matches
        return new ModelSerializer().fromJson(matches, MatchList.class);
    }

    private class AttemptPost extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            String postResponse = "";
            try{
                postResponse = new Http().get(url);
            }catch(IOException e){
                Log.d(Params.TAG_EXCEPTIONS, e.getMessage());
            }
            return postResponse;
        }
    }
}
