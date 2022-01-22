package net.sf.jremoterun.utilities.nonjdk.net.proxynew

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

@CompileStatic
interface ProxyHostInfo {


    String getProxyHost();

    int getProxyPort();

}
