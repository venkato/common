package net.sf.jremoterun.utilities.nonjdk.asmow2.findfield

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.groovystarter.runners.ClRefRef;

import java.util.logging.Logger;

@CompileStatic
interface ReflectionElCommon {

    ClRefRef getClRef()




    int getLineNumber()

}
