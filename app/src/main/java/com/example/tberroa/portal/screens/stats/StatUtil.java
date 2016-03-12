package com.example.tberroa.portal.screens.stats;

import android.content.Context;
import android.util.Log;

import com.androidplot.ui.AnchorPosition;
import com.androidplot.ui.LayoutManager;
import com.androidplot.ui.Size;
import com.androidplot.ui.XLayoutStyle;
import com.androidplot.ui.YLayoutStyle;
import com.androidplot.xy.CatmullRomInterpolator;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.PointLabelFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYGraphWidget;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.example.tberroa.portal.R;
import com.example.tberroa.portal.data.LocalDB;
import com.example.tberroa.portal.data.Params;
import com.example.tberroa.portal.models.match.MatchDetail;
import com.example.tberroa.portal.models.match.ParticipantStats;
import com.example.tberroa.portal.models.matchlist.MatchReference;
import com.example.tberroa.portal.models.summoner.SummonerDto;
import com.example.tberroa.portal.updater.UpdateJobInfo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

class StatUtil {

    private StatUtil() {
    }

    static private boolean hasMatches(long summonerId, String queue){
        List<MatchReference> matches = new LocalDB().getMatchReferences(summonerId, queue);
        return  (matches != null && !matches.isEmpty());
    }

    static private boolean hasMatches(Set<String> friendNames, String queue){
        LocalDB localDB = new LocalDB();
        for (String name : friendNames){
            SummonerDto friendDto = localDB.getSummonerByName(name);
            if (friendDto != null){
                List<MatchReference> references = localDB.getMatchReferences(friendDto.id, queue);
                if (references !=null && !references.isEmpty()){
                    return true;
                }
            }
        }
        return false;
    }

    static public List<ParticipantStats> getStats(long summonerId, String queue, int maxAmount){
        LocalDB localDB = new LocalDB();

        // get match references
        List<MatchReference> matches = localDB.getMatchReferences(summonerId, queue);

        // get match details
        List<MatchDetail> matchDetails = new ArrayList<>();
        for (int i = 0; i<maxAmount && i<matches.size(); i++) {
            matchDetails.add(localDB.getMatchDetail(matches.get(i).matchId));
        }

        // get participant stats for each match detail
        List<ParticipantStats> participantStatsList = new ArrayList<>();
        for (int i = 0; i<maxAmount && i<matchDetails.size(); i++) {
            participantStatsList.add(localDB.getParticipantStats(summonerId, matchDetails.get(i)));
        }

        return participantStatsList;
    }

    static public Map<String, List<ParticipantStats>> getFriendStats(Set<String> names, String queue, int maxMatches) {
        // initialize gson for logging
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

        // turn the set into a local list to prevent making permanent changes to friendNames
        List<String> friendNames = new ArrayList<>(names);
        Type friendNamesType = new TypeToken<List<String>>(){}.getType();
        String friendNamesJson = gson.toJson(friendNames, friendNamesType);
        Log.d(Params.TAG_DEBUG, "@StatUtil/getFriendStats: friendNames is " + friendNamesJson);

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
            Type friendIdsType = new TypeToken<Map<String, Long>>(){}.getType();
            String friendIdsJson = gson.toJson(friendIds, friendIdsType);
            Log.d(Params.TAG_DEBUG, "@StatUtil/getFriendStats: friendIds is " + friendIdsJson);

            // get friend match references
            Map<String, List<MatchReference>> friendMatches = new HashMap<>();
            for (Map.Entry<String, Long> friend : friendIds.entrySet()) {
                friendMatches.put(friend.getKey(), new ArrayList<MatchReference>());
                List<MatchReference> references = localDB.getMatchReferences(friend.getValue(), queue);
                if (references != null){
                    friendMatches.put(friend.getKey(), references);
                }
            }
            Type friendMatchesType = new TypeToken<Map<String, List<MatchReference>>>(){}.getType();
            String friendMatchesJson = gson.toJson(friendMatches, friendMatchesType);
            Log.d(Params.TAG_DEBUG, "@StatUtil/getFriendStats: friendMatches is " + friendMatchesJson);

            // get friend match details
            Map<String, List<MatchDetail>> friendMatchDetails = new HashMap<>();
            for (Map.Entry<String, List<MatchReference>> friend : friendMatches.entrySet()) {
                friendMatchDetails.put(friend.getKey(), new ArrayList<MatchDetail>());
                for (int i = 0; i<friend.getValue().size() && i<maxMatches; i++) {
                    MatchReference reference = friend.getValue().get(i);
                    MatchDetail detail = localDB.getMatchDetail(reference.matchId);
                    if (detail != null){
                        friendMatchDetails.get(friend.getKey()).add(detail);
                    }
                }
            }
            Type friendDetailsType = new TypeToken<Map<String, List<MatchDetail>>>(){}.getType();
            String friendDetailsJson = gson.toJson(friendMatchDetails, friendDetailsType);
            Log.d(Params.TAG_DEBUG, "@StatUtil/getFriendStats: friendMatchDetails is " + friendDetailsJson);

            // get friend participant stats list
            for (Map.Entry<String, List<MatchDetail>> friend : friendMatchDetails.entrySet()) {
                friendParticipantStatsList.put(friend.getKey(), new ArrayList<ParticipantStats>());
                long friendId = friendIds.get(friend.getKey());
                for (int i = 0; i<friend.getValue().size() && i<maxMatches; i++) {
                    MatchDetail matchDetail = friend.getValue().get(i);
                    ParticipantStats stats = localDB.getParticipantStats(friendId, matchDetail);
                    if (stats != null){
                        friendParticipantStatsList.get(friend.getKey()).add(stats);
                    }
                }
            }
        }
        return friendParticipantStatsList;
    }

    static public List<XYSeries> constructXYSeries(Set<String> fNames, Number[] sNums, Map<String, Number[]> fNums){

        List<XYSeries> series = new ArrayList<>();

        // add summoner first
        series.add(new SimpleXYSeries(Arrays.asList(sNums), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, null));

        // add friends
        for (String name : fNames){
            Number[] nums = fNums.get(name);

            // log friend numbers
            Type numbersType = new TypeToken<Number[]>(){}.getType();
            String numbersJson = new Gson().toJson(nums, numbersType);
            Log.d(Params.TAG_DEBUG, "@StatUtil/constructXYSeries: numbers for " + name + " are " + numbersJson);

            series.add(new SimpleXYSeries(Arrays.asList(nums), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, null));
        }

        return series;
    }

    static public void createPlot(Context context, XYPlot plot, List<XYSeries> series){
        // add series to plot
        int i=0;
        for (XYSeries seriesX : series){
            LineAndPointFormatter seriesFormat = new LineAndPointFormatter();
            seriesFormat.setPointLabelFormatter(new PointLabelFormatter());
            seriesFormat.setInterpolationParams(
                    new CatmullRomInterpolator.Params(10, CatmullRomInterpolator.Type.Centripetal));

            switch(i % 3){
                case 0:
                    seriesFormat.configure(context.getApplicationContext(), R.xml.line_green);
                    break;
                case 1:
                    seriesFormat.configure(context.getApplicationContext(), R.xml.line_blue);
                    break;
                case 2:
                    seriesFormat.configure(context.getApplicationContext(), R.xml.line_red);
                    break;
            }
            i++;
            plot.addSeries(seriesX, seriesFormat);
        }

        // plot styling
        plot.setBorderStyle(XYPlot.BorderStyle.NONE, null, null);
        XYGraphWidget g = plot.getGraphWidget();
        g.position(-0.5f, XLayoutStyle.RELATIVE_TO_RIGHT, -0.5f, YLayoutStyle.RELATIVE_TO_BOTTOM, AnchorPosition.CENTER);
        g.setSize(Size.FILL);
        g.setBackgroundPaint(null);
        g.setGridBackgroundPaint(null);
        g.setDomainOriginLinePaint(null);
        LayoutManager l = plot.getLayoutManager();
        l.remove(plot.getTitleWidget());
        l.remove(plot.getRangeLabelWidget());
        l.remove(plot.getDomainLabelWidget());
        l.remove(plot.getLegendWidget());
    }

    static public int checkConditions(Context context, long summonerId, String queue, Set<String> friendNames) {

        // code 100: summoner has no matches for this queue
        if (!StatUtil.hasMatches(summonerId, queue)) {
            return 100;
        }

        // code 200: update job is currently running
        if (new UpdateJobInfo().isRunning(context)) {
            return 200;
        }

        // code 300: summoner has no friends to compare matches to
        if (friendNames.isEmpty()) {
            return 300;
        }

        // code 400: none of the summoners friends have any matches for this queue
        if (!StatUtil.hasMatches(friendNames, queue)) {
            return 400;
        }

        // code 500: no issues, conditions are good for showing data
        return 500;
    }
}
