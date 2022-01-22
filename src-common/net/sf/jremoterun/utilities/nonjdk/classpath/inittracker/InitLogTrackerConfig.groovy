package net.sf.jremoterun.utilities.nonjdk.classpath.inittracker

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

@CompileStatic
class InitLogTrackerConfig {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static boolean registerInJmx = true

}
