package net.sf.jremoterun.utilities.nonjdk.apprunner

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.shellcommands.NativeCommand

import java.util.logging.Logger

@CompileStatic
class RunProgramOnStart3 extends NativeCommand{


    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public ProgramInfo programInfo;
    public File genericLogDir;
    public int rotationDepth;

    String outFileName = 'out.txt'
//    @Deprecated
//    public NativeProcessResult processResult;
//    @Deprecated
//    public Thread thread

    RunProgramOnStart3(ProgramInfo programInfo, File genericLogDir, int rotationDepth) {
        super(programInfo.getRunFile().getAbsolutePath(),[])
        this.programInfo = programInfo
        this.genericLogDir = genericLogDir
        this.rotationDepth = rotationDepth
        printStartDate = true
        printFullCmdToProcessOut = true
        waitSync = false
        process.continueRunningOnTimeoutCheck = false
        process.onFinishedPrintFinished = true
        process.additionalInfoPrint = programInfo.name()
//        processResult = process
        if(!programInfo.getRunFile().exists()){
            throw new FileNotFoundException(programInfo.getRunFile().getAbsolutePath())
        }
        runDir = programInfo.getRunFile().getParentFile()
    }

    @Override
    void buildCustomArgs() {
        if(!customAgrsAdded) {
            buildCustomArgs2()
        }
    }

    @Deprecated
    private  boolean customAgrsAdded = false

    @Deprecated
    void buildCustomArgs2() {
        super.buildCustomArgs()
        customAgrsAdded = true
        File child = genericLogDir.child(programInfo.name())
        child.mkdir()
        assert child.exists()
        File outFile = child.child(outFileName)
        process.addWriteOutToFile(outFile,rotationDepth)
    }

    @Override
    void runCmd() {
        log.info("running " + programInfo);
        if(doWindowsLinkCheck){
            addWindowsLinkPrefix()
        }
        super.runCmd()
        log.info("started : " + programInfo);
    }

    public boolean doWindowsLinkCheck = true

    boolean addWindowsLinkPrefix(){
        if(fullCmd.size()>0){
            if(fullCmd[0].endsWith('.lnk')){
                fullCmd.add(0,'/C')
                fullCmd.add(0,'cmd')
            }
        }
    }

    @Deprecated
    void defaultProcessRunner() {
        runCmd()
    }

}
