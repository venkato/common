package net.sf.jremoterun.utilities.nonjdk.crypto

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.JrrUtilitiesMapBuilder
import net.sf.jremoterun.utilities.nonjdk.crypto.enums.JceProviderType
import net.sf.jremoterun.utilities.nonjdk.crypto.enums.KeyType
import org.apache.commons.codec.binary.Base64

import java.security.Key
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.Provider
import java.security.PublicKey
import java.security.Security
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec;
import java.util.logging.Logger;

/**
 * @see cn.hutool.crypto.OpensslKeyUtil
 */
@CompileStatic
class AsymetricKeyLoader3 {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    static Key loadKey(byte[] bytes, String type, Provider provider) {
        String text = new String(bytes, 'UTF8')
        List<String> lines = text.readLines()
        String firstLine = lines[0]
//        if (firstLine.contains('BEGIN RSA PRIVATE KEY')) {
        if (firstLine.contains( buildBeginString(KeyType.RSA.name(),true)) ){
            String lastLine = lines.last()
            assert lastLine.contains(buildBeginString(KeyType.RSA.name(),false))
//            assert lastLine.contains('END RSA PRIVATE KEY')
            lines.remove(0)
            lines.remove(lines.size() - 1)
            String string = lines.join('')
            byte[] decodeBase64 = Base64.decodeBase64(string)
            if (type == null) {
                type = KeyType.RSA.name()
            }
            return loadPrivateKey(decodeBase64, type, provider)
        }
//        if (firstLine.contains( 'BEGIN DSA PRIVATE KEY')) {
        if (firstLine.contains( buildBeginString(KeyType.DSA.name(),true)) ){
            String lastLine = lines.last()
            assert lastLine.contains(buildBeginString(KeyType.DSA.name(),false))
//            assert lastLine.contains('END DSA PRIVATE KEY')
            lines.remove(0)
            lines.remove(lines.size() - 1)
            String string = lines.join('')
            byte[] decodeBase64 = Base64.decodeBase64(string)
            if (type == null) {
                type = KeyType.DSA.name()
            }
            return loadPrivateKey(decodeBase64, type, provider)
        }
        if (firstLine.contains('BEGIN PUBLIC KEY')) {
            String lastLine = lines.last()
            assert lastLine.contains('END PUBLIC KEY')
            lines.remove(0)
            lines.remove(lines.size() - 1)
            String string = lines.join('')
            byte[] decodeBase64 = Base64.decodeBase64(string)
            return loadPublicKey(decodeBase64, type, provider)
        }
        throw new Exception("unkown type")
    }

    static String buildBeginString(String keyType,boolean begin){
        String fistw = begin?"BEGIN":"END"
//        String privateKS = privateK?"PRIVATE":"PUBLIC"
        return "${fistw} ${keyType} PRIVATE KEY"
    }

    static String savePrivateKey(PrivateKey key){
        String mid = new String(Base64.encodeBase64(key.getEncoded()))
        String dashDash = '-----'
        return dashDash+buildBeginString(key.getAlgorithm(),true)+dashDash+'\n'+mid+'\n'+dashDash+buildBeginString(key.getAlgorithm(),false)+dashDash;
    }

    static PrivateKey loadPrivateKey(byte[] keyBytes, String keyType, Provider provider) {
        if (keyType == null) {
            throw new Exception("set key keyType")
        }
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf;
        if (provider == null) {
            kf = KeyFactory.getInstance(keyType);
        } else {
            kf = KeyFactory.getInstance(keyType, provider);
        }
        PrivateKey privateKey = kf.generatePrivate(spec);
        return privateKey;
    }

    static PublicKey loadPublicKey(byte[] keyBytes, String type, Provider provider) {
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory kf;
        if (provider == null) {
            kf = KeyFactory.getInstance(type);
        } else {
            kf = KeyFactory.getInstance(type, provider);
        }
        PublicKey publicKey = kf.generatePublic(spec);
        return publicKey;
    }

    static byte[] saveX509Certificate(X509Certificate cert1) {
        return cert1.getEncoded()
    }

    static X509Certificate loadX509Certificate(byte[] bytes) {
        return loadX509Certificate2(bytes, JceProviderType.SUN);
    }

    public static String X509 = "X.509"

    static X509Certificate loadX509Certificate2(byte[] bytes, JceProviderType jceProviderType) {
        CertificateFactory fact = CertificateFactory.getInstance(X509, jceProviderType.createProvider());
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes)
        X509Certificate certificate = fact.generateCertificate(bais) as X509Certificate;
        bais.close()
        return certificate
    }


    static Provider[] receiveProviders() {
        return Security.getProviders();
    }


    public static Map<String, Set<String>> securityServices;

    public static Map<String, Set<String>> getSecurityServices() {
        if (securityServices != null) {
            return securityServices;
        }
        final Map<String, Set<String>> result = new TreeMap();
        final Provider[] providers = Security.getProviders();
        for (final Provider provider : providers) {
            final Set<String> keys = (Set) provider.keySet();
            for (String key : keys) {
                key = key.split(" ")[0];
                if (key.startsWith("Alg.Alias.")) {
                    key = key.substring(10);
                }
                final int ix = key.indexOf('.');
                final String name = key.substring(0, ix);
                try {
                    final Set<String> list = (Set) JrrUtilitiesMapBuilder.buildObject(result, name, JrrUtilitiesMapBuilder.constructorTreeSet);
                    final String value = key.substring(ix + 1);
                    list.add(value);
                } catch (final Exception e) {
                    throw new Error(e);
                }
            }
        }
        securityServices = result;
        return result;
    }


}
