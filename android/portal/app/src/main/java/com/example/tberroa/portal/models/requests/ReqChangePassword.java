package com.example.tberroa.portal.models.requests;

import com.google.gson.annotations.Expose;

@SuppressWarnings("unused")
public class ReqChangePassword {

    @Expose
    public String key;
    @Expose
    public String new_password;
    @Expose
    public String old_password;
    @Expose
    public String region;
}