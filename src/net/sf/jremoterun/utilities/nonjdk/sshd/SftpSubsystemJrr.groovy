package net.sf.jremoterun.utilities.nonjdk.sshd

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import org.apache.sshd.server.channel.ChannelSession
import org.apache.sshd.sftp.server.SftpFileSystemAccessor
import org.apache.sshd.sftp.server.SftpSubsystem
import org.apache.sshd.sftp.server.SftpSubsystemConfigurator

import java.nio.file.InvalidPathException
import java.nio.file.Path;
import java.util.logging.Logger;

@CompileStatic
class SftpSubsystemJrr extends SftpSubsystem{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    SftpSubsystemJrr(ChannelSession channel, SftpSubsystemConfigurator configurator) {
        super(channel, configurator)
    }


    void setDefaultDirectory(Path defaultDir1) {
        defaultDir = defaultDir1
        log.info "defaultDir = ${defaultDir}"
    }

    @Override
    Path getDefaultDirectory() {
        return super.getDefaultDirectory()
    }

    protected Path resolveFile(String remotePath)
            throws IOException, InvalidPathException {

//        if(remotePath.toLowerCase().startsWith(defaultDir.toString().replace('\\','/').toLowerCase())){
//            String remotePath1 =  remotePath.substring(defaultDir.toString().length()+1)
//            log.info("return direct ${remotePath}   .  tr ${remotePath1}")
//            return new File(remotePath1).toPath()
//        }
        log.info("remo ${remotePath}")
        Path file1 = super.resolveFile(remotePath)
        log.info "${remotePath} resolved to ${remotePath}"
        return file1
//        Thread.dumpStack()
//        Path defaultDirectory1 = getDefaultDirectory()
//        SftpFileSystemAccessor accessor = getFileSystemAccessor();
//        Path localPath = accessor.resolveLocalFilePath(this, defaultDir, remotePath);
//        return localPath;
    }

    @Override
    protected Map<String, Object> doLStat(int id, String path, int flags) throws IOException {
        Map<String, Object> r = super.doLStat(id, path, flags)
        log.info "${path} = ${r}"
        return r
    }
}
