package net.sf.jremoterun.utilities.nonjdk.crypto.enums

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.classpath.ClRef

import java.security.Provider
import java.security.Security

@CompileStatic
enum JceProviderType implements SecurityProviderType{




    BC(new ClRef('org.bouncycastle.jce.provider.BouncyCastleProvider')),

    // works
    SUN(new ClRef('sun.security.provider.Sun')),

    // may work
    SunJCE(new ClRef('com.sun.crypto.provider.SunJCE')),

    ;


    public ClRef clRef;
    public Provider providerCached;

    JceProviderType(ClRef clRef) {
        this.clRef = clRef
    }

    Provider createProvider(){
        if(providerCached==null){
            providerCached = clRef.newInstance3() as Provider
        }
        return providerCached
    }

    void insertProvider(){
        int insertProviderAt = Security.insertProviderAt(createProvider(), 1);
        assert insertProviderAt ==1
    }

}
