package net.sf.jremoterun.utilities.nonjdk.net.proxy

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef

import java.util.function.BiPredicate;
import java.util.logging.Logger;

@CompileStatic
class Java21ProxyEnabler implements Runnable {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();
    public static Java21ProxyEnabler setBefore
    public String authHeader = 'Proxy-Authorization'
    public String doLogging = true
    public static ClRef clRef1 = new ClRef('jdk.internal.net.http.common.Utils')


    BiPredicate<String, String> createPredicate() {
        new BiPredicate<String, String>() {

            @Override
            boolean test(String s, String s2) {
                boolean r = false
                if (s == authHeader) {
                    r = true
                }
                if (doLogging) {
                    log.info "r=${r} name=${s} value=${s2}"
                }
                return r
            }
        }
    }

    void setImpl() {
        JrrClassUtils.setFieldValue(clRef1, 'PROXY_TUNNEL_FILTER', createPredicate())
        setBefore = this
    }

    void setIfCan() {
        boolean doJob = true
        if(setBefore!=null){
            log.info "set before"
            doJob=false
        }
        if(doJob) {
            try {
                clRef1.loadClass2()
            } catch (ClassNotFoundException e) {
                log.info3(clRef1, e)
                doJob = false
            }
        }
        if (doJob) {
            setImpl()
        }
    }

    @Override
    void run() {
        setIfCan()
    }
}
