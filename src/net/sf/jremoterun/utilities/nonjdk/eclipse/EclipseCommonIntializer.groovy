package net.sf.jremoterun.utilities.nonjdk.eclipse;

import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.nonjdk.LogExitTimeHook
import net.sf.jremoterun.utilities.nonjdk.log.tojdk.JdkCustomLayout
import net.sf.jremoterun.utilities.nonjdk.log.tojdk.JdkLogFormatter2
import net.sf.jremoterun.utilities.nonjdk.net.proxy.ProxyStaticMethods;

import java.util.logging.Logger;
import groovy.transform.CompileStatic;


@CompileStatic
class EclipseCommonIntializer {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    //public static Log4j2CustomLogLayout proxyLogLayout = new Log4j2CustomLogLayout()
    public static JdkCustomLayout proxyLogLayout = new JdkCustomLayout();

    public static List<String> ignoreClasses =['java.', 'sun.', ProxyStaticMethods.getPackage().name, net.sf.jremoterun.utilities.nonjdk.net.proxynew.ProxySelectI.getPackage().getName(), 'org.eclipse.ecf.internal.provider.filetransfer.httpclient4', 'org.apache.http.', 'org.eclipse.ecf.provider.filetransfer.httpclient4', 'org.eclipse.epp.internal.mpc.core.transport.httpclient.HttpClientTransport', 'org.eclipse.ecf.provider.filetransfer.retrieve.AbstractRetrieveFileTransfer', 'org.eclipse.jgit.util.HttpSupport', 'org.eclipse.ecf.provider.filetransfer.retrieve.MultiProtocolRetrieveAdapter', 'org.eclipse.jgit.transport.http.JDKHttpConnection', 'org.eclipse.jgit.transport.TransportHttp',];

    static void init3(){
        LogExitTimeHook.addShutDownHook();



        proxyLogLayout.additionalIgnore.addAll( ignoreClasses)
        JdkLogFormatter2.customLayouts.put(new ClRef('net.sf.jremoterun.utilities.nonjdk.net.proxynew.ProxySelectGeneric').className, proxyLogLayout)
        JdkLogFormatter2.customLayouts.put(new ClRef('net.sf.jremoterun.utilities.nonjdk.net.proxynew.ProxySetter2').className, proxyLogLayout)

//        ProxySetter.setProxySelectorWithJustLogging();
    }


}
