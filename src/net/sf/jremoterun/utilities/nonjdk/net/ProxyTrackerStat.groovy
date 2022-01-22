package net.sf.jremoterun.utilities.nonjdk.net

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.DefaultObjectName;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.problemchecker.JustStackTrace

import javax.management.ObjectName
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

@CompileStatic
class ProxyTrackerStat implements ProxyTrackerI, DefaultObjectName {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public Map<String, Date> firstAccess = new ConcurrentHashMap<>()
    public Map<String, Date> lastAccess = new ConcurrentHashMap<>()
    public Map<URI, JustStackTrace> tracedLast = new ConcurrentHashMap<>()
    public HashSet<String> tracedHosts = new HashSet<>()
    public HashSet<String> rejectedHosts = new HashSet<>()
    public ConcurrentHashMap<String,JustStackTrace> rejectedAccessFirst = new ConcurrentHashMap<>()

    ObjectName defaultObjectName = new ObjectName('jrr:type=inetAccessLogs')

    @Override
    void accessRequested(URI uri, boolean proxyUsed) {
        uri.getHost()
        try {
//            URL toURL = uri.toURL()
            String host4 = uri.getHost()
//            String host4 = toURL.getHost()
            if (host4 == null) {
                log.warning("host is null for ${uri}")
            } else {
//                String host1 = uri.getHost()
                accessRequested(host4, proxyUsed)
                if (tracedHosts.contains(host4)) {
                    tracedLast.put(uri, new JustStackTrace())
                }
            }
        } catch (Exception e) {
            log.warn("failed convert to URL ${uri}", e)
        }
    }

    @Override
    void accessRequested(String host, boolean proxyUsed) {
        Date date = new Date()
        if (!firstAccess.containsKey(host)) {
            firstAccess.put(host, date)
        }
        lastAccess.put(host, date)

    }

    @Override
    boolean canAccess(URI uri) {
//        String host1 = uri.getHost()
        try {
            String host4 = uri.getHost()
//            URL toURL = uri.toURL()
//            String host4 = toURL.getHost()
            if (host4 == null) {
                log.warning("host is null for ${uri}")
                return true
            } else {
                if (rejectedHosts.contains(host4)) {
                    return false
                }
                return true
            }
        } catch (Exception e) {
            log.warn("failed convert to URL ${uri}", e)
            return true
        }
    }

    @Override
    void accessRejected(String s) {
        rejectedAccessFirst.put(s,new JustStackTrace())
    }
}
