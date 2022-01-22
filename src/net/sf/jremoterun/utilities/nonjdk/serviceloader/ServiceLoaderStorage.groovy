package net.sf.jremoterun.utilities.nonjdk.serviceloader

import groovy.transform.CompileStatic
import net.sf.jremoterun.SharedObjectsUtils;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.nonjdk.javassist.ClassRedefintions
import net.sf.jremoterun.utilities.nonjdk.problemchecker.JustStackTrace;

import java.util.logging.Logger;

@CompileStatic
class ServiceLoaderStorage {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public HashMap<Class, JustStackTrace> servicesTried = new HashMap<>()
    public ServiceLoaderFactory factory1 = new ServiceLoaderFactory(this);
    public HashSet<String> customize = new HashSet<>();
    public HashSet<String> ignoreImpl2 = new HashSet<>();
    public volatile boolean logRequestedServices = false;
    public volatile boolean debugMax = false;

    public static ServiceLoaderStorage instance = new ServiceLoaderStorage();
    public static volatile inited = false
    public static String objectKey = "JrrServiceLoaderFactory"


    ServiceLoaderStorage() {
        addIgnore(new ClRef('java.nio.file.spi.FileSystemProvider'),new ClRef('org.apache.sshd.client.subsystem.sftp.SftpFileSystemProvider'))
        addIgnore(new ClRef('org.eclipse.jgit.transport.SshSessionFactory'),new ClRef('org.eclipse.jgit.transport.sshd.SshdSessionFactory'))
    }

    void addIgnore(ClRef service,ClRef impl){
        addCustom(service)
        addSkipImpl(impl)
    }

    @Deprecated
    void addSkipImpl(ClRef clRef) {
        ignoreImpl2.add(clRef.className)
    }

    @Deprecated
    void addCustom(ClRef clRef) {
        customize.add(clRef.className)
    }

    /**
     *  for java9+ add : --add-reads=java.base=java.management
     */
    static void init() {
        if (inited) {
        } else {
            inited = true
            SharedObjectsUtils.getGlobalMap().put(objectKey, instance.factory1);
            ClassRedefintions.redefineServiceLoader(objectKey)
        }

    }

}
