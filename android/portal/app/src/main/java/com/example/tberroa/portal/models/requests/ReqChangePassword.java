package com.example.tberroa.portal.models.requests;

import com.google.gson.annotations.Expose;

@SuppressWarnings("unused")
public class ReqChangePassword {

    @Expose
    public String region;
    @Expose
    public String key;
    @Expose
    public String current_password;
    @Expose
    public String new_password;
}
