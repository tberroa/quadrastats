package com.example.tberroa.portal.models.requests;

import com.google.gson.annotations.Expose;

@SuppressWarnings({"unused", "InstanceVariableNamingConvention"})
public class ReqChangePassword {

    @Expose
    public String current_password;
    @Expose
    public String key;
    @Expose
    public String new_password;
    @Expose
    public String region;
}
