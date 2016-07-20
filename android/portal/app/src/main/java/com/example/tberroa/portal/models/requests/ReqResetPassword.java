package com.example.tberroa.portal.models.requests;

import com.google.gson.annotations.Expose;

@SuppressWarnings("unused")
public class ReqResetPassword {

    @Expose
    public String email;
    @Expose
    public String key;
    @Expose
    public String region;
}
