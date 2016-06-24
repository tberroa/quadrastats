package com.example.tberroa.portal.models;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Type;
import java.util.List;

public class ModelUtil {

    private ModelUtil() {
    }

    public static <T> T fromJson(String jsonString, Class<T> clazz) {
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        return gson.fromJson(jsonString, clazz);
    }

    public static <T> List<T> fromJsonList(String jsonString, Type type) {
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        return gson.fromJson(jsonString, type);
    }

    public static <T> String toJson(T object, Class<T> clazz) {
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        return gson.toJson(object, clazz);
    }
}
