package net.sf.jremoterun.utilities.nonjdk.eclipse

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.net.JrrHttpUtils;

import java.util.logging.Logger;

@CompileStatic
class EclipseSrcDownloader {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    public JrrHttpUtils jrrHttpUtils;
    public File pluginDir
    public File destDir
    // https://download.eclipse.org/releases/2025-09/202509101001/plugins/
    public URL prefix
    public List<String> okk = []
    public List<String> existedAlready = []
    public List<String> failed = []

    EclipseSrcDownloader(JrrHttpUtils jrrHttpUtils, File pluginDir, File destDir, URL prefix) {
        this.jrrHttpUtils = jrrHttpUtils
        this.pluginDir = pluginDir
        this.destDir = destDir
        this.prefix = prefix
        destDir.mkdir()
        assert destDir.exists()
        assert pluginDir.exists()
        assert pluginDir.isDirectory()
    }

    void doJob() {
        List<File> files = findWhatNeedDownload()
        List<String> suffixes = createSuffixes(files)
        suffixes.each {
            downloadOneIfNeeded(it)
        }
    }

    List<File> findWhatNeedDownload() {
        List<File> files = pluginDir.listFiles().toList().findAll { it.isFile() }.findAll { it.getName().endsWith('.jar') && it.getName().contains('_') }.findAll { !it.getName().contains('.source_') }
        return files
    }

    List<String> createSuffixes(List<File> files) {
        List<String> allurls = []
        files.each {

            String name1 = it.getName()
            //assert name1.count('_')==1
            int ii1 = name1.lastIndexOf('_')
            //List<String> tokenize1 = name1.tokenize('_')
//            assert tokenize1.size()==2
            String url2 = name1.substring(0, ii1) + '.source' + name1.substring(ii1)
//            String url2 = name1.substring(0,ii1)+'.source_'+tokenize1[1]
            allurls.add(url2)
        }
        return allurls
    }

    void downloadOneIfNeeded(String name1) {
        File f = destDir.child(name1)
        if (f.exists()) {
            existedAlready.add(name1)
        } else {
            try {
                log.info "trying .. ${name1}"
                downloadUrl(f, new URL(prefix.toString() + name1))
                okk.add(name1)
            } catch (Exception e) {
                onException(name1, e)
            }
        }
    }

    void onException(String name1, Exception e) {
        if (e instanceof FileNotFoundException) {
            log.info "failed ${name1} ${e}"
            failed.add(name1)
        } else {
            throw e
        }
    }


    void downloadUrl(File dest, URL url) {
        dest.bytes = jrrHttpUtils.getContent(url)
    }

    void printStat() {
        log.info "ok=${okk.size()}  failed=${failed.size()} existedAlready=${existedAlready}  \n ${failed.sort().join('\n')}"
    }

}
