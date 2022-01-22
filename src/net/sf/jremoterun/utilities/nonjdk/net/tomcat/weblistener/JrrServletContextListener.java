package net.sf.jremoterun.utilities.nonjdk.net.tomcat.weblistener;


import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <web-app xmlns="http://xmlns.jcp.org/xml/ns/javaee"  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
 * xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee
 * http://xmlns.jcp.org/xml/ns/javaee/web-app_4_0.xsd"
 * version="4.0"  metadata-complete="true">
 *
 * <listener>
 * <listener-class>net.sf.jremoterun.utilities.nonjdk.net.tomcat.weblistener.JrrServletContextListener</listener-class>
 * </listener>
 *
 *
 * <context-param>
 * <param-name>logErrorEveryWhere</param-name>
 * <param-value> false </param-value>
 * </context-param>
 *
 *
 *
 * <context-param>
 * <param-name>AdditionalClassPath</param-name>
 * <param-value> path1 , path2 </param-value>
 * </context-param>
 *
 *
 *
 * <context-param>
 * <param-name>portJmx</param-name>
 * <param-value> 1234 </param-value>
 * </context-param>
 *
 *
 *
 * <context-param>
 * <param-name>InitClass</param-name>
 * <param-value> net.sf.jremoterun.utilities.nonjdk.net.tomcat.jmxweblistener.RunJMXServerContextListener , pkg2.ClassName2 </param-value>
 * </context-param>
 *
 * </web-app>
 */

public class JrrServletContextListener implements ServletContextListener {
    private static final Logger log = Logger.getLogger(JrrServletContextListener.class.getName());
    public static String AdditionalClassPath = "AdditionalClassPath";

    public static String InitClass = "InitClass";
    public static String logErrorEveryWhere = "logErrorEveryWhere";

    public static Method addURLMethod;
    public static ClassLoader classLoader1 = JrrServletContextListener.class.getClassLoader();
    public List<ServletContextListener> delegateServletContextListener;
    public String additionalClassPathValues;
    public String initClassValues;
    public static boolean logErrorEveryWhereB = false;

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        try {
            ServletContext servletContext = servletContextEvent.getServletContext();
            String logErrorEveryWhereS = servletContext.getInitParameter(logErrorEveryWhere);
            if (logErrorEveryWhereS != null) {
                logErrorEveryWhereB = "true".equalsIgnoreCase(logErrorEveryWhereS.trim());
            }

            additionalClassPathValues = servletContext.getInitParameter(AdditionalClassPath);
            if(logErrorEveryWhereB){
                log.info(AdditionalClassPath + " = "+additionalClassPathValues);
            }

            if (additionalClassPathValues != null) {
                addAllFilesToClassLoader(additionalClassPathValues,true);
            }
            initClassValues = servletContext.getInitParameter(InitClass);
            if(logErrorEveryWhereB){
                log.info(InitClass + " = "+initClassValues);
            }
            if (initClassValues == null) {
                log.warning("no init class set : " + InitClass);
            } else {
                delegateServletContextListener = initContextClass(initClassValues, servletContextEvent, logErrorEveryWhereB);
            }
            if(logErrorEveryWhereB){
                log.info(" inited ");
            }
        } catch (Throwable e) {
            log.log(Level.SEVERE, "failed init", e);
            if (logErrorEveryWhereB) {
                System.out.println("failed init " + e);
                System.err.println("failed init " + e);
                e.printStackTrace();
            }
        }

    }

    public static List<ServletContextListener> initContextClass(String initClassValues, ServletContextEvent servletContextEvent, boolean logErrorEveryWhere) throws ClassNotFoundException, InstantiationException, IllegalAccessException {

        String[] classNames = initClassValues.split(",");
        List<ServletContextListener> result = new ArrayList();
        for (String className1 : classNames) {
            Class<?> aClass;
            try {
                className1 = className1.trim();
                aClass = classLoader1.loadClass(className1);
            } catch (ClassNotFoundException ee) {
                log.severe("failed find class " + className1 + " " + ee);
                if (logErrorEveryWhere) {
                    System.out.println("failed find class " + className1 + " " + ee);
                    System.err.println("failed find class " + className1 + " " + ee);
                }
                throw ee;
            }
            ServletContextListener delegateServletContextListener1 = (ServletContextListener) aClass.newInstance();
            delegateServletContextListener1.contextInitialized(servletContextEvent);
            result.add(delegateServletContextListener1);
        }
        return result;
    }

    public static void addAllFilesToClassLoader(String additionalClassPathValues,boolean searchJarsinDir) throws Exception {
        String[] files1 = additionalClassPathValues.split(",");
        for (String f : files1) {
            File f2 = new File(f.trim());
            addFileToClassLoader(f2,searchJarsinDir);
        }
    }

    public static void addFileToClassLoader(File f2,boolean searchJarsInDir) throws Exception {
        if (addURLMethod == null) {
            addURLMethod = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            addURLMethod.setAccessible(true);
        }

        if (f2.exists()) {
            addURLMethod.invoke(classLoader1, f2.toURL());
            if(f2.isDirectory() && searchJarsInDir){
                File[] files = f2.listFiles();
                if(files==null){
                    log.severe("failed list files  in "+f2);
                }else{
                    for(File f :files){
                        if(f.getName().endsWith(".jar")){
                            addFileToClassLoader(f, false);
                        }
                    }
                }
            }
        } else {
            log.severe("file not found : " + f2);
        }

    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        if (delegateServletContextListener != null) {
            try {
                for (ServletContextListener delegateServletContext : delegateServletContextListener) {
                    delegateServletContext.contextDestroyed(servletContextEvent);
                }

            } catch (Throwable e) {
                log.log(Level.SEVERE, "failed deinit", e);
                if (logErrorEveryWhereB) {
                    System.out.println("failed init " + e);
                    System.err.println("failed init " + e);
                    e.printStackTrace();
                }
            }
        }
    }
}
