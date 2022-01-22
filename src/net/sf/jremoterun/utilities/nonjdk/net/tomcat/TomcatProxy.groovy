package net.sf.jremoterun.utilities.nonjdk.net.tomcat

import com.github.jrialland.ajpclient.servlet.AjpServletProxy
import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.PidDetector
import org.apache.catalina.Context
import org.apache.catalina.startup.Tomcat

import javax.servlet.ServletException
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;

@CompileStatic
class TomcatProxy {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();
    public Tomcat tomcat = new Tomcat();
    public Context context1
    public String contextPath = '';

    void init(File baseDir, int port) {
        tomcat.setBaseDir(baseDir.getAbsolutePath());
        tomcat.setPort(port);
        tomcat.setHostname('127.0.0.1');
        String docBase = new File(baseDir, 'docbase')
        context1 = tomcat.addContext(contextPath, docBase);
    }


    void addAjpServlet(String targetHost, int targetPort) {
        HttpServlet servletSimple2 = new AjpSerevrletRequestForwarderServlet(targetHost,targetPort);
        String serveletName = 'ServletAjpForwarder'

        tomcat.addServlet(contextPath, serveletName, servletSimple2)
        context1.addServletMappingDecoded('/*', serveletName)
    }

    void addSimpleServlet() {
        HttpServlet servletSimple1 = new HttpServlet() {
            @Override
            protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
                PrintWriter writer = resp.getWriter()
                writer.println("<html><body> Hello world </body><html>")
            }
        }
        String serveletName = 'SimpleServerlet1'
        tomcat.addServlet(contextPath, serveletName, servletSimple1)
        context1.addServletMappingDecoded('/*', serveletName)
    }

    void start(){

        tomcat.start();
//        PidDetector.printPid()
        log.info "started ${PidDetector.detectPid()} .."
        tomcat.getServer().await();
    }

}
