package com.example.tberroa.portal.helpers;

import com.example.tberroa.portal.database.LocalDB;
import com.example.tberroa.portal.models.match.MatchDetail;
import com.example.tberroa.portal.models.match.ParticipantStats;
import com.example.tberroa.portal.models.matchlist.MatchReference;
import com.example.tberroa.portal.models.summoner.SummonerDto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class StatUtil {

    private StatUtil() {
    }

    static public List<ParticipantStats> getStats(long summonerId, String queue, int numberOfMatches){
        LocalDB localDB = new LocalDB();

        // get match references
        List<MatchReference> matches = localDB.getMatchReferences(summonerId, queue);

        // get match details
        List<MatchDetail> matchDetails = new ArrayList<>();
        for (int i = 0; i < numberOfMatches; i++) {
            matchDetails.add(localDB.getMatchDetail(matches.get(i).matchId));
        }

        // get participant stats for each match detail
        List<ParticipantStats> participantStatsList = new ArrayList<>();
        for (int i = 0; i < numberOfMatches; i++) {
            participantStatsList.add(localDB.getParticipantStats(summonerId, matchDetails.get(i)));
        }

        return participantStatsList;
    }


    static public Map<String, List<ParticipantStats>> getFriendStats(
            Set<String> friendNames, String queue, int numberOfMatches) {

        // initialize map of friend stats
        Map<String, List<ParticipantStats>> friendParticipantStatsList = new HashMap<>();

        if (friendNames.size() > 0) {
            LocalDB localDB = new LocalDB();

            // get friend ids
            Map<String, Long> friendIds = new HashMap<>();
            for (String name : friendNames) {
                SummonerDto friendDto = localDB.getSummonerByName(name);
                if (friendDto != null){
                    friendIds.put(name, friendDto.id);
                }
            }

            // get friend match references
            Map<String, List<MatchReference>> friendMatches = new HashMap<>();
            for (String name : friendNames) {
                friendMatches.put(name, new ArrayList<MatchReference>());
                long friendId = friendIds.get(name);
                List<MatchReference> references = localDB.getMatchReferences(friendId, queue);
                if (references != null){
                    friendMatches.put(name, references);
                }
            }

            // get friend match details
            Map<String, List<MatchDetail>> friendMatchDetails = new HashMap<>();
            for (String name : friendNames) {
                friendMatchDetails.put(name, new ArrayList<MatchDetail>());
                for (int i = 0; i < numberOfMatches; i++) {
                    if (friendMatches.get(name).size() > 0){
                        MatchReference reference = friendMatches.get(name).get(i);
                        MatchDetail detail = localDB.getMatchDetail(reference.matchId);
                        friendMatchDetails.get(name).add(detail);
                    }
                }
            }

            // get friend participant stats list
            for (String name : friendNames) {
                friendParticipantStatsList.put(name, new ArrayList<ParticipantStats>());
                for (int i = 0; i < numberOfMatches; i++) {
                    long friendId = friendIds.get(name);
                    if (friendMatchDetails.get(name).size() > 0){
                        MatchDetail matchDetail = friendMatchDetails.get(name).get(i);
                        ParticipantStats stats = localDB.getParticipantStats(friendId, matchDetail);
                        friendParticipantStatsList.get(name).add(stats);
                    }
                }
            }
        }
        return friendParticipantStatsList;
    }
}
