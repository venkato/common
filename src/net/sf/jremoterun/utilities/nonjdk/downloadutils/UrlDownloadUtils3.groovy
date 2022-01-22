package net.sf.jremoterun.utilities.nonjdk.downloadutils

import com.github.junrar.Junrar
//import com.github.junrar.Archive
//import com.github.junrar.extract.ExtractArchive
import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.MavenCommonUtils
import net.sf.jremoterun.utilities.classpath.MavenDefaultSettings
import net.sf.jremoterun.utilities.nonjdk.GeneralUtils
import net.sf.jremoterun.utilities.nonjdk.git.UrlSymbolsReplacer
import net.sf.jremoterun.utilities.nonjdk.io.JrrIoUtils
import net.sf.jremoterun.utilities.nonjdk.sfdownloader.SfLink
import net.sf.jremoterun.utilities.nonjdk.sfdownloader.UrlProvided
import org.apache.commons.io.IOUtils
import org.rauschig.jarchivelib.ArchiveFormat
import org.rauschig.jarchivelib.Archiver
import org.rauschig.jarchivelib.ArchiverFactory
import org.rauschig.jarchivelib.FileType
import org.zeroturnaround.zip.ZipUtil

import java.util.logging.Logger

@CompileStatic
class UrlDownloadUtils3 {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    private static String gitDirSuffix = "git_download_"
    private static String fileSuffix = "file_"

    static UrlDownloadUtils3 urlDownloadUtils;

    MavenCommonUtils mcu = new MavenCommonUtils();

    public volatile boolean inited = false

    public File tmpDir


    static UrlDownloadUtils3 getUrlDownloadUtils() {
        if (urlDownloadUtils == null) {
            urlDownloadUtils = new UrlDownloadUtils3();
        }
        return urlDownloadUtils;
    }

    UrlDownloadUtils3() {
    }

    void init() {
        if(tmpDir==null) {
            assert MavenDefaultSettings.mavenDefaultSettings.jrrDownloadDir!=null
            tmpDir = new File(MavenDefaultSettings.mavenDefaultSettings.jrrDownloadDir, "tmp")
            tmpDir.mkdir()
        }
    }




    File findFreeFile() {
        init()
        int i = 10
        while (i < 100) {
            i++;
            File tmpGitDir = new File(tmpDir, fileSuffix + "${i}")
            if (!tmpGitDir.exists()) {
                return tmpGitDir;
            }
        }
        throw new Exception("can't find free file in ${tmpDir}")
    }

    File buildDownloadUrl(URL url){
        File f2 = mcu.buildDownloadUrl(url);
        return f2;
    }


    File downloadUrl(URL url) {
        File f2 = buildDownloadUrl(url);
        if (f2.exists()) {
            log.info("already downloaded ${url}")
            return f2
        }
        log.info "downloading ${url} ..."
        File f = findFreeFile();
        if (f.exists()) {
            assert f.delete()
        }
        BufferedOutputStream out = f.newOutputStream()
        BufferedInputStream ins;
        try {
            ins = url.newInputStream()
            IOUtils.copyLarge(ins, out)
            out.flush()
            log.info "downloaded ${url}"
        } finally {
            JrrIoUtils.closeQuietly2(out, log)
            JrrIoUtils.closeQuietly2(ins, log)
        }

        f2.parentFile.mkdirs()
        assert f2.parentFile.exists()
        assert f.renameTo(f2)
        return f2
    }
}
