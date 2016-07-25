package com.example.tberroa.portal.models.requests;

import com.google.gson.annotations.Expose;

@SuppressWarnings({"unused", "InstanceVariableNamingConvention"})
public class ReqResetPassword {

    @Expose
    public String email;
    @Expose
    public String key;
    @Expose
    public String region;
}
