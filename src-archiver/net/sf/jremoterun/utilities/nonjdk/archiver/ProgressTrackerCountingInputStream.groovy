package net.sf.jremoterun.utilities.nonjdk.archiver

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.cvsutils.LineCountStatWithCount
//import org.apache.commons.compress.utils.CountingInputStream
import org.apache.commons.io.input.CountingInputStream

import java.util.logging.Logger

/**
 * Can't handle more that Int.Max value, that is max limit 2 GB
 */
@CompileStatic
class ProgressTrackerCountingInputStream extends CountingInputStream{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public LineCountStatWithCount lineCountStat = new LineCountStatWithCount();

    ProgressTrackerCountingInputStream(InputStream out) {
        super(out)
    }

    @Override
    protected void afterRead(int n) {
        super.afterRead(n)
        lineCountStat.newCountWritten(getByteCount())
    }


}
