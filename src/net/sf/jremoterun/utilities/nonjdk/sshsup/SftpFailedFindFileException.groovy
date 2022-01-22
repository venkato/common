package net.sf.jremoterun.utilities.nonjdk.sshsup

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

@CompileStatic
class SftpFailedFindFileException extends Exception{


    SftpFailedFindFileException() {
    }

    SftpFailedFindFileException(String var1) {
        super(var1)
    }

    SftpFailedFindFileException(String var1, Throwable var2) {
        super(var1, var2)
    }

    SftpFailedFindFileException(Throwable var1) {
        super(var1)
    }

    SftpFailedFindFileException(String var1, Throwable var2, boolean var3, boolean var4) {
        super(var1, var2, var3, var4)
    }
}
