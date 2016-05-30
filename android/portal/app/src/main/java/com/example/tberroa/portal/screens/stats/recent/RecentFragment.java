package com.example.tberroa.portal.screens.stats.recent;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tberroa.portal.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RecentFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup group, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_stats, group, false);

        List<String> plotTitles = new ArrayList<>();
        List<Map<String, Number[]>> plotData = new ArrayList<>();

        if (isAdded()) {
            // grab data passed to fragment
            Bundle bundle = this.getArguments();
            if (bundle != null) {
                plotTitles = bundle.getStringArrayList("plot_titles");
                String plotDataJson = bundle.getString("plot_data");
                Type plotDataType = new TypeToken<List<Map<String, Number[]>>>() {
                }.getType();
                plotData = new Gson().fromJson(plotDataJson, plotDataType);
            }

            // grab context
            Context context = getActivity();

            // initialize recycler view
            RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);

            // populate recycler view
            RecentViewAdapter recentViewAdapter;
            recentViewAdapter = new RecentViewAdapter(context, plotTitles, plotData);
            recyclerView.setAdapter(recentViewAdapter);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
            recyclerView.setLayoutManager(linearLayoutManager);
        }
        return v;
    }
}