package net.sf.jremoterun.utilities.nonjdk.shellcommands.javarunner.versiondetector

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.javalangutils.JavaCmdOptions
import net.sf.jremoterun.utilities.nonjdk.shellcommands.NativeCommand

import java.util.logging.Logger

@CompileStatic
public class JavaVersionDetector2 extends NativeCommand {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    public static String v18S = '1.8.0_'
    public String v18 = v18S

    JavaVersionDetector2(String args) {
        super([args, JavaCmdOptions.version.customName])
        process.timeoutInSec = 2
        process.continueRunningOnTimeoutCheck = false
    }


    public String version1 = 'version "'

    boolean detectIs8() {
        return detectIs8Impl(process.errLast.toString())
    }

    boolean detectIs8Impl(String s) {
        return detectVersionFromOutputImpl(s).startsWith(v18)
    }

    String detectVersionFromOutputImpl(String outt) {
        List<String> lines = outt.readLines()
        String s = lines[0]

        assert s.contains(version1): outt
        int i = s.indexOf(version1)
        String v2 = s.substring(i + version1.length())
        int endOfVer = v2.indexOf('"')
        String ver2 = v2.substring(0, endOfVer)
        return ver2
    }


}
