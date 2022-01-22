package net.sf.jremoterun.utilities.nonjdk.ioutils.filerotate

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.classpath.repohash.FileCheckSumCalc;

import java.util.logging.Logger;

@CompileStatic
class IsFileEquals {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static IsFileEquals isFileEquals = new IsFileEquals();
    public long maxLengthsCompareBytes = 100_000

    boolean isFileEqualsS(File f1, File f2) {
        if (!f2.exists()) {
            return false
        }
        assert f1.exists()
        long length1 = f1.length()
        if (length1 != f2.length()) {
            return false
        }
        if (length1 < maxLengthsCompareBytes) {
            return compareByBytes(f1, f2)
        }
        return compareByHash(f1, f2)
    }


    boolean compareByHash(File f1, File f2) {
        String sha1 = FileCheckSumCalc.calcSha256ForFile(f1)
        String sha2 = FileCheckSumCalc.calcSha256ForFile(f2)
        return sha1 == sha2

    }

    boolean compareByBytes(File f1, File f2) {
        byte[] bytes1 = f1.bytes
        byte[] bytes2 = f2.bytes
        return Arrays.equals(bytes1, bytes2)
    }


}
