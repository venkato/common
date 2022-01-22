package net.sf.jremoterun.utilities.nonjdk.sshsup

import groovy.transform.CompileStatic

@CompileStatic
class SshPasswordReceiverDefault implements SshPasswordReceiverI{

    public SshConSet2 sett2

    SshPasswordReceiverDefault(SshConSet2 sett2) {
        this.sett2 = sett2
    }

    @Override
    String readPassword(SshConSet2 sett) {
        return sett.password
    }

    @Override
    boolean isPasswordSet() {
        return sett2.password!=null
    }
}
