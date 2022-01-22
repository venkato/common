package net.sf.jremoterun.utilities.nonjdk.shellcommands

import groovy.json.JsonSlurper
import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.io.JrrIoUtils

import java.text.DecimalFormat;
import java.util.logging.Logger;

/**
 * https://www.speedtest.net/apps/cli
 */
@CompileStatic
class InternetSpeedTest extends NativeCommand {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public List<String> defaultOpts = '--format=json --progress=no --accept-gdpr'.tokenize(' ')

    long dividerMbit = 1000 * 1000
    public ByteArrayOutputStream out2 = new ByteArrayOutputStream()

    InternetSpeedTest(File cmdPath) {
        super([cmdPath.getAbsolutePath()])
    }

    @Override
    void buildCustomArgs() {
        super.buildCustomArgs()
        fullCmd.addAll(defaultOpts)
        process.err2.addNonClosableStream(System.err)
        process.out2.addStream(out2)
        process.timeoutInSec = 40
    }

    SpeedTestResult handleResult() {
        log.info "speed checking .."
        String string = out2.toString()
        log.info "result = ${string}"
        if (string == null) {
            throw new Exception("null string")
        }
        if (string.isEmpty()) {
            throw new Exception("null is empty")
        }
        try {
            handleResult2(string)
        } catch (Throwable e) {
            log.info("failed parse ${string} ${e}")
            throw e
        }
    }


    SpeedTestResult handleResult2(String string) {
        Map result = new JsonSlurper().parseText(string) as Map;
        assert result.type == 'result'
        Map ping = result.ping as Map
        Map download = result.download as Map
        Map upload = result.upload as Map
        Map server = result.server as Map
        Map interface1 = result.interface as Map
        Map result2 = result.result as Map
        String host = server.host
        String timestamp = result.timestamp
        String externalIp = interface1.externalIp
        BigDecimal jitter = ping.jitter as BigDecimal;
        BigDecimal latency = ping.latency as BigDecimal;
        long upload1 = upload.bandwidth as long;
        long download1 = download.bandwidth as long;
        String id = result2.id


        download1 = download1 * 8 / dividerMbit as long
        upload1 = upload1 * 8 / dividerMbit as long
        SpeedTestResult speedTestResult = new SpeedTestResult()
        speedTestResult.host = host
        speedTestResult.id = id
        speedTestResult.externalIp = externalIp
        speedTestResult.timestamp = timestamp
        speedTestResult.downloadMbitPerSec = download1
        speedTestResult.uploadMbitPerSec = upload1
        speedTestResult.latencyMs = latency
        speedTestResult.jitter = jitter
        return speedTestResult
    }

    public static DecimalFormat nf = new DecimalFormat('0.00');

    static void writeResultToFile(SpeedTestResult speedTestResult, File statFile) {
        String sep = '\t'
        StringBuilder sb = new StringBuilder()
        sb.append(speedTestResult.timestamp).append(sep)
        sb.append(speedTestResult.downloadMbitPerSec).append(sep)
        sb.append(speedTestResult.uploadMbitPerSec).append(sep)
        sb.append(nf.format(speedTestResult.latencyMs)).append(sep)
        sb.append(nf.format(speedTestResult.jitter)).append(sep)

        sb.append(speedTestResult.externalIp).append(sep)
        sb.append(speedTestResult.host).append(sep)
        sb.append(speedTestResult.id).append(sep)

        sb.append('\n')
        FileOutputStream fileOutputStream = new FileOutputStream(statFile, true);
        try {
            fileOutputStream.write(sb.toString().getBytes())
        } finally {
            JrrIoUtils.closeQuietly2(fileOutputStream, log)
        }
    }

}
