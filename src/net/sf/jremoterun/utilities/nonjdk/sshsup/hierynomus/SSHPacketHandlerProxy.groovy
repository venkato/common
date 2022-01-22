package net.sf.jremoterun.utilities.nonjdk.sshsup.hierynomus

import groovy.transform.CompileStatic
import net.schmizz.sshj.common.Message
import net.schmizz.sshj.common.SSHException
import net.schmizz.sshj.common.SSHPacket
import net.schmizz.sshj.common.SSHPacketHandler
import net.schmizz.sshj.transport.Transport
import net.schmizz.sshj.transport.TransportImpl;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef;

import java.util.logging.Logger;

@CompileStatic
class SSHPacketHandlerProxy implements SSHPacketHandler{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public SSHPacketHandler before ;
    public TransportImpl transport;

    SSHPacketHandlerProxy(TransportImpl transport) {
        this.transport = transport
    }

    void selfSet(){
        Object decoder = JrrClassUtils.getFieldValue(transport,'decoder');
        JrrClassUtils.setFieldValueR(new ClRef('net.schmizz.sshj.transport.Decoder'), decoder,'packetHandler',this)
    }

    @Override
    void handle(Message msg, SSHPacket buf) throws SSHException {
        if(before!=null){
            before.handle(msg,buf);
        }
        transport.handle(msg,buf)
    }
}
