package net.sf.jremoterun.utilities.nonjdk.rstarunner

import groovy.transform.CompileStatic

import java.util.logging.Logger

@CompileStatic
abstract class RstaScriptHelper implements Runnable {

    public final Logger log = Logger.getLogger(getClass().getName());
    public volatile RstaRunner runner;


    @Override
    void run() {
        assert runner != null;
        init();
        checkForStop()
        r();
    }

    void init() {};

    abstract void r();

    void checkForStop() {
        if (runner.stopFlag) {
            log.info "Stop flag true"
            throw new Exception("Stop flag true")
        }
    }

}
