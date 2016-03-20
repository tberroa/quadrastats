package com.example.tberroa.portal.screens.stats.recent;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.androidplot.xy.XYPlot;
import com.example.tberroa.portal.R;

import java.util.List;
import java.util.Map;

public class RecentViewAdapter extends RecyclerView.Adapter<RecentViewAdapter.plotViewHolder> {

    private final Context context;
    private final List<String> plotTitles;
    private final List<Map<String, Number[]>> plotData;
    private final int numberOfPlots;

    public RecentViewAdapter(Context context, List<String> plotTitles, List<Map<String, Number[]>> plotData) {
        this.context = context;
        this.plotTitles = plotTitles;
        this.plotData = plotData;
        numberOfPlots = plotData.size();
    }

    public class plotViewHolder extends RecyclerView.ViewHolder {
        final TextView plotTitle;
        final XYPlot plot;

        plotViewHolder(View itemView) {
            super(itemView);
            plotTitle = (TextView) itemView.findViewById(R.id.plot_title);
            plotTitle.setVisibility(View.INVISIBLE);
            plot = (XYPlot) itemView.findViewById(R.id.plot);
            plot.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return numberOfPlots;
    }

    @Override
    public plotViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        View inspection = LayoutInflater.from(context).inflate(R.layout.element_plot, viewGroup, false);
        return new plotViewHolder(inspection);
    }

    @Override
    public void onBindViewHolder(plotViewHolder plotViewHolder, int i) {
        // set views
        plotViewHolder.plotTitle.setText(plotTitles.get(i));
        RecentUtil.createPlot(context, plotViewHolder.plot, plotData.get(i));

        // make views visible
        plotViewHolder.plotTitle.setVisibility(View.VISIBLE);
        plotViewHolder.plot.setVisibility(View.VISIBLE);
    }
}
