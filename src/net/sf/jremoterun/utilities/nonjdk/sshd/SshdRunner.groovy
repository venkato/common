package net.sf.jremoterun.utilities.nonjdk.sshd

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.groovystarter.JrrStarterConstatnts
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.starter.JrrStarterOsSpecificFiles
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.starter.JrrStarterOsSpecificFilesFirstDownload
import net.sf.jremoterun.utilities.nonjdk.sftploader.JrrDownloadFilesLayout
import net.sf.jremoterun.utilities.nonjdk.shell.GroovySehllSshServiceSettings
import org.apache.commons.lang3.SystemUtils
import org.apache.sshd.common.util.GenericUtils
import org.apache.sshd.scp.server.ScpCommandFactory
import org.apache.sshd.server.SshServer
import org.apache.sshd.server.auth.password.PasswordAuthenticator
import org.apache.sshd.server.auth.password.PasswordChangeRequiredException
import org.apache.sshd.server.auth.pubkey.PublickeyAuthenticator
import org.apache.sshd.server.channel.ChannelSession
import org.apache.sshd.server.command.Command
import org.apache.sshd.server.config.keys.AuthorizedKeysAuthenticator
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider
import org.apache.sshd.server.session.ServerSession
import org.apache.sshd.server.shell.ProcessShellFactory
import org.apache.sshd.sftp.server.SftpFileSystemAccessor
import org.apache.sshd.sftp.server.SftpSubsystem
import org.apache.sshd.sftp.server.SftpSubsystemFactory

import java.nio.file.Path
import java.nio.file.Paths;
import java.util.logging.Logger;

@CompileStatic
class SshdRunner {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();
    public SshServer sshd = SshServer.setUpDefaultServer();

//    public PasswordAuthenticator passwordAuthenticator;
//    public PublickeyAuthenticator publickeyAuthenticator
    public int port
//    public File hostKey

    public File deafultDirr

    SshdRunner(int port, File deafultDirr) {
//        this.passwordAuthenticator = passwordAuthenticator
        this.port = port
        this.deafultDirr = deafultDirr
        File jrrdir = new File(deafultDirr,JrrDownloadFilesLayout.jrrfiles.customName);
//        File privateKey = new File(jrrdir,   JrrDownloadFilesLayout.privateKey.customName)
        File customLoader = new File(jrrdir,  JrrStarterConstatnts.rawConfiGrHomeFileName)
//        assert privateKey.exists()
        assert customLoader.exists()
    }

    void setPublickeyAuthenticator(File f) {
        sshd.setPublickeyAuthenticator(new AuthorizedKeysAuthenticator(f.toPath()))
    }

    void setKeyPairProvider(){
        SimpleGeneratorHostKeyProvider hostKeyProvider = new SimpleGeneratorHostKeyProvider()
        hostKeyProvider.setAlgorithm(org.apache.sshd.common.config.keys.KeyUtils.RSA_ALGORITHM)
        sshd.setKeyPairProvider(hostKeyProvider);
    }


    SshServer runSshServer() {
        GroovySehllSshServiceSettings.setSshProps()
        sshd.port = port
//        hostKey.parentFile.mkdir()
//        assert hostKey.parentFile.exists()
        setKeyPairProvider()
        SftpFileSystemAccessor fileSystemAccessor = new SftpFileSystemAccessor() {
            @Override
            public String toString() {
                return SftpFileSystemAccessor.class.getSimpleName() + "[DEFAULT]";
            }
        }
        SftpSubsystemFactory factory = new SftpSubsystemFactory() {
            @Override
            Command createSubsystem(ChannelSession channel) throws IOException {
                return createSubsystem1(channel, this);
            }
        }
        factory.setFileSystemAccessor(fileSystemAccessor)
        sshd.setSubsystemFactories([factory])
        sshd.start();
        log.info "ssh server started"
        return sshd
    }

    SftpSubsystemJrr createSubsystem1(ChannelSession channel, SftpSubsystemFactory factory) {
        SftpSubsystemJrr subsystem = new SftpSubsystemJrr(channel, factory);
        subsystem.setDefaultDirectory(deafultDirr.toPath())
        GenericUtils.forEach(factory.getRegisteredListeners(), subsystem::addSftpEventListener);
        return subsystem;
    }


}
