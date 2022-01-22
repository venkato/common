/*
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */

package net.sf.jremoterun.utilities.nonjdk.net.apachehttpclient.improved;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.jremoterun.utilities.JrrClassUtils;
import net.sf.jremoterun.utilities.classpath.ClRef;
import net.sf.jremoterun.utilities.nonjdk.net.JrrHttpUtils;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.auth.AuthSchemeProvider;
import org.apache.http.auth.AuthState;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.HttpClientParamConfig;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Lookup;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.cookie.CookieSpecProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.execchain.ClientExecChain;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpParamsNames;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Args;

/**
 * Internal class.
 *
 * @since 4.3
 */
@Contract(threading = ThreadingBehavior.SAFE_CONDITIONAL)
@SuppressWarnings("deprecation")
public class InternalHttpClientJrr extends CloseableHttpClient  implements Configurable {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    public ClientExecChain execChain;
    public HttpClientConnectionManager connManager;
    public HttpRoutePlanner routePlanner;
    public Lookup<CookieSpecProvider> cookieSpecRegistry;
    public Lookup<AuthSchemeProvider> authSchemeRegistry;
    public CookieStore cookieStore;
    public CredentialsProvider credentialsProvider;
    public RequestConfig defaultConfig;
    public List<Closeable> closeables;
    public JrrHttpUtils jrrHttpUtils;


    public HttpHost targetLatest;
    public HttpRequest requestLatest;
    public HttpContext contextLatest;



    public HttpRoute routeLatest;
    public HttpRequestWrapper httpRequestWrapperLatest;
    public HttpClientContext localcontextLatest;
    public HttpExecutionAware execAwareLatest;


    public InternalHttpClientJrr(JrrHttpUtils jrrHttpUtils) {
        this.jrrHttpUtils = jrrHttpUtils;
    }

    public static ClRef clRef=new ClRef("org.apache.http.impl.client.InternalHttpClient");

    public void copyPropsFromOtherClient(CloseableHttpClient other){
        execChain = (ClientExecChain) JrrClassUtils.getFieldValueR(new ClRef("org.apache.http.impl.client.InternalHttpClient"), other, "execChain");
        connManager= (HttpClientConnectionManager) JrrClassUtils.getFieldValueR(new ClRef("org.apache.http.impl.client.InternalHttpClient"), other, "connManager");
        routePlanner= (HttpRoutePlanner) JrrClassUtils.getFieldValueR(new ClRef("org.apache.http.impl.client.InternalHttpClient"), other, "routePlanner");
        cookieSpecRegistry= (Lookup<CookieSpecProvider>) JrrClassUtils.getFieldValueR(new ClRef("org.apache.http.impl.client.InternalHttpClient"), other, "cookieSpecRegistry");
        authSchemeRegistry= (Lookup<AuthSchemeProvider>) JrrClassUtils.getFieldValueR(new ClRef("org.apache.http.impl.client.InternalHttpClient"), other, "authSchemeRegistry");
        cookieStore= (CookieStore) JrrClassUtils.getFieldValueR(new ClRef("org.apache.http.impl.client.InternalHttpClient"), other, "cookieStore");
        credentialsProvider= (CredentialsProvider) JrrClassUtils.getFieldValueR(new ClRef("org.apache.http.impl.client.InternalHttpClient"), other, "credentialsProvider");
        defaultConfig= (RequestConfig) JrrClassUtils.getFieldValueR(new ClRef("org.apache.http.impl.client.InternalHttpClient"), other, "defaultConfig");
        closeables= (List<Closeable>) JrrClassUtils.getFieldValueR(new ClRef("org.apache.http.impl.client.InternalHttpClient"), other, "closeables");
    }


    public HttpRoute determineRoute(
            final HttpHost target,
            final HttpRequest request,
            final HttpContext context) throws HttpException {
        HttpHost host = target;
        if (host == null) {
            host = (HttpHost) request.getParams().getParameter(ClientPNames.DEFAULT_HOST);
        }
        return this.routePlanner.determineRoute(host, request, context);
    }

    public void setupContext(final HttpClientContext context) {
        if (context.getAttribute(HttpClientContext.TARGET_AUTH_STATE) == null) {
            context.setAttribute(HttpClientContext.TARGET_AUTH_STATE, new AuthState());
        }
        if (context.getAttribute(HttpClientContext.PROXY_AUTH_STATE) == null) {
            context.setAttribute(HttpClientContext.PROXY_AUTH_STATE, new AuthState());
        }
        if (context.getAttribute(HttpClientContext.AUTHSCHEME_REGISTRY) == null) {
            context.setAttribute(HttpClientContext.AUTHSCHEME_REGISTRY, this.authSchemeRegistry);
        }
        if (context.getAttribute(HttpClientContext.COOKIESPEC_REGISTRY) == null) {
            context.setAttribute(HttpClientContext.COOKIESPEC_REGISTRY, this.cookieSpecRegistry);
        }
        if (context.getAttribute(HttpClientContext.COOKIE_STORE) == null) {
            context.setAttribute(HttpClientContext.COOKIE_STORE, this.cookieStore);
        }
        if (context.getAttribute(HttpClientContext.CREDS_PROVIDER) == null) {
            context.setAttribute(HttpClientContext.CREDS_PROVIDER, this.credentialsProvider);
        }
        if (context.getAttribute(HttpClientContext.REQUEST_CONFIG) == null) {
            context.setAttribute(HttpClientContext.REQUEST_CONFIG, this.defaultConfig);
        }
    }

    @Override
    public CloseableHttpResponse doExecute(
            final HttpHost target,
            final HttpRequest request,
            final HttpContext context) throws IOException, ClientProtocolException {
        Args.notNull(request, "HTTP request");
        HttpExecutionAware execAware = null;

        targetLatest =target;
        requestLatest = request;
        contextLatest = context;


        if(request instanceof Configurable){
            jrrHttpUtils.reConfigure( ((Configurable) request).getConfig());
        }
        if (request instanceof HttpExecutionAware) {
            execAware = (HttpExecutionAware) request;
        }
        try {
            final HttpRequestWrapper wrapper = HttpRequestWrapper.wrap(request, target);
            final HttpClientContext localcontext = HttpClientContext.adapt(
                    context != null ? context : new BasicHttpContext());
            RequestConfig config = null;
            if (request instanceof Configurable) {
                if(jrrHttpUtils.defaultConfigAlwaysUse){

                }else {
                    config = ((Configurable) request).getConfig();
                }
            }
            if (config == null) {
                final HttpParams params = request.getParams();
                if (params instanceof HttpParamsNames) {
                    if (!((HttpParamsNames) params).getNames().isEmpty()) {
                        config = HttpClientParamConfig.getRequestConfig(params, this.defaultConfig);
                    }
                } else {
                    config = HttpClientParamConfig.getRequestConfig(params, this.defaultConfig);
                }
            }
            if (config != null) {
                localcontext.setRequestConfig(config);
            }
            setupContext(localcontext);
            final HttpRoute route = determineRoute(target, wrapper, localcontext);
            return doExecuteImpl(route, wrapper, localcontext, execAware);
        } catch (final HttpException httpException) {
            throw new ClientProtocolException(httpException);
        }
    }

    public CloseableHttpResponse doExecuteImpl(final HttpRoute route,final HttpRequestWrapper wrapper,final HttpClientContext localcontext,HttpExecutionAware execAware ) throws HttpException, IOException {
        try {
            routeLatest = route;
            httpRequestWrapperLatest = wrapper;
            localcontextLatest = localcontext;
            execAwareLatest = execAware;
            CloseableHttpResponse response = this.execChain.execute(route, wrapper, localcontext, execAware);
            jrrHttpUtils.onResponse(response);
            return response;
        }catch (Throwable e){
            jrrHttpUtils.onException(e);
            throw e;
        }
    }



    @Override
    public RequestConfig getConfig() {
        return this.defaultConfig;
    }

    @Override
    public void close() {
        if (this.closeables != null) {
            for (final Closeable closeable: this.closeables) {
                try {
                    closeable.close();
                } catch (final IOException ex) {
                    this.log.log(Level.SEVERE,ex.getMessage(), ex);
                }
            }
        }
    }

    @Override
    public HttpParams getParams() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ClientConnectionManager getConnectionManager() {
        return new JrrHttpClientConnectionManager(connManager);
    }

    @Override
    public CloseableHttpResponse execute(HttpUriRequest request) throws IOException {
        return super.execute(request);
    }


    @Override
    public CloseableHttpResponse execute(HttpUriRequest request, HttpContext context) throws IOException {
        return super.execute(request, context);
    }

    @Override
    public CloseableHttpResponse execute(HttpHost target, HttpRequest request, HttpContext context) throws IOException {
        return super.execute(target, request, context);
    }

}
