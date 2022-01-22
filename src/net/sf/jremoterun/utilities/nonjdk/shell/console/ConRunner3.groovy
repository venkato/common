package net.sf.jremoterun.utilities.nonjdk.shell.console

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.groovystarter.GroovyMethodRunnerParams
import net.sf.jremoterun.utilities.groovystarter.st.GroovyMethodRunnerParams2

import net.sf.jremoterun.utilities.nonjdk.consoleprograms.SetConsoleColoring
import net.sf.jremoterun.utilities.nonjdk.shell.GroovyShellConsole3

import java.nio.charset.Charset
import java.util.logging.Logger

@CompileStatic
class ConRunner3 implements Runnable {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    @Override
    void run() {
        SetConsoleColoring.setConsoleColoringNoRedirect()
        GroovyShellConsole3 runner2 = new GroovyShellConsole3()
        File currentDir = new File(".").absoluteFile.canonicalFile
        runner2.binding.setVariable('currentDir', currentDir)
        runner2.binding.setVariable('gmrp', GroovyMethodRunnerParams.gmrp)
        runner2.binding.setVariable('gmrp2', GroovyMethodRunnerParams2.gmrp2)
        runner2.runConsole()
        Charset.defaultCharset()
    }
}
