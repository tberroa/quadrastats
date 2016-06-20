package com.example.tberroa.portal.screens.stats.recent;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tberroa.portal.R;
import com.example.tberroa.portal.screens.ScreenUtil;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.Utils;
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

        if (isAdded()) {
            // grab data passed to fragment
            Bundle bundle = this.getArguments();
            List<String> titles = bundle.getStringArrayList("titles");
            Type chartDataType = new TypeToken<Map<String, List<List<Number>>>>() {
            }.getType();
            Map<String, List<List<Number>>> chartData = new Gson().fromJson(bundle.getString("chart_data"), chartDataType);

            // organize the data
            Utils.init(getActivity());
            List<List<String>> labelsList = new ArrayList<>();
            List<List<List<Entry>>> dataListList = new ArrayList<>();
            for (Map.Entry<String, List<List<Number>>> entry : chartData.entrySet()) {
                for (int i = 0; i < entry.getValue().size(); i++) {
                    dataListList.add(new ArrayList<List<Entry>>());
                    labelsList.add(new ArrayList<String>());
                    List<Entry> data = new ArrayList<>();
                    for (int j = 0; j < entry.getValue().get(i).size(); j++) {
                        if (j > labelsList.get(i).size() - 1) {
                            labelsList.get(i).add("");
                        }
                        data.add(new Entry(entry.getValue().get(i).get(j).floatValue(), j));
                    }
                    dataListList.get(i).add(data);
                }
            }

            // create the data sets
            List<List<ILineDataSet>> lineDataSetsList = new ArrayList<>();
            int[] colors = ScreenUtil.getChartColors();
            for (List<List<Entry>> dataList : dataListList) {
                int x = 0;
                List<ILineDataSet> lineDataSets = new ArrayList<>();
                for (List<Entry> data : dataList) {
                    int[] color = new int[1];
                    color[0] = colors[x % colors.length];
                    LineDataSet lineDataSet = new LineDataSet(data, null);
                    lineDataSet.setColors(color, getActivity());
                    lineDataSet.setDrawValues(false);
                    lineDataSet.setCircleColor(ContextCompat.getColor(getContext(), color[0]));
                    lineDataSet.setCircleColorHole(ContextCompat.getColor(getActivity(), color[0]));
                    lineDataSets.add(lineDataSet);
                    x++;
                }
                lineDataSetsList.add(lineDataSets);
            }

            // check for empty charts
            List<Integer> emptyDataSets = new ArrayList<>();
            for (int i = 0; i < lineDataSetsList.size(); i++) {
                int count = 0;
                for (ILineDataSet lineDataSet : lineDataSetsList.get(i)) {
                    if (lineDataSet.getYValsForXIndex(0).length == 0) {
                        count++;
                    }
                }
                if (count == lineDataSetsList.get(i).size()) {
                    emptyDataSets.add(i);
                }
            }

            // create view package
            ViewPackage viewPackage = new ViewPackage();
            viewPackage.titles = titles;
            viewPackage.labelsList = labelsList;
            viewPackage.lineDataSetsList = lineDataSetsList;
            viewPackage.data = chartData;
            viewPackage.emptyDataSets = emptyDataSets;

            // initialize recycler view
            RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);
            recyclerView.setHasFixedSize(true);

            // populate recycler view
            recyclerView.setAdapter(new RecentViewAdapter(getActivity(), viewPackage));
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        }
        return v;
    }
}