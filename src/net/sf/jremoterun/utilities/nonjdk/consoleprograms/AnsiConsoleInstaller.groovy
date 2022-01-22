package net.sf.jremoterun.utilities.nonjdk.consoleprograms

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.groovystarter.st.SetConsoleOut2
import org.fusesource.jansi.AnsiConsole;

import java.util.logging.Logger;

@CompileStatic
class AnsiConsoleInstaller implements Runnable{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    @Override
    void run() {
        JrrClassUtils.setFieldValue(AnsiConsole,'installed',1000)
        //TODO set AnsiColor

        SetConsoleOut2.proxyOut.setNestedOut AnsiConsole.wrapOutputStream(SetConsoleOut2.proxyOut.nestedOut)
        SetConsoleOut2.proxyErr.setNestedOut AnsiConsole.wrapOutputStream(SetConsoleOut2.proxyErr.nestedOut)
    }
}
