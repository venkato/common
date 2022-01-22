package net.sf.jremoterun.utilities.nonjdk.net.apachehttpclient.git

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import org.apache.commons.io.input.CountingInputStream
import org.apache.commons.io.output.CountingOutputStream
import org.apache.http.Header
import org.apache.http.HttpEntity
import org.apache.http.HttpEntityEnclosingRequest
import org.apache.http.HttpHeaders
import org.apache.http.HttpResponse
import org.apache.http.client.HttpClient
import org.apache.http.protocol.HttpContext
import org.eclipse.jgit.transport.http.apache.HttpClientConnection
import org.eclipse.jgit.util.TemporaryBuffer;

import java.util.logging.Logger
import java.util.stream.Collectors
import java.util.zip.GZIPInputStream;

@CompileStatic
class GitJrrHttpClientConnection extends HttpClientConnection {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    GitJrrHttpClientConnection(String urlStr) throws MalformedURLException {
        super(urlStr)
    }

    GitJrrHttpClientConnection(String urlStr, Proxy proxy) throws MalformedURLException {
        super(urlStr, proxy)
    }

    GitJrrHttpClientConnection(String urlStr, Proxy proxy, HttpClient cl) throws MalformedURLException {
        super(urlStr, proxy, cl)
    }


    @Override
    List<String> getHeaderFields(String name) {
        HttpResponse resp = JrrClassUtils.getFieldValue(this, 'resp') as HttpResponse;
        return resp.getHeaders(name).collect { it.getValue() }
    }

    @Override
    InputStream getInputStream() throws IOException {
        InputStream stream1 = super.getInputStream()
        CountingInputStream countingInputStream = new CountingInputStream(stream1) {
            @Override
            void close() throws IOException {
                long count1 = getByteCount()
                log.info "received bytes ${count1}"
                super.close()
            }
        }
        return countingInputStream
    }

    @Override
    OutputStream getOutputStream() throws IOException {
        OutputStream stream1 = super.getOutputStream()
        CountingOutputStream countingOutputStream = new CountingOutputStream(stream1) {
            @Override
            void close() throws IOException {
                long count1 = getByteCount()
                log.info "sent bytes ${count1}"
                super.close()
            }
        }
        return countingOutputStream
    }

    @Override
    int getResponseCode() throws IOException {
        return super.getResponseCode()
    }

    static byte[] extractRequestContent(org.apache.http.HttpRequest httpRequest) {
        if (httpRequest instanceof HttpEntityEnclosingRequest) {
            HttpEntityEnclosingRequest enclosingRequest = (HttpEntityEnclosingRequest) httpRequest;
            HttpEntity entity1 = enclosingRequest.getEntity()
            if (entity1 instanceof org.eclipse.jgit.transport.http.apache.TemporaryBufferEntity) {
                org.eclipse.jgit.transport.http.apache.TemporaryBufferEntity jgitEnity = (org.eclipse.jgit.transport.http.apache.TemporaryBufferEntity) entity1;
                TemporaryBuffer buffer1 = jgitEnity.getBuffer()
                byte[] array1 = buffer1.toByteArray()
                Header[] headers = httpRequest.getHeaders(HttpHeaders.CONTENT_ENCODING)
                if (headers != null) {
                    String value = headers[0].getValue()
                //    log.info "${value}  ${headers[0]}"
                    if (value == 'gzip') {
                        GZIPInputStream gzipInputStream = new GZIPInputStream(new ByteArrayInputStream(array1))
                        byte[] bytes1 = gzipInputStream.getBytes()
                        return bytes1
                    }
                }
                return array1
            }

        }
        return null
    }


}
