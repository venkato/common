package net.sf.jremoterun.utilities.nonjdk.net.ssl

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

@CompileStatic
interface SslSocketCatcher {


    DelegatingSSLSocketFactory fetchSSLSocketFactory();


}
