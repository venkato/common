package net.sf.jremoterun.utilities.nonjdk.jmx;

import groovy.transform.CompileStatic;

@CompileStatic
interface JmxLocalhostConnections {

    String name();

    int getPort();

}