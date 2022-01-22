package net.sf.jremoterun.utilities.nonjdk.lang

import groovy.transform.CompileStatic

@CompileStatic
enum JavaDiagnostic1OptionsEnum {


    HeapDumpBeforeFullGC(boolean),
    HeapDumpAfterFullGC(boolean),
    HeapDumpOnOutOfMemoryError(boolean),
    HeapDumpPath(String),
    CMSAbortablePrecleanWaitMillis(int),
    CMSWaitDuration(int),
    CMSTriggerInterval(int),
    PrintGC(boolean),
    PrintGCDetails(boolean),
    PrintGCDateStamps(boolean),
    PrintGCTimeStamps(boolean),
    PrintGCID(boolean),
    PrintClassHistogramBeforeFullGC(boolean),
    PrintClassHistogramAfterFullGC(boolean),
    PrintClassHistogram(boolean),
    MinHeapFreeRatio(int),
    MaxHeapFreeRatio(int),
    PrintConcurrentLocks(boolean),
    ;

    public Class type;

    JavaDiagnostic1OptionsEnum(Class type) {
        this.type = type
    }
}
