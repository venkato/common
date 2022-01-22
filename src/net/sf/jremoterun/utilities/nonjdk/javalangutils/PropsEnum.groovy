package net.sf.jremoterun.utilities.nonjdk.javalangutils

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.enumutils.EnumNameProvider

import java.util.logging.Logger;

/**
 * @see org.apache.commons.lang3.SystemUtils
 */
@CompileStatic
enum PropsEnum implements EnumNameProvider{
    java_home,
    user_home,
    user_dir,
    java_library_path,
    java_version,
    java_vendor,
    file_encoding,
    java_class_path,
    java_runtime_version,
    java_vm_specification_vendor,
    java_specification_version,
    java_awt_headless,
    //https://docs.oracle.com/javase/8/docs/technotes/guides/standards/
    java_endorsed_dirs,
    line_separator,
    os_arch,
    os_name,

    sun_java_command,
    sun_io_useCanonCaches,
    java_net_preferIPv4Stack,

    /**
     * @see sun.reflect.NativeMethodAccessorImpl
     * @see sun.reflect.ReflectionFactory#inflationThreshold
     */
    sun_reflect_inflationThreshold,
    /**
     * @see sun.reflect.ReflectionFactory#noInflation
     */
    sun_reflect_noInflation,

    /**
     * @see javax.management.loading.MLet#init
     */
    jmx_mlet_library_dir,

    /**
     * @see java.util.Locale#initDefault
     */
    user_language_display,

    /**
     * @see javax.net.ssl.SSLSocketFactory#DEBUG
     * @see sun.security.ssl.HandshakeMessage#debug
     * @see sun.security.ssl.Debug#args
     */
    javax_net_debug,

    /**
     * @see sun.security.ssl.HandshakeContext#allowUnsafeRenegotiation
     * @see org.openjsse.sun.security.ssl.HandshakeContext#allowUnsafeRenegotiation
     */
    sun_security_ssl_allowUnsafeRenegotiation,

    /**
     * @see sun.security.ssl.HandshakeContext#allowLegacyHelloMessages
     * @see org.openjsse.sun.security.ssl.HandshakeContext#allowLegacyHelloMessages
     */
    sun_security_ssl_allowLegacyHelloMessages,
    /**
     * @see sun.net.www.protocol.http.HttpURLConnection#userAgent
     */
    http_agent,
    http_auth_preference,


    /**
     * @see sun.security.ssl.SSLContextImpl.CustomizedSSLProtocols#PROPERTY_NAME
     */
    jdk_tls_client_protocols,

    /**
     * @see sun.net.www.http.HttpClient#cacheNTLMProp
     */
    jdk_ntlm_cache,

    // https://docs.oracle.com/javase/7/docs/technotes/guides/net/properties.html
    networkaddress_cache_ttl,
    networkaddress_cache_negative_ttl,
    /**
     * for java11
     * @see sun.security.ssl.SSLContextImpl
     */
    jdk_tls_client_enableStatusRequestExtension,
    /**
     * @see sun.security.ssl.SSLContextImpl.#serverCustomizedCipherSuites
     */
    jdk_tls_server_cipherSuites,
    /**
     * @see sun.security.ssl.SSLContextImpl#clientCustomizedCipherSuites
     */
    jdk_tls_client_cipherSuites,
    /**
     * @see sun.security.ssl.TrustStoreManager.TrustStoreDescriptor#jsseDefaultStore
     */
    javax_net_ssl_trustStore,

    /**
     * @see sun.security.ssl.SSLContextImpl.DefaultManagersHolder#getKeyManagers
     */
    javax_net_ssl_keyStore,
    /**
     * @see sun.security.ssl.TrustStoreManager.TrustStoreDescriptor
     */
    javax_net_ssl_trustStoreProvider,


    /**
     * @see sun.net.www.protocol.http.HttpURLConnection#disabledProxyingSchemes
     */
    jdk_http_auth_proxying_disabledSchemes,

    /**
     * @see sun.net.www.protocol.http.HttpURLConnection#disabledTunnelingSchemes
     */
    jdk_http_auth_tunneling_disabledSchemes,
    sun_net_http_errorstream_timeout,
    /**
     * @see sun.net.www.protocol.http.HttpURLConnection#allowRestrictedHeaders
     */
    sun_net_http_allowRestrictedHeaders,

    /**
     * @see org.apache.log4j.LogManager#DEFAULT_CONFIGURATION_KEY
     */
    log4j_configuration,
    /**
     * @see org.apache.log4j.helpers.LogLog#DEBUG_KEY
     */
    log4j_debug,

    /**
     * https://docs.oracle.com/javase/8/docs/technotes/guides/rmi/javarmiproperties.html
     */
    java_rmi_server_hostname,
    com_sun_management_jmxremote,
    com_sun_management_jmxremote_port,
    com_sun_management_jmxremote_rmi_port,
    com_sun_management_jmxremote_ssl,
    com_sun_management_jmxremote_authenticate,

    // java11
    jdk_module_illegalAccess_silent,
    sun_reflect_debugModuleAccessChecks,
    jdk_attach_allowAttachSelf,
    /**
     * https://docs.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
     * @see javax.swing.LookAndFeel
     */
    swing_defaultlaf,
    ;


    String customName;

    PropsEnum() {
        customName = name().replace('_','.')
    }


    String getValue(){
        return System.getProperty(customName)
    }

    void setValue(String value1){
        System.setProperty(customName,value1)
    }
    
}
