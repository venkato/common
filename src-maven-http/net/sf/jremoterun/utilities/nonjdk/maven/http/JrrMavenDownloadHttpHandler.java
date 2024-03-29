package net.sf.jremoterun.utilities.nonjdk.maven.http;

import groovy.transform.CompileStatic;

import net.sf.jremoterun.utilities.JrrClassUtils;
import org.apache.maven.wagon.providers.http.HttpWagon;
import org.apache.maven.wagon.providers.http.httpclient.HttpException;
import org.apache.maven.wagon.providers.http.httpclient.StatusLine;
import org.apache.maven.wagon.providers.http.httpclient.client.methods.CloseableHttpResponse;
import org.apache.maven.wagon.providers.http.httpclient.client.methods.HttpUriRequest;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


// Replace in META-INF/plexus/components.xml  from  org.apache.maven.wagon.providers.http.HttpWagon
// to this class
// sample META-INF/plexus/components.xml :
//    <?xml version="1.0" encoding="UTF-8"?>
//    <component-set>
//    <components>
//    <component>
//    <role>org.apache.maven.wagon.Wagon</role>
//    <role-hint>http</role-hint>
//    <implementation>net.sf.jremoterun.utilities.nonjdk.maven.http.JrrMavenDownloadHttpHandlerJrrMavenDownloadHttpHandler</implementation>
//    <instantiation-strategy>per-lookup</instantiation-strategy>
//    </component>
//    <component>
//    <role>org.apache.maven.wagon.Wagon</role>
//    <role-hint>https</role-hint>
//    <implementation>net.sf.jremoterun.utilities.nonjdk.maven.http.JrrMavenDownloadHttpHandler</implementation>
//    <instantiation-strategy>per-lookup</instantiation-strategy>
//    </component>
//    </components>
//    </component-set>



@CompileStatic
public class JrrMavenDownloadHttpHandler extends HttpWagon {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    public static int cnt1=12;
    public static List<Integer> fineHttpStatusCodes = new ArrayList<>();

    public List<String> rejectedUrls = new ArrayList<>();

    static {
        fineHttpStatusCodes.add(200);
    }

    void checkUri(URI uri1 ) throws IOException {
        String url3 = uri1.toString();
        for(String r:rejectedUrls){
            if(url3.contains(r)){
                throw new IOException("blacklisted "+url3);
            }
        }

    }

    @Override
    protected CloseableHttpResponse execute(HttpUriRequest httpMethod) throws HttpException, IOException {
        URI uri1 = httpMethod.getURI();
        if(uri1!=null) {
            checkUri(uri1);
        }
        try {
            CloseableHttpResponse response = super.execute(httpMethod);
            onResult(httpMethod,response);
            return response;
        } catch (HttpException|IOException e) {
            log.log(Level.WARNING, httpMethod.getMethod() + " " + uri1 + " failed : ", e);
            throw e;
        }
    }

    void onResult(HttpUriRequest httpMethod,CloseableHttpResponse response){
        StatusLine statusLine = response.getStatusLine();
        if(fineHttpStatusCodes.contains(statusLine.getStatusCode())){

        }else {
            log.info(httpMethod.getMethod() + " " + httpMethod.getURI() + " strange : " + statusLine);
        }
    }
}
