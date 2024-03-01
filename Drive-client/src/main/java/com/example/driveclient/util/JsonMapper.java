package com.example.driveclient.util;

import com.google.gson.Gson;

public class JsonMapper {
    private static final Gson gson = new Gson();

    public static String getJson(Object obj){
        Gson gson = new Gson();
        return gson.toJson(obj);
    }

    public static <T> T JsonToObj(String json ,Class<T> className){
        return gson.fromJson(json,className);
    }
}
