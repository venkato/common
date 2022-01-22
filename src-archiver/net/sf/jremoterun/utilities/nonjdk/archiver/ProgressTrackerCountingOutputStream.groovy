package net.sf.jremoterun.utilities.nonjdk.archiver

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.cvsutils.LineCountStat
import net.sf.jremoterun.utilities.nonjdk.cvsutils.LineCountStatWithCount
import org.apache.commons.compress.utils.CountingOutputStream;

import java.util.logging.Logger;

@CompileStatic
class ProgressTrackerCountingOutputStream extends CountingOutputStream{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public LineCountStatWithCount lineCountStat = new LineCountStatWithCount();

    ProgressTrackerCountingOutputStream(OutputStream out) {
        super(out)
    }

    @Override
    protected void count(long written) {
        super.count(written)
        lineCountStat.newCountWritten(getBytesWritten())
    }

}
