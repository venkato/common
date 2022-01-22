package net.sf.jremoterun.utilities.nonjdk.classpath.classloader.exceptioncollector

import groovy.transform.CompileStatic
import net.sf.jremoterun.JrrUtils;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.NewValueListener
import net.sf.jremoterun.utilities.classpath.AddFilesToClassLoaderCommon
import net.sf.jremoterun.utilities.nonjdk.classpath.AddDirectoryWithFiles
import net.sf.jremoterun.utilities.nonjdk.classpath.AddDirectoryWithFiles2
import net.sf.jremoterun.utilities.nonjdk.classpath.CustomObjectHandlerImpl
import net.sf.jremoterun.utilities.nonjdk.classpath.classloader.AddedLocationDetector
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.AddFileToClassloaderDummy
import net.sf.jremoterun.utilities.nonjdk.problemchecker.JustStackTrace;

import java.util.logging.Logger;

@CompileStatic
class ExceptionLocationCollector implements NewValueListener<Throwable> {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    /**
     * @see net.sf.jremoterun.utilities.nonjdk.classpath.classloader.AllClasspathAnalysis#stackTraceIgnoreClassName
     */
    public HashSet<String> stackTraceIgnoreClassName = new HashSet<>()

    public List<Throwable> list1 = []

    ExceptionLocationCollector() {
        addClassToStackTraceIgnoreClassName(AddedLocationDetector)
        addClassToStackTraceIgnoreClassName(AddFilesToClassLoaderCommon)
        addClassToStackTraceIgnoreClassName(CustomObjectHandlerImpl)
        addClassToStackTraceIgnoreClassName(AddDirectoryWithFiles)
        addClassToStackTraceIgnoreClassName(AddDirectoryWithFiles2)
        addClassToStackTraceIgnoreClassName(net.sf.jremoterun.utilities.JrrUtilitiesFile)
        stackTraceIgnoreClassName.add('org.codehaus.groovy.runtime.')
        stackTraceIgnoreClassName.add('org.apache.ivy.')
        stackTraceIgnoreClassName.add('net.sf.jremoterun.utilities.mdep.ivy.')
        stackTraceIgnoreClassName.add('net.sf.jremoterun.utilities.nonjdk.ivy.')
    }

    void addClassToStackTraceIgnoreClassName(Class clazz) {
        stackTraceIgnoreClassName.add(clazz.getName())
    }


    boolean isGoodStackElement(StackTraceElement el) {
        String find1 = stackTraceIgnoreClassName.find {
            return el.getClassName().startsWith(it)
        }
        return find1 == null
    }

    StackTraceElement resolveLocationByStackTrace(Throwable justStackTrace) {
        StackTraceElement[] trace = justStackTrace.getStackTrace()
        return trace.toList().find { isGoodStackElement(it) }
    }


    String convertExceptionToString(Throwable e){
        e= JrrUtils.getRootException(e)
        return e.toString()
    }

    String convertListToHuman2() {
        return convertListToHuman().collect {"${it.key} - ${it.value}"}.join('\n')
    }

    Map<StackTraceElement, String> convertListToHuman() {
        Map<StackTraceElement, String> result = [:]
        list1.each {
            StackTraceElement value2 = resolveLocationByStackTrace(it)
            result.put(value2, convertExceptionToString(it));
        }
        return result
    }


    @Override
    void newValue(Throwable throwable) {
        list1.add(throwable)
    }


    static String checkClassPathFile(File f){
        AddFileToClassloaderDummy addFileToClassloaderDummy = new AddFileToClassloaderDummy();
        ExceptionLocationCollector exceptionLocationCollector = new ExceptionLocationCollector()
        addFileToClassloaderDummy.onExceptionAction = exceptionLocationCollector
        addFileToClassloaderDummy.addFromGroovyFile(f)
        return exceptionLocationCollector.convertListToHuman2()

    }

}
