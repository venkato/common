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


    public static volatile inited = false
    public static String objectKey = "JrrServiceLoaderFactory"

    public static List<ClRef> ignoredAnnotationProcessors = [
            new ClRef('org.eclipse.sisu.space.SisuIndexAPT6'),
         new ClRef('org.apache.logging.log4j.core.config.plugins.processor.PluginProcessor'),
         new ClRef('org.netbeans.modules.openide.modules.PatchedPublicProcessor'),
         new ClRef('org.netbeans.modules.openide.util.NbBundleProcessor'),
         new ClRef('org.netbeans.modules.openide.util.ServiceProviderProcessor'),
         new ClRef('org.netbeans.modules.openide.util.NamedServiceProcessor'),
         new ClRef('org.netbeans.modules.editor.mimelookup.CreateRegistrationProcessor'),
         new ClRef('org.netbeans.modules.openide.filesystems.declmime.MIMEResolverProcessor'),
         new ClRef('org.netbeans.modules.openide.awt.ActionProcessor'),
         new ClRef('org.netbeans.api.annotations.common.proc.StaticResourceProcessor'),
         new ClRef('org.netbeans.modules.openide.windows.TopComponentProcessor'),
         new ClRef('org.netbeans.modules.openide.nodes.NodesAnnotationProcessor'),
         new ClRef('org.netbeans.modules.openide.loaders.DataObjectFactoryProcessor'),
         new ClRef('org.netbeans.modules.templates.TemplateProcessor'),
         new ClRef('javaslang.match.PatternsProcessor'),
    ]

    public static ServiceLoaderStorage instance = new ServiceLoaderStorage();

    ServiceLoaderStorage() {
        addIgnore(new ClRef('java.nio.file.spi.FileSystemProvider'),new ClRef('org.apache.sshd.client.subsystem.sftp.SftpFileSystemProvider'))
        addIgnore(new ClRef('java.nio.file.spi.FileSystemProvider'),new ClRef('org.apache.sshd.sftp.client.fs.SftpFileSystemProvider'))
        addIgnore(new ClRef('java.nio.file.spi.FileSystemProvider'),new ClRef('com.intellij.platform.ijent.community.impl.nio.IjentNioFileSystemProvider'))
        addIgnore(new ClRef('org.eclipse.jgit.transport.SshSessionFactory'),new ClRef('org.eclipse.jgit.transport.sshd.SshdSessionFactory'))
        // removing as Native2AsciiCharsetProvider compiled without support java8
        addIgnore(new ClRef('java.nio.charset.spi.CharsetProvider'),new ClRef('com.intellij.lang.properties.charset.Native2AsciiCharsetProvider'))
    }

    static void addAnnotationProcessors(ServiceLoaderStorage ref1){
        ignoredAnnotationProcessors.each {
            ref1.addIgnore(new ClRef('javax.annotation.processing.Processor'),it);
        }
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
    static boolean initIfCan() {
        if(net.sf.jremoterun.SimpleJvmTiAgent.instrumentation==null){
            log.info "${net.sf.jremoterun.SimpleJvmTiAgent.getName()}.instrumentation is null"
            return false
        }else{
            init()
            return true
        }
    }

    static void init() {
        if (inited) {
        } else {
            inited = true
            SharedObjectsUtils.getGlobalMap().put(objectKey, instance.factory1);
            ClassRedefintions.redefineServiceLoader(objectKey)
        }

    }

}
