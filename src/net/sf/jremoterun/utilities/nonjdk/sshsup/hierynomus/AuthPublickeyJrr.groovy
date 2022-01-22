package net.sf.jremoterun.utilities.nonjdk.sshsup.hierynomus

import com.hierynomus.sshj.key.KeyAlgorithm
import groovy.transform.CompileStatic
import net.schmizz.sshj.SSHClient
import net.schmizz.sshj.common.Buffer
import net.schmizz.sshj.common.KeyType
import net.schmizz.sshj.common.SSHPacket
import net.schmizz.sshj.userauth.UserAuthException
import net.schmizz.sshj.userauth.keyprovider.KeyProvider
import net.schmizz.sshj.userauth.method.AuthPublickey;
import net.sf.jremoterun.utilities.JrrClassUtils

import java.security.PublicKey;
import java.util.logging.Logger;

@CompileStatic
class AuthPublickeyJrr extends AuthPublickey {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    AuthPublickeyJrr(SSHClient sshClient,File f) {
        super(sshClient.loadKeys(f.getAbsolutePath()))
        assert f.exists()
    }

    AuthPublickeyJrr(KeyProvider kProv) {
        super(kProv)
    }

    KeyAlgorithm findKeyAlgorithm(){
        PublicKey key = kProv.getPublic();
        KeyType keyType = KeyType.fromKey(key);
        List<KeyAlgorithm> algorithms = params.getTransport().getClientKeyAlgorithms(keyType)
        log.info "algorithms = ${algorithms.collect { it.getKeyAlgorithm() }}"
        KeyAlgorithm ka = algorithms[0];
        return ka
    }

    @Override
    protected SSHPacket putPubKey(SSHPacket reqBuf) throws UserAuthException {
        PublicKey key = kProv.getPublic();
        KeyAlgorithm ka = findKeyAlgorithm();
//        KeyAlgorithm ka = JrrClassUtils.invokeJavaMethod(this, 'getPublicKeyAlgorithm', keyType) as KeyAlgorithm;
        String algorithm = ka.getKeyAlgorithm();
        byte[] publicKey2 = new Buffer.PlainBuffer().putPublicKey(key).getCompactData()
        putPubKey2(reqBuf,algorithm,publicKey2)
        return reqBuf;
    }

    protected SSHPacket putPubKey2(SSHPacket reqBuf,String algorithm, byte[] publicKey) throws UserAuthException {
        String s = new String(publicKey);
        log.info "public key : ${algorithm} : ${s.substring(0, 20)}"
        reqBuf.putString(algorithm)    .putString(publicKey);
    }
}
