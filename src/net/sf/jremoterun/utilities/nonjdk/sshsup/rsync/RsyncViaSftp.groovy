package net.sf.jremoterun.utilities.nonjdk.sshsup.rsync

import com.jcraft.jsch.ChannelSftp
import com.jcraft.jsch.SftpATTRS
import com.jcraft.jsch.SftpProgressMonitor
import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.io.FileUtilsJrr
import net.sf.jremoterun.utilities.nonjdk.dateutils.DurationConstants
import net.sf.jremoterun.utilities.nonjdk.io.JrrIoUtils
import net.sf.jremoterun.utilities.nonjdk.sshsup.BytesDivider
import net.sf.jremoterun.utilities.nonjdk.sshsup.SshConSet3
import org.apache.commons.io.FileUtils

import java.text.SimpleDateFormat;
import java.util.logging.Logger;

@CompileStatic
class RsyncViaSftp implements Closeable {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    public File archivePath;
    public SshConSet3 sff
    public SftpProgressMonitor sftpMonitor
    public SimpleDateFormat sdf = new SimpleDateFormat('yyyyMMdd_HHmm')
    public String remoteS = 'remote'
    public String localS = 'local'
    public int minSpaceGap = BytesDivider.mb.dividerInt
    public int depthMax = 10
    public List<String> badRemoteFiles = ['.', '..']

    public boolean allowRemoveDir = false

    public List<String> removed = []
    public List<String> removedDir = []
    public List<String> touched = []
    public List<String> uptoDate = []

    RsyncViaSftp(File archivePath, SshConSet3 sff) {
        this.archivePath = archivePath
        this.sff = sff
    }

    void initConnection() {
        sff.createSftpUtils()
        archivePath.mkdir()
        assert archivePath.exists()
    }

    File createBackupFolder(boolean isLocal, String parentDir) {
        File upload = new File(archivePath, isLocal ? localS : remoteS)
        upload.mkdir()
        File folder1 = new File(upload, sdf.format(new Date()))
        folder1.mkdir()
        assert folder1.exists()
        File folder2 = new File(folder1, parentDir)
        folder2.mkdirs()
        return folder2
    }

    void backupLocalFile(File fileToBackup, String parentDir) {
        assert fileToBackup.exists()
        File folder2 = createBackupFolder(true, parentDir)
        File backupToFile = new File(folder2, fileToBackup.getName())
        checkFreeSpace(folder2, fileToBackup.length())
        FileUtilsJrr.copyFile(fileToBackup, backupToFile)

    }

    void backupRemoteFile(String remoteFullName, ChannelSftp.LsEntry entry, String parentDir) {
        File folder2 = createBackupFolder(false, parentDir)
        File backupToFile = new File(folder2, entry.getFilename())
        checkFreeSpace(folder2, entry.getAttrs().getSize())
        sff.sftpUtils.sftp.get(remoteFullName, backupToFile.getAbsolutePath(), sftpMonitor)
        SftpATTRS attrs = sff.sftpUtils.sftp.stat(remoteFullName)
        setLastModified(backupToFile,attrs)
    }

    void setLastModified(File f,SftpATTRS attrs ){
        long mtine = attrs.getMTime()
        mtine = mtine * DurationConstants.oneSec.timeInMsLong
        f.setLastModified(mtine)
    }

    void checkFreeSpace(File folder2, long spaceNeeded) {
        long diff = folder2.getFreeSpace() - spaceNeeded
        assert diff > minSpaceGap
    }

    void rsyncFromLocalToRemoteEach(File localFolder, String remoteFolder, int depthCount, ChannelSftp.LsEntry entry) {
        String remoteNameFull = remoteFolder + '/' + entry.getFilename()
        File localFile = new File(localFolder, entry.getFilename())
        SftpATTRS attrs = entry.getAttrs()
        if (attrs.isDir()) {
            if (localFolder.exists()) {
                rsyncFromLocalToRemote(localFile, remoteNameFull, depthCount + 1)
            } else {
                if (allowRemoveDir) {
                    List<ChannelSftp.LsEntry> lss = lsRemoteFolder(remoteNameFull)
                    lss.each {
                        String remoteNameFull2 = remoteFolder + '/' + entry.getFilename() + '/' + it.getFilename()
                        try {
                            rsyncFromLocalToRemoteEach(localFolder, remoteNameFull2, depthCount, it)
                        } catch (Throwable e) {
                            log.info "failed ${remoteNameFull} ${e}"
                            throw e
                        }
                    }
                    sff.sftpUtils.sftp.rm(remoteNameFull)
                    removedDir.add(remoteNameFull)
                } else {
                    throw new Exception("not allowed remove remote dir ${remoteNameFull}")
                }
            }
        } else {
            assert attrs.isReg()
            if (localFile.exists()) {
                boolean needPut = isNeedFetch(localFile, attrs)
                if (needPut) {
                    backupRemoteFile(remoteNameFull, entry, remoteFolder)
                    uploadFile(localFile, remoteNameFull)
                } else {
                    uptoDate.add(remoteNameFull)
                }
            } else {
                backupRemoteFile(remoteNameFull, entry, remoteFolder)
                sff.sftpUtils.sftp.rm(remoteNameFull)
                removed.add(remoteNameFull)
            }
        }
    }


    String buildStat() {
        StringBuilder sb = new StringBuilder()
        if (removedDir.size() > 0) {
            sb.append("removedDir ${removedDir.size()} : ").append(removedDir.sort()).append(' ')
        }
        if (removed.size() > 0) {
            sb.append("removed ${removed.size()} : ").append(removed.sort()).append(' ')
        }
        if (touched.size() > 0) {
            sb.append("touched ${touched.size()} : ").append(touched.sort()).append(' ')
        }
        if (uptoDate.size() > 0) {
            sb.append("uptoDate ${uptoDate.size()}").append(' ')
        }
        return sb.toString()
    }

    void assertDone() {
        int totalSize = uptoDate.size() + touched.size()
        if (totalSize == 0) {
            throw new Exception("nothing done")
        }
    }

    void rsyncFromLocalToRemote(File localFolder, String remoteFolder, int depthCount) {
        assert depthCount < depthMax
        assert localFolder.exists()
        assert localFolder.isDirectory()
        List<String> visited = []
        List<ChannelSftp.LsEntry> lss = lsRemoteFolder(remoteFolder)
        lss.each {
            String remoteNameFull = remoteFolder + '/' + it.getFilename()
            try {
                rsyncFromLocalToRemoteEach(localFolder, remoteFolder, depthCount, it)
                visited.add(it.getFilename())
            } catch (Throwable e) {
                log.info "failed ${remoteNameFull} ${e}"
                throw e
            }
        }
        List<String> list3 = localFolder.list().toList()
        list3 = list3 - visited
        if (list3.size() > 0) {
            log.info "uploading new files ${list3}"
            list3.each {
                File localFile = new File(localFolder, it)
                String remoteNameFull = remoteFolder + '/' + it
                if (localFile.isDirectory()) {
                    sff.sftpUtils.sftp.mkdir(remoteNameFull)
                    rsyncFromLocalToRemote(localFile, remoteNameFull, depthCount + 1)
                } else {
                    uploadFile(localFile, remoteNameFull)

                }
            }
        }

    }

    void downloadFile(File localFile, String remoteNameFull, SftpATTRS attrs) {
        sff.sftpUtils.sftp.get(remoteNameFull, localFile.getAbsolutePath(), sftpMonitor)
        setLastModified(localFile,attrs)
        touched.add(remoteNameFull)
    }

    void uploadFile(File localFile, String remoteNameFull) {
        sff.sftpUtils.sftp.put(localFile.getAbsolutePath(), remoteNameFull, sftpMonitor)
        long mtine = (long) (localFile.lastModified() / DurationConstants.oneSec.timeInMsLong)
        sff.sftpUtils.sftp.setMtime(remoteNameFull, mtine as int)
        touched.add(remoteNameFull)
    }

    boolean isNeedFetch(File localFile, SftpATTRS attrs) {
        long mtime = attrs.getMTime()
        mtime = mtime * DurationConstants.oneSec.timeInMsLong
        if (localFile.length() == attrs.getSize()) {
            if (localFile.lastModified() <= mtime) {
                return false
            }
        }
        return true

    }


    void rsyncFromRemoteToLocalEach(File localFolder, String remoteFolder, int depthCount, ChannelSftp.LsEntry entry) {
        String remoteNameFull = remoteFolder + '/' + entry.getFilename()
        File localFile = new File(localFolder, entry.getFilename())
        SftpATTRS attrs = entry.getAttrs()
        if (attrs.isDir()) {
            if (localFolder.exists()) {

            } else {
                localFolder.mkdir()
                assert localFolder.exists()
            }
            rsyncFromRemoteToLocal(localFile, remoteNameFull, depthCount + 1)
        } else {
            assert attrs.isReg()
            if (localFile.exists()) {
                boolean needPut = isNeedFetch(localFile, attrs)
                if (needPut) {
                    checkFreeSpace(localFolder, attrs.getSize())
                    backupLocalFile(localFile, remoteFolder)
                    downloadFile(localFile, remoteNameFull, attrs)
                } else {
                    uptoDate.add(remoteNameFull)
                }
            } else {
                checkFreeSpace(localFolder, attrs.getSize())
                downloadFile(localFile, remoteNameFull, attrs)
            }
        }
    }

    List<ChannelSftp.LsEntry> lsRemoteFolder(String remoteFolder) {
        List<ChannelSftp.LsEntry> v = sff.sftpUtils.sftp.ls(remoteFolder)
        v = v.findAll { !badRemoteFiles.contains(it.getFilename()) }
        return v
    }

    void rsyncFromRemoteToLocal(File localFolder, String remoteFolder, int depthCount) {
        assert depthCount < depthMax
        assert localFolder.exists()
        assert localFolder.isDirectory()
        List<String> visited = []
        List<ChannelSftp.LsEntry> lss = lsRemoteFolder(remoteFolder)
        lss.each {
            String remoteNameFull = remoteFolder + '/' + it.getFilename()
            try {
                rsyncFromRemoteToLocalEach(localFolder, remoteFolder, depthCount, it)
                visited.add(it.getFilename())
            } catch (Throwable e) {
                log.info "failed ${remoteNameFull} ${e}"
                throw e
            }
        }
        List<String> list3 = localFolder.list().toList()
        list3 = list3 - visited
        if (list3.size() > 0) {
            log.info "removing old files ${list3}"
            list3.each {
                File localFile = new File(localFolder, it)
                String remoteNameFull = remoteFolder + '/' + it
                if (localFile.isDirectory()) {
                    if (allowRemoveDir) {
                        FileUtils.deleteDirectory(localFile)
                        removedDir.add(remoteNameFull)
                    } else {
                        throw new Exception("not allowed remve local dir ${localFile}")
                    }
                } else {
                    localFile.delete()
                }
                assert !localFile.exists()
                removed.add(remoteNameFull)
            }
        }
    }

    @Override
    void close() throws IOException {
        JrrIoUtils.closeQuietly2(sff, log)
    }
}
