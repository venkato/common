package net.sf.jremoterun.utilities.nonjdk.javalangutils

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.nonjdk.enumutils.EnumNameProvider;


/**
 * std options and options started with -X
 * https://docs.oracle.com/javase/8/docs/technotes/tools/windows/java.html
 * https://docs.oracle.com/en/java/javase/17/docs/specs/man/java.html
 */
@CompileStatic
enum JavaCmdOptions implements EnumNameProvider{

    agentlib,
    /**
     * used to load java agent
     */
    javaagent,
    /**
     * used to load native java agent lib
     */
    agentpath,
    cp,
    classpath,
    showversion,
    splash,
    verbose,
    // or -Xverify:none
    noverify,

    Xmx,
    Xms,
    Xss,
    Xprof,
    /**
     * Show cgroups settings : -XshowSettings:system
     */
    XshowSettings,
    Xloggc,
    Xnoclassgc,
    Xdiag,
    Xbootclasspath,
    Xint,
    Xrunjdwp,
    Xdebug,
    ;

    String customName;


    JavaCmdOptions() {
        customName = '-'+name()
    }


}