package net.sf.jremoterun.utilities.nonjdk.windowsos

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.nativeprocess.NonClosableStream
import net.sf.jremoterun.utilities.nonjdk.shellcommands.DiskUsageCommand

import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * uses cmd du
 */
@Deprecated
@CompileStatic
class DiskUsageLog {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    public File diskUsageCmdFile;

    public long continueCheckForFolderBiggerInKb = 5_000_000

    public SimpleDateFormat sdf = new SimpleDateFormat('yyyy-MM-dd')
    public DecimalFormat decimalFormat = new DecimalFormat('# ###')

    public String diskUsageFileName = 'disk_usage.txt'
    public OutputStream outputStreamCommonLog

    public volatile boolean needStop = false
    public volatile File lastFileCheck
    public volatile int totalCheckCount = 0;
    public volatile DiskUsageCommand diskUsageCommandLast

    DiskUsageLog(File diskUsageCmdFile) {
        this.diskUsageCmdFile = diskUsageCmdFile
        assert diskUsageCmdFile.exists()
    }

    public static Comparator<? extends Number> numberComparatorReverse = new Comparator<? extends Number>() {
        @Override
        int compare(Number o1, Number o2) {
            return o2.compareTo(o1)
        }
    }

    void createCommonOutLogFromFile(File f){
        BufferedOutputStream bufferedOutputStream = f.newOutputStream()
        outputStreamCommonLog = new NonClosableStream(bufferedOutputStream)
    }

    void doStop() {
        needStop = true
        if (diskUsageCommandLast != null) {
            diskUsageCommandLast.process.forceDestroy2(false)
        }
    }

    void check1() {
        if (needStop) {
            throw new Exception("Stop requested")
        }
    }

    long logUsage(File dir, int maxDepth) {
        log.info "calc usage ${dir}"
        long totalSizeInKb = 0;
        Map<Long, File> entries = new LinkedHashMap<>()
        dir.eachFile {
            try {
                long sizeInKb = calcOnFileInKBytes(it)
                entries.put(sizeInKb, it)
                totalSizeInKb += sizeInKb
            } catch (Exception e) {
                log.log(Level.WARNING, "failed calc size for ${it}", e)
                throw e
            }
        }
        entries = entries.sort(numberComparatorReverse)
        String text1 = sdf.format(new Date()) + '\n' + entries.collect { buildRow(it.key, it.value) }.join('\n')
        File diskUsageF = new File(dir, diskUsageFileName);
        diskUsageF.text = text1
        check1()
        entries.findAll { needCheck(it.key, it.value, maxDepth) }.each {
            long usageSundir = logUsage(it.value, maxDepth - 1)
            long diff = Math.abs(usageSundir - it.key)
            if (diff > 1000) {
                log.info("${it.value} ${it.value} strange diff : ${diff}")
            }
        }
        return totalSizeInKb
    }

    boolean needCheck(long sizeInKb, File file1, long currentDepth) {
        if (!file1.isDirectory()) {
            return false
        }
        if (currentDepth <= 0) {
            return false
        }
        if (sizeInKb < continueCheckForFolderBiggerInKb) {
            return false
        }
        return true
    }

    String buildRow(long sizeInKb, File file1) {
        long sizeInMb = sizeInKb / 1000 as long
        return decimalFormat.format(sizeInMb) + ' ' + file1.getName()
    }


    long calcOnFileInKBytes(File file1) {
        check1()
        lastFileCheck = file1;
        DiskUsageCommand diskUsageCommand = new DiskUsageCommand(diskUsageCmdFile.getAbsolutePath(), file1.getName())
        diskUsageCommandLast = diskUsageCommand
        diskUsageCommand.process.additionalInfoPrint = file1.getAbsolutePath()
        diskUsageCommand.runDir = file1.getParentFile()
        diskUsageCommand.summary = true
        diskUsageCommand.process.onFinishedPrintFinished = false
        ByteArrayOutputStream oo = new ByteArrayOutputStream()
        diskUsageCommand.process.out2.addStream(oo)
        if (outputStreamCommonLog != null) {
            String date66 = new SimpleDateFormat('HH:mm').format(new Date());
            outputStreamCommonLog.write("${date66} running for ${file1.getAbsolutePath()} ..\n".getBytes())
            diskUsageCommand.process.out2.addStream(outputStreamCommonLog)
            diskUsageCommand.process.err2.addStream(outputStreamCommonLog)
        }
        runDiskUsageCmd(diskUsageCommand)
        String s = new String(oo.toByteArray())

        List<String> tokenize = s.tokenize(diskUsageTokenize)
        if (tokenize.size() < 2) {
            throw new Exception("wrong size for ${file1} : ${s}")
        }
        long size = tokenize[0] as long
        totalCheckCount++
        return size
    }

    public static String diskUsageTokenize = '\t'

    void runDiskUsageCmd(DiskUsageCommand diskUsageCommand) {
        diskUsageCommand.runCmd()
    }

}
