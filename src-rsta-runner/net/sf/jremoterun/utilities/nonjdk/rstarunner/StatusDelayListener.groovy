package net.sf.jremoterun.utilities.nonjdk.rstarunner

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

@CompileStatic
interface StatusDelayListener {

    void setStatusWithDelay(String statusNew);

    void delayedStatusChanged();




}
