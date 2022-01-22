package net.sf.jremoterun.utilities.nonjdk.shellcommands

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ToFileRef2
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.AddFileToClassloaderDummy
import net.sf.jremoterun.utilities.nonjdk.nativeprocess.NativeProcessResult

import java.util.logging.Logger

@CompileStatic
public class NativeCommandBuilder {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();
    public boolean waitSync=true

    public File runDir;
    public Map<String, String> envHuman = new TreeMap<>();
    // name and value in format : name=value
    public List<String> env = new ArrayList(NativeProcessResult.defaultEnv);
//    String cmd;
    public List<String> fullCmd = []
    public boolean printCmd = false
    public boolean printFullCmdToProcessOut = false
    public boolean printStartDate = false
    public boolean consumeOutStreamSysoutF = false
    public OutputStream outAdd;
    public OutputStream errAdd;


    NativeCommandBuilder(List<String> args) {
        this.fullCmd.addAll(args)
    }

    NativeCommandBuilder(String cmd, List<String> args) {
        this.fullCmd.add(cmd)
        this.fullCmd.addAll(args)
    }

    void addArg(ToFileRef2 f){
        addArg(f.resolveToFile())
    }

    void addArg(File f){
        fullCmd.add(f.getAbsolutePath())
    }


    NativeCommand buildCustomArgs3(List<String> lastArgs){
        NativeCommand nc = buildCustomArgs()
        nc.fullCmd.addAll(lastArgs)
        nc.runCmd()
        return nc
    }

    NativeCommand buildCustomArgs2(String lastArg){
        NativeCommand nc = buildCustomArgs()
        nc.fullCmd.add(lastArg)
        nc.runCmd()
        return nc
    }

    NativeCommand buildCustomArgs(){
        NativeCommand nc = new NativeCommand(fullCmd)
        nc.waitSync = waitSync
        nc.runDir = runDir;
        nc.envHuman = envHuman;
        nc.printCmd = printCmd;
        nc.printFullCmdToProcessOut = printFullCmdToProcessOut;
        nc.printStartDate = printStartDate;
        if(outAdd!=null){
            nc.process.out2.addNonClosableStream(outAdd)
        }
        if(errAdd!=null){
            nc.process.err2.addNonClosableStream(errAdd)
        }
        if(consumeOutStreamSysoutF){
            nc.process.consumeOutStreamSysout()
        }
        return nc
    }

}
