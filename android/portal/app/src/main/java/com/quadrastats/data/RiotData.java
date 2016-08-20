package com.quadrastats.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.quadrastats.models.ModelUtil;
import com.quadrastats.models.datadragon.Champion;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RiotData {

    private static final String CHAMPIONS_LIST = "champions_list";
    private static final String CHAMPIONS_MAP = "champions_map";
    private static final String VERSION = "version";

    public List<Champion> getChampionsList(Context context) {
        String championsListJson = sharedPreferences(context).getString(CHAMPIONS_LIST, "");
        if (!"".equals(championsListJson)) {
            Type championsListType = new TypeToken<List<Champion>>() {
            }.getType();
            return ModelUtil.fromJsonList(championsListJson, championsListType);
        } else {
            return new ArrayList<>();
        }
    }

    public Map<String, Champion> getChampionsMap(Context context) {
        String championsMapJson = sharedPreferences(context).getString(CHAMPIONS_MAP, "");
        if (!"".equals(championsMapJson)) {
            Type championsMapType = new TypeToken<Map<String, Champion>>() {
            }.getType();
            return ModelUtil.fromJsonStringMap(championsMapJson, championsMapType);
        } else {
            return new HashMap<>();
        }
    }

    public String getVersion(Context context) {
        return sharedPreferences(context).getString(VERSION, "");
    }

    public void setChampionsList(Context context, List<Champion> championsList) {
        Type championsListType = new TypeToken<List<Champion>>() {
        }.getType();
        String championsListJson = ModelUtil.toJsonStringMap(championsList, championsListType);
        Editor editor = sharedPreferences(context).edit();
        editor.putString(CHAMPIONS_LIST, championsListJson);
        editor.apply();
    }

    public void setChampionsMap(Context context, Map<String, Champion> championsMap) {
        Type championsMapType = new TypeToken<Map<String, Champion>>() {
        }.getType();
        String championsMapJson = ModelUtil.toJsonStringMap(championsMap, championsMapType);
        Editor editor = sharedPreferences(context).edit();
        editor.putString(CHAMPIONS_MAP, championsMapJson);
        editor.apply();
    }

    public void setVersion(Context context, String version) {
        Editor editor = sharedPreferences(context).edit();
        editor.putString(VERSION, version);
        editor.apply();
    }

    private SharedPreferences sharedPreferences(Context context) {
        return context.getSharedPreferences("riot_data", Context.MODE_PRIVATE);
    }
}