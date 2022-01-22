/*
 * Copyright 2019 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.api.client.http.apache.v2;

import com.google.api.client.http.LowLevelHttpRequest;
import com.google.api.client.http.LowLevelHttpResponse;
import com.google.api.client.util.Preconditions;
import java.io.IOException;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpRequestBase;

/** @author Yaniv Inbar */
public class ApacheHttpRequestJrr extends LowLevelHttpRequest {
  public HttpClient httpClient;

  public HttpRequestBase request;

  public RequestConfig.Builder requestConfig;

  public GuavaApacheHttpTransportJrr guavaApacheHttpTransportJrr;

  @SuppressWarnings("deprecation")
  ApacheHttpRequestJrr(HttpClient httpClient, HttpRequestBase request,GuavaApacheHttpTransportJrr guavaApacheHttpTransportJrr) {
    this.httpClient = httpClient;
    this.guavaApacheHttpTransportJrr = guavaApacheHttpTransportJrr;
    this.request = request;
    // disable redirects as google-http-client handles redirects
    this.requestConfig =
        RequestConfig.custom()
            .setRedirectsEnabled(false)
            .setNormalizeUri(false)
            // TODO(chingor): configure in HttpClientBuilder when available
            .setStaleConnectionCheckEnabled(false);
  }

  @Override
  public void addHeader(String name, String value) {
    request.addHeader(name, value);
  }

  @Override
  public void setTimeout(int connectTimeout, int readTimeout) throws IOException {
    requestConfig.setConnectTimeout(connectTimeout).setSocketTimeout(readTimeout);
  }

  @Override
  public LowLevelHttpResponse execute() throws IOException {
    if (getStreamingContent() != null) {
      Preconditions.checkState(
          request instanceof HttpEntityEnclosingRequest,
          "Apache HTTP client does not support %s requests with content.",
          request.getRequestLine().getMethod());
      ContentEntity entity = new ContentEntity(getContentLength(), getStreamingContent());
      entity.setContentEncoding(getContentEncoding());
      entity.setContentType(getContentType());
      if (getContentLength() == -1) {
        entity.setChunked(true);
      }
      ((HttpEntityEnclosingRequest) request).setEntity(entity);
    }
    RequestConfig buildApache = guavaApacheHttpTransportJrr.buildRequestConfigJrr(this);
    request.setConfig(buildApache);
    try {
      return guavaApacheHttpTransportJrr.createApacheHttpResponseJrr(this);
    }catch(Exception e){
      guavaApacheHttpTransportJrr.onException3(e);
      throw e;
    }
  }


}
