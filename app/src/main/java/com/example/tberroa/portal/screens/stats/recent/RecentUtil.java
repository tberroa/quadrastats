package com.example.tberroa.portal.screens.stats.recent;

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
import com.androidplot.xy.XYStepMode;
import com.example.tberroa.portal.R;
import com.example.tberroa.portal.data.LocalDB;
import com.example.tberroa.portal.data.Params;
import com.example.tberroa.portal.models.match.MatchDetail;
import com.example.tberroa.portal.models.match.ParticipantStats;
import com.example.tberroa.portal.models.match.ParticipantTimeline;
import com.example.tberroa.portal.models.matchlist.MatchReference;
import com.example.tberroa.portal.models.summoner.FriendsList;
import com.example.tberroa.portal.models.summoner.SummonerDto;
import com.example.tberroa.portal.updater.UpdateJobInfo;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

class RecentUtil {

    private RecentUtil() {
    }

    // =============================================== UTILITY FUNCTIONS ===============================================
    static public int checkConditions(Context context, long summonerId, String queue, FriendsList friendsList) {

        // code 100: summoner has no matches for this queue
        if (!RecentUtil.hasMatches(summonerId, queue)) {
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
        if (!RecentUtil.hasMatches(friendsList, queue)) {
            return 400;
        }

        // code 500: no issues, conditions are good for showing data
        return 500;
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

    static public Map<String, List<ParticipantStats>> getStats(Map<String, Long> ids, String queue) {
        LocalDB localDB = new LocalDB();

        Map<String, List<ParticipantStats>> participantStats = new LinkedHashMap<>();

        // get match references
        Map<String, List<MatchReference>> matches = new LinkedHashMap<>();
        for (Map.Entry<String, Long> summoner : ids.entrySet()) {
            matches.put(summoner.getKey(), new ArrayList<MatchReference>());
            List<MatchReference> references = localDB.getMatchReferences(summoner.getValue(), queue);
            if (references != null) {
                matches.put(summoner.getKey(), references);
            }
        }

        // get match details
        Map<String, List<MatchDetail>> matchDetails = new LinkedHashMap<>();
        for (Map.Entry<String, List<MatchReference>> summoner : matches.entrySet()) {
            matchDetails.put(summoner.getKey(), new ArrayList<MatchDetail>());
            for (int i = 0; i < summoner.getValue().size() && i < Params.MAX_MATCHES; i++) {
                MatchReference reference = summoner.getValue().get(i);
                MatchDetail detail = localDB.getMatchDetail(reference.matchId);
                if (detail != null) {
                    matchDetails.get(summoner.getKey()).add(detail);
                }
            }
        }

        // get participant stats list
        for (Map.Entry<String, List<MatchDetail>> summoner : matchDetails.entrySet()) {
            participantStats.put(summoner.getKey(), new ArrayList<ParticipantStats>());
            long id = ids.get(summoner.getKey());
            for (int i = 0; i < summoner.getValue().size() && i < Params.MAX_MATCHES; i++) {
                MatchDetail matchDetail = summoner.getValue().get(i);
                ParticipantStats stats = localDB.getParticipantStats(id, matchDetail);
                if (stats != null) {
                    participantStats.get(summoner.getKey()).add(stats);
                }
            }
        }

        return participantStats;
    }

    static public Map<String, List<ParticipantTimeline>> getTimeline(Map<String, Long> ids, String queue) {
        LocalDB localDB = new LocalDB();

        Map<String, List<ParticipantTimeline>> participantTimeline = new LinkedHashMap<>();

        // get match references
        Map<String, List<MatchReference>> matches = new LinkedHashMap<>();
        for (Map.Entry<String, Long> summoner : ids.entrySet()) {
            matches.put(summoner.getKey(), new ArrayList<MatchReference>());
            List<MatchReference> references = localDB.getMatchReferences(summoner.getValue(), queue);
            if (references != null) {
                matches.put(summoner.getKey(), references);
            }
        }

        // get match details
        Map<String, List<MatchDetail>> matchDetails = new LinkedHashMap<>();
        for (Map.Entry<String, List<MatchReference>> summoner : matches.entrySet()) {
            matchDetails.put(summoner.getKey(), new ArrayList<MatchDetail>());
            for (int i = 0; i < summoner.getValue().size() && i < Params.MAX_MATCHES; i++) {
                MatchReference reference = summoner.getValue().get(i);
                MatchDetail detail = localDB.getMatchDetail(reference.matchId);
                if (detail != null) {
                    matchDetails.get(summoner.getKey()).add(detail);
                }
            }
        }

        // get participant timeline list
        for (Map.Entry<String, List<MatchDetail>> summoner : matchDetails.entrySet()) {
            participantTimeline.put(summoner.getKey(), new ArrayList<ParticipantTimeline>());
            long id = ids.get(summoner.getKey());
            for (int i = 0; i < summoner.getValue().size() && i < Params.MAX_MATCHES; i++) {
                MatchDetail matchDetail = summoner.getValue().get(i);
                ParticipantTimeline timeline = localDB.getParticipantTimeline(id, matchDetail);
                if (timeline != null) {
                    participantTimeline.get(summoner.getKey()).add(timeline);
                }
            }
        }

        return participantTimeline;
    }

    static public Map<String, long[]> getMatchDuration(Map<String, Long> ids, String queue) {
        LocalDB localDB = new LocalDB();

        Map<String, long[]> matchDurations = new LinkedHashMap<>();

        // get match references
        Map<String, List<MatchReference>> matches = new LinkedHashMap<>();
        for (Map.Entry<String, Long> summoner : ids.entrySet()) {
            matches.put(summoner.getKey(), new ArrayList<MatchReference>());
            List<MatchReference> references = localDB.getMatchReferences(summoner.getValue(), queue);
            if (references != null) {
                matches.put(summoner.getKey(), references);
            }
        }

        // get match details
        Map<String, List<MatchDetail>> matchDetails = new LinkedHashMap<>();
        for (Map.Entry<String, List<MatchReference>> summoner : matches.entrySet()) {
            matchDetails.put(summoner.getKey(), new ArrayList<MatchDetail>());
            for (int i = 0; i < summoner.getValue().size() && i < Params.MAX_MATCHES; i++) {
                MatchReference reference = summoner.getValue().get(i);
                MatchDetail detail = localDB.getMatchDetail(reference.matchId);
                if (detail != null) {
                    matchDetails.get(summoner.getKey()).add(detail);
                }
            }
        }

        // get match durations
        for (Map.Entry<String, List<MatchDetail>> summoner : matchDetails.entrySet()) {
            matchDurations.put(summoner.getKey(), new long[Params.MAX_MATCHES]);
            for (int i = 0; i < summoner.getValue().size() && i < Params.MAX_MATCHES; i++) {
                matchDurations.get(summoner.getKey())[i] = summoner.getValue().get(i).matchDuration;
            }
        }

        return matchDurations;
    }

    static public Map<String, Number[]> createNumberArrayL(Map<String, long[]> data) {
        Map<String, Number[]> numbers = new LinkedHashMap<>();
        for (Map.Entry<String, long[]> summoner : data.entrySet()) {
            Number[] array = new Number[Params.MAX_MATCHES];
            Arrays.fill(array, null);
            numbers.put(summoner.getKey(), array);
            for (int i = 0; i < summoner.getValue().length; i++) {
                numbers.get(summoner.getKey())[i] = summoner.getValue()[i];
            }
        }
        return numbers;
    }

    static public Map<String, Number[]> createNumberArrayD(Map<String, double[]> data) {
        Map<String, Number[]> numbers = new LinkedHashMap<>();
        for (Map.Entry<String, double[]> summoner : data.entrySet()) {
            Number[] array = new Number[Params.MAX_MATCHES];
            Arrays.fill(array, null);
            numbers.put(summoner.getKey(), array);
            for (int i = 0; i < summoner.getValue().length; i++) {
                numbers.get(summoner.getKey())[i] = summoner.getValue()[i];
            }
        }
        return numbers;
    }

    private static Map<String, SimpleXYSeries> createXYSeries(Map<String, Number[]> numbers) {
        Map<String, SimpleXYSeries> series = new LinkedHashMap<>();
        for (Map.Entry<String, Number[]> summoner : numbers.entrySet()) {
            List<Number> nums = Arrays.asList(summoner.getValue());
            series.put(summoner.getKey(), new SimpleXYSeries(nums, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, null));
        }
        return series;
    }

    static public void createPlot(Context context, XYPlot plot, Map<String, Number[]> numbers) {

        // get the min and max values
        double min = 500000, max = 0;
        for (Map.Entry<String, Number[]> entry : numbers.entrySet()) {
            for (Number number : entry.getValue()) {
                if (number != null) {
                    if (max < number.longValue()) {
                        max = number.longValue();
                    }
                    if (min > number.longValue()) {
                        min = number.longValue();
                    }
                }
            }
        }
        Log.d(Params.TAG_DEBUG, "@StatUtil/plot: min/max are " + Double.toString(min) + "/" + Double.toString(max));

        // calculate the range step value
        double step = Math.floor((max - min) / 5);
        if (step < 1) {
            step = 1;
        }
        Log.d(Params.TAG_DEBUG, "@StatUtil/createPlot: step is " + Double.toString(step));

        // turn numbers into an xy series
        Map<String, SimpleXYSeries> series = RecentUtil.createXYSeries(numbers);

        // add series to plot one at a time
        int i = 0;
        for (Map.Entry<String, SimpleXYSeries> seriesX : series.entrySet()) {
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
            plot.addSeries(seriesX.getValue(), seriesFormat);
        }

        // plot styling
        plot.setBorderStyle(XYPlot.BorderStyle.NONE, null, null);
        plot.setRangeStep(XYStepMode.INCREMENT_BY_VAL, step);
        plot.setRangeValueFormat(new DecimalFormat("#"));
        plot.setTicksPerRangeLabel(1);
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

    // ================================================ STAT FUNCTIONS =================================================
    // income
    static public Map<String, double[]> goldPerMin(Map<String, long[]> mD, Map<String, List<ParticipantStats>> stats) {
        Map<String, double[]> data = new LinkedHashMap<>();

        // get names
        ArrayList<String> names = new ArrayList<>(stats.keySet());

        // loop for each player
        for (int i=0; i<names.size(); i++) {
            data.put(names.get(i), new double[stats.get(names.get(i)).size()]);

            // loop for each game
            for (int j = 0; j < stats.get(names.get(i)).size(); j++) {
                double matchDuration = (double) mD.get(names.get(i))[j] / 60;
                long goldEarned = stats.get(names.get(i)).get(j).goldEarned;
                data.get(names.get(i))[j] = (double) goldEarned / matchDuration;
            }
        }
        return data;
    }

    static public Map<String, double[]> csAtTen(Map<String, List<ParticipantTimeline>> timeline) {
        Map<String, double[]> data = new LinkedHashMap<>();
        for (Map.Entry<String, List<ParticipantTimeline>> summoner : timeline.entrySet()) {
            data.put(summoner.getKey(), new double[summoner.getValue().size()]);
            for (int i = 0; i < summoner.getValue().size(); i++) {
                if (summoner.getValue().get(i) != null && summoner.getValue().get(i).creepsPerMinDeltas != null) {
                    data.get(summoner.getKey())[i] = summoner.getValue().get(i).creepsPerMinDeltas.zeroToTen * 10;
                }
            }
        }
        return data;
    }

    static public Map<String, double[]> csDiffAtTen(Map<String, List<ParticipantTimeline>> timeline) {
        Map<String, double[]> data = new LinkedHashMap<>();
        for (Map.Entry<String, List<ParticipantTimeline>> summoner : timeline.entrySet()) {
            data.put(summoner.getKey(), new double[summoner.getValue().size()]);
            for (int i = 0; i < summoner.getValue().size(); i++) {
                if (summoner.getValue().get(i) != null && summoner.getValue().get(i).csDiffPerMinDeltas != null) {
                    data.get(summoner.getKey())[i] = summoner.getValue().get(i).csDiffPerMinDeltas.zeroToTen * 10;
                }
            }
        }
        return data;
    }

    // offense
    static public Map<String, long[]> dmgPerMin(Map<String, long[]> mD, Map<String, List<ParticipantStats>> stats) {
        Map<String, long[]> data = new LinkedHashMap<>();

        // get names
        ArrayList<String> names = new ArrayList<>(stats.keySet());

        // loop for each player
        for (int i=0; i<names.size(); i++) {
            data.put(names.get(i), new long[stats.get(names.get(i)).size()]);

            // loop for each game
            for (int j = 0; j < stats.get(names.get(i)).size(); j++) {
                double matchDuration = (double) mD.get(names.get(i))[j] / 60;
                long damageDealt = stats.get(names.get(i)).get(j).totalDamageDealtToChampions;
                double result = (double) damageDealt / matchDuration;
                data.get(names.get(i))[j] = (long) Math.floor(result);
            }
        }
        return data;
    }

    static public Map<String, long[]> kills(Map<String, List<ParticipantStats>> stats) {
        Map<String, long[]> data = new LinkedHashMap<>();
        for (Map.Entry<String, List<ParticipantStats>> summoner : stats.entrySet()) {
            data.put(summoner.getKey(), new long[summoner.getValue().size()]);
            for (int i = 0; i < summoner.getValue().size(); i++) {
                if (summoner.getValue().get(i) != null) {
                    data.get(summoner.getKey())[i] = summoner.getValue().get(i).kills;
                }
            }
        }
        return data;
    }

    static public Map<String, long[]> killingSprees(Map<String, List<ParticipantStats>> stats) {
        Map<String, long[]> data = new LinkedHashMap<>();
        for (Map.Entry<String, List<ParticipantStats>> summoner : stats.entrySet()) {
            data.put(summoner.getKey(), new long[summoner.getValue().size()]);
            for (int i = 0; i < summoner.getValue().size(); i++) {
                if (summoner.getValue().get(i) != null) {
                    data.get(summoner.getKey())[i] = summoner.getValue().get(i).killingSprees;
                }
            }
        }
        return data;
    }

    static public Map<String, long[]> largestKillingSpree(Map<String, List<ParticipantStats>> stats) {
        Map<String, long[]> data = new LinkedHashMap<>();
        for (Map.Entry<String, List<ParticipantStats>> summoner : stats.entrySet()) {
            data.put(summoner.getKey(), new long[summoner.getValue().size()]);
            for (int i = 0; i < summoner.getValue().size(); i++) {
                if (summoner.getValue().get(i) != null) {
                    data.get(summoner.getKey())[i] = summoner.getValue().get(i).largestKillingSpree;
                }
            }
        }
        return data;
    }

    // utility
    static public Map<String, long[]> assists(Map<String, List<ParticipantStats>> stats) {
        Map<String, long[]> data = new LinkedHashMap<>();
        for (Map.Entry<String, List<ParticipantStats>> summoner : stats.entrySet()) {
            data.put(summoner.getKey(), new long[summoner.getValue().size()]);
            for (int i = 0; i < summoner.getValue().size(); i++) {
                if (summoner.getValue().get(i) != null) {
                    data.get(summoner.getKey())[i] = summoner.getValue().get(i).assists;
                }
            }
        }
        return data;
    }

    static public Map<String, long[]> damageTakenPerDeath(Map<String, List<ParticipantStats>> stats) {
        Map<String, long[]> data = new LinkedHashMap<>();
        for (Map.Entry<String, List<ParticipantStats>> summoner : stats.entrySet()) {
            data.put(summoner.getKey(), new long[summoner.getValue().size()]);
            for (int i = 0; i < summoner.getValue().size(); i++) {
                if (summoner.getValue().get(i) != null) {
                    long damageTaken = summoner.getValue().get(i).totalDamageTaken;
                    long deaths = summoner.getValue().get(i).deaths;
                    if (deaths != 0) {
                        data.get(summoner.getKey())[i] = damageTaken / deaths;
                    } else {
                        data.get(summoner.getKey())[i] = damageTaken;
                    }
                }
            }
        }
        return data;
    }

    // vision
    static public Map<String, long[]> visionWardsBought(Map<String, List<ParticipantStats>> stats) {
        Map<String, long[]> data = new LinkedHashMap<>();
        for (Map.Entry<String, List<ParticipantStats>> summoner : stats.entrySet()) {
            data.put(summoner.getKey(), new long[summoner.getValue().size()]);
            for (int i = 0; i < summoner.getValue().size(); i++) {
                if (summoner.getValue().get(i) != null) {
                    data.get(summoner.getKey())[i] = summoner.getValue().get(i).visionWardsBoughtInGame;
                }
            }
        }
        return data;
    }

    static public Map<String, long[]> wardsPlaced(Map<String, List<ParticipantStats>> stats) {
        Map<String, long[]> data = new LinkedHashMap<>();
        for (Map.Entry<String, List<ParticipantStats>> summoner : stats.entrySet()) {
            data.put(summoner.getKey(), new long[summoner.getValue().size()]);
            for (int i = 0; i < summoner.getValue().size(); i++) {
                if (summoner.getValue().get(i) != null) {
                    data.get(summoner.getKey())[i] = summoner.getValue().get(i).wardsPlaced;
                }
            }
        }
        return data;
    }

    static public Map<String, long[]> wardsKilled(Map<String, List<ParticipantStats>> stats) {
        Map<String, long[]> data = new LinkedHashMap<>();
        for (Map.Entry<String, List<ParticipantStats>> summoner : stats.entrySet()) {
            data.put(summoner.getKey(), new long[summoner.getValue().size()]);
            for (int i = 0; i < summoner.getValue().size(); i++) {
                if (summoner.getValue().get(i) != null) {
                    data.get(summoner.getKey())[i] = summoner.getValue().get(i).wardsKilled;
                }
            }
        }
        return data;
    }

}
