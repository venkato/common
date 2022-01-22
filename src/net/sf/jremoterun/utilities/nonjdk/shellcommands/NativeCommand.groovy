package net.sf.jremoterun.utilities.nonjdk.shellcommands

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.AddFileToClassloaderDummy
import net.sf.jremoterun.utilities.nonjdk.nativeprocess.NativeProcessResult

import java.util.logging.Logger

@CompileStatic
public class NativeCommand {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();
    boolean waitSync = true

    File runDir;
    Map<String, String> envHuman = new TreeMap<>();
    // name and value in format : name=value
    List<String> env = new ArrayList(NativeProcessResult.defaultEnv);
//    String cmd;
    List<String> fullCmd = []
//    List<String> fullCmd = []
    NativeProcessResult process = new NativeProcessResult();
    Date startTime
    boolean printCmd = false
    boolean printFullCmdToProcessOut = false
    boolean printStartDate = false

    NativeCommand(List<String> args) {
        this.fullCmd.addAll(args)
    }

    NativeCommand(File cmd, List<String> args) {
        this(cmd.getAbsoluteFile().getCanonicalFile().getAbsolutePath(), args)
        assert cmd.exists();
        assert cmd.isFile();
    }

    NativeCommand(String cmd, List<String> args) {
        this.fullCmd.add(cmd)
        this.fullCmd.addAll(args)
    }

    void buildCustomArgs() {

    }


    void setProgramPath(File programPath){
        assert programPath.exists()
        assert programPath.isFile()
        fullCmd[0] = programPath.getCanonicalFile().getAbsolutePath()
    }

    String buildClassPath(AddFileToClassloaderDummy cpp, String separator) {
        return cpp.addedFilesWithOrder.collect { buildClassPathElement(it) }.join(separator)
    }

    String buildClassPathElement(File f) {
        return f.getAbsolutePath()
    }


    void runCmd() {
        buildCustomArgs()
        if (printStartDate) {
            process.out2.write "${JrrClassUtils.getCurrentClass().getName()} ${new Date()} starting .. \n".getBytes()
        }
        if (printCmd) {
            log.info "running : ${fullCmd.join(' ')}"
        }
        if (printFullCmdToProcessOut) {
            process.out2.write "running : ${fullCmd.join(' ')} \n".getBytes()
        }
        String[] cmdddd = fullCmd.toArray(new String[0])
        String[] env2
        if (envHuman.size() == 0) {
            env.addAll(envHuman.collect { "${it.key}=${it.value}".toString() })
        }
        if (env.size() == 0) {
            env2 = null
        } else {
            env2 = env.toArray(new String[0])
        }
        startTime = new Date()
        runCommandImpl(cmdddd, env2)
        process.flushOutStreams()
        waitSwitch()
    }

    void waitSwitch() {
        if (waitSync) {
            process.waitWithPeriodicCheck()
        } else {
            process.waitAsyncM()
        }

    }

    void runCommandImpl(String[] cmdArray, String[] env2) {
        process.process = Runtime.getRuntime().exec(cmdArray, env2, runDir)
    }

}
