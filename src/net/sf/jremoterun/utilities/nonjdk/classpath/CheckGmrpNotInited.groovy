package net.sf.jremoterun.utilities.nonjdk.classpath

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.JrrUtilitiesShowE
import net.sf.jremoterun.utilities.groovystarter.GroovyMethodRunnerParams;

import java.util.logging.Logger;

@CompileStatic
class CheckGmrpNotInited {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    static void doCheck() {
        GroovyMethodRunnerParams gmrp = GroovyMethodRunnerParams.gmrp
        if (gmrp == null) {
            log.info "gmrp is null, that is good"
        }else{
            JrrUtilitiesShowE.showException("gmrp inited", gmrp.creationCallStack)
        }
    }

}
