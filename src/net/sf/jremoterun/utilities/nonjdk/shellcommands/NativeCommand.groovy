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
    Collection<String> envVarRemove = [];
//    String cmd;
    List<String> fullCmd = []
//    List<String> fullCmd = []
    NativeProcessResult process = new NativeProcessResult();
    Date startTime
    boolean printCmd = false
    boolean printFullCmdToProcessOut = false
    boolean printDirWhereRunning = false
    boolean printStartDate = false
    File dumpCmdToFile;

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



    void setProgramPath(File programPath) {
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

    List<String> buildStartMsg() {
        List<String> startMsg = []
        if (printStartDate) {
            startMsg.add "${getClass().getName()} ${process.getCurrentDateFormatted()} starting .. ".toString()
        }
        if (printDirWhereRunning) {
            startMsg.add "running in dir : ${runDir}".toString()
        }
        if (printFullCmdToProcessOut) {
            startMsg.add "running : ${fullCmd.join(' ')}".toString()
        }
        return startMsg
    }

    String getLineSeparator1() {
        return System.lineSeparator();
    }

    void dumpCmdToFileM(File dumpCmdToFileL) {
        List<String> lines = []
        envHuman.each {
            // TODO make depends on OS
            lines.add "set ${it.key}=${it.value}".toString()
        }
        if (runDir != null) {
            lines.add "cd ${runDir.getAbsolutePath()}".toString()
        }
        lines.add fullCmd.join(' ')
        dumpCmdToFileL.text = lines.join(getLineSeparator1())
    }

    boolean isKeepEnvVar(String envNameAndValue) {
        String varName = envNameAndValue.tokenize('=')[0].trim()
        return !envVarRemove.contains(varName)
    }

    void runCmd() {
        buildCustomArgs()
        if (dumpCmdToFile != null) {
            dumpCmdToFileM(dumpCmdToFile)
        }
        if (printCmd) {
            log.info "running : ${fullCmd.join(' ')}"
        }
        List<String> startMsg1 = buildStartMsg()
        if (startMsg1 != null && startMsg1.size() > 0) {
            String line1 = startMsg1.join('\n') + '\n'
            process.out2.write line1.getBytes()
        }
        String[] cmdddd = fullCmd.toArray(new String[0])
        String[] env2
        if (!envHuman.isEmpty()) {
            env.addAll(envHuman.collect { "${it.key}=${it.value}".toString() })
        }
        if (!envVarRemove.isEmpty()) {
            env = env.findAll { isKeepEnvVar(it) }
        }
        if (env.isEmpty()) {
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
