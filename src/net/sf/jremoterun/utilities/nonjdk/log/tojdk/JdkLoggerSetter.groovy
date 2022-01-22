package net.sf.jremoterun.utilities.nonjdk.log.tojdk

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.groovystarter.st.JdkLogFormatter
import net.sf.jremoterun.utilities.groovystarter.st.SetConsoleOut2
import net.sf.jremoterun.utilities.nonjdk.console.inout.ConsoleRedirect

import java.util.logging.Logger;

@CompileStatic
class JdkLoggerSetter {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static JdkLogFormatter2 formatter = new JdkLogFormatter2();
    public static boolean setSysOut = true
    //public static OutputStream outputStream =


    static JdkLogFormatter2 setLogFormatter() {
        SetConsoleOut2.setConsoleOutIfNotInited()
        return setLogFormatter2()
    }

    static JdkLogFormatter2 setLogFormatter2() {
        JdkLogFormatter.findConsoleHandler().setFormatter(formatter);
        //JrrClassUtils.invokeJavaMethod(JdkLogFormatter.findConsoleHandler(),"setOutputStream",System.out)
        if(setSysOut) {
            ConsoleRedirect.setOutputForConsleHandler(net.sf.jremoterun.utilities.groovystarter.st.SetConsoleOut2.proxyOut)
        }
        return formatter;
    }


}
