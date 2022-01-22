package net.sf.jremoterun.utilities.nonjdk

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.nonjdk.io.JrrIoUtils

import java.text.SimpleDateFormat
import java.util.logging.Level
import java.util.logging.Logger

@CompileStatic
class GeneralUtils {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    public static long defaultLogIntervalInSeconds = 3600

    static LinkedHashSet<String> receiveDeleteOnexitFiles(){
        return (LinkedHashSet)JrrClassUtils.getFieldValue(new ClRef('java.io.DeleteOnExitHook'),'files')
    }

    static Thread startLogTimer() {
        startLogTimer(defaultLogIntervalInSeconds * 1000)
    }

    static Thread startLogTimer(long interval) {
        Runnable r = {
            while (true) {
                Thread.sleep(interval)
                println "Log time : ${new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())}"
            }

        }
        Thread thread = new Thread(r, "Log time")
        thread.daemon = true
        thread.start()
        return thread
    }

    static void checkDiskFreeSpace(File file, long minFreeSpaceInMb) throws IOException {
        if (!file.exists()) {
            throw new FileNotFoundException(file.getAbsolutePath());
        }
        long freeSpace = file.getFreeSpace() / 1_000_000 as long;
        if (freeSpace < minFreeSpaceInMb) {
            throw new IOException("low free space " + freeSpace + " mb in " + file.getAbsolutePath());
        }
    }

    static Properties readPropsFromFile(File file) {
        assert file.exists()
        Properties props = new Properties()
        BufferedInputStream inputStream = file.newInputStream()
        try {
            props.load(inputStream)
        } finally {
            JrrIoUtils.closeQuietly2(inputStream, log)
        }
        return props
    }

    @Deprecated
    static void closeQuietly2(Closeable out, Logger logger){
        JrrIoUtils.closeQuietly2(out, logger)
    }

}
