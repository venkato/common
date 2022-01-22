package net.sf.jremoterun.utilities.nonjdk.shellcommands.javarunner.versiondetector;


import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils

import java.util.logging.Logger;


@CompileStatic
public class JavaVersionDetector {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static JavaVersionDetector javaVersionDetector = new JavaVersionDetector();

    public Map<String, Boolean> versionMap = [:]

    @Deprecated
    public String v18 = '"1.8.0_'

    boolean detectIs8WithCache(String s) {
        Boolean rrr = versionMap.get(s)
        if (rrr != null) {
            return rrr
        }
        boolean is8 = detectIs8(s)
        versionMap.put(s, is8)
        return is8
    }

    boolean detectIs8(String s) {
        JavaVersionDetector2 detector2=new JavaVersionDetector2(s)
        log.info "running ${s} ..."
        detector2.runCmd()
        boolean b1= detector2.detectIs8()
        log.info "finished3 java8 ? ${b1} ${s} ."
        return b1
    }



}
