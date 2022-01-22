package net.sf.jremoterun.utilities.nonjdk.consoleprograms

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.NewValueListener
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.groovystarter.runners.RunnableFactory
import net.sf.jremoterun.utilities.groovystarter.st.SetConsoleOut2
import net.sf.jremoterun.utilities.nonjdk.ConsoleRedirect
import net.sf.jremoterun.utilities.groovystarter.GroovyMethodRunnerParams
import net.sf.jremoterun.utilities.groovystarter.st.JdkLogFormatter
import net.sf.jremoterun.utilities.groovystarter.PrintExceptionListener

import java.util.logging.Logger

@CompileStatic
class SetConsoleColoring {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static volatile boolean ansibleInstalled = false

    public static ClRef ansiConsoleInstaller =new ClRef('net.sf.jremoterun.utilities.nonjdk.consoleprograms.AnsiConsoleInstaller')
    public static ClRef jdkLogColorFormatter =new ClRef('net.sf.jremoterun.utilities.nonjdk.consoleprograms.JdkLogColorFormatter')
    public static ClRef printExceptionColorListener =new ClRef('net.sf.jremoterun.utilities.nonjdk.consoleprograms.PrintExceptionColorListener')


    static void setConsoleColoringWithOutRedirect(File outFile,int maxDepth) {
        SetConsoleColoring.installAnsible()
        NewValueListener<Throwable> occured = GroovyMethodRunnerParams.gmrpn.onExceptionOccured;
        if (occured.class == PrintExceptionListener) {
            GroovyMethodRunnerParams.gmrpn.onExceptionOccured = printExceptionColorListener.newInstance3() as NewValueListener<Throwable>
        } else {
            log.info "Strange class name : ${occured.class.name}"
        }
        JdkLogFormatter.formatter = jdkLogColorFormatter.newInstance3() as JdkLogFormatter

        ConsoleRedirect.setOutputWithRotationAndFormatter(outFile,maxDepth)
    }

    static void setConsoleColoringNoRedirect() {
        SetConsoleColoring.installAnsible()
        NewValueListener<Throwable>  occured = GroovyMethodRunnerParams.gmrpn.onExceptionOccured;
        if (occured.getClass().getName() == printExceptionColorListener.className) {
        }else if (occured.class == PrintExceptionListener) {
            GroovyMethodRunnerParams.gmrpn.onExceptionOccured = printExceptionColorListener.newInstance3() as NewValueListener<Throwable>
        } else {
            log.info "Strange class name : ${occured.class.name}"
        }
        JdkLogFormatter.formatter = jdkLogColorFormatter.newInstance3() as JdkLogFormatter
        ConsoleRedirect.setOutputForConsleHandler(System.out)
        JdkLogFormatter.setLogFormatter()
    }


    static void installAnsible(){
        if(ansibleInstalled){
            log.info "already installed"
        }else{
            ansibleInstalled = true
            SetConsoleOut2.setConsoleOutIfNotInited()
            RunnableFactory.runRunner(ansiConsoleInstaller)
        }
    }

}
