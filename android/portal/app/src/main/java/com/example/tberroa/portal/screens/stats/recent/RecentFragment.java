package com.example.tberroa.portal.screens.stats.recent;

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

        if (isAdded()) {
            // grab data passed to fragment
            Bundle bundle = this.getArguments();
            List<String> titles = bundle.getStringArrayList("plot_titles");
            Type plotDataType = new TypeToken<Map<String, List<List<Number>>>>() {}.getType();
            Map<String, List<List<Number>>> data = new Gson().fromJson(bundle.getString("plot_data"), plotDataType);

            // initialize recycler view
            RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);
            recyclerView.setHasFixedSize(true);

            // populate recycler view
            recyclerView.setAdapter(new RecentViewAdapter(titles, convertToXYSeries(data)));
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        }
        return v;
    }

    private Map<String, List<SimpleXYSeries>> convertToXYSeries(Map<String, List<List<Number>>> oldMap) {
        // initialize the new map
        Map<String, List<SimpleXYSeries>> newMap = new LinkedHashMap<>();

        // iterate over each entry in the old map
        for (Map.Entry<String, List<List<Number>>> entry : oldMap.entrySet()){
            // initialize a list of simple xy series
            List<SimpleXYSeries> seriesList = new ArrayList<>();

            // iterate over each list
            for (List<Number> list : entry.getValue()){
                // convert the list of numbers into a series and add the series to the list of series
                seriesList.add(new SimpleXYSeries(list, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, null));
            }

            // add the list of simple xy series to the new map
            newMap.put(entry.getKey(), seriesList);
        }

        return newMap;
    }
}