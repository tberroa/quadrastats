package com.example.tberroa.portal.models.requests;

import com.google.gson.annotations.Expose;

import java.util.List;

@SuppressWarnings("unused")
public class ReqGetSummoners {

    @Expose
    public List<String> keys;
    @Expose
    public String region;
}
