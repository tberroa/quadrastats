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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RecentFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup group, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_stats, group, false);

        List<String> plotTitles = new ArrayList<>();
        List<Map<String, List<Number>>> plotData = new ArrayList<>();

        if (isAdded()) {
            // grab data passed to fragment
            Bundle bundle = this.getArguments();
            if (bundle != null) {
                plotTitles = bundle.getStringArrayList("plot_titles");
                String plotDataJson = bundle.getString("plot_data");
                Type plotDataType = new TypeToken<List<Map<String, List<Number>>>>() {}.getType();
                plotData = new Gson().fromJson(plotDataJson, plotDataType);
            }

            // convert data from list of Numbers to SimpleXYSeries
            List<Map<String, SimpleXYSeries>> convertedPlotData = convertToXYSeries(plotData);

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

    private static List<Map<String, SimpleXYSeries>> convertToXYSeries(List<Map<String, List<Number>>> oldMaps) {
        // initialize the list of new maps
        List<Map<String, SimpleXYSeries>> newMaps = new ArrayList<>();

        // iterate over each map in list of old maps
        for (Map<String, List<Number>> map : oldMaps){
            // initialize a new map
            Map<String, SimpleXYSeries> newMap = new LinkedHashMap<>();

            // iterate over each entry in the old map
            for (Map.Entry<String, List<Number>> entry : map.entrySet()) {
                // get the summoner and the stats
                String summoner = entry.getKey();
                List<Number> stats = entry.getValue();

                // convert the list of numbers into a series
                SimpleXYSeries series = new SimpleXYSeries(stats, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, null);

                // put the converted data into the new map
                newMap.put(summoner, series);

                // put the new map into the list of new maps
                newMaps.add(newMap);
            }
        }

        return newMaps;
    }
}