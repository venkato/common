package net.sf.jremoterun.utilities.nonjdk.javalangutils

import groovy.transform.CompileStatic;


/**
 * Options that starts with -XX
 * https://docs.oracle.com/javase/8/docs/technotes/tools/windows/java.html
 * https://docs.oracle.com/en/java/javase/17/docs/specs/man/java.html
 */
@CompileStatic
enum JavaCmdOptionsNonStd {
    UnlockDiagnosticVMOptions,
    HeapDumpOnOutOfMemoryError,
    HeapDumpPath,
    ErrorFile,
    OnError,
    DisableExplicitGC,
    PrintGC,
    PrintGCDetails,
    PrintGCDateStamps,
    UseG1GC,
    UseParallelGC,
    UnlockExperimentalVMOptions,
    PrintCommandLineFlags,
    PrintFlagsFinal,
    EnableJVMCI,


//    public boolean isXx =

    JavaCmdOptions() {
        //this.isXx = isXx
    }
}