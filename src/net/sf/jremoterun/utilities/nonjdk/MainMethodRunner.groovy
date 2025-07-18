package net.sf.jremoterun.utilities.nonjdk

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef

import java.util.logging.Logger

@CompileStatic
class MainMethodRunner {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    static void run(ClRef cnr, List<String> args) {
        Class cll = cnr.loadClass(JrrClassUtils.getCurrentClassLoader())
        String[] args2 = args.toArray(new String[0])
        JrrClassUtils.runMainMethod(cll, args2)  //NOFIELDCHECK
    }

}
