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
package net.sf.jremoterun.utilities.nonjdk.maven.http.win;

import net.sf.jremoterun.utilities.nonjdk.net.apachehttpclient.ntlmjava.NtlmJava;
import net.sf.jremoterun.utilities.nonjdk.net.apachehttpclient.win.WinAuthStatus;
import org.apache.maven.wagon.providers.http.httpclient.Header;
import org.apache.maven.wagon.providers.http.httpclient.HttpHost;
import org.apache.maven.wagon.providers.http.httpclient.HttpRequest;
import org.apache.maven.wagon.providers.http.httpclient.auth.AUTH;
import org.apache.maven.wagon.providers.http.httpclient.auth.AuthenticationException;
import org.apache.maven.wagon.providers.http.httpclient.auth.Credentials;
import org.apache.maven.wagon.providers.http.httpclient.auth.InvalidCredentialsException;
import org.apache.maven.wagon.providers.http.httpclient.auth.MalformedChallengeException;
import org.apache.maven.wagon.providers.http.httpclient.client.config.AuthSchemes;
import org.apache.maven.wagon.providers.http.httpclient.client.protocol.HttpClientContext;
import org.apache.maven.wagon.providers.http.httpclient.conn.routing.RouteInfo;
import org.apache.maven.wagon.providers.http.httpclient.impl.auth.AuthSchemeBase;
import org.apache.maven.wagon.providers.http.httpclient.message.BufferedHeader;
import org.apache.maven.wagon.providers.http.httpclient.protocol.HttpContext;
import org.apache.maven.wagon.providers.http.httpclient.util.CharArrayBuffer;


public class WindowsNegotiateScheme extends AuthSchemeBase {



    public NtlmJava ntlmJava = new NtlmJava();

    public WinAuthStatus winAuthStatus = WinAuthStatus.notInited;

    // NTLM or Negotiate
    public final String scheme;

    //public boolean continueNeeded = true;
    public String challenge;

    public WindowsNegotiateScheme(final String scheme,String sss) {
        this(scheme);
    }

    public WindowsNegotiateScheme(final String scheme) {
        super();
        this.scheme = (scheme == null) ? AuthSchemes.SPNEGO : scheme;
    }


    @Override
    public String getSchemeName() {
        return scheme;
    }

    // String parameters not supported
    @Override
    public String getParameter(final String name) {
        return null;
    }

    // NTLM/Negotiate do not support authentication realms
    @Override
    public String getRealm() {
        return null;
    }

    @Override
    public boolean isConnectionBased() {
        return true;
    }

    @Override
    protected void parseChallenge(
            final CharArrayBuffer buffer,
            final int beginIndex,
            final int endIndex) throws MalformedChallengeException {
        this.challenge = buffer.substringTrimmed(beginIndex, endIndex);
    }


    void onAuth1Done(){
        winAuthStatus = WinAuthStatus.auth1Done;
    }

    void onAuth3Done(){
        winAuthStatus = WinAuthStatus.auth3Done;
    }

    String getType1MessageResponse(){
        String response = ntlmJava.getType1MessageResponse();
        onAuth1Done();
        return response;
    }

    String getType3MessageResponse(String token){
        String response = ntlmJava.getType3MessageResponse(token);
        onAuth3Done();
        return response;
    }

    String getNext() throws AuthenticationException {
        final String response;
        if (winAuthStatus == WinAuthStatus.notInited) {
            response = getType1MessageResponse();
        } else if (this.challenge == null || this.challenge.isEmpty()) {
            throw new AuthenticationException("Authentication Failed");
        } else {
            response = getType3MessageResponse(this.challenge);
        }
        return response;
    }

    @Override
    public Header authenticate(
            final Credentials credentials,
            final HttpRequest request,
            final HttpContext context) throws AuthenticationException {

        final String response=getNext();

        final CharArrayBuffer buffer = new CharArrayBuffer(scheme.length() + 30);
        if (isProxy()) {
            buffer.append(AUTH.PROXY_AUTH_RESP);
        } else {
            buffer.append(AUTH.WWW_AUTH_RESP);
        }
        buffer.append(": ");
        buffer.append(scheme); // NTLM or Negotiate
        buffer.append(" ");
        buffer.append(response);
        return new BufferedHeader(buffer);
    }



    @Override
    public boolean isComplete() {
        return winAuthStatus ==WinAuthStatus.auth3Done;
    }

    /**
     * @deprecated Use {@link #authenticate(Credentials, HttpRequest, HttpContext)}
     */
    @Override
    @Deprecated
    public Header authenticate(
            final Credentials credentials,
            final HttpRequest request) throws AuthenticationException {
        return authenticate(credentials, request, null);
    }

}
