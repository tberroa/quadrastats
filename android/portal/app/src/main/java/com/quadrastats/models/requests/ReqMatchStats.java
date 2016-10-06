package com.quadrastats.models.requests;

import com.google.gson.annotations.Expose;

import java.util.List;

@SuppressWarnings({"unused", "InstanceVariableNamingConvention"})
public class ReqMatchStats {

    @Expose
    public List<String> keys;
    @Expose
    public String region;
}
