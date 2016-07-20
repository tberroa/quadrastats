package com.example.tberroa.portal.models.requests;

import com.google.gson.annotations.Expose;

import java.util.List;

@SuppressWarnings("unused")
public class ReqGetSummoners {

    @Expose
    public String region;
    @Expose
    public List<String> keys;
}
