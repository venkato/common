package net.sf.jremoterun.utilities.nonjdk.sshsup

import com.jcraft.jsch.SftpProgressMonitor
import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.cvsutils.LineCount2Stat;

import java.util.logging.Logger;

@CompileStatic
class SftpProgressMonitorJrr implements SftpProgressMonitor {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();
    public int op;
    public String src;
    public String dest;
    public long max
    public int callsCount
    public long progress
    public long logProgressEveryBytes = 100_000_000
    public Date startTime
    public Date lastReportedDate
    public long progressLastReported = 0
    public LineCount2Stat lineCount2Stat = new LineCount2Stat()
    public static long cent = 100

    public int logProgressEveryFirstCalls = 2
    public int roundProgressSpeed = 2
    public int roundProgressPercent = 2
    public long lastCount = 0

    @Override
    void init(int op, String src, String dest, long max) {
        lastReportedDate = null
        this.op = op;
        this.src = src;
        this.dest = dest;
        this.max = max;
        startTime = new Date()
        progressLastReported = 0
        lineCount2Stat = new LineCount2Stat()
        lineCount2Stat.logEvery = logProgressEveryBytes
        progress = 0
        lastCount = 0
        callsCount = 0
    }

    @Override
    boolean count(long count) {
        lastCount = count
        progress += count
        callsCount++
        if (isNeedLogged()) {
            calcStatLast()
        }
        return true
    }

    boolean isNeedLogged() {
        boolean needPrint = false
        if (lineCount2Stat.isNeedPrintProgress(progress)) {
            needPrint = true
        }
        if (callsCount <= logProgressEveryFirstCalls) {
            needPrint = true
        }
        return needPrint
    }

    String convertHuman(BigDecimal bytesPerSec) {
        if (bytesPerSec < 1000) {
            return "${bytesPerSec.round(roundProgressSpeed)} bytes"
        }
        if (bytesPerSec < 1_000_000) {
            return "${(bytesPerSec / 1_000).round(roundProgressSpeed)} kb"
        }
        if (bytesPerSec < 1_000_000_000) {
            return "${(bytesPerSec / 1_000_000).round(roundProgressSpeed)} mb"
        }
        if (bytesPerSec < 1_0000_000_000_000) {
            return "${(bytesPerSec / 1_000_000_000).round(roundProgressSpeed)} gb"
        }
        return "${bytesPerSec} bytes"
    }

    public long reportSpeedNum = 1000
    public String reportSpeedHuman = '/s'

    void calcStatLast() {
        Date lastReport1 = lastReportedDate == null ? startTime : lastReportedDate;
        calcStat2(lastReport1, progressLastReported,'')
        progressLastReported = progress
    }

    void calcStat2(Date lastReport1, long countBefore, String statId) {
        Date dateNow1 = new Date()
        long diffDownloaded = progress - countBefore
        long timeDiff = dateNow1.getTime() - lastReport1.getTime()
        BigDecimal progressPercent = (progress * cent / max).round(roundProgressPercent)
        String downloadSpeed
        if (timeDiff > 0) {
            BigDecimal bytesPerSec = diffDownloaded * reportSpeedNum / timeDiff
            downloadSpeed = convertHuman(bytesPerSec) + reportSpeedHuman
        } else {
            if (timeDiff == 0) {
                downloadSpeed = "timeDiff is 0"
            } else {
                downloadSpeed = "timediff is negative : ${timeDiff}"
            }
        }
        printStat2(progressPercent, downloadSpeed,statId)
        lastReportedDate = dateNow1
    }


    @Override
    void end() {
        //lineCount2Stat = new LineCount2Stat()
        //progress = 0
        //callsCount=0
        calcStat2(startTime,0,'avg ')
    }

    void printStat2(BigDecimal progressPercent, String speed, String statId) {
        log.info "${statId}${progressPercent} speed=${speed}"
    }
}
