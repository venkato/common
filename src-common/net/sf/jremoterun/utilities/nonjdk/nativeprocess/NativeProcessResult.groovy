package net.sf.jremoterun.utilities.nonjdk.nativeprocess

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.BadExitCodeException
import net.sf.jremoterun.utilities.nonjdk.FileRotate
import net.sf.jremoterun.utilities.nonjdk.io.LastByteArrayOutputStream
import net.sf.jremoterun.utilities.nonjdk.io.JrrIoUtils
import net.sf.jremoterun.utilities.nonjdk.io.StreamCopier

import java.util.logging.Level;
import java.util.logging.Logger;

@CompileStatic
class NativeProcessResult {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    public Process process;
    public final LastByteArrayOutputStream outLast = new LastByteArrayOutputStream();
    public final LastByteArrayOutputStream errLast = new LastByteArrayOutputStream();
    public final FilterOutputStreamJrr out2 = new FilterOutputStreamJrr(outLast);
    public final FilterOutputStreamJrr err2 = new FilterOutputStreamJrr(errLast);
    public StreamCopier threadOut;
    public StreamCopier threadErr;
    public volatile Integer exitCode;
    public final Date startDate = new Date();
    public boolean exceptionOnError = true;
    public boolean printBadExitCode = true;
    private volatile boolean waitAsync = false;
    public volatile boolean continueRunningOnTimeoutCheck = true;
    public volatile long timeoutInSec = 10
    public volatile Runnable onTimeoutRunnable
    public volatile boolean onFinishedPrintFinished  = false;
    public volatile Runnable onFinishedR
    public volatile String additionalInfoPrint;

    static List<String> defaultEnv = createDefaultEnv2()


    public Thread threadTimeout;

    public Thread threadRunningImpl = new Thread() {
        @Override
        void run() {
            doWaitImpl()
        }
    };


// simple runner
    static NativeProcessResult runNativeProcessAndWait(String cmd) {
        runNativeProcessAndWait(cmd,null)
    }
    static NativeProcessResult runNativeProcessAndWait(String cmd, File runDir) {
        if (runDir != null) {
            assert runDir.exists()
        }
        Process process1 = cmd.execute(defaultEnv, runDir);
        NativeProcessResult processResult = new NativeProcessResult(process1) ;
        processResult.onTimeoutRunnable = {log.info "process running too long : ${processResult.buildAdditionalInfo()}${cmd}"}
        processResult.consumeOutStreamSysout()
        processResult.waitWithPeriodicCheck()
        return processResult
    }


    static List<String> createEnvFromMap(Map<String, String> getenv) {
        return getenv.entrySet().collect { "${it.key}=${it.value}".toString() };
    }

    static List<String> createDefaultEnv2() {
        return createEnvFromMap(createDefaultEnv())
    }

    static Map<String, String> createDefaultEnv() {
        Map<String, String> getenv = new Hashtable<>(System.getenv());
        getenv.remove('GROOVY_OPTS')
        getenv.remove('groovypath')
        getenv = getenv.findAll { it.key != null && it.key.length() > 0 && it.value != null && it.value.length() > 0 }
        return getenv;
    }


    NativeProcessResult() {

    }
    NativeProcessResult(Process process) {
        this.process = process
    }

    public volatile Date finishDate;

    void doWaitImpl() {
        try {
            exitCode = process.waitFor();
            continueRunningOnTimeoutCheck = false
            assert threadOut!=null
            threadOut.logException = false
            threadErr.logException = false
            finishDate = new Date()
            waitIOThreadFinished()
            if(onFinishedPrintFinished){
                out2.write "${JrrClassUtils.getCurrentClass().getName()} ${buildAdditionalInfo()}process finished with exit code ${exitCode} at ${new Date()} \n".getBytes()
                flushOutStreams()
            }
            if(onFinishedR!=null){
                flushOutStreams()
                try {
                    onFinishedR.run()
                }catch(Throwable e){
                    log.log(Level.SEVERE,"${buildAdditionalInfo()}failed run finisher",e)
                }
            }
            closeOutStreams()
            if (waitAsync) {
                onFinished();
            }
        } catch (Throwable e) {
            log.log(Level.SEVERE, "${buildAdditionalInfo()}failed wait", e)
        }finally{
            continueRunningOnTimeoutCheck = false
            if(threadOut!=null) {
                threadOut.logException = false
            }
            if(threadErr!=null) {
                threadErr.logException = false
            }
        }
    }

    void consumeOutStreamSysout() {
        out2.addStream(new NonClosableStream(System.out))
        err2.addStream(new NonClosableStream(System.err))
    }


    BufferedOutputStream addWriteOutToFile(File outputFile, int rotationDepth) {
        FileRotate.rotateFile(outputFile, rotationDepth)
        BufferedOutputStream outputStream2 = outputFile.newOutputStream()
        out2.addStream(outputStream2)
        err2.addStream(outputStream2)
        return outputStream2
    }

    void consumeOutStream() {
        if (threadOut != null) {
            throw new Exception("Output already set")
        }
        threadOut = StreamCopier.copyOutputStreamInNewThread(process.getInputStream(),out2);
        threadErr = StreamCopier.copyOutputStreamInNewThread(process.getErrorStream(),err2);
    }

    ///void onTimeout() {}

    void onFinishedFine() {}

    void flushOutStreams(){
        try {
            out2.flush()
        } catch (Exception e) {
            log.log(Level.SEVERE, "${buildAdditionalInfo()}failed flush out stream", e)
        }
        try {
            err2.flush()
        } catch (Exception e) {
            log.log(Level.SEVERE, "${buildAdditionalInfo()}failed flush err stream", e)
        }
    }

    void closeOutStreams() {
        flushOutStreams()
        JrrIoUtils.closeQuietly2(out2,log)
        JrrIoUtils.closeQuietly2(err2,log)
    }

    void onFinished() {
        if (exitCode == 0) {
            onFinishedFine()
        } else {
            onBadExitCode()
        }
    }

    void onBadExitCode() {
        if (exceptionOnError) {
            String errOut = errLast.toString();
            String outOut = outLast.toString();
            throw new BadExitCodeException("${buildAdditionalInfo()}Bad exit code = ${exitCode} : ${errOut},\n${outOut}");
        } else {
            if(printBadExitCode) {
                String errOut = errLast.toString()
                String outOut = outLast.toString()
                log.info "${buildAdditionalInfo()}bad exit code ${exitCode} : ${errOut},\n${outOut}"
            }
        }
    }


    void waitAsyncM() {
        waitAsync = true
        if (threadOut == null) {
            consumeOutStream();
        }
        threadTimeout = new Thread('timeout check') {
            @Override
            void run() {
                onTimeoutContinuesCheck()
            }
        }
        threadTimeout.setDaemon(true)
        threadTimeout.start()

        threadRunningImpl.setDaemon(true);
        threadRunningImpl.start();
    }


    void waitWithPeriodicCheck() {
        if (threadOut == null) {
            consumeOutStream();
        }

        threadRunningImpl.start();
        onTimeoutContinuesCheck()
        assert !threadRunningImpl.isAlive()
        onFinished()
    }

    long getWaitTimeNext(){
        return timeoutInSec * 1000
    }

    protected void onTimeoutContinuesCheck(){
        while (true) {
            threadRunningImpl.join(getWaitTimeNext());
            flushOutStreams()
            if (threadRunningImpl.isAlive()) {
                if(continueRunningOnTimeoutCheck &&  onTimeoutRunnable!=null){
                    onTimeoutRunnable.run()
                }
            } else {
                break
            }
        }
    }

    void waitIOThreadFinished() {
        threadOut.join()
        threadErr.join()
    }

    @Override
    String toString() {
        return "${buildAdditionalInfo()}alive=${threadRunningImpl.isAlive()} exitCode=${exitCode}, errStream=${errLast}, outStream=${outLast}";
    }

    String buildAdditionalInfo(){
        if(additionalInfoPrint==null){
            return ''
        }
        return ' '+additionalInfoPrint+' '
    }


    void forceDestroy(){
        forceDestroy2(true)
    }

    void forceDestroy2(boolean excepionIfStopped){
        if(exitCode!=null){
            if(excepionIfStopped) {
                throw new Exception("already stopped")
            }
            log.info "process already stopped ${buildAdditionalInfo()}"
        }else {
            continueRunningOnTimeoutCheck = false
            String msg = "${JrrClassUtils.getCurrentClass().getName()}.forceDestroy() : ${new Date()} force closing ..${buildAdditionalInfo()}\n"
            err2.write(msg.getBytes())
            threadErr.logException = false
            threadOut.logException = false
            process.destroy()
        }
    }

}
