package com.example.tberroa.portal.models.requests;

import com.google.gson.annotations.Expose;

@SuppressWarnings("unused")
public class ReqChangeEmail {

    @Expose
    public String key;
    @Expose
    public String new_email;
    @Expose
    public String password;
    @Expose
    public String region;
}