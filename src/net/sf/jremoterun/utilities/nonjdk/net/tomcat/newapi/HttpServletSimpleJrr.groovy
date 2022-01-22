package net.sf.jremoterun.utilities.nonjdk.net.tomcat.newapi

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils

//import jakarta.servlet.ServletException
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServlet
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import java.util.logging.Logger

@CompileStatic
class HttpServletSimpleJrr extends HttpServlet {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter writer = resp.getWriter()
        writer.println("<html><body> Hello world </body><html>")
    }

}
