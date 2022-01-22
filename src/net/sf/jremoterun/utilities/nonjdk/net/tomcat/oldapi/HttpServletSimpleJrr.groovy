package net.sf.jremoterun.utilities.nonjdk.net.tomcat.oldapi

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils

import javax.servlet.ServletException
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;

@CompileStatic
class HttpServletSimpleJrr extends HttpServlet {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter writer = resp.getWriter()
        writer.println("<html><body> Hello world </body><html>")
    }

}
