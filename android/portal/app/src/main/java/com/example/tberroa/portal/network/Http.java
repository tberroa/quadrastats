package com.example.tberroa.portal.network;

import android.util.Log;

import com.example.tberroa.portal.data.Params;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Http {

    private final OkHttpClient client = new OkHttpClient();
    private final MediaType mediaType = MediaType.parse("application/json; charset=utf-8");

    public Http() {
    }

    public String post(String url, String jsonString) throws IOException {
        Log.d(Params.TAG_DEBUG, "@HttpPostJson: post body is " + jsonString);
        RequestBody body = RequestBody.create(mediaType, jsonString);
        Request request = new Request.Builder().url(url).post(body).build();
        Response rawResponse = client.newCall(request).execute();
        String response = rawResponse.body().string().trim();
        Log.d(Params.TAG_DEBUG, "@HttpPostJson: response body is " + response);
        return response;
    }
}
