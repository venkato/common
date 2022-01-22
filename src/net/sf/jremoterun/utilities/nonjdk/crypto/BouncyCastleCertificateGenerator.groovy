package net.sf.jremoterun.utilities.nonjdk.crypto

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.nonjdk.crypto.enums.CertificateSignatureAgl
import net.sf.jremoterun.utilities.nonjdk.crypto.enums.JceProviderType
import net.sf.jremoterun.utilities.nonjdk.crypto.enums.KeyStoreType
import net.sf.jremoterun.utilities.nonjdk.crypto.enums.KeyType
import net.sf.jremoterun.utilities.nonjdk.dateutils.DurationConstants
import org.bouncycastle.asn1.ASN1Encodable
import org.bouncycastle.asn1.DERSequence
import org.bouncycastle.asn1.x500.X500Name
import org.bouncycastle.asn1.x509.BasicConstraints
import org.bouncycastle.asn1.x509.Extension
import org.bouncycastle.asn1.x509.GeneralName
import org.bouncycastle.asn1.x509.KeyUsage
import org.bouncycastle.cert.X509CertificateHolder
import org.bouncycastle.cert.X509v3CertificateBuilder
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder
import org.bouncycastle.jcajce.provider.asymmetric.rsa.KeyPairGeneratorSpi
import org.bouncycastle.operator.ContentSigner
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder
import org.bouncycastle.pkcs.PKCS10CertificationRequest
import org.bouncycastle.pkcs.PKCS10CertificationRequestBuilder
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequestBuilder
import org.bouncycastle.util.io.pem.PemObject
import org.bouncycastle.util.io.pem.PemReader

import java.security.*
import java.security.cert.X509Certificate

@CompileStatic
public class BouncyCastleCertificateGenerator {

    public Date startDate = new Date(System.currentTimeMillis() - DurationConstants.oneYear.timeInMsLong)
    public Date endDate = new Date(System.currentTimeMillis() + DurationConstants.oneYear.timeInMsLong*20);
    public static final String SIGNATURE_ALGORITHM = CertificateSignatureAgl.SHA256WITHRSA.customName;
//    public static final String SIGNATURE_ALGORITHM = CertificateSignatureAgl.SHA256withRSA;
    public X500Name issuedCertSubject = new X500Name("CN=127.0.0.1");
    public JceProviderType jceProviderType = JceProviderType.BC;

    public KeyPairGenerator keyPairGenerator = new KeyPairGeneratorSpi();

    public String keyPassword = ''
    public String entryAlias = 'key1'
    public PrivateKey rootPrivateKey;
    public KeyPair issuedCertKeyPair;

    KeyStoreType keyStoreType = KeyStoreType.BCFKS

    public X509Certificate rootCert;
    public X509Certificate issuedCert;
    boolean addLocalNames = true

    public List<GeneralName> namesss = [new GeneralName(GeneralName.dNSName, "localhost"),
                                        new GeneralName(GeneralName.iPAddress, "127.0.0.1")
    ]


    BigInteger generateSerialNumber() {
        return new BigInteger(Long.toString(new SecureRandom().nextLong()));
    }

    void readRootCertificate(File certFile) {
        rootCert = net.sf.jremoterun.utilities.nonjdk.crypto.AsymetricKeyLoader3.loadX509Certificate2(certFile.bytes, jceProviderType)
    }

    void readRootPrivateKeyCertificate(File privateKeyF, KeyType keyType) {
        if (keyType == null) {
            keyType = KeyType.fromKey(rootCert.getPublicKey())
        }
        rootPrivateKey = AsymetricKeyLoader3.loadKey(privateKeyF.bytes, keyType.name(), jceProviderType.createProvider()) as PrivateKey
    }


    BouncyCastleCertificateGenerator() {
        keyPairGenerator.initialize(1024);
    }

    void createSelfSigned(String cn, boolean addHost) {
        KeyPair rootKeyPair = keyPairGenerator.generateKeyPair();
        rootPrivateKey = rootKeyPair.getPrivate()
        BigInteger rootSerialNum = generateSerialNumber();

        X500Name rootCertIssuer = new X500Name("CN=" + cn);
        ContentSigner rootCertContentSigner = new JcaContentSignerBuilder(SIGNATURE_ALGORITHM).setProvider(jceProviderType.createProvider()).build(rootKeyPair.getPrivate());
        X509v3CertificateBuilder rootCertBuilder = new JcaX509v3CertificateBuilder(rootCertIssuer, rootSerialNum, startDate, endDate, rootCertIssuer, rootKeyPair.getPublic());
        JcaX509ExtensionUtils rootCertExtUtils = new JcaX509ExtensionUtils();
        rootCertBuilder.addExtension(Extension.basicConstraints, true, new BasicConstraints(true));
        rootCertBuilder.addExtension(Extension.subjectKeyIdentifier, false, rootCertExtUtils.createSubjectKeyIdentifier(rootKeyPair.getPublic()));
        if (addHost) {
            addF2(rootCertBuilder)
        }
        X509CertificateHolder rootCertHolder = rootCertBuilder.build(rootCertContentSigner);
        rootCert = new JcaX509CertificateConverter().setProvider(jceProviderType.createProvider()).getCertificate(rootCertHolder);
        issuedCert = rootCert;
        issuedCertKeyPair = rootKeyPair
    }


    void createCert() throws Exception {
        assert rootCert != null
        assert rootPrivateKey != null
        issuedCertKeyPair = keyPairGenerator.generateKeyPair();
        PKCS10CertificationRequestBuilder p10Builder = new JcaPKCS10CertificationRequestBuilder(issuedCertSubject, issuedCertKeyPair.getPublic());
        JcaContentSignerBuilder csrBuilder = new JcaContentSignerBuilder(SIGNATURE_ALGORITHM).setProvider(jceProviderType.createProvider());
        ContentSigner csrContentSigner = csrBuilder.build(rootPrivateKey);
        PKCS10CertificationRequest csr = p10Builder.build(csrContentSigner);
        String commonNameRoot = new CertificateChecker().getCommonName(rootCert.getSubjectDN());
        X500Name rootCertIssuer = new X500Name("CN=" + commonNameRoot);
        X509v3CertificateBuilder issuedCertBuilder = new X509v3CertificateBuilder(rootCertIssuer, generateSerialNumber(), startDate, endDate, csr.getSubject(), csr.getSubjectPublicKeyInfo());
        addF1(csr, issuedCertBuilder)
        X509CertificateHolder issuedCertHolder = issuedCertBuilder.build(csrContentSigner);
        issuedCert = new JcaX509CertificateConverter().setProvider(jceProviderType.createProvider()).getCertificate(issuedCertHolder);
        doVerify();
    }


    void addF1(PKCS10CertificationRequest csr, X509v3CertificateBuilder issuedCertBuilder) {
        JcaX509ExtensionUtils issuedCertExtUtils = new JcaX509ExtensionUtils();

        // Add Extensions
        // Use BasicConstraints to say that this Cert is not a CA
        issuedCertBuilder.addExtension(Extension.basicConstraints, true, new BasicConstraints(false));

        // Add Issuer cert identifier as Extension
        issuedCertBuilder.addExtension(Extension.authorityKeyIdentifier, false, issuedCertExtUtils.createAuthorityKeyIdentifier(rootCert));
        issuedCertBuilder.addExtension(Extension.subjectKeyIdentifier, false, issuedCertExtUtils.createSubjectKeyIdentifier(csr.getSubjectPublicKeyInfo()));

        // Add intended key usage extension if needed
        addF2(issuedCertBuilder)
    }

    void addF2(X509v3CertificateBuilder issuedCertBuilder) {
        issuedCertBuilder.addExtension(Extension.keyUsage, false, new KeyUsage(KeyUsage.keyEncipherment));

        // Add DNS name is cert is to used for SSL
        issuedCertBuilder.addExtension(Extension.subjectAlternativeName, false, new DERSequence(createNames()));
    }


    ASN1Encodable[] createNames() {
        List<GeneralName> namesss3432 = new ArrayList<>(namesss)
        if (addLocalNames) {
            InetAddress inetAddress = InetAddress.getLocalHost()
            namesss3432.add new GeneralName(GeneralName.dNSName, inetAddress.getHostName())
            namesss3432.add new GeneralName(GeneralName.iPAddress, inetAddress.getHostAddress())
        }
        ASN1Encodable[] aa = namesss3432.toArray(new ASN1Encodable[0])
        return aa
    }


    void doVerify() {
        issuedCert.verify(rootCert.getPublicKey(), jceProviderType.createProvider());
    }


    KeyStore generateKeyStore() throws Exception {
        KeyStore keyStore = KeyStore.getInstance(keyStoreType.getCustomName(), jceProviderType.createProvider());
        keyStore.load(null, null);
        X509Certificate[] chain
        if (issuedCert == rootCert) {
            chain = [issuedCert];
        } else {
            chain = [issuedCert, rootCert];
        }
        keyStore.setKeyEntry(entryAlias, issuedCertKeyPair.getPrivate(), keyPassword.toCharArray(), chain);
        return keyStore;
    }

    static PemObject readCertificateSignRequest(String s){
        PemReader pemObject =new PemReader(new StringReader(s))
        return pemObject.readPemObject()
    }
}