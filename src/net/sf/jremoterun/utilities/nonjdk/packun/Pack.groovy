package net.sf.jremoterun.utilities.nonjdk.packun

import groovy.transform.CompileStatic
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.archiver.JrrJarArchiver
import net.sf.jremoterun.utilities.nonjdk.sshsup.SshConSet3

import net.sf.jremoterun.utilities.nonjdk.store.complexwriters.ListStore2

import java.text.SimpleDateFormat
import java.util.logging.Logger

@EqualsAndHashCode
@ToString
@CompileStatic
class Pack {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    File baseDir
    List<PackInfo> packInfos
    AntBuilder ant;

    Pack(List<PackInfo> packInfos, File baseDir) {
        this.packInfos = packInfos
        this.baseDir = baseDir
    }

    Pack(File configFile, File baseDir) {
        //ListStore2<PackInfo> config = new ListStore2<PackInfo>()
        List<PackInfo> packInfos2 = ListStore2.loadSettingsS(configFile)
        this.packInfos = packInfos2
        this.baseDir = baseDir
    }

    static String infoFile = 'info.groovy'

    void packAllWithDate() {
        String date = new SimpleDateFormat('yyyy-MM-dd--HH-mm').format(new Date())
        baseDir = baseDir.child(date)
        baseDir.mkdir()
        assert baseDir.exists()
        List<Object> all = packAll()
        File f = baseDir.child(infoFile)
        ListStore2 ls = new ListStore2()
        f.text = ls.saveComplexObject(all)
    }

    void uploadToRemoteHost(SshConSet3 sshConSet3, String remoteDir) {
        sshConSet3.createSftpUtils().sftp2.copyLocalDirectory(baseDir.absolutePath, remoteDir, true, false, true, null)
    }

    List packAll() {
        List result = packInfos.collect { handlePackImpl(it) }
        return result
    }

    Object handlePackImpl(PackInfo packInfo) {

        try {
            if (packInfo.unzipLocation.isDirectory() && packInfo.unzipLocation.listFiles().length == 0) {
//                log.info "skiping empty dir ${packInfo.unzipLocation}"
                return new EmptyDir(packInfo.unzipLocation)
            } else {
                return handlePack2(packInfo)
            }
        } catch (Throwable e) {
            log.info "failed pack : ${packInfo}"
            throw e;
        }
    }

    Object handlePack2(PackInfo packInfo) {
        File zipFile = baseDir.child(packInfo.zipLocation)
        File parentFile = zipFile.parentFile
        parentFile.mkdirs()
        zipFile.delete()
        assert parentFile.exists()
        assert zipFile.parentFile.isDirectory()
        assert !zipFile.exists()

        handlePack2Impl(packInfo,zipFile)
        return packInfo
    }

    void handlePack2Impl(PackInfo packInfo,File zipFile) {
//        JrrAntUtils.addDirToZip(zipFile, packInfo.unzipLocation,null,null)
//        ZipUtil.pack(packInfo.unzipLocation, zipFile)
        JrrJarArchiver archiver = new JrrJarArchiver(false)
//        archiver.dateOfAllFiles = GroovyCustomCompiler.defaultFileTime
        archiver.createSimple(zipFile,packInfo.unzipLocation)


    }



}
