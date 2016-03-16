package com.example.tberroa.portal.screens.stats;

import android.content.Context;
import android.util.Log;

import com.androidplot.ui.AnchorPosition;
import com.androidplot.ui.LayoutManager;
import com.androidplot.ui.Size;
import com.androidplot.ui.XLayoutStyle;
import com.androidplot.ui.YLayoutStyle;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.PointLabelFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYGraphWidget;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.androidplot.xy.XYStepMode;
import com.example.tberroa.portal.R;
import com.example.tberroa.portal.data.LocalDB;
import com.example.tberroa.portal.data.Params;
import com.example.tberroa.portal.models.match.MatchDetail;
import com.example.tberroa.portal.models.match.ParticipantStats;
import com.example.tberroa.portal.models.matchlist.MatchReference;
import com.example.tberroa.portal.models.summoner.FriendsList;
import com.example.tberroa.portal.models.summoner.SummonerDto;
import com.example.tberroa.portal.updater.UpdateJobInfo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

class StatUtil {

    private StatUtil() {
    }

    static private boolean hasMatches(long summonerId, String queue) {
        List<MatchReference> matches = new LocalDB().getMatchReferences(summonerId, queue);
        return (matches != null && !matches.isEmpty());
    }

    static private boolean hasMatches(FriendsList friendsList, String queue) {
        LocalDB localDB = new LocalDB();
        for (SummonerDto friend : friendsList.getFriends()) {
            if (friend != null) {
                List<MatchReference> references = localDB.getMatchReferences(friend.id, queue);
                if (references != null && !references.isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }

    static public List<ParticipantStats> getStats(long summonerId, String queue, int maxAmount) {
        LocalDB localDB = new LocalDB();

        // get match references
        List<MatchReference> matches = localDB.getMatchReferences(summonerId, queue);

        // get match details
        List<MatchDetail> matchDetails = new ArrayList<>();
        for (int i = 0; i < maxAmount && i < matches.size(); i++) {
            matchDetails.add(localDB.getMatchDetail(matches.get(i).matchId));
        }

        // get participant stats for each match detail
        List<ParticipantStats> participantStatsList = new ArrayList<>();
        for (int i = 0; i < maxAmount && i < matchDetails.size(); i++) {
            participantStatsList.add(localDB.getParticipantStats(summonerId, matchDetails.get(i)));
        }

        return participantStatsList;
    }

    static public Map<String, List<ParticipantStats>> getFriendStats(FriendsList fList, String queue, int maxMatches) {
        // initialize gson for logging
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

        // initialize map of friend stats
        Map<String, List<ParticipantStats>> friendParticipantStatsList = new HashMap<>();

        if (fList != null && !fList.getFriends().isEmpty()) {
            LocalDB localDB = new LocalDB();

            // get friend ids
            Map<String, Long> friendIds = new HashMap<>();
            for (SummonerDto friend : fList.getFriends()) {
                friendIds.put(friend.name, friend.id);
            }
            Type friendIdsType = new TypeToken<Map<String, Long>>() {
            }.getType();
            String friendIdsJson = gson.toJson(friendIds, friendIdsType);
            Log.d(Params.TAG_DEBUG, "@StatUtil/getFriendStats: friendIds is " + friendIdsJson);

            // get friend match references
            Map<String, List<MatchReference>> friendMatches = new HashMap<>();
            for (Map.Entry<String, Long> friend : friendIds.entrySet()) {
                friendMatches.put(friend.getKey(), new ArrayList<MatchReference>());
                List<MatchReference> references = localDB.getMatchReferences(friend.getValue(), queue);
                if (references != null) {
                    friendMatches.put(friend.getKey(), references);
                }
            }
            Type friendMatchesType = new TypeToken<Map<String, List<MatchReference>>>() {
            }.getType();
            String friendMatchesJson = gson.toJson(friendMatches, friendMatchesType);
            Log.d(Params.TAG_DEBUG, "@StatUtil/getFriendStats: friendMatches is " + friendMatchesJson);

            // get friend match details
            Map<String, List<MatchDetail>> friendMatchDetails = new HashMap<>();
            for (Map.Entry<String, List<MatchReference>> friend : friendMatches.entrySet()) {
                friendMatchDetails.put(friend.getKey(), new ArrayList<MatchDetail>());
                for (int i = 0; i < friend.getValue().size() && i < maxMatches; i++) {
                    MatchReference reference = friend.getValue().get(i);
                    MatchDetail detail = localDB.getMatchDetail(reference.matchId);
                    if (detail != null) {
                        friendMatchDetails.get(friend.getKey()).add(detail);
                    }
                }
            }
            Type friendDetailsType = new TypeToken<Map<String, List<MatchDetail>>>() {
            }.getType();
            String friendDetailsJson = gson.toJson(friendMatchDetails, friendDetailsType);
            Log.d(Params.TAG_DEBUG, "@StatUtil/getFriendStats: friendMatchDetails is " + friendDetailsJson);

            // get friend participant stats list
            for (Map.Entry<String, List<MatchDetail>> friend : friendMatchDetails.entrySet()) {
                friendParticipantStatsList.put(friend.getKey(), new ArrayList<ParticipantStats>());
                long friendId = friendIds.get(friend.getKey());
                for (int i = 0; i < friend.getValue().size() && i < maxMatches; i++) {
                    MatchDetail matchDetail = friend.getValue().get(i);
                    ParticipantStats stats = localDB.getParticipantStats(friendId, matchDetail);
                    if (stats != null) {
                        friendParticipantStatsList.get(friend.getKey()).add(stats);
                    }
                }
            }
        }
        return friendParticipantStatsList;
    }

    static public List<SimpleXYSeries> createXYSeries(Set<String> fNames, Number[] uNums, Map<String, Number[]> fNums) {

        List<SimpleXYSeries> series = new ArrayList<>();

        // add user first
        series.add(new SimpleXYSeries(Arrays.asList(uNums), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, null));

        // add friends
        for (String name : fNames) {
            List<Number> nums = Arrays.asList(fNums.get(name));
            series.add(new SimpleXYSeries(nums, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, null));
        }
        return series;
    }

    static public void createPlot(Context context, XYPlot plot, List<SimpleXYSeries> series) {
        // add series to plot
        int i = 0;
        for (XYSeries seriesX : series) {
            LineAndPointFormatter seriesFormat = new LineAndPointFormatter();
            seriesFormat.setPointLabelFormatter(new PointLabelFormatter());

            switch (i) {
                case 0:
                    seriesFormat.configure(context.getApplicationContext(), R.xml.line_blue);
                    break;
                case 1:
                    seriesFormat.configure(context.getApplicationContext(), R.xml.line_green);
                    break;
                case 2:
                    seriesFormat.configure(context.getApplicationContext(), R.xml.line_orange);
                    break;
                case 3:
                    seriesFormat.configure(context.getApplicationContext(), R.xml.line_pink);
                    break;
                case 4:
                    seriesFormat.configure(context.getApplicationContext(), R.xml.line_purple);
                    break;
                case 5:
                    seriesFormat.configure(context.getApplicationContext(), R.xml.line_red);
                    break;
                case 6:
                    seriesFormat.configure(context.getApplicationContext(), R.xml.line_sky);
                    break;
                case 7:
                    seriesFormat.configure(context.getApplicationContext(), R.xml.line_yellow);
                    break;
            }
            i++;
            plot.addSeries(seriesX, seriesFormat);
        }

        // plot styling
        plot.setBorderStyle(XYPlot.BorderStyle.NONE, null, null);
        plot.setRangeStep(XYStepMode.INCREMENT_BY_VAL, 5);
        plot.setRangeValueFormat(new DecimalFormat("#"));
        plot.setTicksPerRangeLabel(2);
        XYGraphWidget g = plot.getGraphWidget();
        g.position(-0.5f, XLayoutStyle.RELATIVE_TO_RIGHT,
                -0.5f, YLayoutStyle.RELATIVE_TO_BOTTOM, AnchorPosition.CENTER);
        g.setSize(Size.FILL);
        g.setBackgroundPaint(null);
        g.setGridBackgroundPaint(null);
        g.setDomainOriginLinePaint(null);
        LayoutManager l = plot.getLayoutManager();
        l.remove(plot.getTitleWidget());
        l.remove(plot.getDomainLabelWidget());
        l.remove(plot.getLegendWidget());
    }

    static public int checkConditions(Context context, long summonerId, String queue, FriendsList friendsList) {

        // code 100: summoner has no matches for this queue
        if (!StatUtil.hasMatches(summonerId, queue)) {
            return 100;
        }

        // code 200: update job is currently running
        if (new UpdateJobInfo().isRunning(context)) {
            return 200;
        }

        // code 300: summoner has no friends to compare matches to
        if (friendsList == null || friendsList.getFriends() == null || friendsList.getFriends().size() == 0) {
            return 300;
        }

        // code 400: none of the summoners friends have any matches for this queue
        if (!StatUtil.hasMatches(friendsList, queue)) {
            return 400;
        }

        // code 500: no issues, conditions are good for showing data
        return 500;
    }
}
