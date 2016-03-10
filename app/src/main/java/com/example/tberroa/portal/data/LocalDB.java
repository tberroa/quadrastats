package com.example.tberroa.portal.data;

import android.content.Context;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Select;
import com.example.tberroa.portal.models.match.MatchDetail;
import com.example.tberroa.portal.models.match.Participant;
import com.example.tberroa.portal.models.match.ParticipantIdentity;
import com.example.tberroa.portal.models.match.ParticipantStats;
import com.example.tberroa.portal.models.matchlist.MatchList;
import com.example.tberroa.portal.models.matchlist.MatchReference;
import com.example.tberroa.portal.models.summoner.SummonerDto;

import java.util.List;

public class LocalDB {

    public LocalDB(){
    }

    public SummonerDto getSummonerByName(String name){
        return new Select()
                .from(SummonerDto.class)
                .where("name = ?", name)
                .executeSingle();
    }

    public SummonerDto getSummonerById(long id){
        return new Select()
                .from(SummonerDto.class)
                .where("summoner_id = ?", id)
                .executeSingle();
    }

    private MatchList getMatchList(long summonerId){
        return new Select()
                .from(MatchList.class)
                .where("summoner_id = ?", summonerId)
                .executeSingle();
    }

    public List<MatchReference> getMatchReferences(long summonerId, String queue){
        MatchList matchList = getMatchList(summonerId);
        if (matchList != null && matchList.totalGames > 0){
            return new Select()
                    .from(MatchReference.class)
                    .where("match_list = ?", matchList.getId())
                    .where("queue = ?", queue)
                    .execute();
        }
        else{
            return null;
        }
    }

    public MatchDetail getMatchDetail(long matchId){
        return new Select()
                .from(MatchDetail.class)
                .where("match_id = ?", matchId)
                .executeSingle();
    }

    public ParticipantStats getParticipantStats(long summonerId, MatchDetail matchDetail){
        List<ParticipantIdentity> identities = matchDetail.getParticipantIdentities();
        int i = 0;
        long identityId;
        do {
            identityId = identities.get(i).getPlayer().summonerId;
            i++;
        } while (identityId != summonerId);
        int participantId = identities.get(i-1).participantId;
        Participant participant = new Select()
                .from(Participant.class)
                .where("participant_id = ?", participantId)
                .where("match_detail = ?", matchDetail.getId())
                .executeSingle();
        return participant.getParticipantStats();
    }

    public void clear(Context context){
        ActiveAndroid.dispose();
        ActiveAndroid.initialize(context);
    }
}


