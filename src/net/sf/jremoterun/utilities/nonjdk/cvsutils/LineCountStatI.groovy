package net.sf.jremoterun.utilities.nonjdk.cvsutils

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils

import java.util.logging.Logger

@CompileStatic
interface LineCountStatI {

    boolean isNeedPrintProgress(long linesRead)


    long getLogEvery()

    void setLogEvery(long logEvery)

}
