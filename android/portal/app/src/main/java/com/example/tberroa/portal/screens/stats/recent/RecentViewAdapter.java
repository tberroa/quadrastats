package com.example.tberroa.portal.screens.stats.recent;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.androidplot.ui.AnchorPosition;
import com.androidplot.ui.LayoutManager;
import com.androidplot.ui.Size;
import com.androidplot.ui.XLayoutStyle;
import com.androidplot.ui.YLayoutStyle;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.PointLabelFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYGraphWidget;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYStepMode;
import com.example.tberroa.portal.R;
import com.example.tberroa.portal.screens.ScreenUtil;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

public class RecentViewAdapter extends RecyclerView.Adapter<RecentViewAdapter.plotViewHolder> {

    private final List<String> plotTitles;
    private final Map<String, List<SimpleXYSeries>> plotData;
    private final int numberOfPlots;

    public RecentViewAdapter(List<String> plotTitles, Map<String, List<SimpleXYSeries>> plotData) {
        this.plotTitles = plotTitles;
        this.plotData = plotData;
        numberOfPlots = plotTitles.size();
    }

    public class plotViewHolder extends RecyclerView.ViewHolder {
        final TextView plotTitle;
        final XYPlot plot;
        final TextView noData;

        plotViewHolder(View itemView) {
            super(itemView);
            plotTitle = (TextView) itemView.findViewById(R.id.plot_title);
            plotTitle.setVisibility(View.INVISIBLE);
            plot = (XYPlot) itemView.findViewById(R.id.plot);
            plot.setVisibility(View.INVISIBLE);
            noData = (TextView) itemView.findViewById(R.id.no_data);
            noData.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return numberOfPlots;
    }

    @Override
    public plotViewHolder onCreateViewHolder(ViewGroup vG, int i) {
        return new plotViewHolder(LayoutInflater.from(vG.getContext()).inflate(R.layout.element_plot, vG, false));
    }

    @Override
    public void onBindViewHolder(plotViewHolder plotViewHolder, int i) {
        // make views visible
        plotViewHolder.plotTitle.setVisibility(View.VISIBLE);
        plotViewHolder.plot.setVisibility(View.VISIBLE);

        // set views
        plotViewHolder.plotTitle.setText(plotTitles.get(i));
        createPlot(plotViewHolder.plot, plotViewHolder.noData, plotData, i);
    }

    private void createPlot(final XYPlot plot, final TextView noData,
                            final Map<String, List<SimpleXYSeries>> plotData, final int pos) {
        // initialize the min and max values of the data
        double min = 500000, max = 0;

        // used to count how many data points there are
        int count = 0;

        // iterate over each entry in the plot data map
        int i = 0;
        for (Map.Entry<String, List<SimpleXYSeries>> entry : plotData.entrySet()) {
            // get the correct series
            SimpleXYSeries series = entry.getValue().get(pos);

            // update the min and max values as iteration occurs
            for (int j = 0; j < series.size(); j++) {
                count++;
                if (max < series.getY(j).doubleValue()) {
                    max = series.getY(j).doubleValue();
                }
                if (min > series.getY(j).doubleValue()) {
                    min = series.getY(j).doubleValue();
                }
            }

            // format the series
            LineAndPointFormatter seriesFormat = new LineAndPointFormatter();
            seriesFormat.setPointLabelFormatter(new PointLabelFormatter());
            seriesFormat.configure(plot.getContext(), ScreenUtil.intToSeriesColor(i));

            // add series to plot
            plot.addSeries(series, seriesFormat);

            i++;
        }

        // calculate the range step value
        double step;
        if (count > 1){
            step = Math.floor((max - min) / 5);
        } else{
            step = Math.floor(max/2);
        }

        if (step < 1) {
            step = 1;
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

        if (count == 1){
            plot.setDomainBoundaries(-1, 1, BoundaryMode.FIXED);
            if (max > 0){
                plot.setRangeBoundaries(0, Math.floor(2*max), BoundaryMode.FIXED);
            } else{
                plot.setRangeBoundaries(-2,2, BoundaryMode.FIXED);
            }
        }
        if (count == 0){
            plot.setVisibility(View.GONE);
            noData.setVisibility(View.VISIBLE);
        }
    }
}
