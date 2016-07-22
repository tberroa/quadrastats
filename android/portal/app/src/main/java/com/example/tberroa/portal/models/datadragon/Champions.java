package com.example.tberroa.portal.models.datadragon;

import com.google.gson.annotations.Expose;

import java.util.Map;

@SuppressWarnings("unused")
public class Champions {

    @Expose
    public String type;
    @Expose
    public String version;
    @Expose
    public Map<String, Champion> data;
}
