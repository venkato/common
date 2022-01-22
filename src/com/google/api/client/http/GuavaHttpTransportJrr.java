package com.google.api.client.http;

import com.google.api.client.http.HttpTransport;

public abstract class GuavaHttpTransportJrr extends HttpTransport {

    @Override
    public HttpRequest buildRequest() {
        checkForStop();
        return super.buildRequest();
    }

    public void checkForStop() {

    }
}
