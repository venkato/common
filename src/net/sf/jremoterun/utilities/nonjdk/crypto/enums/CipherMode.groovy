package net.sf.jremoterun.utilities.nonjdk.crypto.enums

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils

import javax.crypto.Cipher;
import java.util.logging.Logger;

/**
 * @see cn.hutool.crypto.CipherMode
 */
@CompileStatic
enum CipherMode {


    encrypt(Cipher.ENCRYPT_MODE),
    wrap(Cipher.WRAP_MODE),
    unwrap(Cipher.UNWRAP_MODE),
    decrypt(Cipher.DECRYPT_MODE);


    int javaMode;

    CipherMode(int javaMode) {
        this.javaMode = javaMode
    }
}
