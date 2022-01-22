package net.sf.jremoterun.utilities.nonjdk.crypto

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.groovystarter.runners.RunnableWithParamsFactory2
import net.sf.jremoterun.utilities.nonjdk.crypto.enums.CipherMode
import net.sf.jremoterun.utilities.nonjdk.io.JrrIoUtils
import org.apache.commons.io.IOUtils
import org.bouncycastle.jce.provider.BouncyCastleProvider

import javax.crypto.Cipher
import javax.crypto.CipherInputStream
import javax.crypto.CipherOutputStream
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec
import java.security.Security
import java.util.logging.Logger

@CompileStatic
class EncDec {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass()

    EncInfo encInfo

    static EncInfo loadConfig(File encInfoFile) {
        RunnableWithParamsFactory2 r = new RunnableWithParamsFactory2()
        EncInfo encInfo2 = new EncInfo()
        r.loadSettingsWithParam(encInfoFile, encInfo2)
        return encInfo2;
    }

    void encDec1(File inf, File outF, CipherMode mode, File encInfo) {
        EncInfo encInfo2 = loadConfig(encInfo)
        encDec2(inf, outF, mode, encInfo2)
    }

    CipherInputStream wrapInputStream(CipherMode mode, EncInfo encInfo,InputStream stream2In){
        SecretKey key2 = getKey()
        Cipher cipher = Cipher.getInstance(encInfo.cipherAlgo, encInfo.provider)
        IvParameterSpec spec = new IvParameterSpec(encInfo.iv.toByteArray())
//        cipher.init(mode, key2)
        cipher.init(mode.javaMode, key2, spec)
        CipherInputStream streamIn = new CipherInputStream(stream2In, cipher)
        return streamIn
    }

    CipherOutputStream wrapOutputStream(CipherMode mode, EncInfo encInfo,OutputStream outputStream1){
        SecretKey key2 = getKey()
        Cipher cipher = Cipher.getInstance(encInfo.cipherAlgo, encInfo.provider)
        IvParameterSpec spec = new IvParameterSpec(encInfo.iv.toByteArray())
//        cipher.init(mode, key2)
        cipher.init(mode.javaMode, key2, spec)
        CipherOutputStream streamIn = new CipherOutputStream(outputStream1, cipher)
        return streamIn
    }

    void encDec2(File inf, File outF, CipherMode mode, EncInfo encInfo) {
        assert inf != outF
        this.encInfo = encInfo
        Security.addProvider(new BouncyCastleProvider())
        InputStream stream2In =  createInputStream(inf)
        OutputStream stream1Out;
        try {
            CipherInputStream streamIn = wrapInputStream(mode,encInfo, stream2In)
//            CipherInputStream streamIn = new CipherInputStream(stream2In, cipher)
            stream1Out = createOutputStream(outF)
            IOUtils.copy(streamIn, stream1Out)
            stream1Out.flush()
            streamIn.close()
        }finally{
            JrrIoUtils.closeQuietly2(stream1Out, log)
            JrrIoUtils.closeQuietly2(stream2In, log)
        }

//        log.info new JavaBeanStore().saveS(encInfo)
        log.info "finished"
    }

    InputStream createInputStream(File f){
        return f.newInputStream();
    }

    OutputStream createOutputStream(File f){
        return f.newOutputStream();
    }

    SecretKey getKey() {
        String pass = encInfo.pass
//        log.info "pass = ${pass}"
        PBEKeySpec keySpec = new PBEKeySpec(pass.toCharArray(), encInfo.keySalt.toByteArray(), encInfo.iter, encInfo.keyLen)
        SecretKeyFactory skf = SecretKeyFactory.getInstance(encInfo.keyParam, encInfo.provider)
//        log.info  skf.getProvider().getName()
        SecretKey secret = skf.generateSecret(keySpec)
        SecretKey secret2 = new SecretKeySpec(secret.getEncoded(), "AES")
        return secret2

    }


}
