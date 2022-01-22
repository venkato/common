package net.sf.jremoterun.utilities.nonjdk.crypto

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.FileRotate
import net.sf.jremoterun.utilities.nonjdk.crypto.enums.JceProviderType
import net.sf.jremoterun.utilities.nonjdk.crypto.enums.KeyStoreType
import net.sf.jremoterun.utilities.nonjdk.fileloayout.Java8Files
import net.sf.jremoterun.utilities.nonjdk.io.JrrIoUtils
import org.apache.commons.lang3.SystemUtils

import java.security.KeyStore
import java.security.Principal
import java.security.cert.Certificate
import java.security.cert.X509Certificate;
import java.util.logging.Logger;

@CompileStatic
class CertificateChecker {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    @Deprecated
    public static String customTrustedStorePath = net.sf.jremoterun.utilities.nonjdk.javalangutils.PropsEnum.javax_net_ssl_trustStore.customName;
// 'javax.net.ssl.trustStore'
    public static String defaultJksPassword = 'changeit'
    public KeyStore keyStore1;
    public HashSet<String> ignoreRootCertDup =[]

    /**
     * see more props in
     * @see org.apache.http.impl.client.HttpClientBuilder
     */
    CertificateChecker() {
        this(loadDefaultKetStoreJks(getDefaultJavaCertPath(), defaultJksPassword))
    }


    CertificateChecker(KeyStore keyStore) {
        this.keyStore1 = keyStore
        if (keyStore == null) {
            throw new NullPointerException('key store is null')
        }
    }

    static File getDefaultJavaCertPath() {
        File javaHome = SystemUtils.getJavaHome()
        File file = new File(javaHome, Java8Files.certificatesWhite.customName)
        assert file.exists()
        return file
    }

    static KeyStore loadDefaultKetStoreJks(File file, String password) {
        return loadDefaultKetStore(file, password, KeyStoreType.JKS, null);
    }

    static KeyStore loadDefaultKetStore(File file, String password, KeyStoreType keyStoreType, JceProviderType jceProviderType) {
        assert file.exists()
        KeyStore keyStore
        if (jceProviderType == null) {
            keyStore = KeyStore.getInstance(keyStoreType.getCustomName())
        } else {
            keyStore = KeyStore.getInstance(keyStoreType.getCustomName(), jceProviderType.createProvider())
        }
        BufferedInputStream inputStream = file.newInputStream()
        try {
            keyStore.load(inputStream, password.toCharArray())
        } finally {
            JrrIoUtils.closeQuietly2(inputStream, log)
        }
        return keyStore;
    }


    static KeyStore loadWindowsBuildInKetStore(KeyStoreType keyStoreType) {
        KeyStore keyStore = KeyStore.getInstance(keyStoreType.getCustomName())
        keyStore.load((KeyStore.LoadStoreParameter) null);
        return keyStore;
    }

    void addCertificate(X509Certificate certificate) {
        String commonName = getCommonName(certificate.getSubjectDN())
        keyStore1.setCertificateEntry(commonName, certificate);
    }

    void saveKeyStore(File file, String password) {
        FileRotate.rotateFile(file, 20)
        DataOutputStream outputStream = file.newDataOutputStream()
        try {
            keyStore1.store(outputStream, password.toCharArray())
            outputStream.flush()
        } finally {
            JrrIoUtils.closeQuietly2(outputStream, log)
        }

    }


    X509Certificate getCertificateFromKeyStoreByName(String neededCommonName) {
        X509Certificate matched;
        List<String> aliases = keyStore1.aliases().toList();
        aliases.each {
            try {
                Certificate certificate1 = keyStore1.getCertificate(it)
                if (certificate1 instanceof X509Certificate) {
                    X509Certificate x509Certificate = (X509Certificate) certificate1;
                    if (isGoodCertificate(it, x509Certificate, neededCommonName)) {
                        if (matched == null) {
                            matched = x509Certificate;
                        } else {
                            if(ignoreRootCertDup.contains(neededCommonName)){

                            }else {
                                foundDupCertificate(neededCommonName, matched, x509Certificate)
                            }
                        }
                    }
                }
            } catch (Exception e) {
                log.info "failed iterate over ${it} : ${e}"
                throw e;
            }

        }
        return matched;
    }

    void foundDupCertificate(String neededCommonName, X509Certificate matched, X509Certificate certificate1) {
        throw new Exception("found many matched certificates with name=${neededCommonName} : \n ${certificate1} , \n matched=${matched}")
    }

    boolean isGoodCertificate(String alias, X509Certificate certificate, String neededCommonName) {
        if (certificate == null) {
            return false
        }

        X509Certificate x509Certificate = (X509Certificate) certificate;
        Principal subjectDN = x509Certificate.getSubjectDN();
        String commonName1 = getCommonName(subjectDN)
        return commonName1 == neededCommonName
    }


    static byte[] saveCertificate(X509Certificate certificate) {
        return certificate.getEncoded()
    }


    void checkCertificate(X509Certificate certificate) {
        String issuerDn = getCommonName(certificate.getIssuerDN())
        X509Certificate certCheckBy = getCertificateFromKeyStoreByName(issuerDn)
        if (certCheckBy == null) {
            throw new Exception("certificate not found : ${issuerDn}")
        }
        verifyCertificateByParent(certificate, certCheckBy)
    }


    String getCommonName(Principal subjectDN) {
        String string = subjectDN.toString();
        if (string == null) {
            return null
        }
        String cn;
        List<String> tokenizeL = string.tokenize(',')
        tokenizeL.each {
            String el1 = it;
            if (el1.contains('=')) {
                List<String> sepp = el1.trim().tokenize('=')
                if (sepp.size() == 2 && sepp[0] == 'CN') {
                    String cnValue = sepp[1]
                    if (cn == null) {
                        cn = cnValue
                    } else {
                        if (cn != cnValue) {
                            throw new Exception("duplicated cn : ${cnValue} , ${cn} ")
                        }
                    }
                }
            }
        }
        return cn;
    }


    void verifyCertificateByParent(X509Certificate child1, X509Certificate parent1) {
        child1.verify(parent1.getPublicKey())
    }
}
