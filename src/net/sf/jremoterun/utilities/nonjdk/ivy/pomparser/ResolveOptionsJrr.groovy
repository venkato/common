package net.sf.jremoterun.utilities.nonjdk.ivy.pomparser

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import org.apache.ivy.core.resolve.ResolveOptions;

import java.util.logging.Logger;

@CompileStatic
class ResolveOptionsJrr extends ResolveOptions{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


}
