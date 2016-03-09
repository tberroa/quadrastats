package com.example.tberroa.portal.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.example.tberroa.portal.data.Friends;
import com.example.tberroa.portal.data.Params;
import com.example.tberroa.portal.data.SummonerInfo;
import com.example.tberroa.portal.data.UpdateJobFlags;
import com.example.tberroa.portal.database.RiotAPI;
import com.example.tberroa.portal.models.match.MatchDetail;
import com.example.tberroa.portal.models.matchlist.MatchList;
import com.example.tberroa.portal.models.summoner.SummonerDto;
import com.example.tberroa.portal.network.NetworkUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class UpdateService extends Service {

    /*  2 states:
        0, service is ready to run.
        1, service is taking a break. (state is switched to 0 at end of break) */

    private final UpdateJobFlags updateJobFlags = new UpdateJobFlags();
    private boolean kill = false;
    private Timer timer;

    @Override
    public void onCreate() {
        // initialize timer
        timer = new Timer();

        // initialize timer task to be done periodically
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                // make timer task run in background via new thread
                new Thread(new Runnable() {
                    public void run() {
                        updateJob();
                    }
                }).start();
            }
        };

        // after an immediate initial run, the task will run every minute
        timer.schedule(timerTask, 0, 1000 * 60);
    }

    // runs in a background thread
    private void updateJob() {
        // let system know the update job is running
        updateJobFlags.setRunning(this, true);
        Log.d(Params.TAG_DEBUG, "@UpdateService: update job is running, flag set");

        // grab previous state
        int previousState = updateJobFlags.getState(this);
        Log.d(Params.TAG_DEBUG, "@UpdateService: previous state was " + Integer.toString(previousState));

        switch (previousState) {
            case 0:
                SummonerInfo summonerInfo = new SummonerInfo();
                // get summoner name
                String summonerName = summonerInfo.getBasicName(UpdateService.this);

                // get friend names
                Set<String> friendNames = new Friends().getNames(UpdateService.this);

                if (NetworkUtil.isInternetAvailable(UpdateService.this)) {
                    // initialize riot api
                    RiotAPI riotAPI = new RiotAPI(UpdateService.this);

                    // construct list of names to get data for
                    List<String> summonerNames = new ArrayList<>();
                    summonerNames.add(summonerName);
                    if (friendNames.size() > 0) {
                        for (String friend : friendNames) {
                            summonerNames.add(friend);
                        }
                    }

                    // query riot api for summoner & friend dto's
                    Map<String, SummonerDto> summoners = riotAPI.getSummonersByName(summonerNames);

                    // if a proper response was received, save and begin gathering data
                    if (summoners != null) {
                        // save summoner id
                        summonerInfo.setId(this, summoners.get(summonerName).id);

                        // initialize match list parameters
                        Map<String, String> matchParameters = new HashMap<>();
                        matchParameters.put("seasons", Params.SEASON_2016);


                        // save summoner & friend dto's locally, get match list for each
                        for (Map.Entry<String, SummonerDto> entry : summoners.entrySet()) {
                            if (!kill) {
                                // save
                                entry.getValue().save();

                                // get match list parameters
                                MatchList matchlist;
                                matchlist = riotAPI.getMatchList(entry.getValue().id, matchParameters);

                                if (matchlist != null){
                                    matchlist.summonerId = entry.getValue().id;
                                    matchlist.cascadeSave();

                                    // get match detail for last 5 games
                                    if (matchlist.totalGames > 0) {
                                        for (int i = 0; i < 5 && i < matchlist.totalGames; i++) {
                                            long matchId = matchlist.getMatchReferences().get(i).matchId;
                                            MatchDetail match = riotAPI.getMatchDetail(matchId);
                                            if (match != null) {
                                                match.cascadeSave();
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                // set state to 1, service was just recently ran and needs a break
                updateJobFlags.setState(UpdateService.this, 1);
                break;
            case 1:
                // set state to 0, service is done taking a break
                updateJobFlags.setState(UpdateService.this, 0);
                break;
        }

        // let system know the update job is done running
        updateJobFlags.setRunning(this, false);
        Log.d(Params.TAG_DEBUG, "@UpdateService: update job is done running, flag set");
    }

    @Override
    public void onDestroy() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        kill = true;
        updateJobFlags.setState(this, 0);
        updateJobFlags.setRunning(this, false);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
