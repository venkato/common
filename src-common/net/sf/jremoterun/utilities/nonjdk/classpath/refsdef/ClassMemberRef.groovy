package net.sf.jremoterun.utilities.nonjdk.classpath.refsdef

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.groovystarter.runners.ClRefRef;

import java.util.logging.Logger;

@CompileStatic
interface ClassMemberRef {


    ClRefRef getClRef()

}
