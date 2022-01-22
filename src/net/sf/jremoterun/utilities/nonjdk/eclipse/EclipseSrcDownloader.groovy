package net.sf.jremoterun.utilities.nonjdk.eclipse

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.AddFilesToUrlClassLoaderGroovy
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.AddFileWithSources
import net.sf.jremoterun.utilities.nonjdk.fileloayout.EclipseFiles
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
        List<File> files = pluginDir.listFiles().toList().findAll { it.isFile() }.findAll { it.getName().endsWith('.jar') && it.getName().contains('_') }.findAll { !it.getName().contains(AddFileWithSources.eclipseSourceFileSuffix) }
        return files
    }

    List<String> createSuffixes(List<File> files) {
        List<String> allurls = []
        files.each {
            String name1 = it.getName()
            int ii1 = name1.lastIndexOf('_')
            String url2 = name1.substring(0, ii1) + '.source' + name1.substring(ii1)
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


    static void addClasspath(File eclipseBaseDir, AddFilesToUrlClassLoaderGroovy adder){
        adder.addAll EclipseFiles.plugins.ref.resolveChild(eclipseBaseDir).listFiles().toList().findAll {!it.getName().contains(net.sf.jremoterun.utilities.nonjdk.classpath.helpers.AddFileWithSources.eclipseSourceFileSuffix)}
    }

}
