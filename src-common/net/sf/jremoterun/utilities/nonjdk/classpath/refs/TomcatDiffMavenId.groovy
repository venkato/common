package net.sf.jremoterun.utilities.nonjdk.classpath.refs

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.MavenId;

import java.util.logging.Logger;

@CompileStatic
class TomcatDiffMavenId {


    // has javax.servlet.http.HttpServlet
    public static MavenId tomcat9 = new MavenId('org.apache.tomcat:tomcat-servlet-api:9.0.118')
    // has jakarta.servlet.http.HttpServlet
    public static MavenId tomcat10 = new MavenId('org.apache.tomcat:tomcat-servlet-api:10.1.55')
    public static MavenId tomcat11 = new MavenId('org.apache.tomcat:tomcat-servlet-api:11.0.22')


}
