package net.sf.jremoterun.utilities.nonjdk.windowsos

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

@CompileStatic
class DiskUsageResult implements Comparable, Serializable{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public File f ;

    public long totalSize = 0;

    public List<DiskUsageResult> childs

    DiskUsageResult(File f, long totalSize) {
        this.f = f
        this.totalSize = totalSize
    }

    DiskUsageResult(File f, long totalSize, List<DiskUsageResult> childs) {
        this.f = f
        this.totalSize = totalSize
        this.childs = childs
    }

    @Override
    int compareTo(@NotNull Object o) {
        if (o instanceof DiskUsageResult) {
            DiskUsageResult ro = (DiskUsageResult) o;
            return ro.totalSize.compareTo(totalSize)
        }
        throw new IllegalStateException("${o}")
//        return 1
    }

    @Override
    String toString() {
        return "${f.getName()} ${totalSize}"
    }
}
