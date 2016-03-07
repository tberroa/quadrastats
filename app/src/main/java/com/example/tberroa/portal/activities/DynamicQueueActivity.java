package com.example.tberroa.portal.activities;

import android.content.Intent;
import android.graphics.DashPathEffect;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;

import com.androidplot.util.PixelUtils;
import com.androidplot.xy.CatmullRomInterpolator;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.PointLabelFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.example.tberroa.portal.R;
import com.example.tberroa.portal.data.Params;
import com.example.tberroa.portal.data.SummonerInfo;
import com.example.tberroa.portal.database.LocalDB;
import com.example.tberroa.portal.helpers.ModelSerializer;
import com.example.tberroa.portal.models.match.BannedChampion;
import com.example.tberroa.portal.models.match.Event;
import com.example.tberroa.portal.models.match.Frame;
import com.example.tberroa.portal.models.match.Mastery;
import com.example.tberroa.portal.models.match.MatchDetail;
import com.example.tberroa.portal.models.match.Participant;
import com.example.tberroa.portal.models.match.ParticipantFrame;
import com.example.tberroa.portal.models.match.ParticipantIdentity;
import com.example.tberroa.portal.models.match.ParticipantStats;
import com.example.tberroa.portal.models.match.ParticipantTimeline;
import com.example.tberroa.portal.models.match.ParticipantTimelineData;
import com.example.tberroa.portal.models.match.Player;
import com.example.tberroa.portal.models.match.Rune;
import com.example.tberroa.portal.models.match.Team;
import com.example.tberroa.portal.models.match.Timeline;
import com.example.tberroa.portal.models.matchlist.MatchList;
import com.example.tberroa.portal.models.matchlist.MatchReference;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class DynamicQueueActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dynamic_queue);
        LocalDB localDB = new LocalDB();
        SummonerInfo summonerInfo = new SummonerInfo();
        ModelSerializer modelSerializer = new ModelSerializer();
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

        long summonerId = summonerInfo.getId(this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.dynamic_queue);
        }

        toolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.back_button));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else {
                    startActivity(new Intent(DynamicQueueActivity.this, HomeActivity.class));
                    finish();
                }
            }
        });

        // ==================================== TEST MATCH LIST DATA ====================================
        // get matchlist
        MatchList matchList = localDB.getMatchList(summonerId);
        String matchListJson = modelSerializer.toJson(matchList, MatchList.class);
        Log.d(Params.TAG_DEBUG, "@DynamicQueueActivity: matchList is "+matchListJson);

        // get list of match references
        List<MatchReference> matches = matchList.getMatchReferences();
        Type matchReferenceListType = new TypeToken<List<MatchReference>>(){}.getType();
        String matchesJson = gson.toJson(matches, matchReferenceListType);
        Log.d(Params.TAG_DEBUG, "@DynamicQueueActivity: matches is " + matchesJson);

        // ================================= TEST MATCH DETAIL DATA ==================================
        // get match detail for most recent match
        MatchDetail matchDetail = localDB.getMatchDetail(matches.get(0).matchId);
        String matchDetailJson = modelSerializer.toJson(matchDetail, MatchDetail.class);
        Log.d(Params.TAG_DEBUG, "@DynamicQueueActivity: matchDetail is "+ matchDetailJson);

        // get participant identities
        List<ParticipantIdentity> participantIdentities = matchDetail.getParticipantIdentities();
        Type participantIdentityListType = new TypeToken<List<ParticipantIdentity>>(){}.getType();
        String participantIdentitiesJson = gson.toJson(participantIdentities, participantIdentityListType);
        Log.d(Params.TAG_DEBUG, "@DynamicQueueActivity: participantIdentitiesJson is "+ participantIdentitiesJson);

        // get player
        Player player = participantIdentities.get(0).player;

        // get participants
        List<Participant> participants = matchDetail.getParticipants();
        Type participantListType = new TypeToken<List<Participant>>(){}.getType();
        String participantsJson = gson.toJson(participants, participantListType);
        Log.d(Params.TAG_DEBUG, "@DynamicQueueActivity: participantsJson is "+ participantsJson);

        // get masteries
        List<Mastery> masteries = participants.get(0).getMasteries();

        // get runes
        List<Rune> runes = participants.get(0).getRunes();

        // get participant stats
        ParticipantStats participantStats = participants.get(0).stats;

        // get participant timeline
        ParticipantTimeline participantTimeline = participants.get(0).timeline;

        // get participant timeline data (doesn't work)
        if (participantTimeline != null){
            ParticipantTimelineData timelineData = participantTimeline.ancientGolemAssistsPerMinCounts;
        }

        // get teams
        List<Team> teams = matchDetail.getTeams();
        Type teamListType = new TypeToken<List<Team>>(){}.getType();
        String teamsJson = gson.toJson(teams, teamListType);
        Log.d(Params.TAG_DEBUG, "@DynamicQueueActivity: teamsJson is "+ teamsJson);

        // get bans
        List<BannedChampion> bans = teams.get(0).getBans();

        // get timeline
        Timeline timeline = matchDetail.timeline;
        String timelineJson = modelSerializer.toJson(timeline, Timeline.class);
        Log.d(Params.TAG_DEBUG, "@DynamicQueueActivity: timelineJson is " + timelineJson);

        // get frames
        List<Frame> frames = null;
        if (timeline != null){
            frames = timeline.getFrames();
        }

        // get events
        if (frames != null){
            List<Event> events = frames.get(0).getEvents();
        }

        // get participant frames
        if (frames != null){
            Map<String, ParticipantFrame> participantFrames = frames.get(0).participantFrames;
        }

        // ========================================= PLOT STUFF =========================================
        // initialize our XYPlot reference:
        XYPlot plot = (XYPlot) findViewById(R.id.plot);

        // create a couple arrays of y-values to plot:
        Number[] series1Numbers = {1, 4, 2, 8, 4, 16, 8, 32, 16, 64};
        Number[] series2Numbers = {5, 2, 10, 5, 20, 10, 40, 20, 80, 40};

        // turn the above arrays into XYSeries':
        // (Y VALUES ONLY means use the element index as the x value)
        XYSeries series1 = new SimpleXYSeries(Arrays.asList(series1Numbers),
                SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Series1");

        XYSeries series2 = new SimpleXYSeries(Arrays.asList(series2Numbers),
                SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Series2");

        // create formatters to use for drawing a series using LineAndPointRenderer
        // and configure them from xml:
        LineAndPointFormatter series1Format = new LineAndPointFormatter();
        series1Format.setPointLabelFormatter(new PointLabelFormatter());
        series1Format.configure(getApplicationContext(),
                R.xml.line_point_formatter_with_labels);

        LineAndPointFormatter series2Format = new LineAndPointFormatter();
        series2Format.setPointLabelFormatter(new PointLabelFormatter());
        series2Format.configure(getApplicationContext(),
                R.xml.line_point_formatter_with_labels_2);

        // add an "dash" effect to the series2 line:
        series2Format.getLinePaint().setPathEffect(new DashPathEffect(new float[] {
                PixelUtils.dpToPix(20), PixelUtils.dpToPix(15)}, 0));

        // just for fun, add some smoothing to the lines:
        series1Format.setInterpolationParams(
                new CatmullRomInterpolator.Params(10, CatmullRomInterpolator.Type.Centripetal));

        series2Format.setInterpolationParams(
                new CatmullRomInterpolator.Params(10, CatmullRomInterpolator.Type.Centripetal));

        // add a new series' to the xy plot
        plot.addSeries(series1, series1Format);
        plot.addSeries(series2, series2Format);

        // reduce the number of range labels
        plot.setTicksPerRangeLabel(3);

        // rotate domain labels 45 degrees to make them more compact horizontally:
        plot.getGraphWidget().setDomainLabelOrientation(-45);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        }
    }
}
