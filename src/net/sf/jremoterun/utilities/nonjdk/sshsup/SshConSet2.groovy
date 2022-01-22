package net.sf.jremoterun.utilities.nonjdk.sshsup

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import org.apache.commons.lang3.SystemUtils

import java.util.logging.Logger

@CompileStatic
class SshConSet2 implements SshConnectionDetailsI{

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static int defaultPort = 22;

    public static SshPasswordReceiverI defaultPasswordReceiver ;

    public static String defaultUser = SystemUtils.USER_NAME;
    public static File defaultSshKey = new File(SystemUtils.USER_HOME + "/.ssh/id_rsa");
    public static File defaultKnownHosts = new File(SystemUtils.USER_HOME + '/.ssh/known_hosts'); ;
    static {
        if (!defaultSshKey.exists()) {
            defaultSshKey = null
        }
        if (!defaultKnownHosts.exists()) {
            defaultKnownHosts = null
        }
    }


    public boolean showMessage5 = true
    public String host5;
    public int port5 = defaultPort;
    public String user5 = defaultUser
    public SshPasswordReceiverI passwordReceiver5 = defaultPasswordReceiver
    public File sshKey5 = defaultSshKey
    public File knownHosts5 = defaultKnownHosts

    @Override
    String toString() {
        boolean pwdSet = passwordReceiver!=null &&passwordReceiver.isPasswordSet()
        return "${user}@${host} , sshkey = ${sshKey}, password set ? ${pwdSet}, ${knownHosts}"
    }

    boolean isShowMessage() {
        return showMessage5
    }

    void setShowMessage(boolean showMessage) {
        this.showMessage5 = showMessage
    }

    String getHost() {
        return host5
    }

    void setHost(String host) {
        this.host5 = host
    }

    int getPort() {
        return port5
    }

    void setPort(int port) {
        this.port5 = port
    }

    String getUser() {
        return user5
    }

    void setUser(String user) {
        this.user5 = user
    }

    SshPasswordReceiverI getPasswordReceiver() {
        return passwordReceiver5
    }

    void setPasswordReceiver(SshPasswordReceiverI passwordReceiver) {
        this.passwordReceiver5 = passwordReceiver
    }

    File getSshKey() {
        return sshKey5
    }

    void setSshKey(File sshKey) {
        this.sshKey5 = sshKey
    }

    File getKnownHosts() {
        return knownHosts5
    }

    void setKnownHosts(File knownHosts) {
        this.knownHosts5 = knownHosts
    }

    SshConSet2 clone2(){
        SshConSet2 conSet2=new SshConSet2();
        cloneTo(conSet2)
        return conSet2
    }

    void cloneTo(SshConSet2 conSet2){
        conSet2.host = host
        conSet2.user = user
        conSet2.port = port
        conSet2.passwordReceiver = passwordReceiver
        conSet2.sshKey = sshKey
        conSet2.knownHosts = knownHosts
    }



}
