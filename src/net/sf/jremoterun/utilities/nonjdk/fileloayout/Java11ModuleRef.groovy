package net.sf.jremoterun.utilities.nonjdk.fileloayout

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ChildPattern
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ChildRedirect
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ExactChildPattern
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.FileChildLazyRef
import net.sf.jremoterun.utilities.nonjdk.enumutils.EnumNameProvider;
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ChildChildPattern;


@CompileStatic
enum Java11ModuleRef  implements ChildRedirect,EnumNameProvider{

    java_base,
    jdk_jlink,
    jdk_xml_dom,
    jdk_zipfs,
    jdk_security_auth,
    jdk_security_jgss,
    jdk_unsupported,
    jdk_unsupported_desktop,
    jdk_localedata,
    jdk_naming_rmi,
    jdk_net,
    jdk_nio_mapmode,
    jdk_random,
    jdk_sctp,
    jdk_management,
    jdk_management_agent,
    jdk_management_jfr,
    jdk_naming_dns,
    jdk_jsobject,
    jdk_jstatd,
    jdk_jpackage,
    jdk_jshell,
    jdk_jfr,
    jdk_jdeps,
    jdk_jdi,
    jdk_jdwp_agent,
    jdk_jartool,
    jdk_javadoc,
    jdk_jcmd,
    jdk_jconsole,
    jdk_internal_vm_compiler_management,
    jdk_internal_vm_ci,
    jdk_internal_vm_compiler,
    jdk_internal_le,
    jdk_internal_opt,
    jdk_internal_jvmstat,
    jdk_incubator_vector,
    jdk_internal_ed,
    jdk_incubator_concurrent,
    jdk_hotspot_agent,
    jdk_httpserver,
    jdk_editpad,
    jdk_dynalink,
    jdk_crypto_ec,
    jdk_crypto_mscapi,
    jdk_crypto_cryptoki,
    jdk_compiler,
    jdk_charsets,
    jdk_accessibility,
    jdk_attach,
    java_xml,
    java_xml_crypto,
    java_sql_rowset,
    java_transaction_xa,
    java_sql,
    java_security_sasl,
    java_smartcardio,
    java_security_jgss,
    java_rmi,
    java_scripting,
    java_se,
    java_management_rmi,
    java_naming,
    java_net_http,
    java_prefs,
    java_desktop,
    java_management,
    java_logging,
    java_datatransfer,
    java_instrument,
    java_compiler,
    ;


    String customName;
    ExactChildPattern ref;

    Java11ModuleRef() {
        customName = name().replace('_', '.')
        ref= new ExactChildPattern ('jmods/'+ customName+'.jmod')
    }


}
