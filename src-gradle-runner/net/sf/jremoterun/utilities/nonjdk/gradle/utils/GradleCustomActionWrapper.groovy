package net.sf.jremoterun.utilities.nonjdk.gradle.utils

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JavaStackTraceFilter;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.groovystarter.PrintExceptionListener;

import java.util.logging.Logger;

@CompileStatic
abstract class GradleCustomActionWrapper implements Runnable{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();
    public JavaStackTraceFilter exceptionListener = new JavaStackTraceFilter();
    public boolean showGradleStackTraces = false

    @Override
    final void run() {
        try {
            addExcludeClasses()
            runImpl()
        }catch(Throwable e){
            onException(e)
        }
    }

    void addExcludeClasses(){
        if(showGradleStackTraces){

        }else {
            exceptionListener.ignoreClassesForCurrentClassThis.add('org.gradle.')
        }
    }

    void onException(Throwable e){
        exceptionListener.newValue(e)
        //e.printStackTrace()
    }

    abstract void runImpl();
}
