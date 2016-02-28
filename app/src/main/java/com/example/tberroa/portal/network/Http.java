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

    private OkHttpClient client = new OkHttpClient();
    private final MediaType mediaType = MediaType.parse(Params.POST_MEDIA_TYPE);

    public Http(){
    }

    public String get(String url) throws IOException {
        Request request = new Request.Builder().url(url).build();
        Response response = client.newCall(request).execute();
        Log.d(Params.TAG_DEBUG, "http response: " + response.body().toString());
        return response.body().string();
    }

    public String post(String url, String keyValuePairs) throws IOException {
        RequestBody body = RequestBody.create(mediaType, keyValuePairs);
        Request request = new Request.Builder().url(url).post(body).build();
        Response response = client.newCall(request).execute();
        return response.body().string().trim();
    }

}
