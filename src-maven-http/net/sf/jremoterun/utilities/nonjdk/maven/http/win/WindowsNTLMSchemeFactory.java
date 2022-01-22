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

import org.apache.maven.wagon.providers.http.httpclient.annotation.Contract;
import org.apache.maven.wagon.providers.http.httpclient.annotation.ThreadingBehavior;
import org.apache.maven.wagon.providers.http.httpclient.auth.AuthScheme;
import org.apache.maven.wagon.providers.http.httpclient.auth.AuthSchemeProvider;
import org.apache.maven.wagon.providers.http.httpclient.client.config.AuthSchemes;
import org.apache.maven.wagon.providers.http.httpclient.protocol.HttpContext;

/**
 * {@link AuthSchemeProvider} implementation that creates and initializes
 * {@link WindowsNegotiateScheme} using JNA to implement NTLM
 * <p>
 * EXPERIMENTAL
 * </p>
 *
 * @since 4.4
 */
@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class WindowsNTLMSchemeFactory implements AuthSchemeProvider {



    @Deprecated
    public WindowsNTLMSchemeFactory(String notused) {

    }

    public WindowsNTLMSchemeFactory() {
        super();
    }

    @Override
    public AuthScheme create(final HttpContext context) {
        return new WindowsNegotiateScheme(AuthSchemes.NTLM);
    }

}



