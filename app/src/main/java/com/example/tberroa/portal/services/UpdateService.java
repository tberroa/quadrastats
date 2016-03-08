package com.example.tberroa.portal.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.example.tberroa.portal.data.Params;
import com.example.tberroa.portal.data.SummonerInfo;
import com.example.tberroa.portal.data.UpdateServiceState;
import com.example.tberroa.portal.database.RiotAPI;
import com.example.tberroa.portal.models.match.MatchDetail;
import com.example.tberroa.portal.models.matchlist.MatchList;
import com.example.tberroa.portal.models.summoner.SummonerDto;
import com.example.tberroa.portal.network.NetworkUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpdateService extends Service {

    // 2 states:
    //      0, service is ready to run.
    //      1, service recently ran, can't run because it's taking a break

    @Override
    public void onCreate() {
            new Thread(new Runnable() {
                public void run() {
                    // grab state
                    UpdateServiceState updateServiceState = new UpdateServiceState();
                    int state = updateServiceState.get(UpdateService.this);
                    Log.d(Params.TAG_DEBUG, "@UpdateService: entering state is " + Integer.toString(state));
                    switch(state){
                        case 0:
                            // initialize summoner info
                            SummonerInfo summonerInfo = new SummonerInfo();

                            // get summoner name
                            String summonerName = summonerInfo.getBasicName(UpdateService.this);
                            Log.d(Params.TAG_DEBUG, "@UpdateService: basic summoner name is " + summonerName);

                            if (NetworkUtil.isInternetAvailable(UpdateService.this) && !summonerName.equals("")){
                                // initialize riot api
                                RiotAPI riotAPI = new RiotAPI(UpdateService.this);

                                // query riot api for summoner dto
                                List<String> summonerNames = new ArrayList<>();
                                summonerNames.add(summonerName);
                                Map<String, SummonerDto> summoners = riotAPI.getSummonersByName(summonerNames);

                                // if a proper response was received, begin gathering data
                                if (summoners != null){
                                    Log.d(Params.TAG_DEBUG, "@UpdateService: summoners map not null");

                                    // save summoner locally
                                    SummonerDto summoner = summoners.get(summonerName);
                                    summoner.save();
                                    Log.d(Params.TAG_DEBUG, "@UpdateService: stylized name is:" + summoner.name);

                                    // get match list for summoner
                                    Map<String, String> matchParameters = new HashMap<>();
                                    matchParameters.put("seasons", Params.PRESEASON_2016 + "," + Params.SEASON_2016);

                                    MatchList matchlist;
                                    matchlist = riotAPI.getMatchList(summoner.id, matchParameters);
                                    matchlist.summonerId = summoner.id;

                                    if (matchlist != null){
                                        matchlist.cascadeSave();

                                        // get match detail for last 5 games
                                        for (int i=0; i<5 && i<matchlist.totalGames; i++){
                                            long matchId = matchlist.getMatchReferences().get(i).matchId;
                                            MatchDetail match = riotAPI.getMatchDetail(matchId);
                                            match.cascadeSave();
                                            try{
                                                Thread.sleep(1200);
                                            } catch (InterruptedException e){
                                                Log.d(Params.TAG_EXCEPTIONS, e.getMessage());
                                            }

                                        }
                                    }
                                }
                            }
                            // set state to 1, service was just recently ran
                            updateServiceState.set(UpdateService.this, 1);
                            break;
                        case 1:
                            // set state to 0, service is done taking a break
                            updateServiceState.set(UpdateService.this, 0);
                            break;
                    }
                    Log.d(Params.TAG_DEBUG, "@UpdateService: end of thread reached");
                    UpdateService.this.stopSelf();
                }
            }).start();
    }

    @Override
    public void onDestroy() {
        // restart this service again in 15 minutes
        // without user request, max time before stats refresh is 30 minutes and min time is 15 minutes
        AlarmManager alarm = (AlarmManager)getSystemService(ALARM_SERVICE);
        alarm.set(
                AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis() + (1000 * 60 * 20),
                PendingIntent.getService(this, 0, new Intent(this, UpdateService.class), 0)
        );
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
