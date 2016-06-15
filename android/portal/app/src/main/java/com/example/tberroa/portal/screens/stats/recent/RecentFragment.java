package com.example.tberroa.portal.screens.stats.recent;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androidplot.xy.SimpleXYSeries;
import com.example.tberroa.portal.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecentFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup group, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_stats, group, false);

        List<String> plotTitles = new ArrayList<>();
        Map<String, List<List<Number>>> plotData = new HashMap<>();

        if (isAdded()) {
            // grab data passed to fragment
            Bundle bundle = this.getArguments();
            if (bundle != null) {
                plotTitles = bundle.getStringArrayList("plot_titles");
                String plotDataJson = bundle.getString("plot_data");
                Type plotDataType = new TypeToken<Map<String, List<List<Number>>>>() {}.getType();
                plotData = new Gson().fromJson(plotDataJson, plotDataType);
            }

            // convert data from list of Numbers to SimpleXYSeries
            Map<String, List<SimpleXYSeries>> convertedPlotData = convertToXYSeries(plotData);

            // grab context
            Context context = getActivity();

            // initialize recycler view
            RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);

            // populate recycler view
            RecentViewAdapter recentViewAdapter;
            recentViewAdapter = new RecentViewAdapter(context, plotTitles, convertedPlotData);
            recyclerView.setAdapter(recentViewAdapter);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
            recyclerView.setLayoutManager(linearLayoutManager);
        }
        return v;
    }

    private static Map<String, List<SimpleXYSeries>> convertToXYSeries(Map<String, List<List<Number>>> oldMap) {
        // initialize the new map
        Map<String, List<SimpleXYSeries>> newMap = new HashMap<>();

        // iterate over each entry in the old map
        for (Map.Entry<String, List<List<Number>>> entry : oldMap.entrySet()){
            // initialize a list of simple xy series
            List<SimpleXYSeries> seriesList = new ArrayList<>();

            // iterate over each list
            for (List<Number> list : entry.getValue()){
                // convert the list of numbers into a series
                SimpleXYSeries series = new SimpleXYSeries(list, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, null);

                // add the series to the list of series
                seriesList.add(series);
            }

            // add the list of simple xy series to the new map
            newMap.put(entry.getKey(), seriesList);
        }

        return newMap;
    }
}