package net.sf.jremoterun.utilities.nonjdk.sshsup

import com.jcraft.jsch.Session
import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils

import java.util.logging.Logger

@CompileStatic
class SshConSetNoAwt extends SshConSet3 implements Closeable {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    public JcraftConnectopnOpener jcraftConnectopnOpener

    void close() {
        super.close()
        if(jcraftConnectopnOpener!=null){
            if(jcraftConnectopnOpener.session!=null){
                jcraftConnectopnOpener.session.disconnect()
            }
        }
//        jcraftConnectopnOpener.close()
    }

    SshConSetNoAwt clone2(){
        SshConSetNoAwt clone9 = new SshConSetNoAwt()
        cloneTo(clone9)
        return clone9
    }


    public Session getJcraftSession() {
        assert jcraftConnectopnOpener != null;
        Session session = jcraftConnectopnOpener.session
        assert session != null
        return session
    }

    void checkConnectAndCreateIfNeeded() {
        if (jcraftConnectopnOpener == null) {
            createJcraftConnection()
        } else {
            if (jcraftConnectopnOpener.session.isConnected()) {

            } else {
                log.info "not connected, reconnecting to ${host} ${user}"
                jcraftConnectopnOpener = null
                checkConnectAndCreateIfNeeded()
                if (sftpUtils != null) {
                    if (sftpUtils.sftp != null) {
                        sftpUtils.sftp = null
                        createJcrftSftp(sftpUtils)
                    }
                }

            }
        }
        super.checkConnectAndCreateIfNeeded()
    }

    JcraftConnectopnOpener createJcraftConnection() {
        if (jcraftConnectopnOpener != null) {
            return jcraftConnectopnOpener;
        }
        if (sshKey != null) {
            assert sshKey.exists()
        } else {
            passwordReceiver!=null
            assert passwordReceiver.isPasswordSet()
        }
        assert host != null
        SshConSetNoAwt thisObj = this;
        jcraftConnectopnOpener = new JcraftConnectopnOpener()
        jcraftConnectopnOpener.conSet2 = this;
        jcraftConnectopnOpener.init()
        return jcraftConnectopnOpener
    }


}
