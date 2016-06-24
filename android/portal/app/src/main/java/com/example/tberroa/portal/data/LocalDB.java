package com.example.tberroa.portal.data;

import android.content.Context;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.From;
import com.activeandroid.query.Select;
import com.example.tberroa.portal.models.stats.MatchStats;
import com.example.tberroa.portal.models.summoner.Summoner;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class LocalDB {

    public void clearDatabase(Context context) {
        ActiveAndroid.dispose();
        context.deleteDatabase("Portal.db");
        ActiveAndroid.initialize(context);
    }

    public MatchStats matchStats(String key, long match_id) {
        return new Select()
                .from(MatchStats.class)
                .where("summoner_key = ?", key)
                .where("match_id = ?", match_id)
                .executeSingle();
    }

    public List<MatchStats> matchStatsList(List<String> keys, long champion, String lane, String role) {
        List<MatchStats> matchStatsList = new ArrayList<>();
        ActiveAndroid.beginTransaction();
        try {
            for (String key : keys) {
                From query = new Select().from(MatchStats.class).orderBy("match_creation DESC");
                if (champion > 0) {
                    query.where("champion = ?", champion);
                }
                if (lane != null) {
                    query.where("lane = ?", lane);
                }
                if (role != null) {
                    query.where("role = ?", role);
                }
                List<MatchStats> stats = query.where("summoner_key = ?", key).execute();
                if (stats != null) {
                    matchStatsList.addAll(stats);
                }
            }
            ActiveAndroid.setTransactionSuccessful();
        } finally {
            ActiveAndroid.endTransaction();
        }
        return matchStatsList;
    }

    public List<MatchStats> matchStatsList(String key) {
        List<MatchStats> matchStatsList = new ArrayList<>();
        ActiveAndroid.beginTransaction();
        try {
            From query = new Select().from(MatchStats.class).orderBy("match_creation DESC");
            List<MatchStats> stats = query.where("summoner_key = ?", key).execute();
            if (stats != null) {
                matchStatsList.addAll(stats);
            }

            ActiveAndroid.setTransactionSuccessful();
        } finally {
            ActiveAndroid.endTransaction();
        }
        return matchStatsList;
    }

    public Map<Long, Map<String, MatchStats>> matchesWithFriends(List<Long> ids, List<String> keys) {
        Map<Long, Map<String, MatchStats>> matchesWithFriends = new LinkedHashMap<>();
        ActiveAndroid.beginTransaction();
        try {
            for (Long matchId : ids) {
                From userQuery = new Select().from(MatchStats.class);
                userQuery.where("match_id = ?", matchId).where("summoner_key = ?", keys.get(0));
                MatchStats userMatchStats = userQuery.executeSingle();
                for (String key : keys.subList(1, keys.size())) {
                    From query = new Select().from(MatchStats.class);
                    query.where("match_id = ?", matchId);
                    query.where("summoner_key = ?", key);
                    MatchStats matchStats = query.executeSingle();
                    if ((matchStats != null) && (matchStats.winner == userMatchStats.winner)) {
                        if (matchesWithFriends.get(matchId) == null) {
                            matchesWithFriends.put(matchId, new LinkedHashMap<String, MatchStats>());
                            matchesWithFriends.get(matchId).put(userMatchStats.summoner_name, userMatchStats);
                        }
                        matchesWithFriends.get(matchId).put(matchStats.summoner_name, matchStats);
                    }
                }
            }
            ActiveAndroid.setTransactionSuccessful();
        } finally {
            ActiveAndroid.endTransaction();
        }
        return matchesWithFriends;
    }

    public Summoner summoner(String key) {
        return new Select()
                .from(Summoner.class)
                .where("key = ?", key)
                .executeSingle();
    }

    public Summoner summoner(long id) {
        return new Select().from(Summoner.class)
                .where("summoner_id = ?", id)
                .executeSingle();
    }

    public List<Summoner> summoners(List<String> keys) {
        List<Summoner> summoners = new ArrayList<>();
        ActiveAndroid.beginTransaction();
        try {
            for (String key : keys) {
                Summoner summoner = new Select()
                        .from(Summoner.class)
                        .where("key = ?", key)
                        .executeSingle();
                if (summoner != null) {
                    summoners.add(summoner);
                }
            }
            ActiveAndroid.setTransactionSuccessful();
        } finally {
            ActiveAndroid.endTransaction();
        }
        return summoners;
    }
}


