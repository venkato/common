package net.sf.jremoterun.utilities.nonjdk.io.byteshuman

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.sshsup.BytesDivider;

import java.util.logging.Logger;

@CompileStatic
class MinBytesThreshold extends BytesToHumanConverter{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public long threshold;

    MinBytesThreshold(BytesDivider bytesDivider) {
        this.threshold = bytesDivider.divider
    }

    MinBytesThreshold(long threshold) {
        this.threshold = threshold
    }

    boolean isLower(long anotherNum){
        return anotherNum<threshold
    }

    boolean isBigger(long anotherNum){
        return anotherNum>threshold
    }

}
