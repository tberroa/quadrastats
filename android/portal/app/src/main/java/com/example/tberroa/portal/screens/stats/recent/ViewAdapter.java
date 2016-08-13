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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.tberroa.portal.R;
import com.example.tberroa.portal.data.Constants;
import com.example.tberroa.portal.screens.ScreenUtil;
import com.example.tberroa.portal.screens.stats.StatsUtil;
import com.example.tberroa.portal.screens.stats.recent.ViewAdapter.ChartViewHolder;
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

public class ViewAdapter extends Adapter<ChartViewHolder> {

    private final Context context;
    private final Map<String, List<List<Number>>> data;
    private final List<Integer> emptyDataSets;
    private final List<List<String>> labelsList;
    private final List<List<ILineDataSet>> lineDataSetsList;
    private final int numberOfCharts;
    private final List<String> titles;

    public ViewAdapter(Context context, ViewPackage viewPackage) {
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
    public void onBindViewHolder(ChartViewHolder chartViewHolder, int i) {
        // set title view
        chartViewHolder.title.setText(titles.get(i));
        chartViewHolder.title.setVisibility(View.VISIBLE);

        if (!emptyDataSets.contains(i)) {
            // resize layout
            chartViewHolder.lineChartLayout.getLayoutParams().height = (55 * ScreenUtil.screenHeight(context)) / 100;
            chartViewHolder.lineChartLayout.setLayoutParams(chartViewHolder.lineChartLayout.getLayoutParams());

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
                    new AverageChartDialog(titles.get(position), position).show();
                    return true;
                }
            });
        } else {
            chartViewHolder.noData.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public ChartViewHolder onCreateViewHolder(ViewGroup vG, int i) {
        return new ChartViewHolder(LayoutInflater.from(vG.getContext()).inflate(R.layout.view_recent, vG, false));
    }

    class ChartViewHolder extends ViewHolder {

        final LineChart lineChart;
        final LinearLayout lineChartLayout;
        final TextView noData;
        final TextView title;

        ChartViewHolder(View itemView) {
            super(itemView);
            lineChartLayout = (LinearLayout) itemView.findViewById(R.id.line_chart_layout);
            title = (TextView) itemView.findViewById(R.id.chart_title_view);
            title.setVisibility(View.GONE);
            lineChart = (LineChart) itemView.findViewById(R.id.line_chart);
            lineChart.setVisibility(View.GONE);
            noData = (TextView) itemView.findViewById(R.id.no_data_view);
            noData.setVisibility(View.GONE);
        }
    }

    private class AverageChartDialog extends Dialog {

        final int i;
        final String title;

        AverageChartDialog(String title, int i) {
            super(context, R.style.AppTheme_Dialog);
            this.title = title;
            this.i = i;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.dialog_chart_average);
            int width = (Constants.UI_DIALOG_WIDTH * ScreenUtil.screenWidth(context)) / 100;
            int height = (Constants.UI_DIALOG_HEIGHT * ScreenUtil.screenHeight(context)) / 100;
            getWindow().setLayout(width, height);

            // resize chart layout
            LinearLayout chartLayout = (LinearLayout) findViewById(R.id.average_chart_layout);
            chartLayout.getLayoutParams().height = (75 * height) / 100;
            chartLayout.setLayoutParams(chartLayout.getLayoutParams());

            // initialize title
            String chartTitle = context.getResources().getString(R.string.rg_average) + " " + title;
            TextView chartTitleView = (TextView) findViewById(R.id.average_chart_title);
            chartTitleView.setText(chartTitle);

            // declare bar chart
            BarChart averageChart = (BarChart) findViewById(R.id.average_chart);

            // initialize the labels and data arrays
            List<String> labels = new ArrayList<>();
            List<BarEntry> data = new ArrayList<>();

            // populate the arrays
            int x = 0;
            for (Entry<String, List<List<Number>>> entry : ViewAdapter.this.data.entrySet()) {
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
            barDataSet.setColors(StatsUtil.chartColors(), context);
            barDataSet.setDrawValues(false);
            averageChart.setData(new BarData(labels, barDataSet));

            // chart formatting
            averageChart.getAxisLeft().setTextColor(Color.WHITE);
            averageChart.setDescription("");
            averageChart.getXAxis().setDrawLabels(false);
            averageChart.getXAxis().setDrawGridLines(false);
            averageChart.getXAxis().setDrawAxisLine(false);
            averageChart.getAxisRight().setDrawLabels(false);
            averageChart.getAxisRight().setDrawGridLines(false);
            averageChart.getAxisRight().setDrawAxisLine(false);
            averageChart.getAxisLeft().setDrawAxisLine(false);
            averageChart.setDrawBorders(false);
            averageChart.getLegend().setEnabled(false);
            averageChart.getData().setHighlightEnabled(false);
            averageChart.setTouchEnabled(false);
        }
    }
}
