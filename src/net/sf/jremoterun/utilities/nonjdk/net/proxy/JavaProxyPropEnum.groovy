package net.sf.jremoterun.utilities.nonjdk.net.proxy

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.enumutils.EnumNameProvider;

import java.util.logging.Logger;

@CompileStatic
enum JavaProxyPropEnum implements EnumNameProvider{

    nonProxyHosts,

    proxyHost,
    proxyPort,
    proxyUser,
    proxyUserName,
    proxyPassword,
    proxySet,

    ;

    String customName = name()


}
