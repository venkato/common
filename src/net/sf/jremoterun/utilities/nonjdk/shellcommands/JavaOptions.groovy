package net.sf.jremoterun.utilities.nonjdk.shellcommands

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

@CompileStatic
class JavaOptions {
    public static String openJavaBase = net.sf.jremoterun.utilities.nonjdk.javalangutils.JavaCmdOptions11.add_opens.buildArg('java.base/java.lang','ALL-UNNAMED')
//    public static String openJavaBase = net.sf.jremoterun.utilities.nonjdk.javalangutils.JavaCmdOptions11.add_opens.customName + '=java.base/java.lang=ALL-UNNAMED'


}
