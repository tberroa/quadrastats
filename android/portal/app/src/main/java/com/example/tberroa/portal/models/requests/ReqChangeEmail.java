package com.example.tberroa.portal.models.requests;

import com.google.gson.annotations.Expose;

@SuppressWarnings("unused")
public class ReqChangeEmail {

    @Expose
    public String region;
    @Expose
    public String key;
    @Expose
    public String password;
    @Expose
    public String new_email;
}
