package com.example.tberroa.portal.screens.stats;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

public class IntValueFormat implements ValueFormatter {

    @Override
    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
        if (Math.floor(value) == value) {
            return String.valueOf((int) Math.floor(value));
        } else {
            return String.valueOf(Math.round(value * 100.0) / 100.0);
        }
    }
}