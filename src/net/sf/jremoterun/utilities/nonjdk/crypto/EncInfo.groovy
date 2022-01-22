package net.sf.jremoterun.utilities.nonjdk.crypto

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.store.JavaBean;

import java.util.logging.Logger;

@CompileStatic
class EncInfo implements JavaBean {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    public String keyParam;
    public BigInteger iv;
    public BigInteger keySalt;
    public String pass;
    public int iter;
    public int keyLen;
    public String cipherAlgo;
    public String provider;

    public static EncInfo createBasic() {
        EncInfo b = new EncInfo()

//        b.keyParam = 'PBEWithHmacSHA256AndAES_256';
        b.keyParam = 'PBEWithHmacSHA128AndAES_128';
        //b.iv
        //b.keySalt;
        //b.pass;
        b.iter = 128;
        b.keyLen = 128;
        b.cipherAlgo = 'AES/CBC/PKCS5Padding';
        b.provider = 'SunJCE';
        return b;
    }


    static BigInteger createBytes(int length) {
        byte[] b = new byte[16];
        new Random().nextBytes(b)
        return new BigInteger(b);
    }


}
