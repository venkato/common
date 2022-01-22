package net.sf.jremoterun.utilities.nonjdk.crypto

import groovy.transform.CompileStatic;


@CompileStatic
enum KeyType {

    RSA,
    // you can restore public key ( many keys) by private key
    DSA,
    // works  ??
    ECDSA,
    ;
}