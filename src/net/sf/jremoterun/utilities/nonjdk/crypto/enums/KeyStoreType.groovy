package net.sf.jremoterun.utilities.nonjdk.crypto.enums

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.enumutils.EnumNameProvider;

import java.util.logging.Logger;

@CompileStatic
enum KeyStoreType implements EnumNameProvider{

    PKCS12,
    JKS,
    /**
     * @see org.bouncycastle.jcajce.provider.keystore.bcfks.BcFKSKeyStoreSpi
     */
    BCFKS,

    Windows_MY,
    Windows_ROOT,

    ;


    String customName;

    KeyStoreType() {
        this.customName = name().replace('_','-')
    }

}
