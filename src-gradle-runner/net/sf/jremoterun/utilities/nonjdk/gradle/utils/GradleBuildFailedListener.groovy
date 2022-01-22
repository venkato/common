package net.sf.jremoterun.utilities.nonjdk.gradle.utils

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import org.gradle.internal.exceptions.MultiCauseException;

import java.util.logging.Logger;

@CompileStatic
class GradleBuildFailedListener extends GradleCustomActionWrapper{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    GradleBuildFailedListener() {
//        showGradleStackTraces = true
    }

    @Override
    void runImpl() {
        Throwable failure = GradleEnvsUnsafe.br2.getFailure()
        //log.warning("print exception1 ")
        if (exceptionListener.printOnlyRootException && failure instanceof MultiCauseException) {
            MultiCauseException mcf = (MultiCauseException) failure;
            List<? extends Throwable> causes1 = mcf.getCauses()
            if(causes1.size()>0){
                onException(causes1[0])
                return
            }
        }
        onException(failure)
    }
}
