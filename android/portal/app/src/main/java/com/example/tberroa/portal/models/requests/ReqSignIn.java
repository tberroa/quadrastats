package com.example.tberroa.portal.models.requests;

import com.google.gson.annotations.Expose;

@SuppressWarnings("unused")
public class ReqSignIn {

    @Expose
    public String key;
    @Expose
    public String password;
    @Expose
    public String region;
}
