package net.sf.jremoterun.utilities.nonjdk.sshsup

import groovy.transform.CompileStatic

@CompileStatic
interface SshConnectionDetailsI {

    String getHost();
    int getPort()
    String getUser()
    SshPasswordReceiverI getPasswordReceiver()
    File getSshKey()
    File getKnownHosts()
    boolean isShowMessage()

    SshConnectionDetailsI clone2()

}
