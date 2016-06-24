package com.example.tberroa.portal.screens.stats.recent;

import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.List;
import java.util.Map;

class ViewPackage {

    Map<String, List<List<Number>>> data;
    List<Integer> emptyDataSets;
    List<List<String>> labelsList;
    List<List<ILineDataSet>> lineDataSetsList;
    List<String> titles;
}
