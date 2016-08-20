package com.quadrastats.models.requests;

import com.google.gson.annotations.Expose;

@SuppressWarnings({"unused", "InstanceVariableNamingConvention"})
public class ReqFriend {

    @Expose
    public String friend_key;
    @Expose
    public String region;
    @Expose
    public String user_key;
}
