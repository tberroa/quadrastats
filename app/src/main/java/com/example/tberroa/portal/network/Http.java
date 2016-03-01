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
    private final MediaType mediaType = MediaType.parse(Params.POST_MEDIA_TYPE);

    public Http(){
    }

    // used to query riot api
    public String[] get(String url) throws IOException {
        Request request = new Request.Builder().url(url).build();
        Response rawResponse = client.newCall(request).execute();
        String code = Integer.toString(rawResponse.code());
        String body = rawResponse.body().string().trim();
        Log.d(Params.TAG_DEBUG, "@HttpGet: response body is " + body);
        return new String[]{code, body};
    }

    // used to query altervista servers for signin/register authentication
    public String post(String url, String keyValuePairs) throws IOException {
        RequestBody body = RequestBody.create(mediaType, keyValuePairs);
        Request request = new Request.Builder().url(url).post(body).build();
        Response rawResponse = client.newCall(request).execute();
        String response = rawResponse.body().string().trim();
        Log.d(Params.TAG_DEBUG, "@HttpPost: response is " + response);
        return response;
    }

}
