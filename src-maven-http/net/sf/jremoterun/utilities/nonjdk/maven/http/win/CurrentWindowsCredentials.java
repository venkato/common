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

import java.io.Serializable;
import java.security.Principal;

import org.apache.maven.wagon.providers.http.httpclient.annotation.Contract;
import org.apache.maven.wagon.providers.http.httpclient.annotation.ThreadingBehavior;
import org.apache.maven.wagon.providers.http.httpclient.auth.Credentials;


/**
 * Returns the current Windows user credentials
 * <p>
 * EXPERIMENTAL
 * </p>
 *
 * @since 4.4
 */
@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class CurrentWindowsCredentials implements Credentials, Serializable, Principal {

    private static final long serialVersionUID = 4361166468529298169L;

    public static final CurrentWindowsCredentials INSTANCE = new CurrentWindowsCredentials();


    public CurrentWindowsCredentials() {
    }

    @Override
    public Principal getUserPrincipal() {
        return this;
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        return getClass().equals(o.getClass());
    }


    /**
     * Returns an empty password
     */
    @Override
    public String getPassword() {
        return "";
    }

    public String userName1= "fake";

    @Override
    public String getName() {
        return userName1;
    }


    @Override
    public String toString() {
        return userName1;
    }

}



