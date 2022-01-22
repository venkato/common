package net.sf.jremoterun.utilities.nonjdk.net.apachehttpclient

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import org.apache.http.StatusLine;

import java.util.logging.Logger

import org.apache.http.Header
import org.apache.http.HttpResponse
import org.apache.http.client.methods.HttpUriRequest

@CompileStatic
class HttpDebugAction {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public boolean logUrl = true
    public boolean logRequestHeaders = true
    public boolean logResponseHeaders = true

    void logActionRequest(HttpUriRequest request){
        if(logUrl) {
            log.info("doing : " + request.getMethod() + " " + request.getURI());
        }
        if(logRequestHeaders) {
            List<Header> headers = request.getAllHeaders().toList()
            log.info "request headers: ${headers.size()}"
            headers.each {
                log.info "${it.getName()} : ${it.getValue()}"
            }
        }
    }

    void logActionResponse(HttpUriRequest request, HttpResponse response){
        StatusLine statusLine = response.getStatusLine()
        log.info "${statusLine}"
        if(logResponseHeaders) {
            List<Header> headers = request.getAllHeaders().toList()
            log.info "response headers: ${headers.size()}"
            headers.each {
                log.info "${it.getName()} : ${it.getValue()}"
            }
        }

    }

}
