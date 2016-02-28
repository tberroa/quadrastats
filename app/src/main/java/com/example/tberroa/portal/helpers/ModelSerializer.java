package com.example.tberroa.portal.helpers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ModelSerializer {

    private final Gson gson;

    public ModelSerializer(){
        gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
    }

    public <T> String toJson(T object, Class<T> clazz){
        return gson.toJson(object, clazz);
    }

    public <T> T fromJson(String jsonString, Class<T> clazz) {
        return gson.fromJson(jsonString, clazz);
    }
}
