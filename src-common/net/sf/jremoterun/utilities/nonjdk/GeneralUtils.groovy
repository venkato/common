package net.sf.jremoterun.utilities.nonjdk

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.nonjdk.dateutils.DurationConstants
import net.sf.jremoterun.utilities.nonjdk.io.JrrIoUtils

import java.text.SimpleDateFormat
import java.util.logging.Logger

@CompileStatic
class GeneralUtils {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    public static long defaultLogIntervalInSeconds = DurationConstants.oneHour.timeInSecInt
    public static volatile Thread defaultLogTimerThread

    static LinkedHashSet<String> receiveDeleteOnexitFiles() {
        return (LinkedHashSet) JrrClassUtils.getFieldValue(new ClRef('java.io.DeleteOnExitHook'), 'files')
    }

    static Thread startLogTimer() {
        if (defaultLogTimerThread!=null) {
            log.info("log timer already created before")
            return defaultLogTimerThread
        }
        Thread timer = startLogTimer(defaultLogIntervalInSeconds * 1000)
        defaultLogTimerThread = timer;
        return timer

    }

    static Thread startLogTimer(long interval) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        Runnable r = {

            while (true) {
                Thread.sleep(interval)
                println "Log time : ${simpleDateFormat.format(new Date())}"
            }

        }
        Thread thread = new Thread(r, "Log time")
        thread.setDaemon( true)
        thread.start()
        return thread
    }

    public static long million = 1_000_000;

    static void checkDiskFreeSpace(File file, long minFreeSpaceInMb) throws IOException {
        checkDiskFreeSpaceInBytes(file, minFreeSpaceInMb * million);
    }

    static void checkDiskFreeSpaceInBytes(File file, long minFreeSpaceInBytes) throws IOException {
        if (!file.exists()) {
            throw new FileNotFoundException(file.getAbsolutePath());
        }
        long freeSpace = file.getFreeSpace();
        if (freeSpace < minFreeSpaceInBytes) {
            throw new IOException("low free space " + (freeSpace / million) + " mb in " + file.getAbsolutePath());
        }
    }


    @Deprecated
    static void closeQuietly2(Closeable out, Logger logger) {
        JrrIoUtils.closeQuietly2(out, logger)
    }

}
