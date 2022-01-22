package net.sf.jremoterun.utilities.nonjdk.sshsup

import groovy.transform.CompileStatic

@CompileStatic
interface SshPasswordReceiverI {

    String readPassword(SshConSet2 sett)

    boolean isPasswordSet();


}
