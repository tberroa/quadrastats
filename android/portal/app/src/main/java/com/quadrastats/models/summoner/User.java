package com.quadrastats.models.summoner;

import com.google.gson.annotations.Expose;

@SuppressWarnings({"unused", "InstanceVariableNamingConvention"})
public class User {

    @Expose
    public String email;

    @SuppressWarnings("RedundantNoArgConstructor")
    public User() {
    }
}