package com.example.tberroa.portal.screens.stats;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.tberroa.portal.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StatsFragment extends Fragment {

    private List<String> names;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup group, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_stats, group, false);

        names = new ArrayList<>();
        List<Map<String, Number[]>> plotData = new ArrayList<>();

        if (isAdded()) {
            // grab data passed to fragment
            Bundle bundle = this.getArguments();
            if (bundle != null) {
                names = bundle.getStringArrayList("names");
                String plotDataJson = bundle.getString("plot_data");
                Type plotDataType = new TypeToken<List<Map<String, Number[]>>>(){}.getType();
                plotData = new Gson().fromJson(plotDataJson, plotDataType);
            }

            // grab context
            Context context = getActivity();

            // create legend
            createLegend(v);

            // initialize recycler view
            RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);

            // populate recycler view
            StatsViewAdapter statsViewAdapter;
            statsViewAdapter = new StatsViewAdapter(context, plotData);
            recyclerView.setAdapter(statsViewAdapter);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
            recyclerView.setLayoutManager(linearLayoutManager);
        }
        return v;
    }

    private void createLegend(View v){
        List<TextView> nameViews = getLegendNames(v);
        List<ImageView> colorViews = getLegendColors(v);
        LinearLayout legendLayout = (LinearLayout) v.findViewById(R.id.legend);
        legendLayout.setVisibility(View.VISIBLE);

        // then friends
        int i = 0;
        for (String name : names) {
            nameViews.get(i).setText(name);
            nameViews.get(i).setVisibility(View.VISIBLE);

            switch (i) {
                case 0:
                    colorViews.get(i).setImageResource(R.color.series_blue);
                    break;
                case 1:
                    colorViews.get(i).setImageResource(R.color.series_green);
                    break;
                case 2:
                    colorViews.get(i).setImageResource(R.color.series_orange);
                    break;
                case 3:
                    colorViews.get(i).setImageResource(R.color.series_pink);
                    break;
                case 4:
                    colorViews.get(i).setImageResource(R.color.series_purple);
                    break;
                case 5:
                    colorViews.get(i).setImageResource(R.color.series_red);
                    break;
                case 6:
                    colorViews.get(i).setImageResource(R.color.series_sky);
                    break;
                case 7:
                    colorViews.get(i).setImageResource(R.color.series_yellow);
                    break;
            }
            colorViews.get(i).setVisibility(View.VISIBLE);
            i++;
        }
    }

    private List<TextView> getLegendNames(View v) {
        // initialize list
        List<TextView> nameViews = new ArrayList<>();

        TextView summoner1 = (TextView) v.findViewById(R.id.summoner_1);
        TextView summoner2 = (TextView) v.findViewById(R.id.summoner_2);
        TextView summoner3 = (TextView) v.findViewById(R.id.summoner_3);
        TextView summoner4 = (TextView) v.findViewById(R.id.summoner_4);
        TextView summoner5 = (TextView) v.findViewById(R.id.summoner_5);
        TextView summoner6 = (TextView) v.findViewById(R.id.summoner_6);
        TextView summoner7 = (TextView) v.findViewById(R.id.summoner_7);
        TextView summoner8 = (TextView) v.findViewById(R.id.summoner_8);

        summoner1.setVisibility(View.GONE);
        summoner2.setVisibility(View.GONE);
        summoner3.setVisibility(View.GONE);
        summoner4.setVisibility(View.GONE);
        summoner5.setVisibility(View.GONE);
        summoner6.setVisibility(View.GONE);
        summoner7.setVisibility(View.GONE);
        summoner8.setVisibility(View.GONE);

        nameViews.add(summoner1);
        nameViews.add(summoner2);
        nameViews.add(summoner3);
        nameViews.add(summoner4);
        nameViews.add(summoner5);
        nameViews.add(summoner6);
        nameViews.add(summoner7);
        nameViews.add(summoner8);

        return nameViews;
    }

    private List<ImageView> getLegendColors(View v) {
        // initialize list
        List<ImageView> colorViews = new ArrayList<>();

        ImageView color1 = (ImageView) v.findViewById(R.id.color_1);
        ImageView color2 = (ImageView) v.findViewById(R.id.color_2);
        ImageView color3 = (ImageView) v.findViewById(R.id.color_3);
        ImageView color4 = (ImageView) v.findViewById(R.id.color_4);
        ImageView color5 = (ImageView) v.findViewById(R.id.color_5);
        ImageView color6 = (ImageView) v.findViewById(R.id.color_6);
        ImageView color7 = (ImageView) v.findViewById(R.id.color_7);
        ImageView color8 = (ImageView) v.findViewById(R.id.color_8);

        color1.setVisibility(View.GONE);
        color2.setVisibility(View.GONE);
        color3.setVisibility(View.GONE);
        color4.setVisibility(View.GONE);
        color5.setVisibility(View.GONE);
        color6.setVisibility(View.GONE);
        color7.setVisibility(View.GONE);
        color8.setVisibility(View.GONE);

        colorViews.add(color1);
        colorViews.add(color2);
        colorViews.add(color3);
        colorViews.add(color4);
        colorViews.add(color5);
        colorViews.add(color6);
        colorViews.add(color7);
        colorViews.add(color8);

        return colorViews;
    }

}