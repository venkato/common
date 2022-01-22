package net.sf.jremoterun.utilities.nonjdk.ivy

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.FileOutputStream2;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.mdep.ivy.UrlSourceChecker
import net.sf.jremoterun.utilities.nonjdk.dateutils.DurationConstants
import net.sf.jremoterun.utilities.nonjdk.io.JrrIoUtils;

import java.util.logging.Logger;

@CompileStatic
class UrlSourceCheckerCommon extends UrlSourceChecker {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public Set<URL> urls

    public String sepLine = '\r\n'
    public String sepColumns = ';'
    public static String fileNameWithUrlsDefault = 'missedMavenSourcesUrls.txt'

    public File fileToStore;

    public void init(){
        assert net.sf.jremoterun.utilities.groovystarter.JrrStarterVariables2.getInstance().filesDir.exists()
        fileToStore = net.sf.jremoterun.utilities.groovystarter.JrrStarterVariables2.getInstance().filesDir.child(fileNameWithUrlsDefault)
        if(fileToStore.exists()) {
            urls = loadFile(fileToStore)
        }else {
            urls = new HashSet<>()
        }
        selfSet()
    }

    void selfSet(){
        UrlSourceChecker.defaultUrlSourceChecker = this
    }


    void addUrl(URL url, File f) {
        FileOutputStream2 stream2 = new FileOutputStream2(f, true)
        try {
            String line2 = sepLine + url.toString() + sepColumns + System.currentTimeMillis()
            stream2.write(line2.getBytes())
        } finally {
            JrrIoUtils.closeQuietly2(stream2, log)
        }
        urls = loadFile(fileToStore)
    }

    Set<URL> loadFile(File f) {
        Set<URL> urls2 = new HashSet<>()
        int copunt = 0
        f.eachLine {
            copunt++
            try {
                if (it.length() > 0) {
                    List<String> tokenize1 = it.tokenize(sepColumns)
                    assert tokenize1.size() == 2
                    urls2.add(new URL(tokenize1[0]));
                }
            } catch (Throwable e) {
                log.warn "failed on ${copunt} ${e}"
                throw e
            }
        }
        log.info "loaded urls ${copunt}, lines=${copunt}"
        return urls2
    }

    void cleanOlderDaays(long days, File f) {
        long dateCutoff = System.currentTimeMillis() - days * DurationConstants.oneDay.timeInMsLong
        List<String> contentRetain = []
        int copunt = 0
        f.eachLine {
            try {
                if (it.length() > 0) {
                    List<String> tokenize1 = it.tokenize(sepColumns)
                    assert tokenize1.size() == 2
                    long datee = tokenize1[1] as long
                    if (datee > dateCutoff) {
                        contentRetain.add(it)
                    }
                }
            } catch (Throwable e) {
                log.warn "failed on ${copunt} ${e}"
                throw e
            }
        }
        f.text = contentRetain.join(sepLine)
    }


    @Override
    boolean isCheckUrl(URL url) {
        if(urls.contains(url)){
            log.info "skipping download : ${url}"
            return false
        }
        return super.isCheckUrl(url)
    }

    public List<String> srcSuffixes = ['-sources.jar','-src.jar',]

    @Override
    void urlExists(boolean b, URL url) {
        if(!b){
            String string1 = url.toString()
            String find1 = srcSuffixes.find { string1.endsWith(it) }
            if(find1!=null) {
                log.info "marking as no source ${url}"
                addUrl(url, fileToStore)
            }
        }
        super.urlExists(b, url)
    }
}
