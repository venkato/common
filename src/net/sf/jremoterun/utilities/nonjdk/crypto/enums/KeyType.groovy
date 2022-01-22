package net.sf.jremoterun.utilities.nonjdk.crypto.enums

import groovy.transform.CompileStatic

import java.security.Key;


@CompileStatic
enum KeyType {

    RSA,
    // you can restore public key ( many keys) by private key
    DSA,
    // works  ??
    ECDSA,
    ;


    static KeyType fromKey(Key key){
        return KeyType.valueOf( key.getAlgorithm())
    }
}