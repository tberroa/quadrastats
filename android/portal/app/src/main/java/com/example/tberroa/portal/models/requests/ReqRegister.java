package com.example.tberroa.portal.models.requests;

import com.google.gson.annotations.Expose;

@SuppressWarnings({"unused"})
public class ReqRegister {

    @Expose
    public User user;

    @Expose
    public String region;

    @Expose
    public String key;

    @Expose
    public String code;

}
