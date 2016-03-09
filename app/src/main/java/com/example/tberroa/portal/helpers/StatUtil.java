package com.example.tberroa.portal.helpers;

import android.content.Context;

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
import com.example.tberroa.portal.database.LocalDB;
import com.example.tberroa.portal.models.match.MatchDetail;
import com.example.tberroa.portal.models.match.ParticipantStats;
import com.example.tberroa.portal.models.matchlist.MatchReference;
import com.example.tberroa.portal.models.summoner.SummonerDto;

import java.util.ArrayList;
import java.util.Arrays;
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

    static public List<XYSeries> constructXYSeries(
            Set<String> friendNames, Number[] sNumbers, Map<String, Number[]> fNumbers){

        List<XYSeries> series = new ArrayList<>();

        // add summoner first
        series.add(new SimpleXYSeries(Arrays.asList(sNumbers),
                SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, null));

        // add friends
        for (String name : friendNames){
            series.add(new SimpleXYSeries(Arrays.asList(fNumbers.get(name)),
                    SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, null));
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
}
