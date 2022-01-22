package net.sf.jremoterun.utilities.nonjdk.net.proxy

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.net.proxynew.ProxyHostInfo
import net.sf.jremoterun.utilities.nonjdk.net.proxynew.ProxyHostInfoImpl;

import java.util.logging.Logger;

@CompileStatic
class ProxyStaticMethods {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static ProxySelector proxySelectorAtStartup = ProxySelector.getDefault()

    public static List<java.net.Proxy> noProxy = [Proxy.NO_PROXY];

    public static List<String> prefixProtocols = ['http', 'https', 'ftp',]

    static void followRedirect() {
        HttpURLConnection.setFollowRedirects(true)
    }


    static void setAuthImpl(String user, String password) {
        ProxyAuth auth = new ProxyAuth(user, password)
        Authenticator.setDefault(auth)
        setPropertyForAll(JavaProxyPropEnum.proxyUser, user)
        setPropertyForAll(JavaProxyPropEnum.proxyUserName, user)
        setPropertyForAll(JavaProxyPropEnum.proxyPassword, password)
    }

    static Authenticator getCurrentAuth() {
        Authenticator initialAuth = JrrClassUtils.getFieldValue(Authenticator, "theAuthenticator") as Authenticator
        return initialAuth
    }

    static void setAuth(String user, String password) {
        Authenticator initialAuth = getCurrentAuth()
        if (initialAuth == null) {
            setAuthImpl(user, password)
        } else {
            log.info "proxy auth alredy set ${initialAuth.getClass().getName()}"
        }

    }

    static void setPropertyOne(JavaProxyPropEnum name1, String value) {
        System.setProperty(name1.customName, value)
    }

    static void setPropertyForAll(JavaProxyPropEnum name1, String value) {
        prefixProtocols.each {
            System.setProperty("${it}.${name1.customName}", value)
            System.setProperty("${it}.${name1.customName}", value)
        }
    }

    static void setNoProxy(List<String> noProxy) {
        setPropertyForAll(JavaProxyPropEnum.nonProxyHosts, noProxy.join('|'))
    }


    static java.net.Proxy setProxyS(ProxyHostInfo proxyHostInfo) {
        ProxyStaticMethods.setPropertyForAll(JavaProxyPropEnum.proxyHost, proxyHostInfo.proxyHost)
        ProxyStaticMethods.setPropertyForAll(JavaProxyPropEnum.proxyPort, "${proxyHostInfo.proxyPort}")
        java.net.Proxy proxy2 = createProxyObject(proxyHostInfo)
        setPropertyOne(JavaProxyPropEnum.proxySet, 'true')
        return proxy2
    }

    static java.net.Proxy createProxyObject(ProxyHostInfo proxyHostInfo) {
        InetSocketAddress proxy = new InetSocketAddress(proxyHostInfo.proxyHost, proxyHostInfo.proxyPort)
        java.net.Proxy proxy2 = new Proxy(java.net.Proxy.Type.HTTP, proxy)
        return proxy2
    }


    static java.net.Proxy setProxyS(String host, int port) {
        ProxyHostInfoImpl proxyHostInfo1=new ProxyHostInfoImpl(host,port)
        return setProxyS(proxyHostInfo1)
    }
}
