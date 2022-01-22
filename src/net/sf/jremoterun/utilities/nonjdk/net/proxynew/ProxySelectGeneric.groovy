package net.sf.jremoterun.utilities.nonjdk.net.proxynew

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.DefaultObjectName;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.net.proxy.HostAccessStatus
import net.sf.jremoterun.utilities.nonjdk.net.proxy.ProxyStaticMethods

import javax.management.ObjectName;
import java.util.logging.Logger;

@CompileStatic
class ProxySelectGeneric implements ProxySelectI, DefaultObjectName  {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public ContainsStringDecision proxyForce =new ContainsStringDecision();
    public ContainsStringDecision noProxyForce =new ContainsStringDecision();
    public ContainsStringDecision rejectHosts =new ContainsStringDecision();

    public ContainsStringDecision urlAccept =new ContainsStringDecision();
    public ContainsStringDecision urlReject =new ContainsStringDecision();

    public boolean urlDefaultDecisionUseProxy = true
    public HostAccessStatus defaultDecisionHost = HostAccessStatus.proxy

    ObjectName defaultObjectName = new ObjectName('jrr:type=proxyConfig')

    ProxySelectGeneric() {

    }

    void addConcreatHostAndIp(String host) {
        long time = System.currentTimeMillis()
        InetAddress address = InetAddress.getByName(host)
        String name = address.hostName
        String address1 = address.address
        long takeTime = System.currentTimeMillis() - time
        if (takeTime > 1000) {
            log.info "resolving ${host} took ${takeTime / 1000} sec"
        }
        noProxyForce.exactValue.add(name)
        noProxyForce.exactValue.add(address1)
    }

    @Override
    boolean acceptUrl(String url) {
        if(url==null){
            throw new NullPointerException('url is null')
        }

        if(url.length()==0){
            throw new NullPointerException('url is empty string')
        }
        if(urlAccept.isMatched(url)){
            return true
        }
        if(urlReject.isMatched(url)){
            return false
        }
        return urlDefaultDecisionUseProxy
    }

    HostAccessStatus decideBaseOnHostIsProxyNeeded(String host) {
        if(host==null){
            throw new NullPointerException('host is null')
        }
        if(host.length()==0){
            throw new NullPointerException('host is empty string')
        }
        if(rejectHosts.isMatched(host)){
            return HostAccessStatus.reject
        }
        if(proxyForce.isMatched(host)){
            return HostAccessStatus.proxy
        }
        if(noProxyForce.isMatched(host)){
            return HostAccessStatus.noProxy
        }
        return defaultDecisionHost
    }

    List<String> getNoProxyProp(){
        List<String> joins = []
        joins.addAll noProxyForce.exactValue
        joins.addAll noProxyForce.endsWithHosts.collect { '*.' + it }
        return joins
    }

    void setNoProxyProp() {
        List<String> joins = getNoProxyProp()
        ProxyStaticMethods.setNoProxy(joins)
    }


}
