package com.example.tberroa.portal.screens.stats.recent;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.tberroa.portal.R;
import com.example.tberroa.portal.screens.ScreenUtil;
import com.example.tberroa.portal.screens.stats.recent.RecentViewAdapter.chartViewHolder;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class RecentViewAdapter extends Adapter<chartViewHolder> {

    private final Context context;
    private final Map<String, List<List<Number>>> data;
    private final List<Integer> emptyDataSets;
    private final List<List<String>> labelsList;
    private final List<List<ILineDataSet>> lineDataSetsList;
    private final int numberOfCharts;
    private final List<String> titles;

    public RecentViewAdapter(Context context, ViewPackage viewPackage) {
        this.context = context;
        titles = viewPackage.titles;
        labelsList = viewPackage.labelsList;
        lineDataSetsList = viewPackage.lineDataSetsList;
        emptyDataSets = viewPackage.emptyDataSets;
        data = viewPackage.data;
        numberOfCharts = titles.size();
    }

    @Override
    public int getItemCount() {
        return numberOfCharts;
    }

    @Override
    public void onBindViewHolder(chartViewHolder chartViewHolder, int i) {
        // set title view
        chartViewHolder.title.setText(titles.get(i));
        chartViewHolder.title.setVisibility(View.VISIBLE);

        if (!emptyDataSets.contains(i)) {
            // populate chart
            chartViewHolder.lineChart.setData(new LineData(labelsList.get(i), lineDataSetsList.get(i)));

            // chart formatting
            chartViewHolder.lineChart.getAxisLeft().setTextColor(Color.WHITE);
            chartViewHolder.lineChart.setDescription("");
            chartViewHolder.lineChart.getXAxis().setDrawLabels(false);
            chartViewHolder.lineChart.getXAxis().setDrawGridLines(false);
            chartViewHolder.lineChart.getXAxis().setDrawAxisLine(false);
            chartViewHolder.lineChart.getAxisRight().setDrawLabels(false);
            chartViewHolder.lineChart.getAxisRight().setDrawGridLines(false);
            chartViewHolder.lineChart.getAxisRight().setDrawAxisLine(false);
            chartViewHolder.lineChart.getAxisLeft().setDrawAxisLine(false);
            chartViewHolder.lineChart.setDrawBorders(false);
            chartViewHolder.lineChart.getLegend().setEnabled(false);
            chartViewHolder.lineChart.getData().setHighlightEnabled(false);
            chartViewHolder.lineChart.setTouchEnabled(false);

            // display chart
            chartViewHolder.lineChart.setVisibility(View.VISIBLE);

            // attach long click listener
            int position = chartViewHolder.getAdapterPosition();
            chartViewHolder.lineChartLayout.setOnLongClickListener(new OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    new BarChartDialog(titles.get(position), position).show();
                    return true;
                }
            });
        } else {
            chartViewHolder.noData.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public chartViewHolder onCreateViewHolder(ViewGroup vG, int i) {
        return new chartViewHolder(LayoutInflater.from(vG.getContext()).inflate(R.layout.view_recent, vG, false));
    }

    public class chartViewHolder extends ViewHolder {
        final LineChart lineChart;
        final RelativeLayout lineChartLayout;
        final TextView noData;
        final TextView title;

        chartViewHolder(View itemView) {
            super(itemView);
            lineChartLayout = (RelativeLayout) itemView.findViewById(R.id.line_chart_layout);
            title = (TextView) itemView.findViewById(R.id.chart_title_view);
            title.setVisibility(View.GONE);
            lineChart = (LineChart) itemView.findViewById(R.id.line_chart);
            lineChart.setVisibility(View.GONE);
            noData = (TextView) itemView.findViewById(R.id.no_data_view);
            noData.setVisibility(View.GONE);
        }
    }

    private class BarChartDialog extends Dialog {

        final int i;
        final String title;

        public BarChartDialog(String title, int i) {
            super(context, R.style.DialogStyle);
            this.title = title;
            this.i = i;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.element_bar_chart);
            int width = (95 * ScreenUtil.screenWidth(context)) / 100;
            int height = (80 * ScreenUtil.screenHeight(context)) / 100;
            getWindow().setLayout(width, height);

            // initialize title
            String chartTitle = context.getResources().getString(R.string.rg_average) + " " + title;
            TextView chartTitleView = (TextView) findViewById(R.id.bar_chart_title);
            chartTitleView.setText(chartTitle);

            // declare bar chart
            BarChart barChart = (BarChart) findViewById(R.id.bar_chart);

            // initialize the labels and data arrays
            List<String> labels = new ArrayList<>();
            List<BarEntry> data = new ArrayList<>();

            // populate the arrays
            int x = 0;
            for (Entry<String, List<List<Number>>> entry : RecentViewAdapter.this.data.entrySet()) {
                labels.add("");

                // calculate average
                float sum = 0;
                for (int j = 0; j < entry.getValue().get(i).size(); j++) {
                    sum += entry.getValue().get(i).get(j).floatValue();
                }
                float average = sum / entry.getValue().get(i).size();

                // add average to data array
                data.add(new BarEntry(average, x));
                x++;
            }

            // use the arrays to create a data set
            BarDataSet barDataSet = new BarDataSet(data, null);
            barDataSet.setColors(ScreenUtil.chartColors(), context);
            barDataSet.setDrawValues(false);
            barChart.setData(new BarData(labels, barDataSet));

            // chart formatting
            barChart.getAxisLeft().setTextColor(Color.WHITE);
            barChart.setDescription("");
            barChart.getXAxis().setDrawLabels(false);
            barChart.getXAxis().setDrawGridLines(false);
            barChart.getXAxis().setDrawAxisLine(false);
            barChart.getAxisRight().setDrawLabels(false);
            barChart.getAxisRight().setDrawGridLines(false);
            barChart.getAxisRight().setDrawAxisLine(false);
            barChart.getAxisLeft().setDrawAxisLine(false);
            barChart.setDrawBorders(false);
            barChart.getLegend().setEnabled(false);
            barChart.getData().setHighlightEnabled(false);
            barChart.setTouchEnabled(false);
        }
    }
}
