package net.sf.jremoterun.utilities.nonjdk.idea.ideaproxy

import com.intellij.util.net.PlatformJdkProxyProvider
import com.intellij.util.proxy.CommonProxyCompatibility
import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

@CompileStatic
class IdeaProxy2025 {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();




    static void ideaSetAuthenticator2(Authenticator authenticator1) {
        JrrClassUtils.setFieldValue(PlatformJdkProxyProvider,'authenticator',authenticator1)
    }

    static void ideaSetAuthenticator(Authenticator authenticator1) {
        CommonProxyCompatibility.mainAuthenticator = authenticator1
    }

    static void ideaProxyDefault1(ProxySelector proxySelector) {
        JrrClassUtils.setFieldValue(PlatformJdkProxyProvider,'proxySelector',proxySelector)
    }

    static void ideaProxyDefault3(ProxySelector proxySelector) {
        assert proxySelector!=null
        CommonProxyCompatibility.mainProxySelector = proxySelector
    }






}
