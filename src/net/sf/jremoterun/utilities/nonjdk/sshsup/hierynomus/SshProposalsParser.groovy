package net.sf.jremoterun.utilities.nonjdk.sshsup.hierynomus

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.nonjdk.classpath.refsdef.FieldRef;

import java.util.logging.Logger;

@CompileStatic
class SshProposalsParser {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();
    public static ClRef proposalsClRef = new ClRef('net.schmizz.sshj.transport.Proposal')
    public static FieldRef packetField = new FieldRef(proposalsClRef,'packet')

    static Object parseProposals(byte[] bs){
        net.schmizz.sshj.common.SSHPacket sp = new net.schmizz.sshj.common.SSHPacket(bs)

        Object proposals2 = JrrClassUtils.invokeConstructor(proposalsClRef,sp)
        return proposals2;
    }


}
