package net.sf.jremoterun.utilities.nonjdk.classpath.ifframecalc.framew

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.ContextClassLoaderWrapper;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.JrrUtilitiesShowE
import net.sf.jremoterun.utilities.classpath.AddFilesToUrlClassLoaderGroovy
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.javaservice.CallProxy
import net.sf.jremoterun.utilities.nonjdk.IfFrameworkSrcDirs
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.starter.JrrStarterJarRefs
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.starter.JrrStarterJarRefs2
import net.sf.jremoterun.utilities.nonjdk.compiler3.AddGroovyToParentCl
import net.sf.jremoterun.utilities.nonjdk.compiler3.CopyFilesTmp
import net.sf.jremoterun.utilities.nonjdk.compiler3.CreateGroovyClassLoader

import java.util.logging.Logger;

@CompileStatic
class MavenIdsCollector {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();
//    public static File ifDirDefault
    public AddFilesToUrlClassLoaderGroovy adderParent
    public URLClassLoader loader
    public AddFilesToUrlClassLoaderGroovy adder
    public boolean autoFallback = true
    public List<ClRef> checkClassExists = []
    public CopyFilesTmp copyFilesTmp =new CopyFilesTmp();
//    public CopyFilesTmp copyFilesParentTmp =new CopyFilesTmp();
    public boolean closeAfterDone = true
    public boolean cleanupedAEarlier = false
//    File ifDir
    File extMethodsCompiledClasses;

    MavenIdsCollector() {
        copyFilesTmp.filePrefix = 'collector-'
//        copyFilesParentTmp.filePrefix = 'collectorparent-'
        createClassLoaders()
        checkClassExists.add(new ClRef(JrrClassUtils))
    }

    List<MavenId> loadMavenIds2(ClassPathMavenIds classPathMavenIds) {
        try {
            if(cleanupedAEarlier){
                throw new Exception("Already cleanuped ${classPathMavenIds.getClass().getName()}")
            }
            return loadMavenIds2Impl(classPathMavenIds)
        } catch (Throwable e) {
            log.error3("failed load ${classPathMavenIds}", e);
            onException(e, classPathMavenIds)
            List<String> mavenIds = classPathMavenIds.loadMavenIds()
            return mavenIds.collect { new MavenId(it) }
        }finally {
            cleanup()
        }
    }

    void cleanup(){
        if(closeAfterDone) {
            copyFilesTmp.close()
            cleanupedAEarlier = true
        }

    }

    void onException(Throwable e, ClassPathMavenIds classPathMavenIds) {
        JrrUtilitiesShowE.showException("failed load ${classPathMavenIds}", e)
    }

    List<MavenId> loadMavenIds2Impl(ClassPathMavenIds classPathMavenIds) {
        addClassPath()
        Class clazz123 = classPathMavenIds.getClass()
        List<String> mavenIds;
        ClassLoader parentLoader = loader.getParent()
        ContextClassLoaderWrapper.wrap2(parentLoader, {
            checkClassExists.each {
                try {
                    parentLoader.loadClass(it.className)
                    throw new Exception("Class ${it.className} loaded by ${parentLoader}")
                } catch (ClassNotFoundException e) {
                    log.fine "ok not load : ${it.className}"
                }
            }
        });
        ContextClassLoaderWrapper.wrap2(loader, {
            checkClassExists.each {
                try {
                    loader.loadClass(it.className)
                } catch (Throwable e) {
                    log.info "failed load ${it.className}"
                    throw e
                }
            }
            Class compileRequestRemoteClass = loader.loadClass(clazz123.getName());
//            assert compileRequestRemoteClass.classLoader == loader
            Object service = compileRequestRemoteClass.newInstance()
            ClassPathMavenIds service2 = (ClassPathMavenIds) CallProxy.makeProxy2(clazz123, service);
            mavenIds = service2.loadMavenIds()

        });
        List<String> oldMavenIds = new ArrayList<String>(classPathMavenIds.loadMavenIds())
        List<String> newMavenIds = new ArrayList<String>(mavenIds)
        newMavenIds.removeAll(oldMavenIds)
        if (newMavenIds.size() > 0) {
            log.info "${clazz123.getName()} new mavenIds = ${newMavenIds}"
        }

        oldMavenIds.removeAll(mavenIds)
        if (oldMavenIds.size() > 0) {
            log.info "${clazz123.getName()} old mavend ids = ${oldMavenIds}"
        }
        return mavenIds.collect { new MavenId(it) }

    }


    void addJarsToParentClassloader() {
        AddGroovyToParentCl.defaultAddtoParentCl.addGroovyJarToParentClassLoader(adderParent,copyFilesTmp)
    }


    void createClassLoaders() {
        ClassLoader extClassLoader = CreateGroovyClassLoader.findExtClassLoader()
        URLClassLoader urlClassLoader = new URLClassLoader(new URL[0], extClassLoader)
        adderParent = new AddFilesToUrlClassLoaderGroovy(urlClassLoader)
        addJarsToParentClassloader()
        loader = CreateGroovyClassLoader.createGroovyClassLoader2(urlClassLoader)
        adder = new AddFilesToUrlClassLoaderGroovy(loader)
        URLClassLoader classLoaderParent = (URLClassLoader) loader.getParent()
        assert classLoaderParent.getClass() == URLClassLoader
    }


    void addClassPath() {
        adder.add IfFrameworkSrcDirs.src_common
        // why net.sf.jremoterun.utilities.JrrClassUtils sometime failed to load from below jar ?
        adder.add copyFilesTmp.addClassPathCopy(JrrStarterJarRefs.jrrutilitiesOneJar)
        adder.add copyFilesTmp.addClassPathCopy(JrrStarterJarRefs2.jremoterun)
        adder.add copyFilesTmp.addClassPathCopy(JrrStarterJarRefs2.jrrassist)
        adder.add copyFilesTmp.addClassPathCopy(JrrStarterJarRefs2.jrrbasetypes)
    }


}
