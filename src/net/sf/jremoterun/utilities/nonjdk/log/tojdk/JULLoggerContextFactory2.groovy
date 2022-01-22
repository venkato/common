/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to you under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.sf.jremoterun.utilities.nonjdk.log.tojdk

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import org.apache.logging.log4j.spi.LoggerContext;
import org.apache.logging.log4j.spi.LoggerContextFactory
import org.apache.logging.log4j.tojul.JULLoggerContextFactory;

@CompileStatic
public class JULLoggerContextFactory2 implements LoggerContextFactory {
    private static LoggerContext context;

    // This implementation is strongly inspired by org.apache.logging.slf4j.SLF4JLoggerContextFactory

    public JULLoggerContextFactory2() {

    }

    void initContext(){
        if(context==null) {
            context = JrrClassUtils.getFieldValue(JULLoggerContextFactory, "context") as LoggerContext;
            //J new ClRef('org.apache.logging.log4j.tojul.JULLoggerContextFactory')
        }
    }

    @Override
    public LoggerContext getContext(
            final String fqcn, final ClassLoader loader, final Object externalContext, final boolean currentContext) {
        initContext()
        return context;
    }

    @Override
    public LoggerContext getContext(
            final String fqcn,
            final ClassLoader loader,
            final Object externalContext,
            final boolean currentContext,
            final URI configLocation,
            final String name) {
        initContext()
        return context;
    }

    @Override
    public void removeContext(final LoggerContext ignored) {}

    //@Override
    public boolean isClassLoaderDependent() {
        // context is always used
        return false;
    }
}
