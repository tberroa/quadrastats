package com.example.tberroa.portal.database;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.tberroa.portal.data.Params;
import com.example.tberroa.portal.data.UserInfo;
import com.example.tberroa.portal.helpers.ModelSerializer;
import com.example.tberroa.portal.models.summoner.SummonerDto;
import com.example.tberroa.portal.network.Http;

import java.io.IOException;
import java.util.List;
import java.util.Map;


public class RiotAPI {

    private String region, url;

    public RiotAPI(Context context){
        region = new UserInfo().getRegion(context);
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
        url = Params.RIOT_BASE_URL + "/api/lol/"+region+"/v1.4/summoner/by-name/"+names+Params.API_KEY;
        Log.d(Params.TAG_DEBUG, "summoner get url: "+url);

        // get summoner map
        String summonerMapJson = "";
        try{
            summonerMapJson =  new AttemptPost().execute().get();
        }catch (java.lang.InterruptedException | java.util.concurrent.ExecutionException e){
            Log.e(Params.TAG_EXCEPTIONS, e.getMessage() );
        }

        Log.d(Params.TAG_DEBUG, "Http().get output: "+ summonerMapJson);

        //return summoner map
        return new ModelSerializer<Map<String,SummonerDto>>().fromJson(summonerMapJson);
    }

    class AttemptPost extends AsyncTask<Void, Void, String> {
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
