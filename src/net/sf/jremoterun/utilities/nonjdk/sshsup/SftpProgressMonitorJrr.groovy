package net.sf.jremoterun.utilities.nonjdk.sshsup

import com.jcraft.jsch.SftpProgressMonitor
import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.cvsutils.LineCount2Stat
import net.sf.jremoterun.utilities.nonjdk.file.FileLengthCheck;

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
    //public int roundProgressSpeed = 2
    public int roundProgressPercent = 2
    public long lastCount = 0
    public File downloadFileToDiskSpaceCheck;
    public long min_remaining_space_in_mb = 1000;
    public BytesToHumanConverter bytesToHumanConverter = new BytesToHumanConverter()
    public FileLengthCheck fileLengthCheck = new FileLengthCheck()

    SftpProgressMonitorJrr(File downloadFileToDiskSpaceCheck) {
        this.downloadFileToDiskSpaceCheck = downloadFileToDiskSpaceCheck
    }

    SftpProgressMonitorJrr() {
    }

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
        checkDiskSpace()
        logFileSize()
    }

    void logFileSize(){
        String humanSize = bytesToHumanConverter.convertHuman(max)
        log.info "file size = ${humanSize}"
    }

    void checkDiskSpace(){
        Long spaceLeft = getFreeSpaceInBytes()
        if (spaceLeft != null) {
            long remaining = spaceLeft - max
            if (remaining < min_remaining_space_in_mb * BytesDivider.mb.divider) {

                String msg = fileLengthCheck.buildSuffix2(max,spaceLeft,fileLengthCheck.lowFreeSpace)
                throw new Exception(msg)
//                throw new Exception("Too low free space = ${bytesToHumanConverter.convertHuman(spaceLeft)}, fileSize=${bytesToHumanConverter.convertHuman(max)}")
            }
        }
    }

    Long getFreeSpaceInBytes() {
        if (downloadFileToDiskSpaceCheck == null) {
            return null
        }
        File parentFile = downloadFileToDiskSpaceCheck.getParentFile()
        assert parentFile.exists()
        return parentFile.getFreeSpace()
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


    public long reportSpeedNum = 1000
    public String reportSpeedHuman = '/s'

    void calcStatLast() {
        Date lastReport1 = lastReportedDate == null ? startTime : lastReportedDate;
        calcStat2(lastReport1, progressLastReported, '')
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
            downloadSpeed = bytesToHumanConverter.convertHuman(bytesPerSec) + reportSpeedHuman
        } else {
            if (timeDiff == 0) {
                downloadSpeed = "timeDiff is 0"
            } else {
                downloadSpeed = "timediff is negative : ${timeDiff}"
            }
        }
        printStat2(progressPercent, downloadSpeed, statId)
        lastReportedDate = dateNow1
    }


    @Override
    void end() {
        //lineCount2Stat = new LineCount2Stat()
        //progress = 0
        //callsCount=0
        calcStat2(startTime, 0, 'avg ')
    }

    void printStat2(BigDecimal progressPercent, String speed, String statId) {
        log.info "${statId}${progressPercent} speed=${speed}"
    }
}
