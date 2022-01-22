package net.sf.jremoterun.utilities.nonjdk.classpath.repohash

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.io.JrrIoUtils
import org.apache.commons.codec.digest.DigestUtils;

import java.util.logging.Logger;

@CompileStatic
class FileCheckSumCalc {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

//    static String calcGenericForFile(File file,DigestUtils digestUtils) {
//        return new .digestAsHex(file)
//    }

    static String calcSha1ForFile(File file) {
        FileInputStream fis = new FileInputStream(file)
        try {
            String hex = DigestUtils.sha1Hex(fis)
            return hex;
        } finally {
            JrrIoUtils.closeQuietly2(fis, log)
        }
    }

    static String calcSha256ForFile(File file) {
        FileInputStream fis = new FileInputStream(file)
        try {
            String hex = DigestUtils.sha256Hex(fis)
            return hex;
        } finally {
            JrrIoUtils.closeQuietly2(fis, log)
        }
    }



}
