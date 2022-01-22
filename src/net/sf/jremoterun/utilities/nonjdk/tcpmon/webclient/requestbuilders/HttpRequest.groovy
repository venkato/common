package net.sf.jremoterun.utilities.nonjdk.tcpmon.webclient.requestbuilders

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.tcpmon.webclient.ConnectionRequest
import org.apache.http.message.BasicHeader;

import java.util.logging.Logger;

/**
 * * https://en.wikipedia.org/wiki/List_of_HTTP_header_fields
 * @see org.apache.http.HttpHeaders
 * @see cn.hutool.http.Header
 */
@CompileStatic
class HttpRequest {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public List<String> headers = []

    public static String keyValueSep=': '
    public static String lineSep ='\n'
    public static String payload= ''


    public static BasicHeader keepAlive=new BasicHeader(org.apache.http.HttpHeaders.CONNECTION, 'keep-alive');
    public static List<String> chromeHeaders='''sec-ch-ua: "Not.A/Brand";v="8", "Chromium";v="114", "Google Chrome";v="114"
sec-ch-ua-mobile: ?0
sec-ch-ua-platform: "Windows"
DNT: 1
Upgrade-Insecure-Requests: 1
User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36
Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7
Sec-Fetch-Site: none
Sec-Fetch-Mode: navigate
Sec-Fetch-User: ?1
Sec-Fetch-Dest: document
Accept-Encoding: gzip, deflate, br
Accept-Language: en-GB,en;q=0.9'''.readLines()

    String buildRequest(){
        if(payload==null||payload.length()==0){
            return   headers.join(lineSep)+ lineSep + lineSep
        }
        return headers.join(lineSep)+ lineSep +lineSep +payload+ lineSep
    }

    void addUserStuff(int addPosition,List<String> userStuff){
        int found=-1
        int i=-1
        userStuff.each {
            i++
            if(it.contains(net.sf.jremoterun.utilities.nonjdk.tcpmon.webclient.HttpClientConst.payloadSep)){
                found=i
            }
        }
        if(found>=0) {
            headers.addAll(addPosition,  userStuff.subList(0, found))
            payload = userStuff.subList(found + 1, userStuff.size()).join(lineSep)
        }else {
            headers.addAll(addPosition, userStuff)
        }
    }

    List<BasicHeader> parseHeaders(List<String> headers){
        return headers.collect {       parseHeader(it)}
    }

    org.apache.http.message.BasicHeader parseHeader(String el){
        int i= el.indexOf(':')
        if(i<=0){
            throw new Exception("bad header : ${el}")
        }
        String name1 = el.substring(0,i)
        String value1 = el.substring(i+1).trim()
        //List<String> tokenize1 = el.tokenize(':');
        return new org.apache.http.message.BasicHeader(name1,value1)
    }

    static String buildGetRequest(String suffix){
        return "GET /${suffix} HTTP/1.1"
    }

    void addHeader(Object h){
        headers.add(h.toString())
    }


    void addHost(ConnectionRequest cr){
        addHeader2(org.apache.http.HttpHeaders.HOST,cr.host+':'+cr.port)
    }

    void addHeader2(String key, String value){
        headers.add(key+keyValueSep+value)
    }

    void addChromeHeaders(){
        headers.addAll(chromeHeaders)
    }

    void addContentLength(){
        if(payload.length()==0){
            throw new Exception("payload is empty")
        }
        String first1 = headers.first()
        if(first1.startsWith('GET')){
            throw new Exception("it is get request")
        }
        addHeader2(org.apache.http.HttpHeaders.CONTENT_LENGTH,''+payload.length())
    }


}
