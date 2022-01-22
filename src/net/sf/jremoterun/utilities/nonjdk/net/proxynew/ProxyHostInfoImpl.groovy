package net.sf.jremoterun.utilities.nonjdk.net.proxynew

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

@CompileStatic
class ProxyHostInfoImpl implements ProxyHostInfo {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    String proxyHost;
    int proxyPort;

    ProxyHostInfoImpl(String proxyHost, int proxyPort) {
        this.proxyHost = proxyHost
        this.proxyPort = proxyPort
    }
}
