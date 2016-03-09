package com.example.tberroa.portal.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;

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
import com.example.tberroa.portal.data.Friends;
import com.example.tberroa.portal.data.Params;
import com.example.tberroa.portal.data.SummonerInfo;
import com.example.tberroa.portal.data.UpdateJobFlags;
import com.example.tberroa.portal.helpers.StatUtil;
import com.example.tberroa.portal.models.match.ParticipantStats;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DynamicQueueActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dynamic_queue);
        SummonerInfo summonerInfo = new SummonerInfo();
        long summonerId = summonerInfo.getId(this);

        // set toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.dynamic_queue);
        }

        // set back button
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

        // get the status of the background update job
        boolean isRunning = new UpdateJobFlags().isRunning(this);

        // only populate activity if the job is not running
        if (!isRunning) {
            // get friends
            Set<String> friendNames = new Friends().getNames(this);
            int numberOfFriends = friendNames.size();

            // only populate activity if the summoner has friends
            if (numberOfFriends > 0) {
                int loadedMatches = 5; // this can be changed by user input in future

                // get friend stats
                Map<String, List<ParticipantStats>> friendStats;
                friendStats = StatUtil.getFriendStats(friendNames, Params.TEAM_BUILDER_DRAFT_RANKED_5, loadedMatches);

                // get summoner stats
                List<ParticipantStats> summonerStats;
                summonerStats = StatUtil.getStats(summonerId, Params.TEAM_BUILDER_DRAFT_RANKED_5, loadedMatches);

                // get wards placed per game
                long[] sWardsPlaced = new long[loadedMatches];
                for (int i = 0; i < loadedMatches; i++) {
                    sWardsPlaced[i] = summonerStats.get(i).wardsPlaced;
                }

                // get friend wards placed per game
                Map<String, long[]> fWardsPlaced = new HashMap<>();
                for (String name : friendNames){
                    fWardsPlaced.put(name, new long[loadedMatches]);
                    if (friendStats.get(name).size() > 0){
                        for (int i=0; i < loadedMatches; i++) {
                            fWardsPlaced.get(name)[i] = friendStats.get(name).get(i).wardsPlaced;
                        }
                    }
                }


                // ========================================= PLOT STUFF =========================================
                // initialize our XYPlot reference:
                XYPlot plot = (XYPlot) findViewById(R.id.plot);

                // create a couple arrays of y-values to plot:
                Number[] sNumbers =
                        {sWardsPlaced[0], sWardsPlaced[1], sWardsPlaced[2], sWardsPlaced[3], sWardsPlaced[4]};

                Map<String, Number[]> fNumbers = new HashMap<>();
                for (String name : friendNames){
                    fNumbers.put(name, new Number[loadedMatches]);
                    for (int i=0; i<loadedMatches; i++){
                        fNumbers.get(name)[i] = fWardsPlaced.get(name)[i];
                    }
                }

                // turn the above arrays into XYSeries':
                // (Y VALUES ONLY means use the element index as the x value)
                XYSeries series1 = new SimpleXYSeries(Arrays.asList(sNumbers),
                        SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, getString(R.string.you));

                String firstFriend = friendNames.iterator().next();
                XYSeries series2 = new SimpleXYSeries(Arrays.asList(fNumbers.get(firstFriend)),
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

                // just for fun, add some smoothing to the lines:
                series1Format.setInterpolationParams(
                        new CatmullRomInterpolator.Params(10, CatmullRomInterpolator.Type.Centripetal));

                series2Format.setInterpolationParams(
                        new CatmullRomInterpolator.Params(10, CatmullRomInterpolator.Type.Centripetal));

                // add a new series' to the xy plot
                plot.addSeries(series1, series1Format);
                plot.addSeries(series2, series2Format);

                // get rid of unneeded elements
                plot.setBorderStyle(XYPlot.BorderStyle.NONE, null, null);

                XYGraphWidget g = plot.getGraphWidget();
                //g.position(0, XLayoutStyle.ABSOLUTE_FROM_LEFT, 0, YLayoutStyle.ABSOLUTE_FROM_TOP);
                g.position(-0.5f, XLayoutStyle.RELATIVE_TO_RIGHT, -0.5f, YLayoutStyle.RELATIVE_TO_BOTTOM, AnchorPosition.CENTER);
                g.setSize(Size.FILL);
                g.setBackgroundPaint(null);
                g.setGridBackgroundPaint(null);
                g.setDomainOriginLinePaint(null);
                //g.setRangeOriginLinePaint(null);

                LayoutManager l = plot.getLayoutManager();
                l.remove(plot.getTitleWidget());
                l.remove(plot.getRangeLabelWidget());
                l.remove(plot.getDomainLabelWidget());
                l.remove(plot.getLegendWidget());
            } else {
                Log.d(Params.TAG_DEBUG, "@DynamicQueueActivity: plot not shown, update job is running");
            }
        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        }
    }
}
