package net.sf.jremoterun.utilities.nonjdk.sftploader

import groovy.transform.CompileStatic
import net.schmizz.sshj.DefaultConfig
import net.schmizz.sshj.SSHClient
import net.schmizz.sshj.common.LoggerFactory
import net.schmizz.sshj.sftp.FileAttributes
import net.schmizz.sshj.sftp.RemoteResourceInfo
import net.schmizz.sshj.sftp.StatefulSFTPClient
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

@CompileStatic
class SftpLoader {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();
    public DefaultConfig defaultConfig = new JrrSshjConfig()
    public String prefixBase1 = JrrDownloadFilesLayout.jrrfiles
    public SSHClient sshClient
    public StatefulSFTPClient sftpClient
    public boolean doLogging = false

    public List<String> downloadedFiles1 = []
    /**
     * contains ref to origin file
     */
    public List<File> downloadedFiles2 = []
    public LoggerFactory loggerFactory = new LoggerFactorySftpJrr()

    void prepare(String host, int port) {
        defaultConfig.setLoggerFactory(loggerFactory)
        sshClient = new SSHClient(defaultConfig)
        sshClient.addHostKeyVerifier(new PromiscuousVerifier())
        sshClient.connect(host, port)
    }


    void doJob1(File baseDir) {
        sftpClient = sshClient.newStatefulSFTPClient()
        doJob(baseDir)
    }

    void doJob(File baseDir) {
        doJob3(baseDir, prefixBase1)
    }

    void doJob3(File baseDir, String prefix2) {
        assert sftpClient != null
        List<RemoteResourceInfo> ls = sftpClient.ls(prefix2)
        ls.each {
            handleResource(prefixBase1 + '/', it, baseDir)
        }

    }


    void handleResource(String prefix, RemoteResourceInfo r, File dir) {
        String fullPrefix = prefix + r.getName()
        try {
            File dirrC = dir.child(r.getName())
            if (r.isDirectory()) {

                dirrC.mkdir()
                assert dirrC.exists()
                List<RemoteResourceInfo> ls = sftpClient.ls(fullPrefix)
                ls.each {
                    handleResource(fullPrefix + '/', it, dirrC)
                }
            } else {
                boolean needed = isNeedDownload(r, dirrC)
                if(doLogging) {
                    log.info "needed = ${needed} ${dirrC}"
                }
                if (needed) {
                    downloaddd(r, fullPrefix, dirrC)
                }
            }
        } catch (Throwable e) {
            log.info("failed handle ${fullPrefix} : ${e}")
            throw e
        }
    }

    void downloaddd(RemoteResourceInfo r, String fullPrefix, File dirrC) {
        File parentFile1 = dirrC.getParentFile()
        assert parentFile1.exists()
        dirrC.delete()
        assert !(dirrC.exists())
        if(doLogging) {
            log.info("downloading ${fullPrefix}    to    ${dirrC.getAbsolutePath()}")
        }
        sftpClient.get(fullPrefix, dirrC.getAbsolutePath())
        dirrC.setLastModified(r.getAttributes().getMtime())
        downloadedFiles1.add(fullPrefix)
        downloadedFiles2.add(dirrC)
    }

    boolean isNeedDownload(RemoteResourceInfo r, File f) {
        if (!f.exists()) {
            return true
        }
        FileAttributes attributes = r.getAttributes()
        long mtime = attributes.getMtime()
        long modified = f.lastModified()
        if (mtime != modified) {
            return true
        }
        if (attributes.getSize() != f.length()) {
            return true
        }
        return false
    }


}
