package net.sf.jremoterun.utilities.nonjdk.sshsup

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils

import java.math.RoundingMode;
import java.util.logging.Logger;

@CompileStatic
class BytesToHumanConverter {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public int roundProgressSpeed

    BytesToHumanConverter() {
        roundProgressSpeed = 2
    }

    BytesToHumanConverter(int roundProgressSpeed) {
        this.roundProgressSpeed = roundProgressSpeed
    }

    String convertHuman(Number bytesPerSec) {
        if (ifMatch(bytesPerSec, BytesDivider.bytes)) {
            return round2(bytesPerSec, BytesDivider.bytes)
        }
        if (ifMatch(bytesPerSec, BytesDivider.kb)) {
            return round2(bytesPerSec, BytesDivider.kb)
        }
        if (ifMatch(bytesPerSec, BytesDivider.mb)) {
            return round2(bytesPerSec, BytesDivider.mb)
        }
        if (ifMatch(bytesPerSec, BytesDivider.gb)) {
            return round2(bytesPerSec, BytesDivider.gb)
        }
        if (ifMatch(bytesPerSec, BytesDivider.tb)) {
            return round2(bytesPerSec, BytesDivider.tb)
        }
        return "${bytesPerSec} bytes"
    }

    boolean ifMatch(Number number, BytesDivider divider) {
        return number < divider.dividerBy1000
    }

    String round2(Number number, BytesDivider divider) {
        return round(number, divider.divider, divider.name())
    }

    String round(Number number, long divider, String suffix) {
        BigDecimal bd = number / divider
        return bd.setScale(roundProgressSpeed, RoundingMode.HALF_UP).toString() + ' ' + suffix
    }



}
