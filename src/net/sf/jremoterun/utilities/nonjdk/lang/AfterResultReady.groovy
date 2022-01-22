package net.sf.jremoterun.utilities.nonjdk.lang

import groovy.transform.CompileStatic

@CompileStatic
interface AfterResultReady {

    void ready();

    void onException(Throwable e);


}
