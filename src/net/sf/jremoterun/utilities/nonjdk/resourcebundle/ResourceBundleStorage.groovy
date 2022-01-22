package net.sf.jremoterun.utilities.nonjdk.resourcebundle

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.serviceloader.ServiceLoaderFactory;

import java.util.logging.Logger;

@CompileStatic
class ResourceBundleStorage {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    public boolean logRequestedServices = false
    public boolean debugMax = false
    public HashSet<String> loadTry = new HashSet<>();
    public HashSet<String> cacheOnly = new HashSet<>();
    public boolean cacheAll = false;
    public HashSet<String> loadResolved = new HashSet<>();
    public Map<String, ResourceBundle> loadResolved2 = new HashMap<>();

    public ResourceLoaderFactory factory1 = new ResourceLoaderFactory(this);


    ResourceBundle getImplBefore(ArrayList serviceLoader1) {
        String second = serviceLoader1.get(1)
        loadTry.add(second)
        ResourceBundle bundle = loadResolved2.get(second)
        if (bundle != null) {
            return bundle
        }
        return null
    }

    ResourceBundle getImplAfter(ArrayList serviceLoader1) {
        String second = serviceLoader1.get(1)
        ResourceBundle result = serviceLoader1.last() as ResourceBundle
        loadResolved.add(second)
        boolean cache = false
        if (cacheAll || cacheOnly.contains(second)) {
            cache = true
        }
        if (cache) {
            loadResolved2.put(second, result)
        }
        return result
    }

    ResourceBundle getImpl(ArrayList serviceLoader1) {
        boolean logReq = false
        if (logRequestedServices || debugMax) {
            logReq = true
        }
        if (logReq) {
            StringBuilder sb = new StringBuilder()
            for (i in 0..<serviceLoader1.size()) {
                try {
                    Object get1 = serviceLoader1.get(i)
                    String msg
                    if (get1 instanceof java.util.ResourceBundle) {
                        msg = get1.getClass().getName()
                    } else if (get1 instanceof Class) {
                        msg = get1.getName()
                    } else if (get1 == null) {
                        msg = 'null'
                    } else {
                        msg = get1.toString()
                    }
                    sb.append(msg).append(", ")
                } catch (Throwable e) {
                    log.info "failed to get ${i} : ${e}"
                    throw e
                }
            }
            log.info "services for ${sb}"
        }
        String first = serviceLoader1.get(0)
        RBCallerType callerType = RBCallerType.valueOf(first as String)


        if (callerType == RBCallerType.before) {
            return getImplBefore(serviceLoader1)
        }
        if (callerType == RBCallerType.after) {
            return getImplAfter(serviceLoader1);
        }
        throw new IllegalStateException("${serviceLoader1}")
    }
}
