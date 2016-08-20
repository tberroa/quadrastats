package com.quadrastats.models.datadragon;

import com.google.gson.annotations.Expose;

import java.util.Map;

@SuppressWarnings({"unused", "InstanceVariableNamingConvention"})
public class Champions {

    @Expose
    public Map<String, Champion> data;
    @Expose
    public String type;
    @Expose
    public String version;
}
