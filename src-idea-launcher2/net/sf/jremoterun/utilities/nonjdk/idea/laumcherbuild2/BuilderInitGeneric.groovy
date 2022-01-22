package net.sf.jremoterun.utilities.nonjdk.idea.laumcherbuild2;

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.java11.Java11ModuleSetDisable
import net.sf.jremoterun.utilities.nonjdk.InfocationFrameworkStructure
import net.sf.jremoterun.utilities.nonjdk.idea.laumcherbuild.IdeaBuildRunnerSettings

import java.util.logging.Logger

@CompileStatic
class BuilderInitGeneric {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();
    public static String buildlog = 'log/buildlogjrr/buildout.txt'


    static void doStuff(File ideaLocalConfigDir) {
        Java11ModuleSetDisable.doIfNeeded()
        IdeaBRunner21.f1(InfocationFrameworkStructure.ifDir)
        assert  ideaLocalConfigDir!=null
        IdeaBuildRunnerSettings.outputFile = new File(ideaLocalConfigDir, buildlog);
        IdeaBuildRunnerSettings.outputFile.getParentFile().mkdir()
        assert IdeaBuildRunnerSettings.outputFile.getParentFile().exists()
        net.sf.jremoterun.utilities.nonjdk.shell.GroovySehllSshServiceSettings.setSshProps()
        net.sf.jremoterun.utilities.nonjdk.LogExitTimeHook.addShutDownHook()
        log.info "java home = ${net.sf.jremoterun.utilities.nonjdk.javalangutils.PropsEnum.java_home.getValue()}"
        IdeaBRunner33.f1()
    }
}
