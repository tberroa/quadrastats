package com.example.tberroa.portal.models.requests;

import com.google.gson.annotations.Expose;

@SuppressWarnings({"unused", "InstanceVariableNamingConvention"})
public class ReqRegister {

    @Expose
    public String code;
    @Expose
    public String email;
    @Expose
    public String key;
    @Expose
    public String password;
    @Expose
    public String region;
}
