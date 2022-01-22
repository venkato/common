package net.sf.jremoterun.utilities.nonjdk.net.tomcat


import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.javalangutils.piddetector.PidDetector
import net.sf.jremoterun.utilities.nonjdk.net.tomcat.oldapi.AjpSerevrletRequestForwarderServlet
import net.sf.jremoterun.utilities.nonjdk.net.tomcat.oldapi.HttpServletSimpleJrr
import org.apache.catalina.Context
import org.apache.catalina.startup.Tomcat

import javax.servlet.http.HttpServlet
import java.util.logging.Logger;

@CompileStatic
class TomcatProxy {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();
    public Tomcat tomcat = new Tomcat();
    public Context context1
    public String contextPath = '';
    public String serveletName = 'SimpleServerlet1'

    void init(File baseDir, int port) {
        tomcat.setBaseDir(baseDir.getAbsolutePath());
        tomcat.setPort(port);
        tomcat.setHostname('127.0.0.1');
        String docBase = new File(baseDir, 'docbase')
        context1 = tomcat.addContext(contextPath, docBase);
    }


    void addAjpServlet(String targetHost, int targetPort) {
        HttpServlet servletSimple2 = new AjpSerevrletRequestForwarderServlet(targetHost, targetPort);
        String serveletName = 'ServletAjpForwarder'

        tomcat.addServlet(contextPath, serveletName, servletSimple2)
        context1.addServletMappingDecoded('/*', serveletName)
    }

    void addSimpleServlet() {
        HttpServletSimpleJrr servletSimple1 = new HttpServletSimpleJrr()
        tomcat.addServlet(contextPath, serveletName, servletSimple1)
        context1.addServletMappingDecoded('/*', serveletName)
    }

    void start() {
        tomcat.start();
        log.info "started ${PidDetector.detectPid()} .."
        tomcat.getServer().await();
    }

}
