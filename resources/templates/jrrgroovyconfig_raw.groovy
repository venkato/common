
import net.sf.jremoterun.utilities.JrrClassUtils

import java.util.logging.Logger;
import groovy.transform.CompileStatic;


@CompileStatic
class jrrgroovyconfig_raw implements Runnable {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    @Override
    void run() {
        f1()
    }

    void f1(){

    }
}
