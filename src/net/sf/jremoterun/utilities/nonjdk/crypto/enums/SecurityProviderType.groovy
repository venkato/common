package net.sf.jremoterun.utilities.nonjdk.crypto.enums

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils

import java.security.Provider;
import java.util.logging.Logger;

@CompileStatic
interface SecurityProviderType {

    Provider createProvider()

}
