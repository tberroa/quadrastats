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
import com.example.tberroa.portal.models.match.MatchDetail;
import com.example.tberroa.portal.models.match.ParticipantStats;
import com.example.tberroa.portal.models.matchlist.MatchList;
import com.example.tberroa.portal.models.matchlist.MatchReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DynamicQueueActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dynamic_queue);
        LocalDB localDB = new LocalDB();
        SummonerInfo summonerInfo = new SummonerInfo();
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

        // get match list
        MatchList matchList = localDB.getMatchList(summonerId);
        int totalMatches = matchList.totalGames;
        Log.d(Params.TAG_DEBUG, "@DynamicQueueActivity: totalMatches is " + Integer.toString(totalMatches));

        // get all match references
        List<MatchReference> matches = matchList.getMatchReferences();

        // get number of past games to show
        int loadedMatches = 5; // this can be user input in the future

        // get match details for number of loaded matches
        List<MatchDetail> matchDetails = new ArrayList<>();
        for (int i=0; i<loadedMatches; i++){
            matchDetails.add(localDB.getMatchDetail(matches.get(i).matchId));
        }

        // get participant stats for each match detail
        List<ParticipantStats> participantStatsList = new ArrayList<>();
        for (int i=0; i<loadedMatches; i++){
            participantStatsList.add(localDB.getParticipantStats(summonerId, matchDetails.get(i)));
        }

        // get wards placed per game
        long[] wardsPlaced = new long[totalMatches];
        for (int i=0; i<loadedMatches; i++){
            wardsPlaced[i] = participantStatsList.get(i).wardsPlaced;
        }
        Log.d(Params.TAG_DEBUG, "@DynamicQueueActivity: wardsPlaced[0] is " + Long.toString(wardsPlaced[0]));

        // ========================================= PLOT STUFF =========================================
        // initialize our XYPlot reference:
        XYPlot plot = (XYPlot) findViewById(R.id.plot);

        // create a couple arrays of y-values to plot:
        Number[] series1Numbers = {wardsPlaced[4], wardsPlaced[3], wardsPlaced[2], wardsPlaced[1], wardsPlaced[0]};
        Number[] series2Numbers = {5, 2, 10, 5, 20};

        // turn the above arrays into XYSeries':
        // (Y VALUES ONLY means use the element index as the x value)
        XYSeries series1 = new SimpleXYSeries(Arrays.asList(series1Numbers),
                SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "You");

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
