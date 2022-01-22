package net.sf.jremoterun.utilities.nonjdk.store.customwriters

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils

import java.util.logging.Logger

@CompileStatic
class NumSeparatorWriter {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public int separator
    public int separatorLength
    public long convertIfBigger = 10_000_000

    NumSeparatorWriter(int separator1) {
        this.separator = (int) 10.power(separator1)
        separatorLength = ('' + separator).length() - 1
    }

    String convert(long obj) {
        if (obj < 0) {
            return '-' + convert(-obj)
        }
        if (obj > convertIfBigger) {
            return doRest(obj)
        }
        return Long.toString(obj)
    }

    String doRest(long intt) {
        long remainder = intt % separator
        long intdiv1 = intt.intdiv(separator)
//log.info "num=${intt}  ${intdiv1} rem=${remainder}"
        if (intdiv1 == 0) {
            return '' + remainder
        }
        String remainderStr = '' + remainder
//        int paddCount = separatorLength-remainderStr.length()
//        log.info "paddCount = ${paddCount}"
        remainderStr = remainderStr.padLeft(separatorLength, '0')
        return doRest(intdiv1) + '_' + remainderStr
    }


}
