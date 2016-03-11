package com.example.tberroa.portal.updater;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.example.tberroa.portal.data.LocalDB;
import com.example.tberroa.portal.models.matchlist.MatchReference;
import com.example.tberroa.portal.data.Params;
import com.example.tberroa.portal.data.RiotAPI;
import com.example.tberroa.portal.models.match.MatchDetail;
import com.example.tberroa.portal.models.matchlist.MatchList;
import com.example.tberroa.portal.models.summoner.SummonerDto;
import com.example.tberroa.portal.network.NetworkUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class UpdateService extends Service {

    /*  2 states:
        0, service is ready to run.
        1, service is taking a break. (state is switched to 0 at end of break) */

    private final UpdateJobInfo updateJobInfo = new UpdateJobInfo();
    private RiotAPI riotAPI;
    private boolean kill = false;
    private Timer timer;

    @Override
    public void onCreate() {
        // initialize riot api
        riotAPI = new RiotAPI(this);
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
    private int updateJob() {
        // grab previous state
        int previousState = updateJobInfo.getState(this);

        int condition = checkConditions(previousState);

        switch (condition) {
            case 100: // code 100, internet is not available
                Log.d(Params.TAG_DEBUG, "@UpdateService: network not available. not able to run");
                break;
            case 200: // code 200, update job is resting
                // set state to 0, service is done taking a break
                updateJobInfo.setState(UpdateService.this, 0);
                Log.d(Params.TAG_DEBUG, "@UpdateService: just finished resting");
                break;
            case 300: // code 300, update job is ready to run
                // let the system know the update job is running
                updateJobInfo.setRunning(this, true);
                Log.d(Params.TAG_DEBUG, "@UpdateService: update job is running");

                if (kill) {
                    updateJobInfo.setRunning(this, false);
                    Log.d(Params.TAG_DEBUG, "@UpdateService: update job was killed");
                    return -1;
                }

                // initialize array of queues
                String[] queues = {Params.DYNAMIC_QUEUE, Params.SOLO_QUEUE, Params.TEAM_5, Params.TEAM_3};

                // get player profiles map
                Map<String, PlayerUpdateProfile> profilesMap = updateJobInfo.getProfiles(this);

                if (kill) {
                    updateJobInfo.setRunning(this, false);
                    Log.d(Params.TAG_DEBUG, "@UpdateService: update job was killed");
                    return -1;
                }

                // save any new summoners
                saveNewSummoners(profilesMap);

                if (kill) {
                    updateJobInfo.setRunning(this, false);
                    Log.d(Params.TAG_DEBUG, "@UpdateService: update job was killed");
                    return -1;
                }

                // update match data for all queues
                for (String queue : queues) {
                    if (kill) {
                        updateJobInfo.setRunning(this, false);
                        Log.d(Params.TAG_DEBUG, "@UpdateService: update job was killed");
                        return -1;
                    }

                    // get any new matches
                    Set<MatchReference> newMatches = getNewMatchReferences(profilesMap, queue);

                    if (kill) {
                        updateJobInfo.setRunning(this, false);
                        Log.d(Params.TAG_DEBUG, "@UpdateService: update job was killed");
                        return -1;
                    }

                    // save the details of any new matches
                    saveNewMatchDetails(newMatches);
                }

                // let the system know the update job is done running
                updateJobInfo.setRunning(this, false);
                sendBroadcast(new Intent().setAction(Params.UPDATE_COMPLETE));
                Log.d(Params.TAG_DEBUG, "@UpdateService: update job is done running");
                break;
        }
        return 1;
    }

    // this method looks for any new summoners (user or friends) and saves their riot provided summoner dto locally
    private void saveNewSummoners(Map<String, PlayerUpdateProfile> profilesMap) {
        // initialize list to store summoner names
        List<String> summonerNames = new ArrayList<>();

        // look through the profile map for new players
        for (Map.Entry<String, PlayerUpdateProfile> profile : profilesMap.entrySet()) {
            if (profile.getValue().newPlayer) {
                // add the name of any new players to the list of names to query for
                summonerNames.add(profile.getKey());

                // update their status
                profile.getValue().newPlayer = false;
            }
        }

        // if new players were found, save the profile map and query the riot api for their summoner dto and save it
        if (!summonerNames.isEmpty()) {
            new UpdateJobInfo().setProfiles(this, profilesMap);
            Map<String, SummonerDto> summoners = riotAPI.getSummonersByName(summonerNames);
            for (Map.Entry<String, SummonerDto> summoner : summoners.entrySet()) {
                summoner.getValue().save();
            }
        }
    }

    // this method returns any new matches of the requested queue for the locally saved summoner dto's
    private Set<MatchReference> getNewMatchReferences(Map<String, PlayerUpdateProfile> profilesMap, String queue) {
        // initialize set
        Set<MatchReference> newMatches = new HashSet<>();

        // get summoner dto's
        List<SummonerDto> summoners = new LocalDB().getAllSummoners();
        Log.d(Params.TAG_DEBUG, "@UpdateService: size of summoners is " + Integer.toString(summoners.size()));

        // initialize search parameters
        Map<String, String> parameters = new HashMap<>();
        parameters.put("seasons", Params.SEASON_2016);
        parameters.put("queue", queue);
        parameters.put("end_index", "1000000");

        // for each summoner, look for new matches
        for (SummonerDto summoner : summoners) {
            // get the begin index from the profile map
            int beginIndex = getBeginIndex(profilesMap, summoner, queue);
            parameters.put("begin_index", Integer.toString(beginIndex));

            // query riot api for the new match list
            MatchList newMatchList = riotAPI.getMatchList(summoner.id, parameters);

            if (beginIndex < newMatchList.endIndex) {
                // update the begin index in the profile map
                updateBeginIndex(profilesMap, summoner, queue, newMatchList.endIndex);

                // trim the list most recent 10 matches
                newMatchList.matches = onlyMostRecent(newMatchList.matches, 10);

                // include the new match references in the returned set
                newMatches.addAll(newMatchList.matches);

                // append the new match references to the summoner's saved list
                MatchList savedMatchList = new LocalDB().getMatchList(summoner.id, queue);
                if (savedMatchList != null) {
                    // get the old matches
                    List<MatchReference> matches = savedMatchList.getMatchReferences();

                    // add the new matches
                    matches.addAll(newMatchList.matches);

                    // trim to only the 10 most recent matches
                    matches = onlyMostRecent(matches, 10);

                    // replace and save
                    savedMatchList.matches = matches;
                    savedMatchList.cascadeSave();
                } else {
                    // if the summoner didn't have a saved match list, create one
                    newMatchList.summonerId = summoner.id;
                    newMatchList.queue = queue;
                    newMatchList.cascadeSave();
                }
            }
        }
        return newMatches;
    }

    // this method takes a set of match references, gets their match details, and saves the details locally
    private void saveNewMatchDetails(Set<MatchReference> newMatches) {
        for (MatchReference matchReference : newMatches) {
            MatchDetail matchDetail = riotAPI.getMatchDetail(matchReference.matchId);
            if (matchDetail != null) {
                matchDetail.cascadeSave();
            }
        }
    }

    private int checkConditions(int previousState) {

        // code 100, internet is not available
        if (!NetworkUtil.isInternetAvailable(this)) {
            return 100;
        }

        // code 200, update job is resting
        if (previousState == 1) {
            return 200;
        }

        // code 300, update job is ready to run
        return 300;
    }

    private int getBeginIndex(Map<String, PlayerUpdateProfile> profilesMap, SummonerDto summoner, String queue) {
        int beginIndex = 0;
        switch (queue) {
            case Params.DYNAMIC_QUEUE:
                beginIndex = profilesMap.get(summoner.name).dQBeginIndex;
                break;
            case Params.SOLO_QUEUE:
                beginIndex = profilesMap.get(summoner.name).sQBeginIndex;
                break;
            case Params.TEAM_5:
                beginIndex = profilesMap.get(summoner.name).t5BeginIndex;
                break;
            case Params.TEAM_3:
                beginIndex = profilesMap.get(summoner.name).t3BeginIndex;
                break;
        }
        return beginIndex;
    }

    private List<MatchReference> onlyMostRecent(List<MatchReference> matches, int max) {
        if (matches.size() > max) {
            int size = matches.size();
            return matches.subList(size-max, size);
        } else {
            return matches;
        }
    }

    private void updateBeginIndex(Map<String, PlayerUpdateProfile> map, SummonerDto summoner, String queue, int index) {
        switch (queue) {
            case Params.DYNAMIC_QUEUE:
                map.get(summoner.name).dQBeginIndex = index;
                break;
            case Params.SOLO_QUEUE:
                map.get(summoner.name).sQBeginIndex = index;
                break;
            case Params.TEAM_5:
                map.get(summoner.name).t5BeginIndex = index;
                break;
            case Params.TEAM_3:
                map.get(summoner.name).t3BeginIndex = index;
                break;
        }
        updateJobInfo.setProfiles(this, map);
    }

    @Override
    public void onDestroy() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        kill = true;
        updateJobInfo.setState(this, 0);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
