package net.sf.jremoterun.utilities.nonjdk.net.proxynew

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.net.proxy.HostAccessStatus;

import java.util.logging.Logger;

@CompileStatic
class ContainsStringDecision implements Serializable {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    private static final long serialVersionUID = -6511634932885188423L;


    public Set<String> startWithHosts = new HashSet<>()
    public Set<String> exactValue = new HashSet<>()
    public Set<String> endsWithHosts = new HashSet<>()

    boolean isMatched(String host) {
        if (exactValue.contains(host)) {
            return true
        }
        String noProxyEndWithRes = endsWithHosts.find { host.endsWith(it) }
        if (noProxyEndWithRes != null) {
            return HostAccessStatus.noProxy
        }
        String noProxyStartWithR = startWithHosts.find { host.startsWith(it) }
        if (noProxyStartWithR != null) {
            return HostAccessStatus.noProxy
        }
        return false
    }

}
