package net.sf.jremoterun.utilities.nonjdk.sshsup.hierynomus

import groovy.transform.CompileStatic
import net.schmizz.sshj.SSHClient
import net.schmizz.sshj.common.Message
import net.schmizz.sshj.common.SSHException
import net.schmizz.sshj.common.SSHPacket
import net.schmizz.sshj.common.SSHPacketHandler;
import net.sf.jremoterun.utilities.JrrClassUtils

import java.util.logging.Level;
import java.util.logging.Logger;

@CompileStatic
class SSHPacketHandlerLog implements SSHPacketHandler {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public SSHClientJrr sshClient
    public CompressionLog compressionLog

    SSHPacketHandlerLog(SSHClientJrr sshClient, CompressionLog compressionLog) {
        this.sshClient = sshClient
        this.compressionLog = compressionLog
    }


    /**
     * https://datatracker.ietf.org/doc/html/draft-ssh-ext-info-04
     * @see com.jcraft.jsch.Session* @see com.github.mwiede/jsch/sources/jsch-0.2.4-sources.jar!/com/jcraft/jsch/Session.java:1327
     */
    @Override
    void handle(Message msg, SSHPacket buf) throws SSHException {
        if (!sshClient.isAuthenticated()) {
            compressionLog.selfSet()
            int rpos1 = buf.rpos()
            String uu = new String(buf.array(), rpos1, buf.available())
            log.info "got ${msg} ${msg.toByte()} : ${uu}"
            if (msg == Message.EXT_INFO) {
                try {
//                log.info "rpos1 = ${rpos1}"
//                log.info2 buf.readUInt32()
                    long num1 = buf.readUInt32()
//                    log.info "num  = ${num1}"
                    for (long i = 0; i < num1; i++) {
                        String ext_name = buf.readString()
                        String ext_value = buf.readString()
                        sshClient.customServerProps.put(ext_name, ext_value)
                    }
                } catch (Exception e) {
                    log.log(Level.FINE, "failed parse ${uu}", e)
                }
                buf.rpos(rpos1)
            }
        }
    }
}
