package net.sf.jremoterun.utilities.nonjdk.net.ssl.enums

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.nonjdk.enumutils.EnumNameProvider

@CompileStatic
enum SslProtocolsEnum implements EnumNameProvider{

    SSLv2Hello,
    SSLv3,
    TLS,
    TLSv1,
    TLSv1_1,
    TLSv1_2,
    TLSv1_3,
    ;

    String customName;

    SslProtocolsEnum() {
        this.customName = name().replace('_','.')
    }
}
