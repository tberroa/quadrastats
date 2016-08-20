package com.quadrastats.models.datadragon;

import com.google.gson.annotations.Expose;

@SuppressWarnings({"unused", "InstanceVariableNamingConvention"})
public class Champion {

    @Expose
    public long id;
    @Expose
    public String key;
    @Expose
    public String name;
    @Expose
    public String title;
}
