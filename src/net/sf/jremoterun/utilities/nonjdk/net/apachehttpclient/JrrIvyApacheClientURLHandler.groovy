package net.sf.jremoterun.utilities.nonjdk.net.apachehttpclient

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
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


    static JrrIvyApacheClientURLHandler setHandlerForce(CloseableHttpClient impl) {
        JrrIvyApacheClientURLHandler handler = new JrrIvyApacheClientURLHandler(impl)
        handler.before2 = URLHandlerRegistry.getDefault()
        URLHandlerRegistry.setDefault(handler)
        return handler;
    }


    static void setHandler(CloseableHttpClient impl) {
        if (beforeS == null) {
            JrrIvyApacheClientURLHandler handler = setHandlerForce(impl)
            beforeS = handler.before2
        } else {
            log.info "custom url handler already set : ${beforeS.getClass().getName()}"
        }
    }

    static void setHttpClient(HttpClientHandler cl, CloseableHttpClient impl){
        JrrClassUtils.setFieldValue(cl,'httpClient',impl)
    }


    static URLHandler.URLInfo createUrlInfo(boolean available, long contentLength, long lastModified){
        return JrrClassUtils.invokeConstructor(URLHandler.URLInfo, available, contentLength, lastModified)
    }

    JrrIvyApacheClientURLHandler(CloseableHttpClient impl) {
        setHttpClient1(impl)
    }

    void setHttpClient1( CloseableHttpClient impl){
        setHttpClient(this,impl)
    }

    void setURLHandler2(){
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

    String filePrefix = 'file:/'

    File convertToFile(URL url) {
        String string1 = url.toString()
        if (string1.startsWith(filePrefix)) {
            String fs = string1.substring(filePrefix.length())
            File f = new File(fs);
            return f
        }
        return null
    }

    @Override
    void download(URL src, File dest, CopyProgressListener listener, TimeoutConstraint timeoutConstraint) throws IOException {
        File file1 = convertToFile(src)
        if (file1 != null) {
            if (file1.exists()) {
                FileUtilsJrr.copyFile(file1, dest)
            } else {
                log.warning("File not found ${file1}")
                throw new FileNotFoundException("${file1}")
            }
        } else {
            // this used in ivy 2.5.0
            if (isNeedLogUrl(src)) {
                log.info2(src)
            }
            super.download(src, dest, listener, timeoutConstraint)
        }
    }

    String jarFilePrefix = 'jar:file:/'


    InputStream openStreamJarFile(URL url){
        String string1 = url.toString()
        String fileAndZipPath=string1.substring(jarFilePrefix.length())
        List<String> tokenize1 = fileAndZipPath.tokenize('!')
        if(tokenize1.size()<2){
            log.info "failed parse ${string1}"
            throw new IOException("failed parse ${string1}")
        }
        if(tokenize1.size()>2){
            log.info "starnge size ${string1}"
            throw new IOException("starnge size ${string1}")
        }
        File f = new File(tokenize1[0])
            String zipPath = tokenize1[1].substring(1)
        byte[] bytes1
        ZipEntryCallback zipEntryCallback = new ZipEntryCallback() {
            @Override
            void process(InputStream inputStream, ZipEntry zipEntry) throws IOException {
                if (zipPath ==zipEntry.getName()) {
                    bytes1 = inputStream.bytes

                }
            }
        }
        ZipUtil.iterate(f, zipEntryCallback)
        if(bytes1==null){
            log.info "failed find ${zipPath} in ${f}"
            throw new IOException("failed find ${zipPath} in ${f}")
        }
        return new ByteArrayInputStream(bytes1);
    }

    @Override
    InputStream openStream(URL url, TimeoutConstraint timeoutConstraint) throws IOException {
        String string1 = url.toString()
        if(string1.startsWith(jarFilePrefix)){
            return openStreamJarFile(url)
        }
        File file1 = convertToFile(url)
        if (file1 != null) {
            if (file1.exists()) {
                return new FileInputStream(file1)
            } else {
                log.warning("File not found ${file1}")
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
                log.warning("File not found ${file1}")
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
