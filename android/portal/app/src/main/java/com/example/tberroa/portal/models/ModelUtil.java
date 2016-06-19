package com.example.tberroa.portal.models;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Type;
import java.util.List;

public class ModelUtil {

    private ModelUtil() {
    }

    static public <T> String toJson(T object, Class<T> clazz) {
        final Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        return gson.toJson(object, clazz);
    }

    static public <T> T fromJson(String jsonString, Class<T> clazz) {
        final Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        return gson.fromJson(jsonString, clazz);
    }

    static public <T> List<T> fromJsonList(String jsonString, Type type) {
        final Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        return gson.fromJson(jsonString, type);
    }
}
