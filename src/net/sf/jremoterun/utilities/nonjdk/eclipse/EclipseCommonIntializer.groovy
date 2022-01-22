package net.sf.jremoterun.utilities.nonjdk.eclipse;

import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.nonjdk.LogExitTimeHook

import net.sf.jremoterun.utilities.nonjdk.log.Log4j2CustomLogLayout
import net.sf.jremoterun.utilities.nonjdk.log.Log4j2PatternLayout
import net.sf.jremoterun.utilities.nonjdk.net.proxy.ProxyStaticMethods;

import java.util.logging.Logger;
import groovy.transform.CompileStatic;


@CompileStatic
class EclipseCommonIntializer {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static Log4j2CustomLogLayout proxyLogLayout = new Log4j2CustomLogLayout()

    public static List<String> ignoreClasses =['java.', 'sun.', ProxyStaticMethods.getPackage().name, net.sf.jremoterun.utilities.nonjdk.net.proxynew.ProxySelectI.getPackage().getName(), 'org.eclipse.ecf.internal.provider.filetransfer.httpclient4', 'org.apache.http.', 'org.eclipse.ecf.provider.filetransfer.httpclient4', 'org.eclipse.epp.internal.mpc.core.transport.httpclient.HttpClientTransport', 'org.eclipse.ecf.provider.filetransfer.retrieve.AbstractRetrieveFileTransfer', 'org.eclipse.jgit.util.HttpSupport', 'org.eclipse.ecf.provider.filetransfer.retrieve.MultiProtocolRetrieveAdapter', 'org.eclipse.jgit.transport.http.JDKHttpConnection', 'org.eclipse.jgit.transport.TransportHttp',];

    static void init3(){
        LogExitTimeHook.addShutDownHook();



        proxyLogLayout.additionalIgnore.addAll( ignoreClasses)
        Log4j2PatternLayout.customLayouts.put(new ClRef('net.sf.jremoterun.utilities.nonjdk.net.proxynew.ProxySelectGeneric').className, proxyLogLayout)
        Log4j2PatternLayout.customLayouts.put(new ClRef('net.sf.jremoterun.utilities.nonjdk.net.proxynew.ProxySetter2').className, proxyLogLayout)

//        ProxySetter.setProxySelectorWithJustLogging();
    }


}
