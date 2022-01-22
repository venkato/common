package net.sf.jremoterun.utilities.nonjdk.javassist.retransform

import groovy.transform.CompileStatic
import net.sf.jremoterun.SimpleJvmTiAgent;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.nonjdk.asmow2.accessmodif.AccessModifControllerP

import java.lang.instrument.ClassFileTransformer
import java.lang.instrument.IllegalClassFormatException
import java.security.ProtectionDomain;
import java.util.logging.Logger;

@CompileStatic
class JrrReTransform implements ClassFileTransformer , Runnable{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static volatile JrrReTransform reTransformLatest ;
    public HashSet reTransformClasses = new HashSet()
    public HashSet reTransformedClasses = new HashSet()

    public AccessModifControllerP accessModifControllerP = new AccessModifControllerP(){
        @Override
        boolean needMakeMethodPublic(String methodName, String className) {
            return false
        }
    };

    public static List<ClRef> clRefsForReTransform = [
            new ClRef('org.rauschig.jarchivelib.CommonsArchiver'),
            new ClRef('com.jcraft.jsch.KnownHosts'),
            new ClRef('com.jcraft.jsch.ChannelExec'),
            new ClRef('com.jcraft.jsch.ChannelForwardedTCPIP'),
            new ClRef('com.jcraft.jsch.ChannelSession'),
            new ClRef('com.jcraft.jsch.ChannelSftp'),
            new ClRef('com.jcraft.jsch.ChannelShell'),
            new ClRef('com.jcraft.jsch.ChannelX11'),
            new ClRef('com.jcraft.jsch.Session'),
            new ClRef('com.jcraft.jsch.IO'),
            new ClRef('com.jcraft.jsch.UserAuthGSSAPIWithMIC'),
            new ClRef('com.jcraft.jsch.UserAuthKeyboardInteractive'),
            new ClRef('com.jcraft.jsch.UserAuthNone'),
            new ClRef('com.jcraft.jsch.UserAuthPassword'),
            new ClRef('com.jcraft.jsch.UserAuthPublicKey'),
//            new ClRef('com.jcraft.jsch.Request'),
            new ClRef('org.apache.maven.repository.internal.MavenSnapshotMetadata'),
            new ClRef('org.jetbrains.kotlin.cli.jvm.K2JVMCompiler'),
//        new ClRef('org.eclipse.aether.impl.MetadataGeneratorFactory'),
            new ClRef('com.google.api.client.http.HttpTransport'),
            new ClRef('com.google.cloud.tools.jib.api.JibContainerBuilder'),
            new ClRef('com.google.cloud.tools.jib.builder.steps.PullBaseImageStep'),
            new ClRef('com.google.cloud.tools.jib.builder.steps.StepsRunner'),
            new ClRef('com.google.cloud.tools.jib.cache.CacheStorageFiles'),
            new ClRef('com.google.cloud.tools.jib.http.FailoverHttpClient'),
            new ClRef('com.google.cloud.tools.jib.registry.AuthenticationMethodRetriever'),
            new ClRef('com.google.cloud.tools.jib.registry.AbstractManifestPuller'),

    ]

    void addDefault(){
//        accessModifControllerP.isNeedVerify2 = true
//        addClass(new ClRef(''))
        clRefsForReTransform.each {
            addClass(it)
        }

    }

    void addClass(ClRef clRef){
        reTransformClasses.add(clRef.getClassPath())
    }


    @Override
    void run() {
        addSelfIfCan()
    }


    static void addSelfIfCanS(){
        if(reTransformLatest ==null ) {
            if(SimpleJvmTiAgent.instrumentation==null){
                log.info "instrumentation is null"
            }else {
                new JrrReTransform().addSelfIfCan()
            }
        }
    }

    void addSelfIfCan(){
        if(SimpleJvmTiAgent.instrumentation!=null){
            if(reTransformLatest ==null) {
                addSelf()
                reTransformLatest  = this
            }
        }else{
            log.info "instrumentation is null"
        }
    }

    void addSelf(){
        addDefault()
        addSelfToTransform()
    }

    void addSelfToTransform(){
        log.info "adding class transforms .."
        SimpleJvmTiAgent.instrumentation.addTransformer(this,true)
        log.info "added class transforms"
    }


    @Override
    byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        if(reTransformClasses.contains(className)){
            log.info "retransforming ${className} .."
            byte[] bs=  accessModifControllerP.removeFinalModifier3(classfileBuffer)
            reTransformedClasses.add(className)
            log.info "retransformed ${className}"
            return bs;
        }
        return classfileBuffer
    }
}
