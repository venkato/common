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
public class JrrMavenDownloadHttpHandlerCustomMavenUtils extends JrrMavenDownloadHttpHandler {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static JrrMavenHttpUtils jrrMavenHttpUtils = new JrrMavenHttpUtils();

    static boolean inited = false;

    public JrrMavenDownloadHttpHandlerCustomMavenUtils() {
        if (inited) {

        } else {
            inited = true;
            doInit();
        }
    }

    void doInit() {
        jrrMavenHttpUtils.addLoggers();
        jrrMavenHttpUtils.setRef();
    }


}
