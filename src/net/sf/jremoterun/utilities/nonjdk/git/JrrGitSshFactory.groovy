package net.sf.jremoterun.utilities.nonjdk.git

import com.jcraft.jsch.JSch
import com.jcraft.jsch.JSchException
import com.jcraft.jsch.Session
import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.sshsup.JrrJSch
import net.sf.jremoterun.utilities.nonjdk.sshsup.JrrJschSession
import net.sf.jremoterun.utilities.nonjdk.sshsup.JschSshConfigEnum
import org.eclipse.jgit.errors.TransportException
import org.eclipse.jgit.transport.CredentialsProvider
import org.eclipse.jgit.transport.JschConfigSessionFactory
import org.eclipse.jgit.transport.OpenSshConfig
import org.eclipse.jgit.transport.RemoteSession
import org.eclipse.jgit.transport.SshConstants
import org.eclipse.jgit.transport.SshSessionFactory
import org.eclipse.jgit.transport.URIish
import org.eclipse.jgit.util.FS

import java.lang.reflect.Field;
import java.util.logging.Logger;

@CompileStatic
class JrrGitSshFactory extends JschConfigSessionFactory {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static volatile boolean useJrrSsh = false
    public static volatile boolean inited = false
    public static volatile JrrGitSshFactory gitSshFactory;

    static void init() {
        if (inited) {
            log.info("already inited")
        } else {
            inited = true
            gitSshFactory = new JrrGitSshFactory();
            gitSshFactory.init5()
        }
    }

    void init5() {
        setSessionFactory(this)
        JSch.setConfig(JschSshConfigEnum.StrictHostKeyChecking.name(), "no");
        new net.sf.jremoterun.utilities.nonjdk.git.fs.GitFSFactoryJrr().install()
        net.sf.jremoterun.utilities.nonjdk.git.fs.GitFSFactoryJrr.registerLFS()
    }

    public static Field INSTANCEfield

    static Field findInstanceField() {
        Class cccc = SshSessionFactory;
        try {
            cccc = SshSessionFactory.getClassLoader().loadClass('org.eclipse.jgit.transport.SshSessionFactory$DefaultFactory')
        } catch (ClassNotFoundException e) {
            log.debug3('', e)
        }
        return JrrClassUtils.findField(cccc, 'INSTANCE')
    }


    static void setSessionFactory(JschConfigSessionFactory config) {
        if (INSTANCEfield == null) {
            INSTANCEfield = findInstanceField()
        }
        INSTANCEfield.set(null, config)
    }

    static void setOpenSshConfig(JschConfigSessionFactory factory, OpenSshConfig config) {
        JrrClassUtils.setFieldValue(factory, 'config', config)
    }

    static OpenSshConfig createCustomOpenSshConfig2(File baseDir, File configDir) {
        return (OpenSshConfig) JrrClassUtils.invokeConstructor(OpenSshConfig, baseDir, configDir)
    }

    static OpenSshConfig createCustomOpenSshConfig(File baseDir) {
        final File config = new File(new File(baseDir, SshConstants.SSH_DIR), SshConstants.CONFIG);
        return createCustomOpenSshConfig2(baseDir, config)
    }

//    public boolean wrapUserMsg = true;

    @Override
    protected void configure(OpenSshConfig.Host hc, Session session) {
//        if (wrapUserMsg) {
//            UserInfo userInfo = session.getUserInfo()
//            if(userInfo==null){
//
//            }else{
//                UserInfoWrapper userInfoWrapper=new UserInfoWrapper(userInfo)
//                session.setUserInfo(userInfoWrapper)
//            }
//        }else{
//
//        }

    }


//    @Override
//    RemoteSession getSession(URIish uri, CredentialsProvider credentialsProvider, FS fs, int tms) throws TransportException {
//        return super.getSession(uri, credentialsProvider, fs, tms)
//    }

    @Override
    protected JSch getJSch(OpenSshConfig.Host hc, FS fs) throws JSchException {
//        log.info "getting jsch .."
        return super.getJSch(hc, fs)
    }

    @Override
    protected Session createSession(OpenSshConfig.Host hc, String user, String host, int port, FS fs) throws JSchException {
        log.info "useJrrSsh = ${useJrrSsh}"
        if (useJrrSsh) {
            JSch jSch = getJSch(hc, fs);
            JrrJschSession s = new JrrJschSession(jSch, user, host, port);
            return s;
        }
        return super.createSession(hc, user, host, port, fs);
    }

    @Override
    RemoteSession getSession(URIish uri, CredentialsProvider credentialsProvider, FS fs, int tms) throws TransportException {
        RemoteSession session1 = super.getSession(uri, credentialsProvider, fs, tms)
//        if (session instanceof JschSession) {
//            JschSession sessionSsh = (JschSession) session;
//            return new RemoteSessionGitJrr(sessionSsh.disconnect())
//        }
        return session1
    }

    public boolean addDefaultProps = false
    public boolean addDefaultsignature = false

    JrrJSch createJrrJSch() {
        return new JrrJSch();
    }


    @Override
    protected JSch createDefaultJSch(FS fs) throws JSchException {
        log.info "getting jsch .."
        if (useJrrSsh) {
            final JrrJSch jsch = createJrrJSch();
            if (addDefaultsignature) {
                String rsaSig = JSch.getConfig("signature.rsa")
                if (rsaSig != null) {
                    JSch.setConfig(JschSshConfigEnum.ssh_rsa.customName, rsaSig);
                }
                String dssSig = JSch.getConfig("signature.dss")
                if (dssSig != null) {
                    JSch.setConfig(JschSshConfigEnum.ssh_dss.customName, dssSig);
                }
            }
            configureJSch(jsch);
            if (addDefaultProps) {
                addKnownHosts(jsch, fs)
                addIdentities(jsch, fs)
            }
            return jsch;
        } else {
            return super.createDefaultJSch(fs)
        }
    }

    static void addIdentities(JSch jsch, FS fs) {
        JrrClassUtils.invokeJavaMethod(JschConfigSessionFactory, 'identities', jsch, fs);
    }

    static void addKnownHosts(JSch jsch, FS fs) {
        JrrClassUtils.invokeJavaMethod(JschConfigSessionFactory, 'knownHosts', jsch, fs);
    }
}
