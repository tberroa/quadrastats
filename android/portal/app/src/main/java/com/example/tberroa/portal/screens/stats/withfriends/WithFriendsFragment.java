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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WithFriendsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup group, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_with_friends, group, false);

        if (isAdded()) {
            // grab data passed to fragment
            Bundle bundle = getArguments();
            String matchStatsMapJson = bundle.getString("match_stats_map");
            Type matchStatsMapType = new TypeToken<Map<String, MatchStats>>() {
            }.getType();
            Map<String, MatchStats> matchStatsMap = new Gson().fromJson(matchStatsMapJson, matchStatsMapType);

            if (matchStatsMap != null) {
                List<String> names = new ArrayList<>(matchStatsMap.keySet());

                // initialize recycler view
                RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);
                recyclerView.setHasFixedSize(true);

                // populate recycler view
                recyclerView.setAdapter(new WithFriendsViewAdapter(getActivity(), matchStatsMap));
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

                // display legend
                createLegend(names, v);
            }
        }
        return v;
    }

    private void createLegend(List<String> names, View v) {
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
            textView.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
            textView.setPadding(ScreenUtil.dpToPx(getActivity(), 5), 0, ScreenUtil.dpToPx(getActivity(), 5), 0);
            legendNames.addView(textView);

            ImageView imageView = new ImageView(getActivity());
            imageView.setMinimumWidth(ScreenUtil.dpToPx(getActivity(), 10));
            imageView.setMinimumHeight(ScreenUtil.dpToPx(getActivity(), 10));
            imageView.setPadding(0, ScreenUtil.dpToPx(getActivity(), 5), 0, 0);
            imageView.setImageResource(ScreenUtil.intToColor(i));
            legendNames.addView(imageView);

            i++;
        }
    }
}
