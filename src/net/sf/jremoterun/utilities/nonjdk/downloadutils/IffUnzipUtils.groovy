package net.sf.jremoterun.utilities.nonjdk.downloadutils

import com.github.junrar.Junrar
import groovy.transform.CompileStatic

//import com.github.junrar.Archive
//import com.github.junrar.extract.ExtractArchive

import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.MavenCommonUtils
import net.sf.jremoterun.utilities.classpath.MavenDefaultSettings
import net.sf.jremoterun.utilities.nonjdk.git.UrlSymbolsReplacer
import net.sf.jremoterun.utilities.nonjdk.io.JrrIoUtils
import net.sf.jremoterun.utilities.nonjdk.sfdownloader.SfLink
import net.sf.jremoterun.utilities.nonjdk.sfdownloader.UrlProvided
import org.apache.commons.io.IOUtils
import org.rauschig.jarchivelib.ArchiveFormat
import org.rauschig.jarchivelib.Archiver
import org.rauschig.jarchivelib.ArchiverFactory
import org.rauschig.jarchivelib.FileType

import java.util.logging.Logger

@CompileStatic
class IffUnzipUtils {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public File unzipDir

    IffUnzipUtils(File unzipdir3) {
        this.unzipDir = unzipdir3
    }

    void init() {
        if(unzipDir == null) {
            unzipDir.mkdir()
            assert unzipDir.exists()
        }
    }


    File downloadUrlAndUnzip(SfLink url) {
        File f = url.resolveToFile()
        return unzip(f, 'sf/' + url.path,false)
    }


//    File downloadUrlAndUnzip(UrlProvided url) {
//        return downloadUrlAndUnzip(url.convertToUrl())
//    }

//    File downloadUrlAndUnzip(URL url) {
//        File f = downloadUrl(url)
//        return unzip(f, url.toString(),false)
//    }

    File buildRefToUnzipFile(String fileSuffix){
        File f2 = new File(unzipDir, UrlSymbolsReplacer.replaceBadSymbols(fileSuffix))
        return f2
    }


    File unzip(File zipFile, String fileSuffix,boolean isZipSure) {
        init()
        File f2 = buildRefToUnzipFile(fileSuffix)
        if (f2.exists()) {
            log.info "already unarchived : ${f2}"
            return f2;
        }
        String fileName = zipFile.getName()
        if(zipSuffixes.find{fileName.endsWith(it)}!=null){
            isZipSure = true
        }
        if(isZipSure){
            Archiver archiver = ArchiverFactory.createArchiver(ArchiveFormat.ZIP)
            archiver.extract(zipFile, f2)
            if(!f2.exists()){
                throw new Exception("failed unzip : ${zipFile}")
            }
            return f2
        }


        if (fileName.endsWith('.rar')) {
            // https://github.com/edmund-wagner/junrar
            // https://github.com/jukka/java-unrar
            // https://github.com/Albertus82/JUnRAR
            // https://github.com/asm-labs/junrar
            Junrar.extract(zipFile, f2);
            return f2
        }
        FileType fileType = FileType.get(zipFile);
        if (fileType == FileType.UNKNOWN) {
            throw new Exception("unsupported type ${zipFile}")
//            return zipFile;
        }
        Archiver archiver = ArchiverFactory.createArchiver(zipFile)
        archiver.extract(zipFile, f2)
        if(!f2.exists()){
            throw new Exception("failed unzip : ${zipFile}")
        }
        return f2
    }

    public List<String> zipSuffixes = ['.war',]


}
