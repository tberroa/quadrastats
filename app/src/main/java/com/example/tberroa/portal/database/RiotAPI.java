package com.example.tberroa.portal.database;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.tberroa.portal.data.MostRecentError;
import com.example.tberroa.portal.data.Params;
import com.example.tberroa.portal.data.UserInfo;
import com.example.tberroa.portal.helpers.ModelSerializer;
import com.example.tberroa.portal.models.Error;
import com.example.tberroa.portal.models.matchlist.MatchList;
import com.example.tberroa.portal.models.summoner.RunePagesDto;
import com.example.tberroa.portal.models.summoner.SummonerDto;
import com.example.tberroa.portal.network.Http;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RiotAPI {

    private final String region;
    private String url;
    private Context context;

    public RiotAPI(Context context){
        this.context = context;
        region = new UserInfo().getRegion(context);
        Log.d(Params.TAG_DEBUG, "@RAPI Constructor: region is " + region);
    }

    public boolean summonerExists(String summonerName){
        // construct url
        url = Params.RIOT_BASE_URL + "/api/lol/"+region+"/v1.4/summoner/by-name/" +
                summonerName +
                "?api_key=" + Params.API_KEY;
        Log.d(Params.TAG_DEBUG, "@summonerExists: url is " + url);

        // get summoner map
        String summonerMap = "";
        try{
            summonerMap =  new AttemptPost().execute().get();
        }catch (java.lang.InterruptedException | java.util.concurrent.ExecutionException e){
            Log.e(Params.TAG_EXCEPTIONS,"@summonerExists: " + e.getMessage() );
        }
        Log.d(Params.TAG_DEBUG, "@summonerExists: summonerMap is " + summonerMap);

        // validate response
        return (validResponse(summonerMap));
    }

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
        Log.d(Params.TAG_DEBUG, "@getSummoners: url is " + url);

        // get summoner map
        String summonerMap = "";
        try{
            summonerMap =  new AttemptPost().execute().get();
        }catch (java.lang.InterruptedException | java.util.concurrent.ExecutionException e){
            Log.e(Params.TAG_EXCEPTIONS,"@getSummoners: " + e.getMessage() );
        }
        Log.d(Params.TAG_DEBUG, "@getSummoners: summonerMap is " + summonerMap);

        // validate response
        if (validResponse(summonerMap)){
            // return summoner map
            Type typeMap = new TypeToken<Map<String, SummonerDto>>(){}.getType();
            Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
            return gson.fromJson(summonerMap, typeMap);
        }
        else{
            return null;
        }
    }

    public RunePagesDto getRunePages(String summonerName){
        List<String> summonerNames = new ArrayList<>();
        summonerNames.add(summonerName);
        long id = getSummoners(summonerNames).get(summonerName).id;
        url = Params.RIOT_BASE_URL + "/api/lol/" +
                region + "/v1.4/summoner/" + id +"/runes?api_key="+Params.API_KEY;
        Log.d(Params.TAG_DEBUG, "@getRunePages: url is " + url);

        // get rune pages map
        String runePagesMap = "";
        try{
            runePagesMap =  new AttemptPost().execute().get();
        }catch (java.lang.InterruptedException | java.util.concurrent.ExecutionException e){
            Log.e(Params.TAG_EXCEPTIONS,"@getRunePages: " + e.getMessage() );
        }
        Log.d(Params.TAG_DEBUG, "@getRunePages: runePagesMap is " + runePagesMap);

        // validate response
        if (validResponse(runePagesMap)){
            // return individual summoners rune pages
            Type typeMap = new TypeToken<Map<String, RunePagesDto>>(){}.getType();
            Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
            Map<String, RunePagesDto> runePagesDtoMap = gson.fromJson(runePagesMap, typeMap);
            return runePagesDtoMap.get(Long.toString(id));
        }
        else{
            return null;
        }
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
        Log.d(Params.TAG_DEBUG, "@getMatches: url is " + url);

        // get matches
        String matches = "";
        try{
            matches =  new AttemptPost().execute().get();
        }catch (java.lang.InterruptedException | java.util.concurrent.ExecutionException e){
            Log.e(Params.TAG_EXCEPTIONS,"@getMatches: " + e.getMessage() );
        }
        Log.d(Params.TAG_DEBUG, "@getMatches: matches is " + matches);

        // validate response
        if (validResponse(matches)){
            //return matches
            return new ModelSerializer().fromJson(matches, MatchList.class);
        }
        else{
            return null;
        }
    }

    private class AttemptPost extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            String postResponse = "";
            try{
                postResponse = new Http().get(url);
            }catch(IOException e){
                Log.d(Params.TAG_EXCEPTIONS,"@RAPI AttemptPost: " + e.getMessage());
            }
            return postResponse;
        }
    }

    private boolean validResponse(String response){
        if (response.contains("status_code")){
            // deserialize the response
            Type errorMapType = new TypeToken<Map<String, Error>>(){}.getType();
            Map<String, Error> errorMap = new Gson().fromJson(response, errorMapType);
            // get the code and corresponding message
            int code = errorMap.get("status").status_code;
            String message = errorMap.get("status").message;
            // save the code and message
            MostRecentError mostRecentError = new MostRecentError();
            mostRecentError.setMessage(context, message);
            mostRecentError.setCode(context, code);
            Log.d(Params.TAG_DEBUG, "@validResponse: error code is " + Integer.toString(code));
            return false;
        }
        else{
            return true;
        }
    }

}
