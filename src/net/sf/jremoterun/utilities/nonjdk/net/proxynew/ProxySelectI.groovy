package net.sf.jremoterun.utilities.nonjdk.net.proxynew

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.nonjdk.net.proxy.HostAccessStatus

@CompileStatic
interface ProxySelectI {

    HostAccessStatus decideBaseOnHostIsProxyNeeded(String host)

    boolean acceptUrl(String url)


}
