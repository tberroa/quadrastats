package com.example.tberroa.portal.screens.stats.withfriends;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tberroa.portal.R;
import com.example.tberroa.portal.models.stats.MatchStats;
import com.example.tberroa.portal.screens.ScreenUtil;
import com.example.tberroa.portal.screens.stats.StaticRiotData;
import com.example.tberroa.portal.screens.stats.StatsUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
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
            Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
            Map<String, MatchStats> matchStatsMap = gson.fromJson(matchStatsMapJson, matchStatsMapType);
            String staticRiotDataJson = bundle.getString("static_riot_data");
            Type staticRiotDataType = new TypeToken<StaticRiotData>(){}.getType();
            StaticRiotData staticRiotData = new Gson().fromJson(staticRiotDataJson, staticRiotDataType);

            if (matchStatsMap != null) {
                List<String> names = new ArrayList<>(matchStatsMap.keySet());

                // if it wasn't a five man queue, include label for non friends
                boolean notFiveMan = false;
                if (names.size() < 5) {
                    notFiveMan = true;
                    names.add(getResources().getString(R.string.gwf_others));
                }

                // initialize recycler view
                RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);
                recyclerView.setHasFixedSize(true);

                // populate recycler view
                recyclerView.setAdapter(new WFViewAdapter(getActivity(), matchStatsMap, staticRiotData));
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

                // display legend
                createLegend(names, v, notFiveMan);
            }
        }
        return v;
    }

    private void createLegend(List<String> names, View v, boolean notFiveMan) {
        // set unused elements to gone
        ImageView positionIcon = (ImageView) v.findViewById(R.id.position_view);
        positionIcon.setVisibility(View.GONE);
        ImageView championIcon = (ImageView) v.findViewById(R.id.champ_icon_view);
        championIcon.setVisibility(View.GONE);

        // set names
        GridLayout legendNames = (GridLayout) v.findViewById(R.id.names_layout);
        legendNames.removeAllViews();
        int i = 0;
        for (String name : names) {
            TextView textView = new TextView(getActivity());
            textView.setText(name);
            textView.setTextSize(12);
            if ((notFiveMan) && (i == (names.size() - 1))) {
                textView.setTextColor(ContextCompat.getColor(getActivity(), R.color.gray));
            } else {
                textView.setTextColor(ContextCompat.getColor(getActivity(), StatsUtil.intToColor(i)));
            }
            textView.setPadding(ScreenUtil.dpToPx(getActivity(), 5), 0, ScreenUtil.dpToPx(getActivity(), 5), 0);
            legendNames.addView(textView);
            i++;
        }
    }
}
