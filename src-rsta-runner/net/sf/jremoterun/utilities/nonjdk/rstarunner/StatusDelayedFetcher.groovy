package net.sf.jremoterun.utilities.nonjdk.rstarunner

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

@CompileStatic
interface StatusDelayedFetcher {


    String getDelayedStatus();

}
