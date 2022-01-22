package net.sf.jremoterun.utilities.nonjdk.net.apachehttpclient

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.UrlToFileConverter
import net.sf.jremoterun.utilities.nonjdk.FileUtilsJrr
import net.sf.jremoterun.utilities.nonjdk.asmow2.usedclasses.UsedClasses
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.ivy.core.settings.TimeoutConstraint
import org.apache.ivy.util.CopyProgressEvent
import org.apache.ivy.util.CopyProgressListener

//import org.apache.ivy.core.settings.TimeoutConstraint

import org.apache.ivy.util.url.HttpClientHandler
import org.apache.ivy.util.url.URLHandler
import org.apache.ivy.util.url.URLHandlerRegistry
import org.zeroturnaround.zip.ZipEntryCallback
import org.zeroturnaround.zip.ZipUtil

import java.util.logging.Logger
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

/**
 * @see net.sf.jremoterun.utilities.nonjdk.ivy.JrrIvyURLHandler
 */
@CompileStatic
class JrrIvyApacheClientURLHandler extends HttpClientHandler {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static URLHandler beforeS;
    public URLHandler before2;


    static void setHandler(CloseableHttpClient impl) {
        if (beforeS == null) {
            JrrIvyApacheClientURLHandler handler = new JrrIvyApacheClientURLHandler(impl)
            handler.setURLHandler2()
            beforeS = handler.before2
        } else {
            log.info "custom url handler already set : ${beforeS.getClass().getName()}"
        }
    }

    static void setHttpClient(HttpClientHandler cl, CloseableHttpClient impl) {
        JrrClassUtils.setFieldValue(cl, 'httpClient', impl)
    }


    static URLHandler.URLInfo createUrlInfo(boolean available, long contentLength, long lastModified) {
        return JrrClassUtils.invokeConstructor(URLHandler.URLInfo, available, contentLength, lastModified)
    }

    JrrIvyApacheClientURLHandler(CloseableHttpClient impl) {
        setHttpClient1(impl)
    }

    void setHttpClient1(CloseableHttpClient impl) {
        setHttpClient(this, impl)
    }

    void setURLHandler2() {
        before2 = URLHandlerRegistry.getDefault()
        URLHandlerRegistry.setDefault(this)
    }


    boolean isNeedLogUrl(URL url) {
        if (url.toString().startsWith('jar:file:')) {
            return false;
        }
        if (url.toString().startsWith('file:')) {
            return false
        }
        return true
    }


    /**
     * here actual download happening
     */
    @Override
    URLInfo getURLInfo(URL url, TimeoutConstraint timeoutConstraint) {
        File file1 = convertToFile(url)
        if (file1 != null) {
            if (file1.exists()) {
                return createUrlInfo(true, file1.length(), file1.lastModified())
            }
            return UNAVAILABLE;
        }
        if (isNeedLogUrl(url)) {
            log.info "downloading : ${url}"
        }
        return super.getURLInfo(url, timeoutConstraint)
    }

    public String filePrefix = 'file:/'
    public char column = ':'

    boolean isWindowsUrl(String filePrefix2, String url) {
        if (url.length() > (filePrefix2.length() + 2)) {
            char at1 = url.charAt(filePrefix2.length() + 2)
            return at1 == column
        }
        return false

    }

    File convertToFile(URL url) {
        String string1 = url.toString()
        if (string1.startsWith('http://') || string1.startsWith('https://')) {
            return null;
        }
        return UrlToFileConverter.c.convert(url)
    }

    @Override
    void download(URL src, File dest, CopyProgressListener listener, TimeoutConstraint timeoutConstraint) throws IOException {
        File file1 = convertToFile(src)
        if (file1 == null) {
            // this used in ivy 2.5.0
            if (isNeedLogUrl(src)) {
                log.info2(src)
            }
            super.download(src, dest, listener, timeoutConstraint)
        } else {
            if (file1.exists()) {
                FileUtilsJrr.copyFile(file1, dest)
            } else {
                log.warning("File not found ${file1} ${src}")
                throw new FileNotFoundException("${file1}")
            }
        }
    }

    public String jarFilePrefix = 'jar:file:/'

    List<String> convertJarUrlToFile(URL url) {
        return UrlToFileConverter.c.convertJarUrlToFile(url)
    }

    InputStream openStreamJarFile(URL url) {
        List<String> tokenize1 = convertJarUrlToFile(url)
        File f = new File(tokenize1[0])
        if (!f.exists()) {
            throw new FileNotFoundException("${f} ${url}")
        }
        String zipPath = tokenize1[1].substring(1)
        return openStreamInJar(f, zipPath)
    }

    public long maxResourceSize = 100_000_000; // 100 mb
    @Override
    long getLastModified(URL url) {
        return super.getLastModified(url)
    }

    @Override
    long getLastModified(URL url, int timeout) {
        return super.getLastModified(url, timeout)
    }

    @Override
    long getLastModified(URL url, TimeoutConstraint timeoutConstraint) {
        return super.getLastModified(url, timeoutConstraint)
    }

    InputStream openStreamInJar(File f, String zipPath) {
        byte[] bytes1
        ZipEntryCallback zipEntryCallback = new ZipEntryCallback() {
            @Override
            void process(InputStream inputStream, ZipEntry zipEntry) throws IOException {

                if (zipPath == zipEntry.getName()) {
                    long size1 = zipEntry.getSize()
                    if (size1 > maxResourceSize) {
                        throw new Exception("too big resource ${size1} ${zipPath} ${f} ")
                    }
                    bytes1 = inputStream.bytes

                }
            }
        }
        ZipUtil.iterate(f, zipEntryCallback)
        if (bytes1 == null) {
            log.info "failed find ${zipPath} in ${f}"
            throw new IOException("failed find ${zipPath} in ${f}")
        }
        return new ByteArrayInputStream(bytes1);
    }

    @Override
    InputStream openStream(URL url, TimeoutConstraint timeoutConstraint) throws IOException {
        String string1 = url.toString()
        if (string1.startsWith(jarFilePrefix)) {
            return openStreamJarFile(url)
        }
        File file1 = convertToFile(url)
        if (file1 == null) {
            log.warning("File not found ${url}")
            throw new FileNotFoundException("${url}")
        }
        if (file1 != null) {
            if (file1.exists()) {
                return new FileInputStream(file1)
            } else {
                log.warning("File not found ${file1} ${url}")
                throw new FileNotFoundException("${file1}")
            }
        }
        if (isNeedLogUrl(url)) {
            log.info2(url)
        }
        return super.openStream(url, timeoutConstraint)
    }

    @Override
    void download(URL src, File dest, CopyProgressListener listener) throws IOException {
        File file1 = convertToFile(src)
        if (file1 != null) {
            if (file1.exists()) {
                if (listener != null) {
                    listener.start(new CopyProgressEvent(file1.getAbsolutePath().getBytes(), file1.length()))
                }
                FileUtilsJrr.copyFile(file1, dest)
                if (listener != null) {
                    listener.end(new CopyProgressEvent(file1.getAbsolutePath().getBytes(), file1.length()))
                }
            } else {
                log.warning("File not found ${file1} ${src}")
                throw new FileNotFoundException("${file1}")
            }
        } else {
            if (isNeedLogUrl(src)) {
                log.info2(src)
            }
            super.download(src, dest, listener)
        }
    }

    @Override
    void upload(File src, URL dest, CopyProgressListener listener) throws IOException {
        log.info2(dest)
        File file1 = convertToFile(dest)
        if (file1 != null) {
            if (listener != null) {
                listener.start(new CopyProgressEvent(file1.getAbsolutePath().getBytes(), file1.length()))
            }
            FileUtilsJrr.copyFile(src, file1)
            if (listener != null) {
                listener.end(new CopyProgressEvent(file1.getAbsolutePath().getBytes(), file1.length()))
            }
        } else {
            super.upload(src, dest, listener)
        }
    }
}
