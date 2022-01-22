package net.sf.jremoterun.utilities.nonjdk.crypto

import groovy.time.TimeDuration
import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.crypto.enums.CertificateSignatureAgl
import net.sf.jremoterun.utilities.nonjdk.crypto.enums.KeyStoreType
import net.sf.jremoterun.utilities.nonjdk.crypto.enums.KeyType
import net.sf.jremoterun.utilities.nonjdk.dateutils.DurationConstants
import sun.security.tools.keytool.CertAndKeyGen
import sun.security.x509.X500Name

import java.security.KeyStore
import java.security.PrivateKey
import java.security.cert.X509Certificate;
import java.util.logging.Logger;

@CompileStatic
class CertificatedGeneratorJdk {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    private  int keysize = 1024;
    private String commonName = "127.0.0.1";
    private String organizationalUnit = "IT";
    private String organization = "test";
    private String city = "test";
    private String state = "test";
    private String country = "DE";
    private long validity = 365*30; //  years
    private String alias = "tomcat";

    public char[] keyPass = []

    public X509Certificate[] chain = new X509Certificate[1];
    //public CertAndKeyGen keypair

    CertificatedGeneratorJdk() {
    }

    KeyStore generateKeyStore(){
        KeyStore keyStore = KeyStore.getInstance(KeyStoreType.JKS.name());
        keyStore.load(null, null);
        CertAndKeyGen keypair = generateCertificate();
        PrivateKey privKey = keypair.getPrivateKey();
        keyStore.setKeyEntry(alias, privKey, keyPass, chain);
        return keyStore
    }

    CertAndKeyGen generateCertificate(){
        // was : SHA512WithRSA
        CertAndKeyGen keypair = new CertAndKeyGen(KeyType.RSA.name(), CertificateSignatureAgl.SHA512WITHRSA.name(), null);
        keypair.generate(keysize);

        X500Name x500Name = new X500Name(commonName, organizationalUnit, organization, city, state, country);
        X509Certificate x509Certificate = keypair.getSelfCertificate(x500Name, new Date(), (long) validity * DurationConstants.oneDay.getTimeInSecLong());
        chain[0]=x509Certificate
        return keypair
    }

    CertAndKeyGen generateSignedCertificate(){
        CertAndKeyGen keypair = new CertAndKeyGen(KeyType.RSA.name(), CertificateSignatureAgl.SHA512WITHRSA.name(), null);
        keypair.generate(keysize);

        X500Name x500Name = new X500Name(commonName, organizationalUnit, organization, city, state, country);
        X509Certificate x509Certificate = keypair.getSelfCertificate(x500Name, new Date(), (long) validity * DurationConstants.oneDay.getTimeInSecLong());
        chain[0]=x509Certificate
        return keypair
    }

}
