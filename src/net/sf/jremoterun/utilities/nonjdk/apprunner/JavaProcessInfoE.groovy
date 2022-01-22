package net.sf.jremoterun.utilities.nonjdk.apprunner

import groovy.transform.CompileStatic;

@CompileStatic
interface JavaProcessInfoE {

    String name();

    File getLogPath();
    File getClassesDumpFile();
    int getClassesDumpRotateCount();
    NetworkTcpPorts getJmxPort();

}