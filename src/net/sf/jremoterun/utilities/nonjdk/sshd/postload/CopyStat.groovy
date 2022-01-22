package net.sf.jremoterun.utilities.nonjdk.sshd.postload

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.sftploader.settings.SftpConnectionSettings;

import java.util.logging.Logger;

@CompileStatic
class CopyStat {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public SftpConnectionSettings settings;

    CopyStat() {
        this.settings = SftpConnectionSettings.settings
    }

    CopyStat(SftpConnectionSettings settings) {
        this.settings = settings
    }

    String dumpDownloaded() {
        List<String> files1 = settings.sftpLoader.downloadedFiles1
        return buildStr('downloaded files', files1)
    }

    String buildStr(String prefix, List files1) {
        int size1 = files1.size()
        if (size1 == 0) {
            return "${prefix} count=0"
        }
        return "${prefix} count=${size1}  files=${files1.sort()}"
    }

    String dumpUnziped() {
        List<String> files1 = settings.unzipedEntries
        return buildStr('unzipped files', files1)
    }

    String dumpCopied() {
        File originDir = settings.originDir
        List<String> files1 = settings.copyAndUnpack.copiedFiles.collect { originDir.getPathToParent(it) }
        return buildStr('copied files', files1)
    }

    void dumpAll() {
        List<String> ll = []
        ll.add(dumpDownloaded())
        ll.add(dumpUnziped())
        ll.add(dumpCopied())
        String s = ll.join('\n')
        log.info "download stat : \n${s}"
    }


}
