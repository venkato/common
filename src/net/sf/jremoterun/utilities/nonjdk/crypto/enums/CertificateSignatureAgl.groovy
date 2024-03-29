package net.sf.jremoterun.utilities.nonjdk.crypto.enums

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.enumutils.EnumNameProvider;

import java.util.logging.Logger;

/**
 * @see org.bouncycastle.operator.DefaultSignatureAlgorithmIdentifierFinder
 */
@CompileStatic
enum CertificateSignatureAgl implements EnumNameProvider{

    MD2WITHRSAENCRYPTION,
    MD2WITHRSA , 
    MD5WITHRSAENCRYPTION , 
    MD5WITHRSA , 
    SHA1WITHRSAENCRYPTION , 
    SHA1WITHRSA , 
    SHA224WITHRSAENCRYPTION , 
    SHA224WITHRSA , 
    SHA256WITHRSAENCRYPTION , 
    SHA256WITHRSA , 
    SHA384WITHRSAENCRYPTION , 
    SHA384WITHRSA , 
    SHA512WITHRSAENCRYPTION , 
    SHA512WITHRSA , 
//    SHA512(224)WITHRSAENCRYPTION ,
//    SHA512(224)WITHRSA ,
//    SHA512(256)WITHRSAENCRYPTION ,
//    SHA512(256)WITHRSA ,
    SHA1WITHRSAANDMGF1 , 
    SHA224WITHRSAANDMGF1 , 
    SHA256WITHRSAANDMGF1 , 
    SHA384WITHRSAANDMGF1 , 
    SHA512WITHRSAANDMGF1 , 
    SHA3_224WITHRSAANDMGF1 ,
    SHA3_256WITHRSAANDMGF1 ,
    SHA3_384WITHRSAANDMGF1 ,
    SHA3_512WITHRSAANDMGF1 ,
    RIPEMD160WITHRSAENCRYPTION , 
    RIPEMD160WITHRSA , 
    RIPEMD128WITHRSAENCRYPTION , 
    RIPEMD128WITHRSA , 
    RIPEMD256WITHRSAENCRYPTION , 
    RIPEMD256WITHRSA , 
    SHA1WITHDSA , 
    DSAWITHSHA1 , 
    SHA224WITHDSA , 
    SHA256WITHDSA , 
    SHA384WITHDSA , 
    SHA512WITHDSA , 
    SHA3_224WITHDSA ,
    SHA3_256WITHDSA ,
    SHA3_384WITHDSA ,
    SHA3_512WITHDSA ,
    SHA3_224WITHECDSA ,
    SHA3_256WITHECDSA ,
    SHA3_384WITHECDSA ,
    SHA3_512WITHECDSA ,
    SHA3_224WITHRSA ,
    SHA3_256WITHRSA ,
    SHA3_384WITHRSA ,
    SHA3_512WITHRSA ,
    SHA3_224WITHRSAENCRYPTION ,
    SHA3_256WITHRSAENCRYPTION ,
    SHA3_384WITHRSAENCRYPTION ,
    SHA3_512WITHRSAENCRYPTION ,
    SHA1WITHECDSA , 
    ECDSAWITHSHA1 , 
    SHA224WITHECDSA , 
    SHA256WITHECDSA , 
    SHA384WITHECDSA , 
    SHA512WITHECDSA , 
    GOST3411WITHGOST3410 , 
    GOST3411WITHGOST3410_94 ,
    GOST3411WITHECGOST3410 , 
    GOST3411WITHECGOST3410_2001 ,
    GOST3411WITHGOST3410_2001 ,
    GOST3411WITHECGOST3410_2012_256 ,
    GOST3411WITHECGOST3410_2012_512 ,
    GOST3411WITHGOST3410_2012_256 ,
    GOST3411WITHGOST3410_2012_512 ,
    GOST3411_2012_256WITHECGOST3410_2012_256 ,
    GOST3411_2012_512WITHECGOST3410_2012_512 ,
    GOST3411_2012_256WITHGOST3410_2012_256 ,
    GOST3411_2012_512WITHGOST3410_2012_512 ,

    SHA1WITHCVC_ECDSA ,
    SHA224WITHCVC_ECDSA ,
    SHA256WITHCVC_ECDSA ,
    SHA384WITHCVC_ECDSA ,
    SHA512WITHCVC_ECDSA ,
    SHA3_512WITHSPHINCS256 ,
    SHA512WITHSPHINCS256 , 

    SHA1WITHPLAIN_ECDSA ,
    RIPEMD160WITHPLAIN_ECDSA ,
    SHA224WITHPLAIN_ECDSA ,
    SHA256WITHPLAIN_ECDSA ,
    SHA384WITHPLAIN_ECDSA ,
    SHA512WITHPLAIN_ECDSA ,
    SHA3_224WITHPLAIN_ECDSA ,
    SHA3_256WITHPLAIN_ECDSA ,
    SHA3_384WITHPLAIN_ECDSA ,
    SHA3_512WITHPLAIN_ECDSA ,

    ED25519 , 
    ED448 , 

    // RFC 8702
    SHAKE128WITHRSAPSS , 
    SHAKE256WITHRSAPSS , 
    SHAKE128WITHRSASSA_PSS ,
    SHAKE256WITHRSASSA_PSS ,
    SHAKE128WITHECDSA , 
    SHAKE256WITHECDSA , 

//        RIPEMD160WITHSM2 , 
//        SHA1WITHSM2 , 
//        SHA224WITHSM2 , 
    SHA256WITHSM2 , 
//        SHA384WITHSM2 , 
//        SHA512WITHSM2 , 
    SM3WITHSM2 , 

    SHA256WITHXMSS , 
    SHA512WITHXMSS , 
    SHAKE128WITHXMSS , 
    SHAKE256WITHXMSS , 

    SHA256WITHXMSSMT , 
    SHA512WITHXMSSMT , 
    SHAKE128WITHXMSSMT , 
    SHAKE256WITHXMSSMT , 

    SHA256WITHXMSS_SHA256 ,
    SHA512WITHXMSS_SHA512 ,
    SHAKE128WITHXMSS_SHAKE128 ,
    SHAKE256WITHXMSS_SHAKE256 ,

    SHA256WITHXMSSMT_SHA256 ,
    SHA512WITHXMSSMT_SHA512 ,
    SHAKE128WITHXMSSMT_SHAKE128 ,
    SHAKE256WITHXMSSMT_SHAKE256 ,

    LMS , 

    XMSS , 
    XMSS_SHA256 ,
    XMSS_SHA512 ,
    XMSS_SHAKE128 ,
    XMSS_SHAKE256 ,

    XMSSMT , 
    XMSSMT_SHA256 ,
    XMSSMT_SHA512 ,
    XMSSMT_SHAKE128 ,
    XMSSMT_SHAKE256 ,

    QTESLA_P_I ,
    QTESLA_P_III ,
    
    ;

    String customName;

    CertificateSignatureAgl() {
        this.customName = name().replace('_','-')
    }
}
