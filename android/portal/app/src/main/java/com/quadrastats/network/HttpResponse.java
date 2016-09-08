package com.quadrastats.network;

public class HttpResponse {

    public String body;
    public int code;
    public String error;
    public boolean valid;

    public HttpResponse() {
    }

    HttpResponse(int code, String body) {
        this.code = code;
        this.body = body;
    }
}
