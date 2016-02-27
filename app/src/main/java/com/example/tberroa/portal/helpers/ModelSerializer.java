package com.example.tberroa.portal.helpers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class ModelSerializer<T> {

    private Gson gson;
    private Type type;

    public ModelSerializer(){
        gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        type = new TypeToken<T>(){}.getType();
    }

    public String toJson(T object){
        return gson.toJson(object, type);
    }

    public T fromJson(String json){
        return gson.fromJson(json, type);
    }
}
