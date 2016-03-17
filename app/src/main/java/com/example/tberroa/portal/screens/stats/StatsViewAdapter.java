package com.example.tberroa.portal.screens.stats;

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

public class StatsViewAdapter extends RecyclerView.Adapter<StatsViewAdapter.plotViewHolder> {

    private final Context context;
    private final List<Map<String, Number[]>> plotData;
    private final int numberOfPlots;

    public StatsViewAdapter(Context context, List<Map<String, Number[]>> plotData){
        this.context = context;
        this.plotData = plotData;
        numberOfPlots = plotData.size();
    }

    public class plotViewHolder extends RecyclerView.ViewHolder{
        final TextView plotTitle;
        final XYPlot plot;
        plotViewHolder(View itemView) {
            super(itemView);
            plotTitle = (TextView) itemView.findViewById(R.id.plot_title);
            plot = (XYPlot) itemView.findViewById(R.id.plot);
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
    public void onBindViewHolder(final plotViewHolder plotViewHolder, final int i) {
        // set plot title
        plotViewHolder.plotTitle.setText(R.string.wards_placed_per_game);

        new Thread(new Runnable() {
            @Override
            public void run() {
                // create plot
                StatUtil.createPlot(context, plotViewHolder.plot, plotData.get(i));
            }
        }).start();

    }
}
