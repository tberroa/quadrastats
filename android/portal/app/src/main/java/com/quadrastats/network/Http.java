package com.quadrastats.network;

import android.util.Log;

import com.quadrastats.data.Constants;

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

    public HttpResponse get(String url) throws IOException {
        // create the request
        Request request = new Builder().url(url).get().build();

        // execute the request
        Response response = client.newCall(request).execute();

        // get response code
        int code = response.code();

        // log response code
        Log.d(Constants.TAG_DEBUG, "@" + getClass().getSimpleName() + ": response code is " + code);

        // get response body
        String body = response.body().string().trim();

        // log response body
        Log.d(Constants.TAG_DEBUG, "@" + getClass().getSimpleName() + ": response body is " + body);

        // return HttpResponse object
        return new HttpResponse(code, body);
    }

    public HttpResponse post(String url, String jsonString) throws IOException {
        // log the post body
        Log.d(Constants.TAG_DEBUG, "@" + getClass().getSimpleName() + ": post body is " + jsonString);

        // create the request
        RequestBody requestBody = RequestBody.create(mediaType, jsonString);
        Request request = new Builder().url(url).post(requestBody).build();

        // execute the request
        Response response = client.newCall(request).execute();

        // get response code
        int code = response.code();

        // log response code
        Log.d(Constants.TAG_DEBUG, "@" + getClass().getSimpleName() + ": response code is " + code);

        // get response body
        String body = response.body().string().trim();

        // log response body
        Log.d(Constants.TAG_DEBUG, "@" + getClass().getSimpleName() + ": response body is " + body);

        // return HttpResponse object
        return new HttpResponse(code, body);
    }
}
