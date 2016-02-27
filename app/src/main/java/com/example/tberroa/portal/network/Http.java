package com.example.tberroa.portal.network;


import android.util.Log;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Http {

    private OkHttpClient client = new OkHttpClient();

    public Http(){

    }

    public String get(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();
        // =================== TEST BLOCK =================
        Log.d("test1", "http response: " + response.body().toString());
        // ===============================================
        return response.body().string();
    }
}
