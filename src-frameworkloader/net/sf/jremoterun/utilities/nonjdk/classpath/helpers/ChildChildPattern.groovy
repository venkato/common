package net.sf.jremoterun.utilities.nonjdk.classpath.helpers

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

@CompileStatic
interface ChildChildPattern extends ChildPattern {


    ChildChildPattern childL(String child1);

    ChildChildPattern childP(ChildPattern child1) ;


}
