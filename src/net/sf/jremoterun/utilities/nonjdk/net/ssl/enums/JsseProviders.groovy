package net.sf.jremoterun.utilities.nonjdk.net.ssl.enums

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.nonjdk.crypto.enums.SecurityProviderType

import java.security.Provider

/**
 * https://github.com/google/conscrypt
 */
@CompileStatic
enum JsseProviders implements SecurityProviderType {
    /**
     * native, only for linux. Need java9+
     */
    openSSL4J(new ClRef('de.sfuhrm.openssl4j.OpenSSL4JProvider')),
    /**
     *  native, for any platform
     */
    conscrypt(new ClRef('org.conscrypt.OpenSSLProvider')),
    /**
     * pure java
     */
    BC(new ClRef('org.bouncycastle.jsse.provider.BouncyCastleJsseProvider')),
    /**
     * pure java
     */
    openJsse(new ClRef('org.openjsse.net.ssl.OpenJSSE')),
    /**
     * default provided with jdk
     */
    jdkInternal(new ClRef('com.sun.net.ssl.internal.ssl.Provider')),

    ;


    public ClRef clRef;
    public Provider providerCached;

    JsseProviders(ClRef clRef) {
        this.clRef = clRef
    }

    Provider createProvider() {
        if (providerCached == null) {
            providerCached = clRef.newInstance3() as Provider
        }
        return providerCached
    }
}
