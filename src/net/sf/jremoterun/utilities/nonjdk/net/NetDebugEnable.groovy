package net.sf.jremoterun.utilities.nonjdk.net

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.nonjdk.javalangutils.PropsEnum;

import java.util.logging.Logger;

@CompileStatic
class NetDebugEnable {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

// https://docs.oracle.com/javase/7/docs/technotes/guides/security/jsse/ReadDebug.html


    static void enableDebug() {
        System.setProperty(PropsEnum.javax_net_debug.customName, 'ssl:handshake:verbose:keymanager:trustmanager')
        System.setProperty(PropsEnum.javax_net_debug.customName, 'all')
        System.setProperty(PropsEnum.jdk_http_auth_tunneling_disabledSchemes.customName, "");
        System.setProperty(PropsEnum.http_auth_preference.customName, "");

    }

    static void setJavaAgent(String agent) {
        // !use below instead
        //sun.net.www.protocol.http.HttpURLConnection.userAgent = agent
        PropsEnum.http_agent.toString()
        JrrClassUtils.setFieldValue(new ClRef('sun.net.www.protocol.http.HttpURLConnection'), 'userAgent', agent);
    }

    static void enableSslDebug(boolean doLogDebug) {
        PropsEnum.javax_net_debug.toString()
        JrrClassUtils.setFieldValue(javax.net.ssl.SSLSocketFactory, 'DEBUG', doLogDebug);
    }
}
