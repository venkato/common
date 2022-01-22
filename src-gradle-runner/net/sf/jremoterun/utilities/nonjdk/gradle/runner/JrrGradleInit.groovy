package net.sf.jremoterun.utilities.nonjdk.gradle.runner;

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.javassist.codeinjector.InjectedCode
import net.sf.jremoterun.utilities.nonjdk.classpath.inittracker.InitLogTracker

import java.util.logging.Logger

@CompileStatic
class JrrGradleInit extends InjectedCode {
// How to use :

//    org.gradle.invocation.DefaultGradle gd = getScriptTarget();
//    ClassLoader loader2 = gd.getServices().find(org.gradle.initialization.ClassLoaderRegistry).getPluginsClassLoader();
//    Object loader123 = new Object(){
//
//        public GroovyClassLoader groovyClassLoader1;
//
//        void addFile(File f){
//            assert f.exists()
//            assert f.canRead()
//            groovyClassLoader1.addURL(f.toURL())
//        }
//
//        void addJarsFromDir(File f){
//            assert f.exists()
//            assert f.isDirectory()
//            List<File> list = f.listFiles().toList()
//            list.each {
//                if(it.getName().endsWith('.jar')){
//                    addFile(it)
//                }
//            }
//        }
//
//
//        void loadClassss(Object params,ClassLoader loader3){
//            groovyClassLoader1 = new GroovyClassLoader(loader3)
//            addFile(new File('gitrepo/https/github.com/venkato/starter3/git/onejar/jrrutilities.jar'))
//            addFile(new File('gitrepo/https/github.com/venkato/starter3/git/libs/copy/jremoterun.jar'))
//            addFile(new File('gitrepo/https/github.com/venkato/starter3/git/libs/copy/jrrassist.jar'))
//            addFile(new File('gitrepo/https/github.com/venkato/common/git/src-gradle-runner/'))
//            addFile(new File('gitrepo/https/github.com/venkato/common/git/src-common/'))
//
//            Map mapp =  groovyClassLoader1.loadClass('net.sf.jremoterun.utilities.nonjdk.gradle.runner.JrrGradleInit').newInstance();
//            mapp.get([params,'customRunnerOrNull'])
//        }
//
//    }
//
//    loader123.loadClassss(this,loader2);
//
//
//


    @Override
    Object getImpl(Object key) throws Exception {
        if(InitLogTracker.defaultTracker==null){
            throw new NullPointerException('InitLogTracker.defaultTracker is null')
        }
        InitLogTracker.defaultTracker.addLog('gradle called')
        //println "works1"
        Map mapp3 = new ClRef('net.sf.jremoterun.utilities.nonjdk.gradle.runner.JrrGradleInit2').newInstance(getClass().getClassLoader()) as Map
        mapp3.get(key)
        return null;
    }

    @Override
    protected Object handleException(Object key, Throwable throwable) {
        throwable.printStackTrace()
        InitLogTracker.defaultTracker.addException('gradle init failed', throwable)
        return null;
        //return super.handleException(key, throwable)
    }
}
