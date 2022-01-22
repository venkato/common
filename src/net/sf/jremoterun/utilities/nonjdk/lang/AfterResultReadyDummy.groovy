package net.sf.jremoterun.utilities.nonjdk.lang

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

@CompileStatic
class AfterResultReadyDummy implements AfterResultReady{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();
    public Throwable exception;

    @Override
    void ready() {

    }

    @Override
    void onException(Throwable e) {
        exception =e
    }
}
