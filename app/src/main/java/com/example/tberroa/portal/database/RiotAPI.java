package com.example.tberroa.portal.database;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.tberroa.portal.data.MostRecentError;
import com.example.tberroa.portal.data.Params;
import com.example.tberroa.portal.data.SummonerInfo;
import com.example.tberroa.portal.helpers.ModelSerializer;
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

// all methods, except boolean methods, return null if http response validation fails
public class RiotAPI {

    private final String region;
    private String url;
    private final Context context;

    public RiotAPI(Context context){
        this.context = context;
        region = new SummonerInfo().getRegion(context);
        Log.d(Params.TAG_DEBUG, "@RAPI Constructor: region is " + region);
    }

    public Map<String, SummonerDto> getSummonersByName(List<String> summonerNames){
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
        url = Params.RIOT_API_BASE_URL + region + Params.API_SUMMONER + "by-name/" +
                names + "?api_key=" + Params.API_KEY;
        Log.d(Params.TAG_DEBUG, "@getSummonersByName: url is " + url);

        // query the riot api
        String[] response = {Params.HTTP_GET_FAILED, ""};
        try{
            response =  new AttemptGet().execute().get();
        }catch (java.lang.InterruptedException | java.util.concurrent.ExecutionException e){
            Log.e(Params.TAG_EXCEPTIONS,"@getSummonersByName: " + e.getMessage() );
        }
        Log.d(Params.TAG_DEBUG, "@getSummonersByName: response body is " + response[1]);

        // validate response
        if (validResponse(response[0])){
            // return summoner map
            Type typeMap = new TypeToken<Map<String, SummonerDto>>(){}.getType();
            Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
            return gson.fromJson(response[1], typeMap);
        }
        else{
            return null;
        }
    }

    public RunePagesDto getRunePages(String summonerName){
        // construct list with summoner name
        List<String> summonerNames = new ArrayList<>();
        summonerNames.add(summonerName);

        // query riot api
        Map<String, SummonerDto> summonerMap = getSummonersByName(summonerNames);
        if (summonerMap != null){
            // get summoner id
            long id = summonerMap.get(summonerName).id;

            // construct url
            url = Params.RIOT_API_BASE_URL + region + Params.API_SUMMONER + id +
                    "/runes?api_key="+Params.API_KEY;
            Log.d(Params.TAG_DEBUG, "@getRunePages: url is " + url);

            // query the riot api
            String[] response = {Params.HTTP_GET_FAILED, ""};
            try{
                response =  new AttemptGet().execute().get();
            }catch (java.lang.InterruptedException | java.util.concurrent.ExecutionException e){
                Log.e(Params.TAG_EXCEPTIONS,"@getRunePages: " + e.getMessage() );
            }
            Log.d(Params.TAG_DEBUG, "@getRunePages: response body is " + response[1]);

            // validate response
            if (validResponse(response[0])){
                // return individual summoners rune pages
                Type typeMap = new TypeToken<Map<String, RunePagesDto>>(){}.getType();
                Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
                Map<String, RunePagesDto> runePagesDtoMap = gson.fromJson(response[1], typeMap);
                return runePagesDtoMap.get(Long.toString(id));
            }
            else{
                return null;
            }
        }
        else{
            return null;
        }
    }

    public MatchList getMatches(long id, Map<String, String> parameters){
        // declare and initialize parameters
        String queue, season, beginTime, endTime, beginIndex, endIndex;
        queue = (parameters.get("queue") != null) ? parameters.get("queue") : "";
        season = (parameters.get("season") != null) ? parameters.get("season") : "";
        beginTime = (parameters.get("begin_time") != null) ? parameters.get("begin_time") : "";
        endTime = (parameters.get("end_time") != null) ? parameters.get("end_time") : "";
        beginIndex = (parameters.get("begin_index") != null) ? parameters.get("begin_index") : "";
        endIndex = (parameters.get("end_index") != null) ? parameters.get("end_index") : "";

        // construct url
        url = Params.RIOT_API_BASE_URL + region + Params.API_MATCHLIST + "by-summoner/" +
                id +
                "?rankedQueues=" + queue +
                "&seasons=" + season +
                "&beginTime=" + beginTime +
                "&endTime=" + endTime +
                "&beginIndex=" + beginIndex +
                "&endIndex=" + endIndex +
                "&api_key=" + Params.API_KEY;
        Log.d(Params.TAG_DEBUG, "@getMatches: url is " + url);

        // query riot api
        String[] response = {Params.HTTP_GET_FAILED, ""};
        try{
            response =  new AttemptGet().execute().get();
        }catch (java.lang.InterruptedException | java.util.concurrent.ExecutionException e){
            Log.e(Params.TAG_EXCEPTIONS,"@getMatches: " + e.getMessage() );
        }
        Log.d(Params.TAG_DEBUG, "@getMatches: response body is " + response[1]);

        // validate response
        if (validResponse(response[0])){
            //return matches
            return new ModelSerializer().fromJson(response[1], MatchList.class);
        }
        else{
            return null;
        }
    }

    private class AttemptGet extends AsyncTask<Void, Void, String[]> {
        @Override
        protected String[] doInBackground(Void... params) {
            String[] response = {Params.HTTP_GET_FAILED, ""};
            try{
                response = new Http().get(url);
            }catch(IOException e){
                Log.d(Params.TAG_EXCEPTIONS,"@RAPI AttemptGet: " + e.getMessage());
            }
            return response;
        }
    }

    private boolean validResponse(String responseCode){
        switch(responseCode){
            case Params.RC_200_SUCCESS:
                return true;
            default:
                new MostRecentError().setCode(context, responseCode);
                return false;
        }
    }
}
