package com.quadrastats.screens.stats.withfriends;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.quadrastats.R;
import com.quadrastats.models.ModelUtil;
import com.quadrastats.models.stats.MatchStats;
import com.quadrastats.screens.StaticRiotData;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Map;

public class WFFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup group, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_with_friends, group, false);

        if (isAdded()) {
            // grab data passed to fragment
            Bundle bundle = getArguments();
            String matchStatsMapJson = bundle.getString("match_stats_map");
            Type matchStatsMapType = new TypeToken<Map<String, MatchStats>>() {
            }.getType();
            Map<String, MatchStats> matchStatsMap = ModelUtil.fromJsonStringMap(matchStatsMapJson, matchStatsMapType);
            String staticRiotDataJson = bundle.getString("static_riot_data");
            Type staticRiotDataType = new TypeToken<StaticRiotData>() {
            }.getType();
            StaticRiotData staticRiotData = new Gson().fromJson(staticRiotDataJson, staticRiotDataType);

            if (matchStatsMap != null) {
                // initialize recycler view
                RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);
                recyclerView.setHasFixedSize(true);

                // populate recycler view
                recyclerView.setAdapter(new ViewAdapter(getActivity(), matchStatsMap, staticRiotData));
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            }
        }
        return v;
    }
}
