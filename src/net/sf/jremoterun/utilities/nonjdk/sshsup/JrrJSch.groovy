package net.sf.jremoterun.utilities.nonjdk.sshsup

import com.jcraft.jsch.JSch
import com.jcraft.jsch.JSchException
import com.jcraft.jsch.JrrKnowHostOriginal
import com.jcraft.jsch.KnownHosts
import com.jcraft.jsch.Session
import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

@CompileStatic
class JrrJSch extends JSch{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    JrrJSch() {
        JrrClassUtils.setFieldValue(this,'known_hosts',new JrrJrrKnowHost(this))
    }

    public volatile JrrJschSession lastSession

    public static String server_host_keyS = 'server_host_key'

    /**
     * https://github.com/apache/mina-sshd
     */
    static boolean addUnsafeRsaKeys(){
        String config1 = getConfig(server_host_keyS)
        List<String> tokenize1 = config1.tokenize(',')
        //ssh-rsa,ssh-dss
        boolean added = false
        if(!tokenize1.contains('ssh-rsa')){
            config1+=',ssh-rsa'
            added = true
        }
        if(!tokenize1.contains('ssh-dss')){
            config1+=',ssh-dss'
            added = true
        }
        setConfig(server_host_keyS,config1)
        return added
    }

    @Override
    JrrJschSession getSession(String username, String host, int port) throws JSchException {
        assert host!=null
        JrrJschSession s = new JrrJschSession(this, username, host, port);
        lastSession = s
        return s
    }

    @Override
    protected void addSession(Session session) {
        log.info "connection established : ${session.getHost()} ${session.getPort()}"
        JrrJschSession s = session as JrrJschSession
        lastSession = s
        s.onConnected();
        super.addSession(session)
    }



}
