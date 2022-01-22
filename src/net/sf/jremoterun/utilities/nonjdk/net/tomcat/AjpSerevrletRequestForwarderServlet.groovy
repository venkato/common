package net.sf.jremoterun.utilities.nonjdk.net.tomcat

import com.github.jrialland.ajpclient.servlet.AjpServletProxy
import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils

import javax.servlet.ServletException
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;

@CompileStatic
class AjpSerevrletRequestForwarderServlet extends HttpServlet{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public String targetHost;
    public int targetPort;

    AjpSerevrletRequestForwarderServlet(String targetHost, int targetPort) {
        this.targetHost = targetHost
        this.targetPort = targetPort
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        AjpServletProxy ajpServletProxy = AjpServletProxy.forHost(targetHost, targetPort)
        ajpServletProxy.forward(req, resp);
    }


}
