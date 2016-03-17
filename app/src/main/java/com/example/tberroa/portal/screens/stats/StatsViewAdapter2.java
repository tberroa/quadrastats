package com.example.tberroa.portal.screens.stats;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidplot.xy.XYPlot;
import com.example.tberroa.portal.R;

import java.util.List;
import java.util.Map;

public class StatsViewAdapter2 extends ArrayAdapter<Map<String, Number[]>> {

    private final Context context;
    private final List<Map<String, Number[]>> plotData;

    public StatsViewAdapter2(Context context, List<Map<String, Number[]>> plotData) {
        super(context, -1, plotData);
        this.context = context;
        this.plotData = plotData;
    }

    class ViewHolder {
        LinearLayout renderingPlotLayout;
        RelativeLayout plotLayout;
        TextView plotTitle;
        XYPlot plot;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final ViewHolder viewHolder;

        if (convertView == null) {
            // initialize view holder and layout inflater
            viewHolder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.element_plot, parent, false);

            // initialize views
            viewHolder.plotLayout = (RelativeLayout) convertView.findViewById(R.id.plot_layout);
            viewHolder.plotLayout.setVisibility(View.VISIBLE);
            viewHolder.plotTitle = (TextView) convertView.findViewById(R.id.plot_title);
            viewHolder.plot = (XYPlot) convertView.findViewById(R.id.plot);

            // set tag
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // set name
        viewHolder.plotTitle.setText(R.string.wards_placed_per_game);

        // create plot in background
        new Thread(new Runnable() {
            @Override
            public void run() {
                // create plot
                StatUtil.createPlot(context, viewHolder.plot, plotData.get(position));
            }
        }).start();
        return convertView;
    }
}