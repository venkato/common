package net.sf.jremoterun.utilities.nonjdk.log

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.nonjdk.log.log4j2.Log4j1Utils

import java.util.logging.Logger;

@CompileStatic
class AddDefaultIgnoreClasses implements Runnable{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    @Override
    void run() {
        addIgnoreClasses();
    }

    static void addAzureIgnoreClasses(){
        JrrClassUtils.ignoreClassesForCurrentClass.add('com.azure.core.util.logging.');
        JrrClassUtils.ignoreClassesForCurrentClass.add('com.microsoft.rest.interceptors.');
        addClRef(new ClRef('com.microsoft.rest.interceptors.LoggingInterceptor'));
        addClRef(new ClRef('com.microsoft.rest.retry.RetryHandler'));
        addClRef(new ClRef('com.microsoft.azure.credentials.AzureTokenCredentialsInterceptor'));
        addClRef(new ClRef('com.microsoft.azure.management.resources.fluentcore.utils.ResourceManagerThrottlingInterceptor'));
        addClRef(new ClRef('com.microsoft.azure.management.resources.fluentcore.utils.ProviderRegistrationInterceptor'));

    }

    static void addOkHttpIgnoreClasses(){
        addClRef(new ClRef('okhttp3.internal.http.RealInterceptorChain'));
        addClRef(new ClRef('okhttp3.internal.connection.ConnectInterceptor'));
        addClRef(new ClRef('okhttp3.internal.cache.CacheInterceptor'));
        addClRef(new ClRef('okhttp3.internal.http.BridgeInterceptor'));
        addClRef(new ClRef('okhttp3.internal.http.RetryAndFollowUpInterceptor'));
        addClRef(new ClRef('okhttp3.internal.connection.RealCall'));

    }

    static void addRetrofit(){
        addClRef(new ClRef('retrofit2.OkHttpCall'));
        addClRef(new ClRef('retrofit2.adapter.rxjava.CallExecuteOnSubscribe'));
        addClRef(new ClRef('rx.Observable'));
        addClRef(new ClRef('rx.internal.operators.OnSubscribeMap'));
        addClRef(new ClRef('rx.internal.operators.OnSubscribeLift'));
        addClRef(new ClRef('rx.observables.BlockingObservable'));
    }

    static void addIgnoreClasses(){
        addClRef(new ClRef('net.sf.jremoterun.utilities.nonjdk.net.proxy.ProxyAuthLogAccess'));
        addClRef(new ClRef('net.sf.jremoterun.utilities.nonjdk.sshsup.JscpLogger'));
//        addClRef(new ClRef('net.sf.jremoterun.utilities.nonjdk.sshsup.hierynomus.SSHPacketHandlerLog'));
//        addClRef(new ClRef('net.sf.jremoterun.utilities.nonjdk.sshsup.hierynomus.SSHPacketHandlerProxy'));
        addClRef(new ClRef('net.sf.jremoterun.utilities.nonjdk.git.GitProgressLogger'));
        JrrClassUtils.ignoreClassesForCurrentClass.add('net.sf.jremoterun.utilities.nonjdk.net.proxy.');
        JrrClassUtils.ignoreClassesForCurrentClass.add('net.sf.jremoterun.utilities.nonjdk.net.proxynew.');
        //addClRef(new ClRef('jdk.internal.net.http.HttpRequestImpl'));
        //addClRef(new ClRef('sun.net.www.http.HttpClient'));
        addClRef(new ClRef('net.sf.jremoterun.utilities.nonjdk.idea.IdeaProxyStore'));
        addClRef(new ClRef('net.sf.jremoterun.utilities.javassist.codeinjector.InjectedCode'));
        addClRef(new ClRef('net.sf.jremoterun.utilities.nonjdk.sshsup.hierynomus.CompressionLog'));
        addClRef(new ClRef('net.schmizz.sshj.transport.Encoder'));
        addClRef(new ClRef('org.jetbrains.java.decompiler.IdeaLogger'));
        addClRef(new ClRef('com.intellij.openapi.fileEditor.impl.IdeUiServiceImpl'));
//        addClRef(new ClRef('kotlin.coroutines.jvm.internal.BaseContinuationImpl'));
//        addClRef(new ClRef('kotlinx.coroutines.DispatchedTask'));
//        addClRef(new ClRef('kotlinx.coroutines.internal.LimitedDispatcher'));
        addClRef(new ClRef('reactor.util.Loggers'));
//        addClRef(new ClRef('sun.net.NetworkClient'));
//        addClRef(new ClRef('java.net.Socket'));
//        addClRef(new ClRef('sun.security.ssl.SSLSocketImpl'));
        addClRef(new ClRef('net.sf.jremoterun.utilities.nonjdk.classpath.refsdef.FieldRefPrint'));
        addClRef(new ClRef('org.netbeans.lib.profiler.ProfilerLogger'));
        addClRef(new ClRef('net.sf.jremoterun.utilities.nonjdk.idwutils.alerttable.AlertTableWrapper'));
        addClRef(new ClRef('com.jcraft.jsch.JrrSchSessionLog'));
        addClRef(new ClRef('org.eclipse.core.internal.runtime.PlatformLogWriter'));
        addClRef(new ClRef(net.sf.jremoterun.utilities.nonjdk.ioutils.CopyOneFileToManyDirs));
        addClRef(new ClRef('org.jboss.windup.decompiler.fernflower.FernflowerJDKLogger'));
        addClRef(new ClRef("org.apache.kafka.common.utils.LogContext"));
        addClRef(new ClRef("sun.net.www.protocol.http.HttpURLConnection"));
        addClRef(new ClRef("com.sun.jmx.remote.util.ClassLogger"));
        addClRef(new ClRef("sun.net.www.protocol.https.AbstractDelegateHttpsURLConnection"));
        addClRef(new ClRef("java.net.SocksSocketImpl"));
        addClRef(new ClRef("sun.reflect.NativeMethodAccessorImpl"));
        addClRef(new ClRef("org.sonatype.guice.bean.reflect.Logs"));
        addClRef(new ClRef("org.glassfish.jersey.logging.LoggingInterceptor"));
        addClRef(new ClRef("net.sf.jremoterun.utilities.nonjdk.git.GitProgressMonitorJrr"));
        addClRef(new ClRef('net.sf.jremoterun.utilities.nonjdk.docker.JibEventLogger'));
        addClRef(new ClRef('com.google.cloud.tools.jib.event.Handler'));
        addClRef(new ClRef('com.google.cloud.tools.jib.event.EventHandlers'));
        addClRef(new ClRef('com.google.cloud.tools.jib.builder.TimerEventDispatcher'));
        addClRef(new ClRef('com.google.cloud.tools.jib.builder.ProgressEventDispatcher'));
        addClRef(new ClRef('com.google.cloud.tools.jib.builder.steps.ThrottledProgressEventDispatcherWrapper'));
        addClRef(new ClRef('com.google.cloud.tools.jib.event.progress.ThrottledAccumulatingConsumer'));
        addClRef(new ClRef('com.google.common.collect.ImmutableList'));
        addClRef(new ClRef("org.apache.http.impl.conn.LoggingManagedHttpClientConnection"));
        addClRef(new ClRef("org.apache.http.impl.conn.PoolingHttpClientConnectionManager"));

        JrrClassUtils.ignoreClassesForCurrentClass.add("org.apache.maven.cli.logging");
        JrrClassUtils.ignoreClassesForCurrentClass.add("org.apache.maven.monitor.logging");
        JrrClassUtils.ignoreClassesForCurrentClass.add("org.eclipse.jdt.internal.junit");
        JrrClassUtils.ignoreClassesForCurrentClass.add("org.eclipse.osgi.internal.log.");
        JrrClassUtils.ignoreClassesForCurrentClass.add("org.eclipse.core.internal.runtime.Log.");
        JrrClassUtils.ignoreClassesForCurrentClass.add('io.netty.util.internal.logging.');
//        JrrClassUtils.ignoreClassesForCurrentClass.add(Log4j2Utils.class.getPackage().getName());
        JrrClassUtils.ignoreClassesForCurrentClass.add(net.sf.jremoterun.utilities.nonjdk.log.tojdk.JdkLogFormatter2.getPackage().getName());
        JrrClassUtils.ignoreClassesForCurrentClass.add(net.sf.jremoterun.utilities.nonjdk.log.log4j2.Log4j1Utils.getPackage().getName());
        addClRef(new ClRef('org.eclipse.jgit.lib.BatchingProgressMonitor'));
        addClRef(new ClRef('org.eclipse.jgit.lib.ThreadSafeProgressMonitor'));
        addClRef(new ClRef('net.sf.jremoterun.utilities.nonjdk.ivy.JrrIvyURLHandler'));
        addClRef(new ClRef('net.sf.jremoterun.utilities.nonjdk.net.apachehttpclient.JrrIvyApacheClientURLHandler'));
        addClRef(new ClRef('net.sf.jremoterun.utilities.nonjdk.log.log4j2.JdkIntoLog4j2Converter'));
        addClRef(new ClRef('net.sf.jremoterun.utilities.mdep.ivy.JrrIvyMessageLogger'));
        addClRef(new ClRef('org.apache.ivy.util.DefaultMessageLogger'));
        addClRef(new ClRef('org.apache.ivy.util.AbstractMessageLogger'));
        addClRef(new ClRef('org.apache.ivy.util.MessageLoggerEngine'));
        addClRef(new ClRef('org.apache.ivy.util.Message'));
        addOkHttpIgnoreClasses()
        //added for azure http headers logs
        addAzureIgnoreClasses()
        addRetrofit()
    }


    static void addClRef(Class clRef){
        JrrClassUtils.ignoreClassesForCurrentClass.add(clRef.getName());
    }

    static void addClRef(ClRef clRef){
        JrrClassUtils.ignoreClassesForCurrentClass.add(clRef.className);
    }

}
