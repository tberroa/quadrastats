package com.example.tberroa.portal.network;

import android.util.Log;

import com.example.tberroa.portal.data.Constants;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Http {

    private final OkHttpClient client = new OkHttpClient();
    private final MediaType mediaType = MediaType.parse("application/json; charset=utf-8");

    public String get(String url) throws IOException {
        Request request = new Builder().url(url).get().build();
        Response rawResponse = client.newCall(request).execute();
        String response = rawResponse.body().string().trim();
        Log.d(Constants.TAG_DEBUG, "@" + getClass().getSimpleName() + ": response is " + response);
        return response;
    }

    public String post(String url, String jsonString) throws IOException {
        Log.d(Constants.TAG_DEBUG, "@HttpPostJson: post body is " + jsonString);
        RequestBody body = RequestBody.create(mediaType, jsonString);
        Request request = new Builder().url(url).post(body).build();
        Response rawResponse = client.newCall(request).execute();
        String response = rawResponse.body().string().trim();
        Log.d(Constants.TAG_DEBUG, "@" + getClass().getSimpleName() + ": response is " + response);
        return response;
    }
}
