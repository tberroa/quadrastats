package com.example.tberroa.portal.screens.stats.recent;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.tberroa.portal.R;
import com.example.tberroa.portal.screens.ScreenUtil;
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

public class RecentViewAdapter extends RecyclerView.Adapter<RecentViewAdapter.chartViewHolder> {

    private final Context context;
    private final List<String> titles;
    private final List<List<String>> labelsList;
    private final List<List<ILineDataSet>> lineDataSetsList;
    private final List<Integer> emptyDataSets;
    private final Map<String, List<List<Number>>> data;
    private final int numberOfCharts;

    public RecentViewAdapter(Context context, ViewPackage viewPackage) {
        this.context = context;
        this.titles = viewPackage.titles;
        this.labelsList = viewPackage.labelsList;
        this.lineDataSetsList = viewPackage.lineDataSetsList;
        this.emptyDataSets = viewPackage.emptyDataSets;
        this.data = viewPackage.data;
        numberOfCharts = titles.size();
    }

    public class chartViewHolder extends RecyclerView.ViewHolder {
        final TextView title;
        final LineChart lineChart;
        final TextView noData;

        chartViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.chart_title);
            title.setVisibility(View.GONE);
            lineChart = (LineChart) itemView.findViewById(R.id.line_chart);
            lineChart.setVisibility(View.GONE);
            noData = (TextView) itemView.findViewById(R.id.no_data);
            noData.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return numberOfCharts;
    }

    @Override
    public chartViewHolder onCreateViewHolder(ViewGroup vG, int i) {
        return new chartViewHolder(LayoutInflater.from(vG.getContext()).inflate(R.layout.element_line_chart, vG, false));
    }

    @Override
    public void onBindViewHolder(chartViewHolder chartViewHolder, final int i) {
        // set title view
        chartViewHolder.title.setText(titles.get(i));
        chartViewHolder.title.setVisibility(View.VISIBLE);

        if (!emptyDataSets.contains(i)) {
            // populate chart
            chartViewHolder.lineChart.setData(new LineData(labelsList.get(i), lineDataSetsList.get(i)));

            // chart formatting
            chartViewHolder.lineChart.getAxisLeft().setTextColor(ContextCompat.getColor(context, R.color.white));
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

            // display chart
            chartViewHolder.lineChart.setVisibility(View.VISIBLE);
            chartViewHolder.lineChart.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    new BarChartDialog(titles.get(i), i).show();
                    return true;
                }
            });

        } else {
            chartViewHolder.noData.setVisibility(View.VISIBLE);
        }
    }

    private class BarChartDialog extends Dialog {

        final String title;
        final int i;

        public BarChartDialog(String title, int i) {
            super(context, R.style.DialogStyle);
            this.title = title;
            this.i = i;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.element_bar_chart);
            setTitle(context.getResources().getString(R.string.average) + " " + title);
            BarChart barChart = (BarChart) findViewById(R.id.bar_chart);

            // initialize the labels and data arrays
            List<String> labels = new ArrayList<>();
            List<BarEntry> data = new ArrayList<>();

            // populate the arrays
            int x = 0;
            for (Map.Entry<String, List<List<Number>>> entry : RecentViewAdapter.this.data.entrySet()) {
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
            barDataSet.setColors(ScreenUtil.getChartColors(), context);
            barDataSet.setDrawValues(false);
            barChart.setData(new BarData(labels, barDataSet));

            // chart formatting
            barChart.getAxisLeft().setTextColor(ContextCompat.getColor(context, R.color.white));
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
        }
    }
}
