package com.example.tberroa.portal.models.requests;

import com.google.gson.annotations.Expose;

import java.util.List;

@SuppressWarnings({"unused"})
public class ReqMatchStats {

    @Expose
    public List<String> keys;

    @Expose
    public int champion;

    @Expose
    public String lane;

    @Expose
    public String role;

}
