package com.quadrastats.models.requests;

import com.google.gson.annotations.Expose;

@SuppressWarnings({"unused", "InstanceVariableNamingConvention"})
public class ReqError {

    @Expose
    public int error;
    @Expose
    public String message;
}
