package net.sf.jremoterun.utilities.nonjdk.javalangutils

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.enumutils.EnumNameProvider;

import java.util.logging.Logger;

@CompileStatic
enum GrEnumProps implements EnumNameProvider{
    jrrrunnerPrintCmd,
    jrrrunnerEchoTest,
    jrrrunnerPauseOnExit,
    jrrrunnerFork,
    jstd,
    jw,
    jxmx,
    jpr,
    jrrVmParam,
    ;

    @Override
    String getCustomName() {
        return name()
    }
}
