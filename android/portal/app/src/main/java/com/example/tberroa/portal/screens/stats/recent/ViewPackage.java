package com.example.tberroa.portal.screens.stats.recent;

import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.List;
import java.util.Map;

class ViewPackage {
    List<String> titles;
    List<List<String>> labelsList;
    List<List<ILineDataSet>> lineDataSetsList;
    List<Integer> emptyDataSets;
    Map<String, List<List<Number>>> data;
}
