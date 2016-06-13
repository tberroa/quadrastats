package com.example.tberroa.portal.models.requests;

import com.google.gson.annotations.Expose;

public class ReqSignIn {

    @Expose
    public String region;

    @Expose
    public String key;

    @Expose
    public String password;

}
