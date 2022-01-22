package net.sf.jremoterun.utilities.nonjdk.eclipse.proxy

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.enumutils.EnumNameProvider;

import java.util.logging.Logger;

/**
 * @see org.eclipse.core.net.proxy.IProxyData
 */
@CompileStatic
enum EclipseProxyDataType implements EnumNameProvider {

    HTTP,
    HTTPS,

    ;

    String customName = name()

    public String nameLowerCase = name().toLowerCase();


}
