package com.google.cloud.tools.jib.http;

import com.google.api.client.http.HttpTransport;
import com.google.cloud.tools.jib.api.LogEvent;
import com.google.cloud.tools.jib.http.FailoverHttpClient;

import java.io.IOException;
import java.net.URL;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class FailoverHttpClientJrr extends FailoverHttpClient {

    public FailoverHttpClientJrr(boolean enableHttpAndInsecureFailover, boolean sendAuthorizationOverHttp, Consumer<LogEvent> logger) {
        super(enableHttpAndInsecureFailover, sendAuthorizationOverHttp, logger);
    }

    public FailoverHttpClientJrr(boolean enableHttpAndInsecureFailover, boolean sendAuthorizationOverHttp, Consumer<LogEvent> logger, boolean enableRetry) {
        super(enableHttpAndInsecureFailover, sendAuthorizationOverHttp, logger, enableRetry);
    }

    public FailoverHttpClientJrr(boolean enableHttpAndInsecureFailover, boolean sendAuthorizationOverHttp, Consumer<LogEvent> logger, Supplier<HttpTransport> secureHttpTransportFactory, Supplier<HttpTransport> insecureHttpTransportFactory, boolean enableRetry) {
        super(enableHttpAndInsecureFailover, sendAuthorizationOverHttp, logger, secureHttpTransportFactory, insecureHttpTransportFactory, enableRetry);
    }

    @Override
    public Response call(String httpMethod, URL url, Request request) throws IOException {
        return super.call(httpMethod, url, request);
    }


}
