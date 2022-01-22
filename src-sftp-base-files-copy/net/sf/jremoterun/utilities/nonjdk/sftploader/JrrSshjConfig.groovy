package net.sf.jremoterun.utilities.nonjdk.sftploader

import com.hierynomus.sshj.transport.kex.DHGroups
import com.hierynomus.sshj.transport.kex.ExtInfoClientFactory
import com.hierynomus.sshj.transport.kex.ExtendedDHGroups
import groovy.transform.CompileStatic
import net.schmizz.sshj.DefaultConfig
import net.schmizz.sshj.transport.kex.Curve25519SHA256
import net.schmizz.sshj.transport.kex.DHGexSHA1
import net.schmizz.sshj.transport.kex.DHGexSHA256
import net.schmizz.sshj.transport.kex.ECDHNistP;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

@CompileStatic
class JrrSshjConfig extends DefaultConfig{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();



    @Override
    protected void initKeyExchangeFactories() {
        setKeyExchangeFactories(
//                new Curve25519SHA256.Factory(),
//                new Curve25519SHA256.FactoryLibSsh(),
                new DHGexSHA256.Factory(),
                new ECDHNistP.Factory521(),
                new ECDHNistP.Factory384(),
                new ECDHNistP.Factory256(),
                new DHGexSHA1.Factory(),
                DHGroups.Group1SHA1(),
                DHGroups.Group14SHA1(),
                DHGroups.Group14SHA256(),
                DHGroups.Group15SHA512(),
                DHGroups.Group16SHA512(),
                DHGroups.Group17SHA512(),
                DHGroups.Group18SHA512(),
                ExtendedDHGroups.Group14SHA256AtSSH(),
                ExtendedDHGroups.Group15SHA256(),
                ExtendedDHGroups.Group15SHA256AtSSH(),
                ExtendedDHGroups.Group15SHA384AtSSH(),
                ExtendedDHGroups.Group16SHA256(),
                ExtendedDHGroups.Group16SHA384AtSSH(),
                ExtendedDHGroups.Group16SHA512AtSSH(),
                ExtendedDHGroups.Group18SHA512AtSSH(),
                new ExtInfoClientFactory()
        );
    }


}
